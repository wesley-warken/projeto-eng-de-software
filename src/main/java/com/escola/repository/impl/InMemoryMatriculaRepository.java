package com.escola.repository.impl;

import com.escola.domain.Aluno;
import com.escola.domain.Curso;
import com.escola.domain.Matricula;
import com.escola.repository.MatriculaRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementação thread-safe em memória do repositório de Matrículas.
 * Utiliza CopyOnWriteArrayList para leituras rápidas e consistência concorrente em escrita.
 */
public class InMemoryMatriculaRepository implements MatriculaRepository {

    private final List<Matricula> matriculas = new CopyOnWriteArrayList<>();

    @Override
    public Matricula save(Matricula matricula) {
        if (matricula == null) {
            throw new IllegalArgumentException("A matrícula a ser salva não pode ser nula.");
        }
        matriculas.add(matricula);
        return matricula;
    }

    @Override
    public List<Matricula> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(matriculas));
    }

    @Override
    public boolean existsByAlunoAndCurso(Aluno aluno, Curso curso) {
        if (aluno == null || curso == null) {
            return false;
        }
        return matriculas.stream()
                .anyMatch(m -> m.getAluno().equals(aluno) && m.getCurso().equals(curso));
    }
}
