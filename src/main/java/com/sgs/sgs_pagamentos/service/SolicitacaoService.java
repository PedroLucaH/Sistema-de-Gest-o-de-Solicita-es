package com.sgs.sgs_pagamentos.service;

import com.sgs.sgs_pagamentos.dto.SolicitacaoCadastroDTO;
import com.sgs.sgs_pagamentos.dto.SolicitacaoListagemDTO;
import com.sgs.sgs_pagamentos.exception.BusinessException;
import com.sgs.sgs_pagamentos.model.Categoria;
import com.sgs.sgs_pagamentos.model.Solicitacao;
import com.sgs.sgs_pagamentos.model.Solicitante;
import com.sgs.sgs_pagamentos.model.StatusSolicitacao;
import com.sgs.sgs_pagamentos.repository.CategoriaRepository;
import com.sgs.sgs_pagamentos.repository.SolicitacaoRepository;
import com.sgs.sgs_pagamentos.repository.SolicitacaoRepositoryCustom;
import com.sgs.sgs_pagamentos.repository.SolicitanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final SolicitacaoRepository repository;
    private final SolicitacaoRepositoryCustom repositoryCustom;
    private final SolicitanteRepository solicitanteRepository;
    private final CategoriaRepository categoriaRepository;

    // Ajustado nome para bater com o Controller
    public List<SolicitacaoListagemDTO> listarComFiltros(String status, Long categoriaId, String dataInicio, String dataFim) {
        return repositoryCustom.buscarComFiltrosNativos(status, categoriaId, dataInicio, dataFim);
    }

    public Solicitacao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("Solicitação não encontrada"));
    }

    // Ajustado nome para bater com o Controller
    @Transactional
    public Solicitacao criarSolicitacao(SolicitacaoCadastroDTO dto) {
        Solicitante solicitante = solicitanteRepository.findById(dto.getSolicitanteId())
                .orElseThrow(() -> new BusinessException("Solicitante não encontrado"));
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new BusinessException("Categoria não encontrada"));

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setSolicitante(solicitante);
        solicitacao.setCategoria(categoria);
        solicitacao.setDescricao(dto.getDescricao());
        solicitacao.setValor(dto.getValor());
        solicitacao.setDataSolicitacao(LocalDateTime.now());
        solicitacao.setStatus(StatusSolicitacao.SOLICITADO);

        return repository.save(solicitacao);
    }

    // Ajustado nome para bater com o Controller
    @Transactional
    public Solicitacao atualizarStatus(Long id, StatusSolicitacao novoStatus) {
        Solicitacao solicitacao = buscarPorId(id);

        if (!solicitacao.getStatus().podeTransicionarPara(novoStatus)) {
            throw new BusinessException("Transição inválida de " + solicitacao.getStatus() + " para " + novoStatus);
        }

        solicitacao.setStatus(novoStatus);
        return repository.save(solicitacao);
    }
}