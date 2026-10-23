package com.api.entregas.modelos;

import java.math.BigDecimal;
import java.time.Instant;

public record FacturaModel(String id, String pedidoId, String rfc, String razonSocial, String correo,
        BigDecimal total, String sistemaOrigen, Instant creadaEn, String estado) {}
