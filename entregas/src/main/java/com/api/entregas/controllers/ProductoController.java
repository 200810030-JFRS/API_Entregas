package com.api.entregas.controllers;

import com.api.entregas.modelos.ProductoModel;
import com.api.entregas.services.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/restaurantes/{restauranteId}/productos")
public class ProductoController {
    private final RestauranteService restaurantes;
    public ProductoController(RestauranteService restaurantes){this.restaurantes=restaurantes;}
    @GetMapping @Operation(summary="Listar productos disponibles en un restaurante")
    public List<ProductoModel> listar(@PathVariable String restauranteId){return restaurantes.listarMenu(restauranteId).stream().filter(ProductoModel::disponible).toList();}
}
