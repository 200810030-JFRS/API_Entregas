package com.api.entregas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ItemPedidoRequest(@NotBlank String productoId, @Min(1) int cantidad) {}
