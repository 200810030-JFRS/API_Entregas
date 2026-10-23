package com.api.entregas.services;

import com.api.entregas.exceptions.RecursoNoEncontradoException;
import com.api.entregas.exceptions.ReglaNegocioException;
import com.api.entregas.modelos.*;
import com.api.entregas.repositories.PedidoRepository;
import com.api.entregas.repositories.RepartidorRepository;
import com.api.entregas.repositories.RestauranteRepository;
import com.api.entregas.events.EventoPedidoService;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class AsignacionRepartidorService {
    private final PedidoRepository pedidos; private final RepartidorRepository repartidores; private final RestauranteRepository restaurantes; private final EventoPedidoService eventos;
    public AsignacionRepartidorService(PedidoRepository pedidos, RepartidorRepository repartidores, RestauranteRepository restaurantes, EventoPedidoService eventos){this.pedidos=pedidos;this.repartidores=repartidores;this.restaurantes=restaurantes;this.eventos=eventos;}
    public PedidoModel asignar(String pedidoId){
        PedidoModel p=pedido(pedidoId); synchronized(p){
            if(p.getEstado()!=EstadoPedido.ACEPTADO) throw new ReglaNegocioException("Solo se asigna un repartidor a pedidos aceptados.");
            RestauranteModel r=restaurantes.findById(p.getRestauranteId()).orElseThrow(()->new RecursoNoEncontradoException("Restaurante asociado no encontrado."));
            RepartidorModel elegido=repartidores.findDisponibles().stream().min(Comparator.comparingDouble(d->distanciaKm(d.getLatitud(),d.getLongitud(),r.latitud(),r.longitud()))).orElseThrow(()->new ReglaNegocioException("No hay repartidores disponibles."));
            elegido.setDisponible(false); repartidores.save(elegido); p.setRepartidorId(elegido.getId()); p.setRepartidorNombre(elegido.getNombre()); p.setEstado(EstadoPedido.REPARTIDOR_ASIGNADO); pedidos.save(p);
            eventos.emitir(p,"REPARTIDOR_ASIGNADO","Asignado el repartidor "+elegido.getNombre()+" ("+elegido.getId()+")."); return p;
        }
    }
    public PedidoModel actualizarUbicacion(String pedidoId,String repartidorId,double lat,double lon){
        PedidoModel p=pedido(pedidoId); synchronized(p){
            validarRepartidorAsignado(p,repartidorId);
            if(p.getEstado()!=EstadoPedido.REPARTIDOR_ASIGNADO && p.getEstado()!=EstadoPedido.EN_CAMINO) throw new ReglaNegocioException("El pedido no admite actualizaciones de ubicación en su estado actual.");
            RepartidorModel d=repartidores.findById(repartidorId).orElseThrow(()->new RecursoNoEncontradoException("Repartidor no encontrado."));
            d.setLatitud(lat);d.setLongitud(lon);repartidores.save(d);
            p.setUltimaLatitud(lat);p.setUltimaLongitud(lon);p.setUltimaActualizacionUbicacion(Instant.now());p.setEstado(EstadoPedido.EN_CAMINO);pedidos.save(p);
            eventos.emitir(p,"UBICACION_ACTUALIZADA","Nueva ubicación del repartidor disponible para seguimiento.");return p;
        }
    }
    public PedidoModel confirmarEntrega(String pedidoId,String repartidorId){
        PedidoModel p=pedido(pedidoId); synchronized(p){
            validarRepartidorAsignado(p,repartidorId);
            if(p.getEstado()!=EstadoPedido.EN_CAMINO && p.getEstado()!=EstadoPedido.REPARTIDOR_ASIGNADO) throw new ReglaNegocioException("Solo se puede confirmar entrega a un pedido asignado que siga activo.");
            p.setEstado(EstadoPedido.ENTREGADO);pedidos.save(p);
            repartidores.findById(repartidorId).ifPresent(d->{d.setDisponible(true);repartidores.save(d);});
            eventos.emitir(p,"ENTREGA_CONFIRMADA","El pedido fue entregado. Ya puede calificarse el servicio.");return p;
        }
    }
    public List<RepartidorModel> listar(){return repartidores.findAll();}
    private PedidoModel pedido(String id){return pedidos.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Pedido no encontrado: "+id));}
    private void validarRepartidorAsignado(PedidoModel p,String id){if(p.getRepartidorId()==null||!p.getRepartidorId().equals(id)) throw new ReglaNegocioException("El repartidor indicado no es el asignado a este pedido.");}
    private double distanciaKm(double lat1,double lon1,double lat2,double lon2){double r=6371, dLat=Math.toRadians(lat2-lat1),dLon=Math.toRadians(lon2-lon1);double a=Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(dLon/2)*Math.sin(dLon/2);return 2*r*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));}
}
