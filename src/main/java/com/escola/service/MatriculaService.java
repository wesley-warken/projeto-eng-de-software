package com.escola.service;

import com.escola.domain.Aluno;
import com.escola.domain.Curso;
import com.escola.domain.Matricula;
import com.escola.repository.AlunoRepository;
import com.escola.repository.CursoRepository;
import com.escola.repository.MatriculaRepository;
import com.escola.repository.impl.InMemoryAlunoRepository;
import com.escola.repository.impl.InMemoryCursoRepository;
import com.escola.repository.impl.InMemoryMatriculaRepository;

import java.util.List;

/**
 * Serviço de aplicação responsável por orquestrar a lógica de negócio de Matrículas.
 * Segue os princípios de baixo acoplamento e injeção de dependências,
 * dependendo de interfaces de Repositório em vez de implementações concretas.
 */
public class MatriculaService {
    
    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;
    private final MatriculaRepository matriculaRepository;

    /**
     * Construtor padrão que inicializa os repositórios em memória.
     * Facilita o uso direto sem necessidade de frameworks de injeção de dependência.
     */
    public MatriculaService() {
        this(new InMemoryAlunoRepository(), new InMemoryCursoRepository(), new InMemoryMatriculaRepository());
    }

    /**
     * Construtor para injeção explícita de dependências.
     * Altamente recomendado para testes unitários com mocks.
     *
     * @param alunoRepository     Repositório de Alunos.
     * @param cursoRepository     Repositório de Cursos.
     * @param matriculaRepository Repositório de Matrículas.
     */
    public MatriculaService(AlunoRepository alunoRepository, 
                            CursoRepository cursoRepository, 
                            MatriculaRepository matriculaRepository) {
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
        this.matriculaRepository = matriculaRepository;
    }

    /**
     * Cadastra um novo aluno no sistema.
     *
     * @param nome  Nome do aluno.
     * @param email E-mail do aluno (identificador único).
     * @return O aluno recém-cadastrado.
     * @throws IllegalArgumentException se o e-mail for vazio ou inválido.
     * @throws IllegalStateException    se já existir um aluno cadastrado com este e-mail.
     */
    public Aluno cadastrarAluno(String nome, String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("O e-mail do aluno não pode ser vazio ou nulo.");
        }
        
        String emailFormatado = email.trim().toLowerCase();
        if (alunoRepository.findByEmail(emailFormatado).isPresent()) {
            throw new IllegalStateException("Já existe um aluno cadastrado com este e-mail.");
        }

        Aluno novoAluno = new Aluno(nome, emailFormatado);
        return alunoRepository.save(novoAluno);
    }

    /**
     * Busca um aluno pelo seu e-mail.
     *
     * @param email E-mail do aluno.
     * @return O aluno encontrado, ou null se não existir.
     */
    public Aluno buscarAlunoPorEmail(String email) {
        return alunoRepository.findByEmail(email).orElse(null);
    }

    /**
     * Cadastra um novo curso no sistema.
     *
     * @param nome  Nome do curso.
     * @param vagas Quantidade total de vagas.
     * @return O curso cadastrado.
     * @throws IllegalArgumentException se o nome do curso for vazio ou inválido.
     * @throws IllegalStateException    se já existir um curso cadastrado com este nome.
     */
    public Curso cadastrarCurso(String nome, int vagas) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do curso não pode ser vazio ou nulo.");
        }

        String nomeFormatado = nome.trim().toLowerCase();
        if (cursoRepository.findByNome(nomeFormatado).isPresent()) {
            throw new IllegalStateException("Já existe um curso cadastrado com este nome.");
        }

        Curso novoCurso = new Curso(nome, vagas);
        return cursoRepository.save(novoCurso);
    }

    /**
     * Busca um curso pelo seu nome.
     *
     * @param nome Nome do curso.
     * @return O curso encontrado, ou null se não existir.
     */
    public Curso buscarCursoPorNome(String nome) {
        return cursoRepository.findByNome(nome).orElse(null);
    }

    /**
     * Matricula um aluno em um curso específico.
     * Realiza todas as validações de regra de negócio (existência, duplicidade, vagas).
     *
     * @param emailAluno E-mail do aluno.
     * @param nomeCurso  Nome do curso.
     * @return A matrícula realizada com sucesso.
     * @throws IllegalArgumentException se o aluno ou o curso não forem encontrados.
     * @throws IllegalStateException    se o aluno já estiver matriculado ou se não houver vagas.
     */
    public Matricula matricular(String emailAluno, String nomeCurso) {
        Aluno aluno = alunoRepository.findByEmail(emailAluno)
                .orElseThrow(() -> new IllegalArgumentException("Aluno com e-mail " + emailAluno + " não encontrado."));

        Curso curso = cursoRepository.findByNome(nomeCurso)
                .orElseThrow(() -> new IllegalArgumentException("Curso " + nomeCurso + " não encontrado."));

        if (matriculaRepository.existsByAlunoAndCurso(aluno, curso)) {
            throw new IllegalStateException("Aluno já está matriculado neste curso.");
        }

        // Fluxo seguro: tenta decrementar a vaga primeiro. Se falhar, lança IllegalStateException.
        curso.decrementarVaga();

        Matricula novaMatricula = new Matricula(aluno, curso);
        return matriculaRepository.save(novaMatricula);
    }

    /**
     * Lista todas as matrículas ativas no sistema.
     *
     * @return Uma lista não modificável de matrículas.
     */
    public List<Matricula> listarMatriculas() {
        return matriculaRepository.findAll();
    }

    /**
     * Lista todos os alunos cadastrados.
     *
     * @return Uma lista não modificável de alunos.
     */
    public List<Aluno> listarAlunos() {
        return alunoRepository.findAll();
    }

    /**
     * Lista todos os cursos disponíveis.
     *
     * @return Uma lista não modificável de cursos.
     */
    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }
}
