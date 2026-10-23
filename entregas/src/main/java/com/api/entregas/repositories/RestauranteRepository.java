package com.api.entregas.repositories;

import com.api.entregas.modelos.ProductoModel;
import com.api.entregas.modelos.RestauranteModel;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RestauranteRepository {
    private final Map<String, RestauranteModel> restaurantes = new ConcurrentHashMap<>();
    public RestauranteRepository() {
      restaurantes.put("R001", new RestauranteModel("R001","La Trattoria Demo","Pasta y pizza artesanal","Av. Central 100, Ciudad Demo",19.4326,-99.1332,true,List.of(
        new ProductoModel("P001","Pizza margarita","Salsa de tomate, mozzarella y albahaca",new BigDecimal("149.00"),true),
        new ProductoModel("P002","Pasta Alfredo","Pasta en salsa cremosa",new BigDecimal("139.00"),true),
        new ProductoModel("P003","Lasaña","Lasaña de carne gratinada",new BigDecimal("165.00"),true))));
      restaurantes.put("R002", new RestauranteModel("R002","Tacos del Centro","Tacos y antojitos mexicanos","Calle Mercado 25, Ciudad Demo",19.4350,-99.1290,true,List.of(
        new ProductoModel("P101","Orden de tacos","Orden de cinco tacos al pastor",new BigDecimal("89.00"),true),
        new ProductoModel("P102","Quesadillas","Tres quesadillas de queso",new BigDecimal("75.00"),true),
        new ProductoModel("P103","Agua fresca","Agua del día de 500 ml",new BigDecimal("28.00"),true))));
      restaurantes.put("R003", new RestauranteModel("R003","Sushi Nori Demo","Rollos y bowls japoneses","Paseo Norte 300, Ciudad Demo",19.4280,-99.1375,true,List.of(
        new ProductoModel("P201","California roll","Rollo de ocho piezas",new BigDecimal("115.00"),true),
        new ProductoModel("P202","Yakimeshi","Arroz frito con verduras",new BigDecimal("95.00"),true))));
    }
    public List<RestauranteModel> findAll(){return restaurantes.values().stream().sorted(java.util.Comparator.comparing(RestauranteModel::id)).toList();}
    public Optional<RestauranteModel> findById(String id){return Optional.ofNullable(restaurantes.get(id));}
}
