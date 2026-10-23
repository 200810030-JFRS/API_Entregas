package com.api.entregas.services;

import com.api.entregas.dto.FacturaRequest;
import com.api.entregas.exceptions.RecursoNoEncontradoException;
import com.api.entregas.exceptions.ReglaNegocioException;
import com.api.entregas.integration.facturacion.SistemaFacturacionLegadoPort;
import com.api.entregas.modelos.EstadoPedido;
import com.api.entregas.modelos.FacturaModel;
import com.api.entregas.modelos.PedidoModel;
import com.api.entregas.repositories.FacturaRepository;
import com.api.entregas.repositories.PedidoRepository;
import org.springframework.stereotype.Service;

@Service
public class FacturaService {
    private final PedidoRepository pedidos; private final FacturaRepository facturas; private final SistemaFacturacionLegadoPort legado;
    public FacturaService(PedidoRepository pedidos,FacturaRepository facturas,SistemaFacturacionLegadoPort legado){this.pedidos=pedidos;this.facturas=facturas;this.legado=legado;}
    public FacturaModel solicitar(FacturaRequest req){
        PedidoModel p=pedidos.findById(req.pedidoId()).orElseThrow(()->new RecursoNoEncontradoException("Pedido no encontrado: "+req.pedidoId()));
        if(p.getEstado()!=EstadoPedido.ENTREGADO) throw new ReglaNegocioException("La factura solo puede solicitarse para pedidos entregados.");
        if(facturas.findByPedidoId(p.getId()).isPresent()) throw new ReglaNegocioException("Ya existe una factura para este pedido.");
        var result=legado.emitir(p,req.rfc().trim().toUpperCase(),req.razonSocial().trim(),req.correo().trim());
        FacturaModel factura=new FacturaModel(result.idFactura(),result.pedidoId(),result.rfc(),result.razonSocial(),result.correo(),result.total(),"Sistema legado 2012 (adaptador simulado)",result.creadaEn(),result.estado());
        facturas.save(factura); p.setFacturaId(factura.id());pedidos.save(p);return factura;
    }
}
