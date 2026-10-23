package com.api.entregas.controllers;

import com.api.entregas.modelos.RepartidorModel;
import com.api.entregas.services.AsignacionRepartidorService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/repartidores")
public class RepartidorController {
    private final AsignacionRepartidorService service;
    public RepartidorController(AsignacionRepartidorService service){this.service=service;}
    @GetMapping public List<RepartidorModel> listar(){return service.listar();}
}
