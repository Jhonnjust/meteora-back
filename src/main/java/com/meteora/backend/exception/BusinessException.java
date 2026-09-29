package com.meteora.backend.exception;

/** Erro de regra de negocio (estoque insuficiente, carrinho vazio, cupom invalido, etc) */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
