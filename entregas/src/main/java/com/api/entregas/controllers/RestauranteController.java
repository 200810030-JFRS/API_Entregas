package com.api.entregas.controllers;

import com.api.entregas.modelos.RestauranteModel;
import com.api.entregas.services.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurantes")
public class RestauranteController {
    private final RestauranteService service;
    public RestauranteController(RestauranteService service){this.service=service;}
    @GetMapping @Operation(summary="Listar restaurantes abiertos")
    public List<RestauranteModel> listar(){return service.listar();}
    @GetMapping("/{id}") @Operation(summary="Consultar restaurante y su menú")
    public RestauranteModel obtener(@PathVariable String id){return service.obtener(id);}
}
