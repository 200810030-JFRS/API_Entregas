package com.api.entregas.controllers;

import com.api.entregas.dto.CalificacionRequest;
import com.api.entregas.modelos.CalificacionModel;
import com.api.entregas.services.CalificacionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pedidos/{pedidoId}/calificacion")
public class CalificacionController {
    private final CalificacionService service;
    public CalificacionController(CalificacionService service){this.service=service;}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary="Calificar un pedido entregado de 1 a 5")
    public CalificacionModel calificar(@PathVariable String pedidoId,@Valid @RequestBody CalificacionRequest req){return service.calificar(pedidoId,req.puntuacion(),req.comentario());}
}
