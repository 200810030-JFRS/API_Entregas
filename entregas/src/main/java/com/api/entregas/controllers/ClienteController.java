package com.api.entregas.controllers;

import com.api.entregas.modelos.PedidoModel;
import com.api.entregas.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service){this.service=service;}
    @GetMapping("/pedidos") @Operation(summary="Consultar pedidos de un correo (solo demostración; no sustituye autenticación)")
    public List<PedidoModel> pedidos(@RequestParam String correo){return service.consultarPedidos(correo);}
}
