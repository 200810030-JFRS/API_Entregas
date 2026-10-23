package com.api.entregas.repositories;

import com.api.entregas.modelos.FacturaModel;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class FacturaRepository {
    private final Map<String,FacturaModel> facturas=new ConcurrentHashMap<>();
    public FacturaModel save(FacturaModel f){facturas.put(f.id(),f);return f;}
    public Optional<FacturaModel> findById(String id){return Optional.ofNullable(facturas.get(id));}
    public Optional<FacturaModel> findByPedidoId(String pedidoId){return facturas.values().stream().filter(f->f.pedidoId().equals(pedidoId)).findFirst();}
}
