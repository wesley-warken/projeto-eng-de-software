package com.escola;

import com.escola.domain.Aluno;
import com.escola.domain.Curso;
import com.escola.domain.Matricula;
import com.escola.service.MatriculaService;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final MatriculaService service = new MatriculaService();
    private static final Scanner scanner = new Scanner(System.in);

    // ANSI Colors for aesthetic CLI
    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";

    public static void main(String[] args) {
        // Seed some initial data to facilitate testing
        seedData();

        // Inicializa o servidor Web administrativo em segundo plano
        WebServer.start(service);

        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            String opcao = scanner.nextLine().trim();
            System.out.println();

            switch (opcao) {
                case "1":
                    cadastrarAluno();
                    break;
                case "2":
                    cadastrarCurso();
                    break;
                case "3":
                    matricularAluno();
                    break;
                case "4":
                    listarMatriculas();
                    break;
                case "5":
                    listarAlunos();
                    break;
                case "6":
                    listarCursos();
                    break;
                case "0":
                    rodando = false;
                    WebServer.stop();
                    System.out.println(GREEN + "Obrigado por utilizar o Sistema de Gestão de Matrículas! Até mais." + RESET);
                    break;
                default:
                    System.out.println(RED + "Opção inválida. Por favor, tente novamente." + RESET);
            }
            if (rodando) {
                aguardarTecla();
            }
        }
    }

    private static void exibirMenu() {
        System.out.println();
        System.out.println(BLUE + "==================================================" + RESET);
        System.out.println(CYAN + "      SISTEMA DE MATRÍCULAS - ESCOLA DE IDIOMAS   " + RESET);
        System.out.println(BLUE + "==================================================" + RESET);
        System.out.println(" 1. Cadastrar Aluno");
        System.out.println(" 2. Cadastrar Curso");
        System.out.println(" 3. Matricular Aluno em um Curso");
        System.out.println(" 4. Listar Todas as Matrículas");
        System.out.println(" 5. Listar Alunos Cadastrados");
        System.out.println(" 6. Listar Cursos Disponíveis");
        System.out.println(" 0. Sair");
        System.out.println(BLUE + "--------------------------------------------------" + RESET);
        System.out.print("Escolha uma opção: ");
    }

    private static void cadastrarAluno() {
        System.out.println(CYAN + "--- CADASTRO DE ALUNO ---" + RESET);
        System.out.print("Nome do Aluno: ");
        String nome = scanner.nextLine().trim();
        System.out.print("E-mail do Aluno: ");
        String email = scanner.nextLine().trim();

        try {
            Aluno aluno = service.cadastrarAluno(nome, email);
            System.out.println(GREEN + "\n✔ Aluno cadastrado com sucesso!" + RESET);
            System.out.println("Detalhes: " + aluno);
        } catch (Exception e) {
            System.out.println(RED + "\n❌ Erro ao cadastrar aluno: " + e.getMessage() + RESET);
        }
    }

    private static void cadastrarCurso() {
        System.out.println(CYAN + "--- CADASTRO DE CURSO ---" + RESET);
        System.out.print("Nome do Curso (ex: Inglês Básico): ");
        String nome = scanner.nextLine().trim();
        System.out.print("Quantidade de Vagas: ");
        
        try {
            int vagas = Integer.parseInt(scanner.nextLine().trim());
            Curso curso = service.cadastrarCurso(nome, vagas);
            System.out.println(GREEN + "\n✔ Curso cadastrado com sucesso!" + RESET);
            System.out.println("Detalhes: " + curso);
        } catch (NumberFormatException e) {
            System.out.println(RED + "\n❌ Erro: A quantidade de vagas deve ser um número inteiro válido." + RESET);
        } catch (Exception e) {
            System.out.println(RED + "\n❌ Erro ao cadastrar curso: " + e.getMessage() + RESET);
        }
    }

    private static void matricularAluno() {
        System.out.println(CYAN + "--- MATRICULAR ALUNO EM CURSO ---" + RESET);
        System.out.print("E-mail do Aluno: ");
        String email = scanner.nextLine().trim();
        System.out.print("Nome exato do Curso: ");
        String cursoNome = scanner.nextLine().trim();

        try {
            Matricula matricula = service.matricular(email, cursoNome);
            System.out.println(GREEN + "\n✔ Matrícula realizada com sucesso!" + RESET);
            System.out.println(matricula);
        } catch (Exception e) {
            System.out.println(RED + "\n❌ Erro ao realizar matrícula: " + e.getMessage() + RESET);
        }
    }

    private static void listarMatriculas() {
        System.out.println(CYAN + "--- RELAÇÃO DE MATRÍCULAS ---" + RESET);
        List<Matricula> matriculas = service.listarMatriculas();
        if (matriculas.isEmpty()) {
            System.out.println(YELLOW + "Nenhuma matrícula registrada no sistema até o momento." + RESET);
        } else {
            System.out.printf(BLUE + "%-25s | %-20s\n" + RESET, "Aluno (E-mail)", "Curso");
            System.out.println("--------------------------------------------------");
            for (Matricula m : matriculas) {
                System.out.printf("%-25s | %-20s\n", 
                        m.getAluno().getNome() + " (" + m.getAluno().getEmail() + ")", 
                        m.getCurso().getNome());
            }
            System.out.println("Total de Matrículas: " + matriculas.size());
        }
    }

    private static void listarAlunos() {
        System.out.println(CYAN + "--- ALUNOS CADASTRADOS ---" + RESET);
        List<Aluno> lista = service.listarAlunos();
        if (lista.isEmpty()) {
            System.out.println(YELLOW + "Nenhum aluno cadastrado." + RESET);
        } else {
            for (Aluno a : lista) {
                System.out.println("• " + a);
            }
        }
    }

    private static void listarCursos() {
        System.out.println(CYAN + "--- CURSOS DISPONÍVEIS ---" + RESET);
        List<Curso> lista = service.listarCursos();
        if (lista.isEmpty()) {
            System.out.println(YELLOW + "Nenhum curso cadastrado." + RESET);
        } else {
            for (Curso c : lista) {
                String statusVagas = c.getVagasDisponiveis() > 0 
                        ? GREEN + c.getVagasDisponiveis() + " disponíveis" + RESET 
                        : RED + "ESGOTADO" + RESET;
                System.out.printf("• %s - Vagas totais: %d | Status: %s\n", 
                        c.getNome(), c.getVagasTotais(), statusVagas);
            }
        }
    }

    private static void seedData() {
        // Seed default records to make initial experience wonderful
        try {
            service.cadastrarAluno("Alice Smith", "alice@email.com");
            service.cadastrarAluno("Bob Marley", "bob@email.com");
            service.cadastrarCurso("Inglês Iniciante", 2);
            service.cadastrarCurso("Espanhol Conversação", 10);
            service.matricular("alice@email.com", "Inglês Iniciante");
        } catch (Exception ignored) {
        }
    }

    private static void aguardarTecla() {
        System.out.println(YELLOW + "\nPressione [ENTER] para voltar ao menu principal..." + RESET);
        scanner.nextLine();
    }
}
