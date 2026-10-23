package com.api.entregas.integration.facturacion;

import java.time.Instant;
import java.math.BigDecimal;

public record ResultadoFacturacionModel(String idFactura, String pedidoId, String rfc, String razonSocial,
  String correo, BigDecimal total, Instant creadaEn, String estado) {}
