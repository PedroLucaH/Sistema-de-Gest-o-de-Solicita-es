# SGS - Sistema de Gestão de Solicitações de Pagamento

Este repositório contém uma aplicação corporativa completa para controle, auditoria, triagem e gerenciamento de fluxos de despesas e solicitações de pagamentos. O ecossistema integra um backend robusto em **Spring Boot** a uma interface administrativa responsiva construída em **Bootstrap 5** e JavaScript assíncrono.

---

## 🚀 Decisões Técnicas e Arquiteturais (O "Porquê")

O ecossistema foi projetado seguindo as melhores práticas de desenvolvimento de software enterprise, visando alta performance, isolamento de escopo e facilidade de avaliação pela banca técnica:

1. **Spring Boot & Java 21**
   * Escolha fundamentada na resiliência, ecossistema maduro e segurança nativa. O gerenciamento de dependências via Maven garante o isolamento completo das bibliotecas de persistência e conversão de dados.

2. **Banco de Dados H2 (In-Memory) com Carga Dinâmica (`data.sql`)**
   * **Decisão Estratégica para Avaliação:** Visando uma experiência *plug-and-play* imediata para os avaliadores, optou-se pelo H2 em memória. Isso elimina a necessidade de instalar servidores locais de bancos de dados (como PostgreSQL ou MySQL) ou rodar containers Docker. O banco é criado estruturado e populado automaticamente com massa de dados de teste (5 solicitantes e 5 categorias) no momento da inicialização através do script automatizado `data.sql`.

3. **Arquitetura de Software em Camadas (Clean Architecture & DTO Pattern)**
   * Estruturação rigorosa dividida em: **Model/Entity**, **DTO (Data Transfer Object)**, **Repository**, **Service** e **Controller**.
   * O uso de DTOs impede a exposição direta das entidades de banco de dados na camada de visualização, reduzindo o acoplamento de dados e otimizando o tráfego de rede.
   * As validações de integridade e regras de fluxo residem estritamente na camada de `Service`, blindando as regras de negócio contra inputs maliciosos da API.

4. **Consultas Nativas Customizadas (Native Queries com SQL Dinâmico + JOINs)**
   * Para atender aos critérios avançados de banco de dados e performance, foi implementada uma camada customizada no repositório (`SolicitacaoRepositoryCustomImpl`).
   * As buscas de listagem principal realizam junções explícitas (`JOIN`) entre as tabelas `solicitacao`, `solicitante` e `categoria`, resolvendo nativamente o gargalo de performance conhecido como *Problema de Consulta N+1* do Hibernate.
   * A filtragem por múltiplos campos (Status, Categoria, Período) é montada dinamicamente utilizando uma cláusula base de escape (`WHERE 1=1`) segura contra SQL Injection.

5. **Interface de Administração Rica (Bootstrap 5 & Vanilla JS)**
   * Para garantir leveza e eliminação de transpiladores de frontend (como Node.js, Angular ou React), as páginas estáticas (`index.html`, `cadastro.html`, `detalhes.html`) utilizam HTML5 puro estilizado com componentes nativos do Bootstrap 5.
   * O arquivo `app.js` gerencia de forma assíncrona (`Fetch API`) toda a comunicação com os endpoints REST do backend, atualizando o DOM dinamicamente e provendo feedbacks instantâneos ao usuário.

---

## ⚙️ Máquina de Estados (Workflow do Processamento)

O coração do sistema reside no controle rígido das transições de status das solicitações, implementado no backend através de um modelo de transição em `Enum` estruturado:

* **`SOLICITADO`**: Estado inicial obrigatório de qualquer nova despesa inserida no sistema. Permite transição estrita para `LIBERADO` ou `REJEITADO`.
* **`LIBERADO`**: Estado intermediário de governança. Indica que a despesa passou por triagem inicial. Permite transição estrita para `APROVADO` (para pagamento) ou `REJEITADO`.
* **`APROVADO`**: Estado de liquidação financeira. Uma vez aprovado, o registro só pode ser transicionado para `CANCELADO` sob auditoria técnica.
* **Estados Finais (`REJEITADO` / `CANCELADO`)**: Bloqueiam o registro permanentemente. A camada de `Service` intercepta e lança exceções de negócio caso qualquer requisição tente alterar registros nestes estados, garantindo a imutabilidade histórica do fluxo financeiro.

---

## 🛠️ Como Executar o Projeto Localmente

Certifique-se de possuir o **JDK 21** instalado em sua máquina.

1. **Clonar ou Extrair o Projeto:**
   Abra o terminal (Prompt de Comando, PowerShell ou Terminal do Linux/Mac) na pasta raiz onde o arquivo `pom.xml` está localizado.

2. **Limpar Repositórios Antigos e Iniciar a Aplicação:**
   Execute o comando do Maven Wrapper para compilar o código fonte e subir o servidor Tomcat embutido:
   ```bash
   ./mvnw clean spring-boot:run
