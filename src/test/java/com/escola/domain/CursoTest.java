package com.escola.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CursoTest {

    @Test
    public void deveCriarCursoComDadosValidos() {
        Curso curso = new Curso("Inglês Intensivo", 10);
        assertEquals("Inglês Intensivo", curso.getNome());
        assertEquals(10, curso.getVagasTotais());
        assertEquals(10, curso.getVagasDisponiveis());
    }

    @Test
    public void deveLancarExcecaoQuandoNomeDoCursoForVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Curso("", 5);
        });
        assertEquals("O nome do curso não pode ser vazio ou nulo.", exception.getMessage());
    }

    @Test
    public void deveLancarExcecaoQuandoNomeDoCursoForNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Curso(null, 5);
        });
        assertEquals("O nome do curso não pode ser vazio ou nulo.", exception.getMessage());
    }

    @Test
    public void deveLancarExcecaoQuandoVagasForZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Curso("Espanhol", 0);
        });
        assertEquals("O número de vagas deve ser maior que zero.", exception.getMessage());
    }

    @Test
    public void deveLancarExcecaoQuandoVagasForNegativo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Curso("Espanhol", -5);
        });
        assertEquals("O número de vagas deve ser maior que zero.", exception.getMessage());
    }

    @Test
    public void deveDecrementarVagaDisponivel() {
        Curso curso = new Curso("Francês", 3);
        curso.decrementarVaga();
        assertEquals(2, curso.getVagasDisponiveis());
    }

    @Test
    public void deveLancarExcecaoAoDecrementarSemVagasDisponiveis() {
        Curso curso = new Curso("Alemão", 1);
        curso.decrementarVaga();
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            curso.decrementarVaga();
        });
        assertEquals("Não há vagas disponíveis neste curso.", exception.getMessage());
    }
}
