package com.api.entregas.integration.facturacion;

import com.api.entregas.modelos.PedidoModel;

public interface SistemaFacturacionLegadoPort {
    ResultadoFacturacionModel emitir(PedidoModel pedido, String rfc, String razonSocial, String correo);
}
