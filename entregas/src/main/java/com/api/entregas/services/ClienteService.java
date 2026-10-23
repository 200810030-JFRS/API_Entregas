package com.api.entregas.services;

import com.api.entregas.exceptions.ReglaNegocioException;
import com.api.entregas.modelos.PedidoModel;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClienteService {
    private final PedidoService pedidos;
    public ClienteService(PedidoService pedidos){this.pedidos=pedidos;}
    public List<PedidoModel> consultarPedidos(String correo){
        if(correo==null||correo.isBlank()) throw new ReglaNegocioException("El correo es obligatorio.");
        return pedidos.listarPorCliente(correo.trim());
    }
}
