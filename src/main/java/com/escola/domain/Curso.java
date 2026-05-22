package com.escola.domain;

import java.util.Objects;

/**
 * Entidade de Domínio que representa um Curso.
 * Gerencia seu próprio estado de vagas de forma thread-safe para garantir
 * consistência lógica no sistema de matrículas.
 */
public class Curso {
    
    private final String nome;
    private final int vagasTotais;
    private int vagasDisponiveis;

    /**
     * Construtor da classe Curso.
     *
     * @param nome        Nome do curso (não nulo e não vazio).
     * @param vagasTotais Quantidade total de vagas (deve ser maior que zero).
     * @throws IllegalArgumentException caso os dados fornecidos sejam inválidos.
     */
    public Curso(String nome, int vagasTotais) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do curso não pode ser vazio ou nulo.");
        }
        if (vagasTotais <= 0) {
            throw new IllegalArgumentException("O número de vagas deve ser maior que zero.");
        }
        this.nome = nome.trim();
        this.vagasTotais = vagasTotais;
        this.vagasDisponiveis = vagasTotais;
    }

    public String getNome() {
        return nome;
    }

    public int getVagasTotais() {
        return vagasTotais;
    }

    public synchronized int getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    /**
     * Decrementa uma vaga disponível. Operação thread-safe.
     * 
     * @throws IllegalStateException se não houver vagas disponíveis.
     */
    public synchronized void decrementarVaga() {
        if (vagasDisponiveis <= 0) {
            throw new IllegalStateException("Não há vagas disponíveis neste curso.");
        }
        vagasDisponiveis--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Curso curso = (Curso) o;
        return Objects.equals(nome.toLowerCase(), curso.nome.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome.toLowerCase());
    }

    @Override
    public String toString() {
        return nome + " (Vagas: " + vagasDisponiveis + "/" + vagasTotais + ")";
    }
}
