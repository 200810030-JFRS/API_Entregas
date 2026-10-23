package com.api.entregas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public record CalificacionRequest(@NotNull @Min(1) @Max(5) Integer puntuacion, String comentario) {}
