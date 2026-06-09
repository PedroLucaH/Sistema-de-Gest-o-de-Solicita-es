package com.sgs.sgs_pagamentos.repository;
import com.sgs.sgs_pagamentos.dto.SolicitacaoListagemDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;



@Repository
public class SolicitacaoRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<SolicitacaoListagemDTO> buscarComFiltrosNativos(String status, Long categoriaId, String dataInicio, String dataFim) {
        StringBuilder sql = new StringBuilder(
                "SELECT s.id as id, sol.nome as solicitanteNome, sol.cpf_cnpj as solicitanteDocumento, " +
                        "c.nome as categoriaNome, s.status as status, s.valor as valor, s.data_solicitacao as dataSolicitacao " +
                        "FROM solicitacao s " +
                        "JOIN solicitante sol ON s.solicitante_id = sol.id " +
                        "JOIN categoria c ON s.categoria_id = c.id WHERE 1=1 " // Exigência de SQL Nativo com JOIN [cite: 5, 71]
        );

        // Filtros dinâmicos exigidos no edital [cite: 53, 71]
        if (status != null && !status.isEmpty()) sql.append("AND s.status = :status "); // [cite: 54]
        if (categoriaId != null) sql.append("AND c.id = :categoriaId "); // [cite: 56]
        if (dataInicio != null && !dataInicio.isEmpty()) sql.append("AND s.data_solicitacao >= :dataInicio "); // [cite: 55]
        if (dataFim != null && !dataFim.isEmpty()) sql.append("AND s.data_solicitacao <= :dataFim "); // [cite: 55]

        sql.append("ORDER BY s.data_solicitacao DESC");

        Query query = em.createNativeQuery(sql.toString(), "SolicitacaoListagemMapping");

        if (status != null && !status.isEmpty()) query.setParameter("status", status);
        if (categoriaId != null) query.setParameter("categoriaId", categoriaId);

        if (dataInicio != null && !dataInicio.isEmpty()) {
            query.setParameter("dataInicio", LocalDate.parse(dataInicio).atStartOfDay());
        }
        if (dataFim != null && !dataFim.isEmpty()) {
            query.setParameter("dataFim", LocalDate.parse(dataFim).atTime(LocalTime.MAX));
        }

        return query.getResultList();
    }
}