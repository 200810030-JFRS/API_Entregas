package com.api.entregas.services;

import com.api.entregas.exceptions.RecursoNoEncontradoException;
import com.api.entregas.exceptions.ReglaNegocioException;
import com.api.entregas.modelos.CalificacionModel;
import com.api.entregas.modelos.EstadoPedido;
import com.api.entregas.modelos.PedidoModel;
import com.api.entregas.repositories.CalificacionRepository;
import com.api.entregas.repositories.PedidoRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
public class CalificacionService {
    private final PedidoRepository pedidos; private final CalificacionRepository calificaciones;
    public CalificacionService(PedidoRepository pedidos,CalificacionRepository calificaciones){this.pedidos=pedidos;this.calificaciones=calificaciones;}
    public CalificacionModel calificar(String pedidoId,int puntuacion,String comentario){
        PedidoModel p=pedidos.findById(pedidoId).orElseThrow(()->new RecursoNoEncontradoException("Pedido no encontrado: "+pedidoId));
        if(p.getEstado()!=EstadoPedido.ENTREGADO) throw new ReglaNegocioException("Solo se puede calificar un pedido entregado.");
        if(calificaciones.findByPedidoId(pedidoId).isPresent()) throw new ReglaNegocioException("Este pedido ya fue calificado.");
        CalificacionModel c=new CalificacionModel("CAL-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(),pedidoId,puntuacion,comentario,Instant.now());
        calificaciones.save(c);p.setCalificacion(puntuacion);p.setComentarioCalificacion(comentario);pedidos.save(p);return c;
    }
}
