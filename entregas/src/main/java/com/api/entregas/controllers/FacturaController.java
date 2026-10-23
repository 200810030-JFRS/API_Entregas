package com.api.entregas.controllers;

import com.api.entregas.dto.FacturaRequest;
import com.api.entregas.modelos.FacturaModel;
import com.api.entregas.services.FacturaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/facturas")
public class FacturaController {
    private final FacturaService service;
    public FacturaController(FacturaService service){this.service=service;}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary="Solicitar factura por el adaptador del sistema legado de 2012")
    public FacturaModel solicitar(@Valid @RequestBody FacturaRequest req){return service.solicitar(req);}
}
