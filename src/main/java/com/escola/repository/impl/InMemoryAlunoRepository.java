package com.escola.repository.impl;

import com.escola.domain.Aluno;
import com.escola.repository.AlunoRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação thread-safe em memória do repositório de Alunos.
 * Utiliza ConcurrentHashMap para garantir consistência em ambientes concorrentes.
 */
public class InMemoryAlunoRepository implements AlunoRepository {
    
    private final Map<String, Aluno> alunos = new ConcurrentHashMap<>();

    @Override
    public Aluno save(Aluno aluno) {
        if (aluno == null) {
            throw new IllegalArgumentException("O aluno a ser salvo não pode ser nulo.");
        }
        alunos.put(aluno.getEmail().toLowerCase(), aluno);
        return aluno;
    }

    @Override
    public Optional<Aluno> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(alunos.get(email.trim().toLowerCase()));
    }

    @Override
    public List<Aluno> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(alunos.values()));
    }
}
