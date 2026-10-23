package com.api.entregas.events;

import java.time.Instant;
public record PedidoActualizadoEvent(String pedidoId, String tipo, String estado, String mensaje, Instant fecha) {}
