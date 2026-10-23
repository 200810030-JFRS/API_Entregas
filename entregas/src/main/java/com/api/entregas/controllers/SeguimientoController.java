package com.api.entregas.controllers;

import com.api.entregas.services.PedidoService;
import com.api.entregas.services.SeguimientoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/pedidos")
public class SeguimientoController {
    private final PedidoService pedidos; private final SeguimientoService seguimiento;
    public SeguimientoController(PedidoService pedidos,SeguimientoService seguimiento){this.pedidos=pedidos;this.seguimiento=seguimiento;}
    @GetMapping(value="/{id}/eventos", produces=MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter eventos(@PathVariable String id){pedidos.obtener(id);return seguimiento.suscribir(id);}
}
