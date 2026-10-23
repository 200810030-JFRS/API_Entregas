package com.api.entregas.controllers;

import com.api.entregas.modelos.LineaPedidoModel;
import com.api.entregas.services.PedidoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/pedidos/{pedidoId}/detalles")
public class DetallePedidoController {
    private final PedidoService pedidos;
    public DetallePedidoController(PedidoService pedidos){this.pedidos=pedidos;}
    @GetMapping public List<LineaPedidoModel> listar(@PathVariable String pedidoId){return pedidos.obtener(pedidoId).getDetalles();}
}
