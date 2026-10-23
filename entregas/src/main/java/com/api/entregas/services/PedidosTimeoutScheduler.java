package com.api.entregas.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PedidosTimeoutScheduler {
    private final PedidoService pedidoService;
    public PedidosTimeoutScheduler(PedidoService pedidoService){this.pedidoService=pedidoService;}
    @Scheduled(fixedDelayString="${rapientrega.timeout.scan-ms:10000}")
    public void revisarPedidosSinRespuesta(){pedidoService.cancelarPedidosVencidos();}
}
