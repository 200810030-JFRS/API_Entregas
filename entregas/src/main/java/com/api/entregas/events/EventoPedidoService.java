package com.api.entregas.events;

import com.api.entregas.modelos.PedidoModel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class EventoPedidoService {
    private final ApplicationEventPublisher publisher;
    public EventoPedidoService(ApplicationEventPublisher publisher){this.publisher=publisher;}
    public void emitir(PedidoModel pedido, String tipo, String mensaje){
        publisher.publishEvent(new PedidoActualizadoEvent(pedido.getId(),tipo,pedido.getEstado().name(),mensaje,Instant.now()));
    }
}
