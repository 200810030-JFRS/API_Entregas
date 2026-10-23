package com.api.entregas.modelos;

import java.time.Instant;

public record CalificacionModel(String id, String pedidoId, int puntuacion, String comentario, Instant creadaEn) {}
