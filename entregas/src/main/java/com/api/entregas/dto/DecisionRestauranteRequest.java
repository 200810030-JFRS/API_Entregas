package com.api.entregas.dto;

import jakarta.validation.constraints.NotNull;

public record DecisionRestauranteRequest(@NotNull Boolean aceptado, String motivo) {}
