package com.api.entregas.controllers;

import com.api.entregas.dto.ActualizarUbicacionRequest;
import com.api.entregas.dto.DecisionRestauranteRequest;
import com.api.entregas.dto.EntregaRequest;
import com.api.entregas.modelos.PedidoModel;
import com.api.entregas.services.AsignacionRepartidorService;
import com.api.entregas.services.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pedidos/{pedidoId}")
public class EntregaController {
    private final PedidoService pedidos; private final AsignacionRepartidorService entregas;
    public EntregaController(PedidoService pedidos,AsignacionRepartidorService entregas){this.pedidos=pedidos;this.entregas=entregas;}
    @PostMapping("/decision-restaurante") @Operation(summary="Aceptar o rechazar pedido dentro de cinco minutos")
    public PedidoModel decidir(@PathVariable String pedidoId,@Valid @RequestBody DecisionRestauranteRequest req){return pedidos.decidirRestaurante(pedidoId,req.aceptado(),req.motivo());}
    @PostMapping("/asignacion") @Operation(summary="Asignar al repartidor disponible más cercano al restaurante")
    public PedidoModel asignar(@PathVariable String pedidoId){return entregas.asignar(pedidoId);}
    @PostMapping("/ubicacion") @Operation(summary="Publicar coordenadas actuales del repartidor")
    public PedidoModel ubicacion(@PathVariable String pedidoId,@Valid @RequestBody ActualizarUbicacionRequest req){return entregas.actualizarUbicacion(pedidoId,req.repartidorId(),req.latitud(),req.longitud());}
    @PostMapping("/entrega") @Operation(summary="Confirmar la entrega del pedido")
    public PedidoModel entrega(@PathVariable String pedidoId,@Valid @RequestBody EntregaRequest req){return entregas.confirmarEntrega(pedidoId,req.repartidorId());}
}
