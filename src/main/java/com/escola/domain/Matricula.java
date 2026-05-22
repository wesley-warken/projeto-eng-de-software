package com.escola.domain;

import java.util.Objects;

/**
 * Entidade de Domínio que representa uma Matrícula.
 * Associa de forma imutável um Aluno a um Curso correspondente.
 */
public class Matricula {
    
    private final Aluno aluno;
    private final Curso curso;

    /**
     * Construtor da classe Matricula.
     *
     * @param aluno O aluno a ser matriculado (não nulo).
     * @param curso O curso no qual o aluno será matriculado (não nulo).
     * @throws IllegalArgumentException caso algum parâmetro seja nulo.
     */
    public Matricula(Aluno aluno, Curso curso) {
        if (aluno == null) {
            throw new IllegalArgumentException("Aluno não pode ser nulo.");
        }
        if (curso == null) {
            throw new IllegalArgumentException("Curso não pode ser nulo.");
        }
        this.aluno = aluno;
        this.curso = curso;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Curso getCurso() {
        return curso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matricula matricula = (Matricula) o;
        return Objects.equals(aluno, matricula.aluno) && Objects.equals(curso, matricula.curso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aluno, curso);
    }

    @Override
    public String toString() {
        return "Matrícula: " + aluno.getNome() + " -> " + curso.getNome();
    }
}
