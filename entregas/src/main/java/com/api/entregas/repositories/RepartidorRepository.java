package com.api.entregas.repositories;

import com.api.entregas.modelos.RepartidorModel;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RepartidorRepository {
    private final Map<String,RepartidorModel> repartidores=new ConcurrentHashMap<>();
    public RepartidorRepository(){
      repartidores.put("D001",new RepartidorModel("D001","Alex Demo",19.4310,-99.1320,true));
      repartidores.put("D002",new RepartidorModel("D002","Sam Demo",19.4380,-99.1280,true));
      repartidores.put("D003",new RepartidorModel("D003","Cris Demo",19.4200,-99.1400,true));
    }
    public List<RepartidorModel> findAll(){return repartidores.values().stream().sorted(java.util.Comparator.comparing(RepartidorModel::getId)).toList();}
    public List<RepartidorModel> findDisponibles(){return repartidores.values().stream().filter(RepartidorModel::isDisponible).toList();}
    public Optional<RepartidorModel> findById(String id){return Optional.ofNullable(repartidores.get(id));}
    public RepartidorModel save(RepartidorModel d){repartidores.put(d.getId(),d);return d;}
}
