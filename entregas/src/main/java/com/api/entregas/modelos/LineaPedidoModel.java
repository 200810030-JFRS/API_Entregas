package com.api.entregas.modelos;

import java.math.BigDecimal;

public record LineaPedidoModel(String productoId, String nombre, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {}
