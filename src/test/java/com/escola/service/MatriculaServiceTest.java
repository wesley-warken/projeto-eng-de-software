package com.escola.service;

import com.escola.domain.Aluno;
import com.escola.domain.Curso;
import com.escola.domain.Matricula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatriculaServiceTest {

    private MatriculaService service;

    @BeforeEach
    public void setUp() {
        service = new MatriculaService();
    }

    @Test
    public void deveCadastrarAlunoComSucesso() {
        Aluno aluno = service.cadastrarAluno("Maria Santos", "maria@email.com");
        assertNotNull(aluno);
        assertEquals("Maria Santos", aluno.getNome());
        
        // Verificar se foi armazenado
        Aluno buscado = service.buscarAlunoPorEmail("maria@email.com");
        assertEquals(aluno, buscado);
    }

    @Test
    public void deveCadastrarCursoComSucesso() {
        Curso curso = service.cadastrarCurso("Inglês Básico", 2);
        assertNotNull(curso);
        assertEquals("Inglês Básico", curso.getNome());

        // Verificar se foi armazenado
        Curso buscado = service.buscarCursoPorNome("Inglês Básico");
        assertEquals(curso, buscado);
    }

    @Test
    public void deveMatricularAlunoComSucesso() {
        Aluno aluno = service.cadastrarAluno("José Pedro", "jose@email.com");
        Curso curso = service.cadastrarCurso("Inglês Avançado", 5);

        Matricula matricula = service.matricular(aluno.getEmail(), curso.getNome());

        assertNotNull(matricula);
        assertEquals(aluno, matricula.getAluno());
        assertEquals(curso, matricula.getCurso());
        assertEquals(4, curso.getVagasDisponiveis());
    }

    @Test
    public void deveLancarExcecaoAoMatricularAlunoNaoCadastrado() {
        service.cadastrarCurso("Italiano", 5);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.matricular("inexistente@email.com", "Italiano");
        });
        assertEquals("Aluno com e-mail inexistente@email.com não encontrado.", exception.getMessage());
    }

    @Test
    public void deveLancarExcecaoAoMatricularEmCursoNaoCadastrado() {
        service.cadastrarAluno("José Pedro", "jose@email.com");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.matricular("jose@email.com", "Curso Fantasma");
        });
        assertEquals("Curso Curso Fantasma não encontrado.", exception.getMessage());
    }

    @Test
    public void deveLancarExcecaoAoMatricularDuplicado() {
        Aluno aluno = service.cadastrarAluno("José Pedro", "jose@email.com");
        Curso curso = service.cadastrarCurso("Inglês", 5);

        service.matricular(aluno.getEmail(), curso.getNome());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            service.matricular(aluno.getEmail(), curso.getNome());
        });
        assertEquals("Aluno já está matriculado neste curso.", exception.getMessage());
    }

    @Test
    public void deveLancarExcecaoAoMatricularEmCursoSemVagas() {
        Aluno aluno1 = service.cadastrarAluno("Aluno Um", "aluno1@email.com");
        Aluno aluno2 = service.cadastrarAluno("Aluno Dois", "aluno2@email.com");
        Aluno aluno3 = service.cadastrarAluno("Aluno Três", "aluno3@email.com");
        Curso curso = service.cadastrarCurso("Japonês", 2);

        service.matricular(aluno1.getEmail(), curso.getNome());
        service.matricular(aluno2.getEmail(), curso.getNome());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            service.matricular(aluno3.getEmail(), curso.getNome());
        });
        assertEquals("Não há vagas disponíveis neste curso.", exception.getMessage());
    }

    @Test
    public void deveListarTodasAsMatriculas() {
        Aluno aluno1 = service.cadastrarAluno("José", "jose@email.com");
        Aluno aluno2 = service.cadastrarAluno("Maria", "maria@email.com");
        Curso curso = service.cadastrarCurso("Inglês", 10);

        service.matricular(aluno1.getEmail(), curso.getNome());
        service.matricular(aluno2.getEmail(), curso.getNome());

        List<Matricula> matriculas = service.listarMatriculas();
        assertEquals(2, matriculas.size());
        
        assertEquals(aluno1, matriculas.get(0).getAluno());
        assertEquals(curso, matriculas.get(0).getCurso());
        assertEquals(aluno2, matriculas.get(1).getAluno());
        assertEquals(curso, matriculas.get(1).getCurso());
    }

    @Test
    public void devePermitirInjecaoDeDependenciasCustomizadas() {
        com.escola.repository.impl.InMemoryAlunoRepository mockAlunoRepo = new com.escola.repository.impl.InMemoryAlunoRepository();
        com.escola.repository.impl.InMemoryCursoRepository mockCursoRepo = new com.escola.repository.impl.InMemoryCursoRepository();
        com.escola.repository.impl.InMemoryMatriculaRepository mockMatriculaRepo = new com.escola.repository.impl.InMemoryMatriculaRepository();

        MatriculaService customService = new MatriculaService(mockAlunoRepo, mockCursoRepo, mockMatriculaRepo);
        
        Aluno aluno = customService.cadastrarAluno("Injetado", "injetado@email.com");
        Curso curso = customService.cadastrarCurso("Curso Injetado", 5);
        Matricula matricula = customService.matricular("injetado@email.com", "Curso Injetado");

        assertNotNull(matricula);
        assertEquals(1, mockMatriculaRepo.findAll().size());
        assertEquals(aluno, mockAlunoRepo.findByEmail("injetado@email.com").orElse(null));
        assertEquals(curso, mockCursoRepo.findByNome("Curso Injetado").orElse(null));
    }
}

