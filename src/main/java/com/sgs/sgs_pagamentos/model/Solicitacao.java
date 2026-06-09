package com.sgs.sgs_pagamentos.model;

import com.sgs.sgs_pagamentos.dto.SolicitacaoListagemDTO;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@SqlResultSetMapping(
        name = "SolicitacaoListagemMapping",
        classes = @ConstructorResult(
                targetClass = SolicitacaoListagemDTO.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "solicitanteNome", type = String.class),
                        @ColumnResult(name = "solicitanteDocumento", type = String.class),
                        @ColumnResult(name = "categoriaNome", type = String.class),
                        @ColumnResult(name = "status", type = String.class),
                        @ColumnResult(name = "valor", type = BigDecimal.class),
                        @ColumnResult(name = "dataSolicitacao", type = LocalDateTime.class)
                }
        )
)
public class Solicitacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Solicitante solicitante;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacao status;
}