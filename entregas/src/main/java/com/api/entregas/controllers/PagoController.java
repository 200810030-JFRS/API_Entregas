package com.api.entregas.controllers;

import com.api.entregas.dto.PagoRequest;
import com.api.entregas.modelos.PedidoModel;
import com.api.entregas.services.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pedidos/{pedidoId}/pago")
public class PagoController {
    private final PedidoService pedidos;
    public PagoController(PedidoService pedidos){this.pedidos=pedidos;}
    @PostMapping @Operation(summary="Confirmar pago mediante token de pasarela")
    public PedidoModel pagar(@PathVariable String pedidoId,@Valid @RequestBody PagoRequest req){return pedidos.confirmarPago(pedidoId,req.tokenPago());}
}
