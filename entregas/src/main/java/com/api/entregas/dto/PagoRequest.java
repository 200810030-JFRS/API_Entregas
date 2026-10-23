package com.api.entregas.dto;

import jakarta.validation.constraints.NotBlank;

public record PagoRequest(@NotBlank String tokenPago) {}
