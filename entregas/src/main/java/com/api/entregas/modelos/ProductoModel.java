package com.api.entregas.modelos;

import java.math.BigDecimal;

public record ProductoModel(String id, String nombre, String descripcion, BigDecimal precio, boolean disponible) {}
