package com.api.entregas.repositories;

import com.api.entregas.modelos.CalificacionModel;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CalificacionRepository {
    private final Map<String,CalificacionModel> calificaciones=new ConcurrentHashMap<>();
    public CalificacionModel save(CalificacionModel c){calificaciones.put(c.pedidoId(),c);return c;}
    public Optional<CalificacionModel> findByPedidoId(String pedidoId){return Optional.ofNullable(calificaciones.get(pedidoId));}
}
