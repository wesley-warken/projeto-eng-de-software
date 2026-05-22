package com.escola.repository.impl;

import com.escola.domain.Curso;
import com.escola.repository.CursoRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação thread-safe em memória do repositório de Cursos.
 * Utiliza ConcurrentHashMap para garantir consistência em ambientes concorrentes.
 */
public class InMemoryCursoRepository implements CursoRepository {

    private final Map<String, Curso> cursos = new ConcurrentHashMap<>();

    @Override
    public Curso save(Curso curso) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso a ser salvo não pode ser nulo.");
        }
        cursos.put(curso.getNome().toLowerCase(), curso);
        return curso;
    }

    @Override
    public Optional<Curso> findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(cursos.get(nome.trim().toLowerCase()));
    }

    @Override
    public List<Curso> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(cursos.values()));
    }
}
