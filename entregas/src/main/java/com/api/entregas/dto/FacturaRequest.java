package com.api.entregas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record FacturaRequest(@NotBlank String pedidoId, @NotBlank String rfc,
  @NotBlank String razonSocial, @Email @NotBlank String correo) {}
