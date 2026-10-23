package com.api.entregas.dto;

import jakarta.validation.constraints.NotBlank;
public record EntregaRequest(@NotBlank String repartidorId) {}
