package com.api.entregas.integration.pagos;

import com.api.entregas.modelos.ResultadoPagoModel;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

/** Adaptador local de demostración. No cobra dinero real ni procesa números de tarjeta. */
@Component
public class PasarelaPagosSimulada implements PasarelaPagos {
    @Override public ResultadoPagoModel cobrar(String pedidoId, BigDecimal monto, String tokenPago) {
        if (tokenPago == null || tokenPago.isBlank()) return new ResultadoPagoModel(false,"", "Token de pago requerido");
        if (tokenPago.equalsIgnoreCase("tok_demo_rechazado")) return new ResultadoPagoModel(false,"", "Pago rechazado por el simulador");
        return new ResultadoPagoModel(true,"PAY-"+UUID.randomUUID().toString().substring(0,12).toUpperCase(),"Pago simulado aprobado por $"+monto);
    }
    @Override public ResultadoPagoModel reembolsar(String pedidoId, BigDecimal monto, String referenciaOriginal) {
        return new ResultadoPagoModel(true,"REF-"+UUID.randomUUID().toString().substring(0,12).toUpperCase(),"Reembolso simulado aplicado");
    }
}
