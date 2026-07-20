# InfoTechG

Sistema desktop de gestão para lojas de assistência técnica em informática, desenvolvido como Projeto Integrador II do curso Técnico em Desenvolvimento de Sistemas (SENAC).

## Status do Projeto

✅ Projeto Integrador II concluído — todas as etapas finalizadas.

| Etapa | Descrição | Status |
|-------|-----------|--------|
| 1 | Levantamento de requisitos, diagrama de classes UML e protótipo funcional em console | ✅ Concluída |
| 2 | Projeto de UX/UI (wireframes, paleta de cores, acessibilidade) | ✅ Concluída |
| 3 | Implementação das telas em Java Swing com navegação completa | ✅ Concluída |
| 4 | Integração com banco de dados MySQL via JDBC | ✅ Concluída |
| 5 | Controle de versão, documentação final e entrega no GitHub | ✅ Concluída |

## Objetivo

Substituir o controle manual (planilhas/papel) usado por pequenas lojas de assistência técnica em informática por um sistema desktop simples que centralize o cadastro de clientes, fornecedores e produtos, o controle de estoque, o registro de vendas e a abertura/acompanhamento de ordens de serviço — com controle de acesso por perfil de usuário (Gerente, Vendedor, Técnico).

## Tecnologias

- **Java 17** (Swing para interface gráfica)
- **MySQL 8.0** (persistência via JDBC, `mysql-connector-j`)
- **Apache Ant** / NetBeans (`j2seproject`) — build sem gerenciador de dependências externo
- **SHA-256 + salt** para hash de senhas (`infotechg.util.SenhaUtil`)

## Equipe de Desenvolvimento

- Jonas Henrique dos Santos Estevão — desenvolvimento, design e documentação

## Funcionalidades

- **Login e controle de acesso**: autenticação por login/senha com três perfis (Gerente, Vendedor, Técnico), cada um com permissões distintas nas telas do sistema.
- **Clientes**: cadastro, listagem, edição e exclusão.
- **Fornecedores**: cadastro, listagem, edição e exclusão (exclusivo do perfil Gerente).
- **Produtos e estoque**: cadastro de produtos vinculados a um fornecedor, com controle de quantidade em estoque.
- **Vendas**: registro de vendas com baixa automática de estoque e validação de quantidade disponível.
- **Ordens de serviço**: abertura de OS vinculada a um cliente e a um técnico responsável, com acompanhamento de status (Em análise, Aguardando peça, Concluído, Entregue).
- **Dashboard**: visão geral com indicadores do sistema.

## Regras de Negócio

- **RN02**: uma venda não pode ser confirmada se a quantidade solicitada exceder o estoque disponível do produto.
- **RN03**: ao confirmar uma venda, o estoque do produto é reduzido automaticamente na quantidade vendida.
- **RN06**: o módulo de Fornecedores é restrito ao perfil Gerente.

## Como Executar

1. Ter um servidor MySQL 8.0 em execução localmente.
2. Executar o script `Projeto_InfoTechG_Script_Completo.sql` para criar o schema `mydb` e popular os dados de teste.
3. Copiar `InfoTechG-NetBeans/src/infotechg/dao/db.properties.example` para `db.properties` no mesmo diretório e preencher usuário/senha do seu MySQL local.
4. Abrir o projeto `InfoTechG-NetBeans` no NetBeans (ou compilar via Ant) e executar `infotechg.Main`.

### Usuários de teste

| Login | Senha | Perfil |
|-------|-------|--------|
| gerente | gerente123 | Gerente |
| vendedor | vendedor123 | Vendedor |
| tecnico | tecnico123 | Tecnico |
