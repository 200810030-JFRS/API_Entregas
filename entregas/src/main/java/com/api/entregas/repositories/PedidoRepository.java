package com.api.entregas.repositories;

import com.api.entregas.modelos.PedidoModel;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PedidoRepository {
    private final Map<String,PedidoModel> pedidos=new ConcurrentHashMap<>();
    public PedidoModel save(PedidoModel p){pedidos.put(p.getId(),p); return p;}
    public Optional<PedidoModel> findById(String id){return Optional.ofNullable(pedidos.get(id));}
    public List<PedidoModel> findAll(){return pedidos.values().stream().sorted(java.util.Comparator.comparing(PedidoModel::getCreadoEn).reversed()).toList();}
}
