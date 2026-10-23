package com.api.entregas.modelos;

import java.util.List;

public record RestauranteModel(String id, String nombre, String descripcion, String direccion, double latitud,
        double longitud, boolean abierto, List<ProductoModel> menu) {}
