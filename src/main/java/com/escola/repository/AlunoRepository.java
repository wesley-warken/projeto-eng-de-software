package com.escola.repository;

import com.escola.domain.Aluno;
import java.util.List;
import java.util.Optional;

/**
 * Interface que define as operações de persistência para a entidade Aluno.
 * Segue o padrão Repository para abstração da camada de dados.
 */
public interface AlunoRepository {
    
    /**
     * Salva ou atualiza um aluno no repositório.
     * 
     * @param aluno O aluno a ser salvo.
     * @return O aluno salvo.
     */
    Aluno save(Aluno aluno);

    /**
     * Busca um aluno pelo seu e-mail (identificador único).
     * 
     * @param email O e-mail do aluno.
     * @return Um Optional contendo o aluno, se encontrado.
     */
    Optional<Aluno> findByEmail(String email);

    /**
     * Retorna todos os alunos cadastrados.
     * 
     * @return Uma lista não modificável de alunos.
     */
    List<Aluno> findAll();
}
