# Retrospectiva da Equipe (Extreme Programming - Iteração 1)

Esta retrospectiva analisa o processo de desenvolvimento e os valores do **Extreme Programming (XP)** empregados durante a primeira iteração do **Sistema de Gestão de Matrículas**.

---

## 🛡️ Os Três Valores XP em Ação

### 1. Simplicidade (Simplicity)
> *"Fazer a coisa mais simples que pode funcionar."*

Em vez de projetar esquemas complexos de banco de dados relacionais (PostgreSQL/MySQL), criar migrações e lidar com conexões persistentes, optamos por armazenar os Alunos, Cursos e Matrículas em coleções eficientes em memória (`LinkedHashMap` e `List` de forma encapsulada). 
- **Benefício:** Reduziu o tempo de entrega para minutos e nos permitiu focar puramente nas regras de negócio e validações solicitadas pelo cliente.
- **Resultado:** A interface CLI consome o serviço diretamente, mantendo a arquitetura leve e livre de acoplamento com drivers externos.

### 2. Feedback Rápido
> *"Testes automatizados dão feedback em segundos, não em dias."*

Ao codificar em ciclos de TDD, obtivemos feedback instantâneo sobre cada detalhe das regras.
- Se uma alteração de código quebrasse a verificação de duplicidade de matrícula, ou se a contagem de vagas ficasse inconsistente, o JUnit nos alertaria imediatamente.
- A build e a suite de 20 testes unitários rodam em **menos de 0.1 segundos**, dando extrema confiança para refatoração.

### 3. Coragem
> *"Ter coragem de refatorar código funcional para torná-lo melhor."*

Desenvolvemos o código sabendo que a suite de testes cobria todas as frentes de validação. Isso nos deu coragem de:
- Adicionar validações estritas aos construtores das entidades de domínio (`Aluno` e `Curso`) para barrar qualquer estado inválido de forma centralizada.
- Encapsular os retornos de coleções no `MatriculaService` usando `Collections.unmodifiableList`, impedindo que classes externas corrompam o estado interno do repositório.

---

## 🎯 Como o TDD Garantiu os Critérios de Aceitação

A prática do **TDD (Test-Driven Development)** nos forçou a escrever os testes antes da implementação física das classes. Isso trouxe grandes vantagens práticas:

1. **Barrar Nome Vazio/Nulo (US01 e US02):**
   Escrevemos testes específicos para testar os limites físicos dos dados:
   ```java
   @Test
   public void deveLancarExcecaoQuandoNomeForVazio() {
       assertThrows(IllegalArgumentException.class, () -> { new Aluno("", "email@email.com"); });
   }
   ```
   Apenas quando implementamos o lançamento de `IllegalArgumentException` no construtor de `Aluno`, a barra de testes mudou para verde.
   
2. **Impedir Vagas Negativas ou Zeradas (US02):**
   Definimos em testes que um curso com 0 ou -1 vagas deve ser sumariamente recusado.
   
3. **Consistência de Matrículas e Vagas (US03):**
   Validamos que realizar uma matrícula reduz em exatamente `1` o número de vagas restantes e que se as vagas esgotarem, tentativas subsequentes devem lançar `IllegalStateException` com a mensagem apropriada.

---

## 🚀 Preparação para Mudanças Futuras (Baixo Acoplamento)

Graças à refatoração limpa e à aderência aos princípios SOLID e XP, nosso sistema está altamente preparado para evoluções na próxima iteração:

- **Substituição da Camada de Persistência:** A lógica de armazenamento está centralizada no `MatriculaService` (e potencialmente poderia ser abstraída em interfaces de repositórios `AlunoRepository`, `CursoRepository`). Se o cliente decidir migrar para um banco de dados (como Spring Data JPA / Hibernate ou JDBC), a classe `Main` (CLI) não precisará mudar sequer uma linha de código.
- **Transição para Interface Web ou API REST:** Caso a escola queira substituir a CLI por uma página Web moderna ou API REST com Spring Boot/Micronaut, bastará injetar o `MatriculaService`, já que o serviço é 100% independente do console e livre de acoplamento de entrada/saída de dados de terminal.
