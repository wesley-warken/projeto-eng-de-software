package com.escola.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Entidade de Domínio que representa um Aluno.
 * Esta classe é imutável (Value Object / Entity pattern) e garante a consistência
 * de seus dados diretamente no construtor.
 */
public class Aluno {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private final String nome;
    private final String email;

    /**
     * Construtor da classe Aluno.
     * Realiza todas as validações necessárias para garantir um estado sempre válido.
     *
     * @param nome  Nome do aluno (não nulo e não vazio).
     * @param email E-mail do aluno (deve ser um e-mail com formato válido).
     * @throws IllegalArgumentException caso os dados fornecidos sejam inválidos.
     */
    public Aluno(String nome, String email) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do aluno não pode ser vazio ou nulo.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("O e-mail do aluno não pode ser vazio ou nulo.");
        }
        
        String emailTrimmado = email.trim();
        if (!EMAIL_PATTERN.matcher(emailTrimmado).matches()) {
            throw new IllegalArgumentException("O e-mail informado possui um formato inválido.");
        }

        this.nome = nome.trim();
        this.email = emailTrimmado.toLowerCase();
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aluno aluno = (Aluno) o;
        return Objects.equals(email, aluno.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return nome + " (" + email + ")";
    }
}
