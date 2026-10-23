package com.api.entregas.integration.facturacion;

import com.api.entregas.modelos.PedidoModel;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;

/** Fachada/adaptador didáctico que representa el contrato con el sistema heredado de 2012.
 * Sustituir el cuerpo por el conector real (SOAP/REST/archivo) sin cambiar FacturaService.
 */
@Component
public class SistemaFacturacionLegadoAdapter implements SistemaFacturacionLegadoPort {
    @Override public ResultadoFacturacionModel emitir(PedidoModel p, String rfc, String razonSocial, String correo) {
        return new ResultadoFacturacionModel("LEG-"+UUID.randomUUID().toString().substring(0,10).toUpperCase(),p.getId(),rfc,razonSocial,correo,p.getTotal(),Instant.now(),"ACEPTADA_POR_ADAPTADOR_DEMO");
    }
}
