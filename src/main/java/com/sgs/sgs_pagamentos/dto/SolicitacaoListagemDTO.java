package com.sgs.sgs_pagamentos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SolicitacaoListagemDTO {
    private Long id;
    private String solicitanteNome;
    private String solicitanteDocumento;
    private String categoriaNome;
    private String status;
    private BigDecimal valor;
    private LocalDateTime dataSolicitacao;
}
