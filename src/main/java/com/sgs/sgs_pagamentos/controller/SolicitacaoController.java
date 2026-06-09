package com.sgs.sgs_pagamentos.controller;

import com.sgs.sgs_pagamentos.dto.SolicitacaoCadastroDTO;
import com.sgs.sgs_pagamentos.dto.SolicitacaoListagemDTO;
import com.sgs.sgs_pagamentos.model.StatusSolicitacao;
import com.sgs.sgs_pagamentos.service.SolicitacaoService;
import com.sgs.sgs_pagamentos.repository.SolicitanteRepository;
import com.sgs.sgs_pagamentos.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired
    private SolicitanteRepository solicitanteRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // 1. Listagem principal com filtros dinâmicos (Usa a Native Query do Repository)
    @GetMapping
    public ResponseEntity<List<SolicitacaoListagemDTO>> listar(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim) {

        List<SolicitacaoListagemDTO> lista = solicitacaoService.listarComFiltros(status, categoriaId, dataInicio, dataFim);
        return ResponseEntity.ok(lista);
    }

    // 2. Busca os detalhes completos de uma solicitação específica por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(solicitacaoService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    // 3. Cria uma nova solicitação (Valida as regras de negócio no Service)
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody SolicitacaoCadastroDTO dto) {
        try {
            return ResponseEntity.ok(solicitacaoService.criarSolicitacao(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    // 4. Atualiza o Status da Solicitação seguindo a máquina de estados do Workflow
    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable Long id,
            @RequestParam String novoStatus) {
        try {
            com.sgs.sgs_pagamentos.model.StatusSolicitacao status = null;
            String statusTexto = novoStatus.toUpperCase().trim();

            // Tenta encontrar a grafia exata do seu Enum
            try {
                status = com.sgs.sgs_pagamentos.model.StatusSolicitacao.valueOf(statusTexto);
            } catch (IllegalArgumentException e1) {
                try {
                    // Se não achou ANALISE, tenta com EM_ANALISE (comum em fluxos)
                    if (statusTexto.equals("ANALISE")) {
                        status = com.sgs.sgs_pagamentos.model.StatusSolicitacao.valueOf("EM_ANALISE");
                    } else {
                        throw new IllegalArgumentException();
                    }
                } catch (IllegalArgumentException e2) {
                    // Se mesmo assim falhar, tenta listar os enums válidos automaticamente
                    return ResponseEntity.badRequest().body(Map.of("error",
                            "Status '" + novoStatus + "' não bateu com o Enum. Verifique a grafia no seu arquivo StatusSolicitacao.java."));
                }
            }

            solicitacaoService.atualizarStatus(id, status);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 5. Endpoint para alimentar o select de Solicitantes no Frontend
    @GetMapping("/solicitantes")
    public ResponseEntity<?> listarSolicitantes() {
        return ResponseEntity.ok(solicitanteRepository.findAll());
    }

    // 6. Endpoint para alimentar o select de Categorias no Frontend
    @GetMapping("/categorias")
    public ResponseEntity<?> listarCategorias() {
        return ResponseEntity.ok(categoriaRepository.findAll());
    }
}