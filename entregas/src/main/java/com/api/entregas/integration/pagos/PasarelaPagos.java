package com.api.entregas.integration.pagos;

import com.api.entregas.modelos.ResultadoPagoModel;
import java.math.BigDecimal;

public interface PasarelaPagos {
    ResultadoPagoModel cobrar(String pedidoId, BigDecimal monto, String tokenPago);
    ResultadoPagoModel reembolsar(String pedidoId, BigDecimal monto, String referenciaOriginal);
}
