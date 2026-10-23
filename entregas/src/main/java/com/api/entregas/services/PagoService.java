package com.api.entregas.services;

import com.api.entregas.integration.pagos.PasarelaPagos;
import com.api.entregas.modelos.PedidoModel;
import com.api.entregas.modelos.ResultadoPagoModel;
import org.springframework.stereotype.Service;

/** Encapsula la frontera con el proveedor de pagos. El controlador no recibe datos de tarjeta. */
@Service
public class PagoService {
    private final PasarelaPagos pasarela;
    public PagoService(PasarelaPagos pasarela){this.pasarela=pasarela;}
    public ResultadoPagoModel cobrar(PedidoModel pedido,String token){return pasarela.cobrar(pedido.getId(),pedido.getTotal(),token);}
    public ResultadoPagoModel reembolsar(PedidoModel pedido){return pasarela.reembolsar(pedido.getId(),pedido.getTotal(),pedido.getReferenciaPago());}
}
