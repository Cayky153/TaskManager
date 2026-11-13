package com.example.demo.exception;

public class TarefaNaoEncontradaException extends RuntimeException {

    public TarefaNaoEncontradaException() {
        super("Tarefa não encontrada");
    }
}