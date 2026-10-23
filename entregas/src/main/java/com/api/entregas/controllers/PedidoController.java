package com.api.entregas.controllers;

import com.api.entregas.dto.CrearPedidoRequest;
import com.api.entregas.modelos.PedidoModel;
import com.api.entregas.services.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
    private final PedidoService pedidos;
    public PedidoController(PedidoService pedidos){this.pedidos=pedidos;}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary="Crear pedido a partir del menú vigente")
    public PedidoModel crear(@Valid @RequestBody CrearPedidoRequest req){return pedidos.crear(req);}
    @GetMapping @Operation(summary="Listar pedidos de demostración")
    public List<PedidoModel> listar(){return pedidos.listar();}
    @GetMapping("/{id}") @Operation(summary="Consultar estado y detalle de un pedido")
    public PedidoModel obtener(@PathVariable String id){return pedidos.obtener(id);}
}
