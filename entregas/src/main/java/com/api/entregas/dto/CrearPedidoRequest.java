package com.api.entregas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.util.List;

public record CrearPedidoRequest(
    @NotBlank String clienteNombre,
    @Email @NotBlank String clienteCorreo,
    @NotBlank String restauranteId,
    @NotBlank String direccionEntrega,
    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitudEntrega,
    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitudEntrega,
    @NotEmpty List<@Valid ItemPedidoRequest> productos
) {}
