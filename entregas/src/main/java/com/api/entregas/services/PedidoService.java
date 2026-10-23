package com.api.entregas.services;

import com.api.entregas.dto.CrearPedidoRequest;
import com.api.entregas.exceptions.RecursoNoEncontradoException;
import com.api.entregas.exceptions.ReglaNegocioException;
import com.api.entregas.modelos.*;
import com.api.entregas.repositories.PedidoRepository;
import com.api.entregas.repositories.RestauranteRepository;
import com.api.entregas.repositories.RepartidorRepository;
import com.api.entregas.events.EventoPedidoService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PedidoService {
    private static final Duration PLAZO_ACEPTACION=Duration.ofMinutes(5);
    private static final BigDecimal TARIFA_ENVIO=new BigDecimal("29.00");
    private final PedidoRepository pedidos;
    private final RestauranteRepository restaurantes;
    private final RepartidorRepository repartidores;
    private final PagoService pagoService;
    private final EventoPedidoService eventos;
    public PedidoService(PedidoRepository pedidos, RestauranteRepository restaurantes, RepartidorRepository repartidores,
            PagoService pagoService, EventoPedidoService eventos){
        this.pedidos=pedidos; this.restaurantes=restaurantes; this.repartidores=repartidores; this.pagoService=pagoService; this.eventos=eventos;
    }
    public PedidoModel crear(CrearPedidoRequest req){
        RestauranteModel r=restaurantes.findById(req.restauranteId())
          .orElseThrow(()->new RecursoNoEncontradoException("Restaurante no encontrado: "+req.restauranteId()));
        if(!r.abierto()) throw new ReglaNegocioException("El restaurante está cerrado: "+req.restauranteId());
        List<LineaPedidoModel> lineas=new ArrayList<>(); BigDecimal subtotal=BigDecimal.ZERO;
        for(var item:req.productos()){
            ProductoModel p=r.menu().stream().filter(x->x.id().equals(item.productoId())).findFirst()
              .orElseThrow(()->new RecursoNoEncontradoException("Producto no pertenece al restaurante: "+item.productoId()));
            if(!p.disponible()) throw new ReglaNegocioException("Producto no disponible: "+p.nombre());
            BigDecimal importe=p.precio().multiply(BigDecimal.valueOf(item.cantidad())).setScale(2,RoundingMode.HALF_UP);
            lineas.add(new LineaPedidoModel(p.id(),p.nombre(),item.cantidad(),p.precio(),importe)); subtotal=subtotal.add(importe);
        }
        PedidoModel pedido=new PedidoModel();
        pedido.setId("PED-"+UUID.randomUUID().toString().substring(0,8).toUpperCase());
        pedido.setClienteNombre(req.clienteNombre().trim()); pedido.setClienteCorreo(req.clienteCorreo().trim());
        pedido.setRestauranteId(r.id()); pedido.setRestauranteNombre(r.nombre()); pedido.setDireccionEntrega(req.direccionEntrega().trim());
        pedido.setLatitudEntrega(req.latitudEntrega()); pedido.setLongitudEntrega(req.longitudEntrega()); pedido.setDetalles(List.copyOf(lineas));
        pedido.setSubtotal(subtotal.setScale(2,RoundingMode.HALF_UP)); pedido.setTarifaEnvio(TARIFA_ENVIO);
        pedido.setTotal(subtotal.add(TARIFA_ENVIO).setScale(2,RoundingMode.HALF_UP));
        pedido.setEstado(EstadoPedido.PENDIENTE_PAGO); pedido.setEstadoPago(EstadoPago.PENDIENTE); pedido.setCreadoEn(Instant.now());
        pedidos.save(pedido); eventos.emitir(pedido,"PEDIDO_CREADO","Pedido creado; falta confirmar el pago."); return pedido;
    }
    public PedidoModel confirmarPago(String id, String tokenPago){
        PedidoModel p=obtener(id); synchronized(p){
            if(p.getEstado()!=EstadoPedido.PENDIENTE_PAGO) throw new ReglaNegocioException("El pedido no está pendiente de pago.");
            ResultadoPagoModel resultado=pagoService.cobrar(p,tokenPago);
            if(!resultado.aprobado()){
                p.setEstadoPago(EstadoPago.RECHAZADO); p.setEstado(EstadoPedido.CANCELADO); p.setMotivoCancelacion("Pago rechazado: "+resultado.mensaje());
                pedidos.save(p); eventos.emitir(p,"PAGO_RECHAZADO",p.getMotivoCancelacion()); return p;
            }
            p.setEstadoPago(EstadoPago.APROBADO); p.setReferenciaPago(resultado.referencia()); p.setPagadoEn(Instant.now());
            p.setEstado(EstadoPedido.PENDIENTE_ACEPTACION); pedidos.save(p);
            eventos.emitir(p,"PAGO_CONFIRMADO","Pago aprobado. El restaurante dispone de 5 minutos para responder."); return p;
        }
    }
    public PedidoModel decidirRestaurante(String id, boolean aceptado, String motivo){
        PedidoModel p=obtener(id); synchronized(p){
            if(p.getEstado()!=EstadoPedido.PENDIENTE_ACEPTACION) throw new ReglaNegocioException("El pedido no está pendiente de aceptación del restaurante.");
            if(p.getPagadoEn()==null) throw new ReglaNegocioException("No se puede decidir un pedido sin pago confirmado.");
            if(!Instant.now().isBefore(p.getPagadoEn().plus(PLAZO_ACEPTACION))){cancelarYReembolsar(p,"Se agotó el plazo de 5 minutos para aceptar."); return p;}
            if(aceptado){p.setEstado(EstadoPedido.ACEPTADO); pedidos.save(p); eventos.emitir(p,"PEDIDO_ACEPTADO","El restaurante aceptó el pedido.");}
            else {cancelarYReembolsar(p,(motivo==null||motivo.isBlank())?"El restaurante rechazó el pedido.":"Rechazado por el restaurante: "+motivo);}
            return p;
        }
    }
    public PedidoModel obtener(String id){return pedidos.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Pedido no encontrado: "+id));}
    public List<PedidoModel> listar(){return pedidos.findAll();}
    public List<PedidoModel> listarPorCliente(String correo){return pedidos.findAll().stream().filter(p->p.getClienteCorreo().equalsIgnoreCase(correo)).toList();}
    public void cancelarPedidosVencidos(){
        Instant limite=Instant.now().minus(PLAZO_ACEPTACION);
        for(PedidoModel p:pedidos.findAll()){
            if(p.getEstado()==EstadoPedido.PENDIENTE_ACEPTACION && p.getPagadoEn()!=null && !p.getPagadoEn().isAfter(limite)){
                synchronized(p){if(p.getEstado()==EstadoPedido.PENDIENTE_ACEPTACION) cancelarYReembolsar(p,"El restaurante no respondió dentro de 5 minutos; cancelación automática.");}
            }
        }
    }
    private void cancelarYReembolsar(PedidoModel p,String motivo){
        if(p.getEstadoPago()==EstadoPago.APROBADO){
            ResultadoPagoModel reembolso=pagoService.reembolsar(p);
            if(reembolso.aprobado()){p.setEstadoPago(EstadoPago.REEMBOLSADO);} else {throw new ReglaNegocioException("La cancelación requiere reembolso manual: "+reembolso.mensaje());}
        }
        p.setEstado(EstadoPedido.CANCELADO); p.setMotivoCancelacion(motivo); pedidos.save(p);
        eventos.emitir(p,"PEDIDO_CANCELADO",motivo+" El estado del pago es "+p.getEstadoPago()+".");
    }
}
