package com.example.demo.IntegrationTest;

import com.jayway.jsonpath.JsonPath;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.model.Tarefas;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; // New package path
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class TarefaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Test
    void deveCriarTarefa() throws Exception {

        Tarefas tarefa = new Tarefas();
        tarefa.setTitulo("Estudar Spring");
        tarefa.setDescricao("Teste integração");
        tarefa.setTipo("ESTUDO");
        tarefa.setPrioridade("ALTA");

        mockMvc.perform(post("/adicionar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarefa)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.titulo").value("Estudar Spring"));
    }


    @Test
    void deveFalharQuandoCamposInvalidos() throws Exception {

        mockMvc.perform(post("/adicionar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "titulo": "",
                    "descricao": "",
                    "tipo": "",
                    "prioridade": ""
                }
                """))
            .andExpect(status().isBadRequest());
    }

    
    @Test
    void deveListarTarefas() throws Exception {

        mockMvc.perform(get("/listar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray());
    }

 
    @Test
    void deveCriarEDeletarTarefa() throws Exception {

        Tarefas tarefa = new Tarefas();
        tarefa.setTitulo("Deletar");
        tarefa.setDescricao("Teste");
        tarefa.setTipo("TESTE");
        tarefa.setPrioridade("ALTA");

        String response = mockMvc.perform(post("/adicionar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarefa)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Number idNumber = JsonPath.read(response, "$.data.id");
        Long id = idNumber.longValue();

        mockMvc.perform(delete("/deletar/" + id))
            .andExpect(status().isOk());
    }
}
