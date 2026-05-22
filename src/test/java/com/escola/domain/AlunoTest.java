package com.escola.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AlunoTest {

    @Test
    public void deveCriarAlunoComDadosValidos() {
        Aluno aluno = new Aluno("João Silva", "joao@email.com");
        assertEquals("João Silva", aluno.getNome());
        assertEquals("joao@email.com", aluno.getEmail());
    }

    @Test
    public void deveLancarExcecaoQuandoNomeForVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Aluno("", "joao@email.com");
        });
        assertEquals("O nome do aluno não pode ser vazio ou nulo.", exception.getMessage());
    }

    @Test
    public void deveLancarExcecaoQuandoNomeForNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Aluno(null, "joao@email.com");
        });
        assertEquals("O nome do aluno não pode ser vazio ou nulo.", exception.getMessage());
    }

    @Test
    public void deveLancarExcecaoQuandoEmailForVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Aluno("João Silva", "");
        });
        assertEquals("O e-mail do aluno não pode ser vazio ou nulo.", exception.getMessage());
    }

    @Test
    public void deveLancarExcecaoQuandoEmailForNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Aluno("João Silva", null);
        });
        assertEquals("O e-mail do aluno não pode ser vazio ou nulo.", exception.getMessage());
    }

    @Test
    public void deveLancarExcecaoQuandoEmailTiverFormatoInvalido() {
        String[] emailsInvalidos = {
            "joao",
            "joao@",
            "@email.com",
            "joao@email",
            "joao.email.com",
            "joao@.com"
        };

        for (String email : emailsInvalidos) {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Aluno("João Silva", email);
            }, "Deveria falhar para o email: " + email);
            assertEquals("O e-mail informado possui um formato inválido.", exception.getMessage());
        }
    }
}
