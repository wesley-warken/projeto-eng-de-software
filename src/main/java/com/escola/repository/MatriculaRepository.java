package com.escola.repository;

import com.escola.domain.Aluno;
import com.escola.domain.Curso;
import com.escola.domain.Matricula;
import java.util.List;

/**
 * Interface que define as operações de persistência para a entidade Matricula.
 * Segue o padrão Repository para abstração da camada de dados.
 */
public interface MatriculaRepository {

    /**
     * Salva ou registra uma matrícula no repositório.
     * 
     * @param matricula A matrícula a ser registrada.
     * @return A matrícula salva.
     */
    Matricula save(Matricula matricula);

    /**
     * Retorna todas as matrículas registradas.
     * 
     * @return Uma lista não modificável de matrículas.
     */
    List<Matricula> findAll();

    /**
     * Verifica se já existe uma matrícula para o mesmo aluno no mesmo curso.
     * 
     * @param aluno O aluno.
     * @param curso O curso.
     * @return true se já existir a matrícula, false caso contrário.
     */
    boolean existsByAlunoAndCurso(Aluno aluno, Curso curso);
}
