package com.escola.repository;

import com.escola.domain.Curso;
import java.util.List;
import java.util.Optional;

/**
 * Interface que define as operações de persistência para a entidade Curso.
 * Segue o padrão Repository para abstração da camada de dados.
 */
public interface CursoRepository {

    /**
     * Salva ou atualiza um curso no repositório.
     * 
     * @param curso O curso a ser salvo.
     * @return O curso salvo.
     */
    Curso save(Curso curso);

    /**
     * Busca um curso pelo seu nome (identificador único).
     * 
     * @param nome O nome do curso.
     * @return Um Optional contendo o curso, se encontrado.
     */
    Optional<Curso> findByNome(String nome);

    /**
     * Retorna todos os cursos cadastrados.
     * 
     * @return Uma lista não modificável de cursos.
     */
    List<Curso> findAll();
}
