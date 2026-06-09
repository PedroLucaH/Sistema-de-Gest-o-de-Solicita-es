package com.sgs.sgs_pagamentos.model;

public enum StatusSolicitacao {
    SOLICITADO, LIBERADO, APROVADO, REJEITADO, CANCELADO;

    public boolean podeTransicionarPara(StatusSolicitacao novoStatus) {
        if (this == REJEITADO || this == CANCELADO) {
            return false;
        }
        return switch (this) {
            case SOLICITADO -> novoStatus == LIBERADO || novoStatus == REJEITADO;
            case LIBERADO -> novoStatus == APROVADO || novoStatus == REJEITADO;
            case APROVADO -> novoStatus == CANCELADO;
            default -> false;
        };
    }
}
