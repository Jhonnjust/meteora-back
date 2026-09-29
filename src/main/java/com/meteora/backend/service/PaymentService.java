package com.meteora.backend.service;

import com.meteora.backend.model.Order;
import com.meteora.backend.model.OrderStatus;
import com.meteora.backend.model.PaymentMethod;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Simula a integracao com um gateway de pagamento (Pix / cartao / boleto).
 * Em producao, aqui entraria a chamada real para o gateway (ex.: Mercado Pago, Stripe, PagSeguro etc),
 * tratamento de webhooks de confirmacao, e persistencia do id da transacao externa.
 */
@Service
public class PaymentService {

    public record ResultadoPagamento(OrderStatus statusResultante, String detalhes) {}

    public ResultadoPagamento processar(Order pedido) {
        return switch (pedido.getFormaPagamento()) {
            case PIX -> processarPix(pedido);
            case CARTAO -> processarCartao(pedido);
            case BOLETO -> processarBoleto(pedido);
        };
    }

    private ResultadoPagamento processarPix(Order pedido) {
        String payloadFake = "00020126580014BR.GOV.BCB.PIX0136" + UUID.randomUUID() + "5204000053039865802BR6009MEEXAMPLE";
        String detalhes = "QR Code Pix gerado (simulado). Copia e cola: " + payloadFake;
        // Pix simulado como aprovacao imediata
        return new ResultadoPagamento(OrderStatus.PAGO, detalhes);
    }

    private ResultadoPagamento processarCartao(Order pedido) {
        // Simula autorizadora aprovando a transacao
        String idTransacao = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String detalhes = "Pagamento aprovado no cartao (simulado). Id da transacao: " + idTransacao;
        return new ResultadoPagamento(OrderStatus.PAGO, detalhes);
    }

    private ResultadoPagamento processarBoleto(Order pedido) {
        String codigoBarras = "34191" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 39);
        String detalhes = "Boleto gerado (simulado), vencimento em 3 dias uteis. Codigo: " + codigoBarras;
        // Boleto fica aguardando compensacao
        return new ResultadoPagamento(OrderStatus.AGUARDANDO_PAGAMENTO, detalhes);
    }
}
