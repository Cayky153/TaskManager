package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record TarefaRequest(
    @NotBlank String titulo,
    @NotBlank String descricao,
    @NotBlank String tipo,
    @NotBlank String prioridade
) {}