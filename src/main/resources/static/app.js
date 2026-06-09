// URL base da API de Solicitações
const API_BASE = "/api/solicitacoes";

document.addEventListener("DOMContentLoaded", () => {
    const path = window.location.pathname;

    if (path.includes("cadastro.html")) {
        inicializarFormularioCadastro();
    } else if (path.includes("detalhes.html")) {
        inicializarDetalhes();
    } else {
        inicializarListagemPrincipal();
    }
});

// =========================================================================
// 1. CONFIGURAÇÃO DA LISTAGEM PRINCIPAL (index.html)
// =========================================================================
async function inicializarListagemPrincipal() {
    try {
        const resC = await fetch("/api/solicitacoes/categorias");
        const categorias = await resC.json();
        const filtroC = document.getElementById("filtroCategoria");
        if (filtroC) {
            categorias.forEach(c => {
                filtroC.innerHTML += `<option value="${c.id}">${c.nome}</option>`;
            });
        }
    } catch (e) {
        console.error("Erro ao carregar categorias no filtro:", e);
    }

    const btnFiltrar = document.getElementById("btnFiltrar");
    if (btnFiltrar) {
        btnFiltrar.addEventListener("click", carregarSolicitacoes);
    }
    carregarSolicitacoes();
}

async function carregarSolicitacoes() {
    const status = document.getElementById("filtroStatus")?.value || "";
    const categoriaId = document.getElementById("filtroCategoria")?.value || "";
    const dataInicio = document.getElementById("filtroDataInicio")?.value || "";
    const dataFim = document.getElementById("filtroDataFim")?.value || "";

    const url = `${API_BASE}?status=${status}&categoriaId=${categoriaId}&dataInicio=${dataInicio}&dataFim=${dataFim}`;

    try {
        const response = await fetch(url);
        const dados = await response.json();
        const tbody = document.getElementById("conteudoTabela");

        if (!tbody) return;
        tbody.innerHTML = "";

        if (dados.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-4">Nenhuma solicitação encontrada com os filtros aplicados.</td></tr>`;
            return;
        }

        dados.forEach(item => {
            const dataFormatada = new Date(item.dataSolicitacao).toLocaleString('pt-BR');

            const sNome = item.solicitanteNome || (item.solicitante ? item.solicitante.nome : "N/A");
            const sDoc = item.solicitanteDocumento || (item.solicitante ? item.solicitante.cpfCnpj : "");
            const cNome = item.categoriaNome || (item.categoria ? item.categoria.nome : "N/A");
            const valor = item.valor ? item.valor : 0;

            tbody.innerHTML += `
                <tr>
                    <td><strong>#${item.id}</strong></td>
                    <td><strong>${sNome}</strong><br><small class="text-muted">${sDoc}</small></td>
                    <td><span class="badge bg-light text-dark border">${cNome}</span></td>
                    <td><span class="badge ${getBadgeClass(item.status)}">${item.status}</span></td>
                    <td><strong class="text-success">R$ ${valor.toFixed(2)}</strong></td>
                    <td><small>${dataFormatada}</small></td>
                    <td class="text-center">
                        <a href="detalhes.html?id=${item.id}" class="btn btn-sm btn-primary">
                            <i class="bi bi-sliders me-1"></i> Administrar
                        </a>
                    </td>
                </tr>
            `;
        });
    } catch (erro) {
        console.error("Erro ao listar solicitações:", erro);
        const tbody = document.getElementById("conteudoTabela");
        if (tbody) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center text-danger py-4">Erro ao processar dados da listagem.</td></tr>`;
        }
    }
}

// =========================================================================
// 2. CONFIGURAÇÃO DA TELA DE CADASTRO (cadastro.html)
// =========================================================================
async function inicializarFormularioCadastro() {
    try {
        const resS = await fetch("/api/solicitacoes/solicitantes");
        const solicitantes = await resS.json();
        const selectS = document.getElementById("selectSolicitante");

        selectS.innerHTML = '<option value="" disabled selected>Selecione um solicitante...</option>';
        solicitantes.forEach(s => {
            selectS.innerHTML += `<option value="${s.id}">${s.nome} (${s.cpfCnpj})</option>`;
        });

        const resC = await fetch("/api/solicitacoes/categorias");
        const categorias = await resC.json();
        const selectC = document.getElementById("selectCategoria");

        selectC.innerHTML = '<option value="" disabled selected>Selecione uma categoria...</option>';
        categorias.forEach(c => {
            selectC.innerHTML += `<option value="${c.id}">${c.nome}</option>`;
        });
    } catch (erro) {
        console.error("Erro ao alimentar selects:", erro);
    }

    document.getElementById("formCadastro").addEventListener("submit", async (e) => {
        e.preventDefault();
        const body = {
            solicitanteId: document.getElementById("selectSolicitante").value,
            categoriaId: document.getElementById("selectCategoria").value,
            valor: document.getElementById("inputValor").value,
            descricao: document.getElementById("inputDescricao").value
        };

        const response = await fetch(API_BASE, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        if (response.ok) {
            alert("Solicitação enviada com sucesso!");
            window.location.href = "index.html";
        } else {
            const erro = await response.json();
            alert("Erro de validação: " + (erro.error || "Verifique os dados"));
        }
    });
}

// =========================================================================
// 3. CONFIGURAÇÃO DOS DETALHES ADMINISTRATIVOS (detalhes.html)
// =========================================================================
async function inicializarDetalhes() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");
    if (!id) {
        window.location.href = "index.html";
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/${id}`);
        if (!response.ok) throw new Error("Não encontrado");
        const s = await response.json();

        const sNome = s.solicitanteNome || (s.solicitante ? s.solicitante.nome : "Não informado");
        const sDoc = s.solicitanteDocumento || (s.solicitante ? s.solicitante.cpfCnpj : "Não informado");
        const cNome = s.categoriaNome || (s.categoria ? s.categoria.nome : "Não informado");
        const valorOriginal = s.valor ? s.valor : 0;

        document.getElementById("detalheId").innerText = s.id;
        document.getElementById("detalheSolicitante").innerText = sNome;
        document.getElementById("detalheDocumento").innerText = sDoc;
        document.getElementById("detalheCategoria").innerText = cNome;
        document.getElementById("detalheDescricao").innerText = s.descricao || "Sem justificativa anexada.";
        document.getElementById("detalheValor").innerText = `R$ ${valorOriginal.toFixed(2)}`;
        document.getElementById("detalheData").innerText = new Date(s.dataSolicitacao).toLocaleString('pt-BR');

        const badgeStatus = document.getElementById("detalheStatus");
        badgeStatus.innerText = s.status;
        badgeStatus.className = `badge fs-6 ${getBadgeClass(s.status)}`;

        configurarBotoesAcao(s.id, s.status);

    } catch (erro) {
        console.error("Erro interno no detalhamento:", erro);
        alert("Erro técnico: Não foi possível estruturar os detalhes desta solicitação.");
        window.location.href = "index.html";
    }
}

function configurarBotoesAcao(id, statusAtual) {
    const container = document.getElementById("containerAcoes");
    if (!container) return;
    container.innerHTML = "";

    // Mapeamento EXATO da máquina de estados do seu Enum Java
    if (statusAtual === "SOLICITADO") {
        container.innerHTML += `<button onclick="alterarStatus(${id}, 'LIBERADO')" class="btn btn-warning btn-sm fw-bold shadow-sm"><i class="bi bi-unlock-fill me-1"></i> Liberar Solicitação</button>`;
        container.innerHTML += `<button onclick="alterarStatus(${id}, 'REJEITADO')" class="btn btn-danger btn-sm fw-bold shadow-sm"><i class="bi bi-slash-circle me-1"></i> Rejeitar</button>`;
    } else if (statusAtual === "LIBERADO") {
        container.innerHTML += `<button onclick="alterarStatus(${id}, 'APROVADO')" class="btn btn-success btn-sm fw-bold shadow-sm"><i class="bi bi-check2-circle me-1"></i> Aprovar Pagamento</button>`;
        container.innerHTML += `<button onclick="alterarStatus(${id}, 'REJEITADO')" class="btn btn-danger btn-sm fw-bold shadow-sm"><i class="bi bi-slash-circle me-1"></i> Rejeitar Pagamento</button>`;
    } else if (statusAtual === "APROVADO") {
        container.innerHTML += `<button onclick="alterarStatus(${id}, 'CANCELADO')" class="btn btn-secondary btn-sm fw-bold shadow-sm"><i class="bi bi-x-circle me-1"></i> Cancelar</button>`;
    } else {
        container.innerHTML += `<p class="text-muted small fst-italic mb-0"><i class="bi bi-lock-fill me-1"></i> Esta solicitação encontra-se em um estado final (${statusAtual}) e está arquivada para alterações.</p>`;
    }
}

async function alterarStatus(id, novoStatus) {
    if (!confirm(`Deseja efetuar a transição de status administrativa para: ${novoStatus}?`)) return;

    try {
        const response = await fetch(`${API_BASE}/${id}/status?novoStatus=${novoStatus}`, {
            method: 'PUT'
        });

        if (response.ok) {
            alert(`Sucesso! Status da solicitação #${id} atualizado para ${novoStatus}.`);
            inicializarDetalhes();
        } else {
            const erro = await response.json();
            alert("Regra de Negócio Violada: " + (erro.error || "Operação não autorizada."));
        }
    } catch (erro) {
        console.error("Erro de conexão:", erro);
    }
}

function getBadgeClass(status) {
    switch (status) {
        case 'SOLICITADO': return 'bg-info text-dark';
        case 'LIBERADO': return 'bg-warning text-dark';
        case 'APROVADO': return 'bg-success text-white';
        case 'REJEITADO': return 'bg-danger text-white';
        case 'CANCELADO': return 'bg-secondary text-white';
        default: return 'bg-dark text-white';
    }
}