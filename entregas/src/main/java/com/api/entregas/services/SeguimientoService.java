package com.api.entregas.services;

import com.api.entregas.events.PedidoActualizadoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SeguimientoService {
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> suscriptores = new ConcurrentHashMap<>();
    public SseEmitter suscribir(String pedidoId) {
        SseEmitter emitter = new SseEmitter(0L);
        var lista = suscriptores.computeIfAbsent(pedidoId, k -> new CopyOnWriteArrayList<>());
        lista.add(emitter);
        Runnable remover=()->{lista.remove(emitter); if(lista.isEmpty()) suscriptores.remove(pedidoId,lista);};
        emitter.onCompletion(remover); emitter.onTimeout(remover); emitter.onError(ex->remover.run());
        return emitter;
    }
    @Async @EventListener
    public void difundir(PedidoActualizadoEvent evento) {
        List<SseEmitter> lista=suscriptores.get(evento.pedidoId());
        if(lista==null) return;
        for(SseEmitter emitter:lista) {
            try { emitter.send(SseEmitter.event().id(evento.fecha().toString()).name(evento.tipo()).data(evento)); }
            catch(IOException ex){emitter.completeWithError(ex); lista.remove(emitter);}
        }
    }
}
