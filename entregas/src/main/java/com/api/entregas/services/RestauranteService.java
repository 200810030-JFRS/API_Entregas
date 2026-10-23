package com.api.entregas.services;

import com.api.entregas.exceptions.RecursoNoEncontradoException;
import com.api.entregas.modelos.RestauranteModel;
import com.api.entregas.repositories.RestauranteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RestauranteService {
    private final RestauranteRepository repository;
    public RestauranteService(RestauranteRepository repository){this.repository=repository;}
    public List<RestauranteModel> listar(){return repository.findAll().stream().filter(RestauranteModel::abierto).toList();}
    public RestauranteModel obtener(String id){return repository.findById(id).filter(RestauranteModel::abierto).orElseThrow(()->new RecursoNoEncontradoException("Restaurante no encontrado o cerrado: "+id));}
    public List<com.api.entregas.modelos.ProductoModel> listarMenu(String id){return obtener(id).menu();}
}
