package com.example.demo.IntegrationTest;

import com.jayway.jsonpath.JsonPath;
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
@AutoConfigureMockMvc
public class TarefaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    // ✔ CREATE + VALIDATION + JSON + DB
    @Test
    void deveCriarTarefa() throws Exception {

        Tarefas tarefa = new Tarefas();
        tarefa.setTitulo("Estudar Spring");
        tarefa.setDescricao("Teste integração");
        tarefa.setTipo("ESTUDO");
        tarefa.setPrioridade("ALTA");

        mockMvc.perform(post("/adicionarTarefa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarefa)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.titulo").value("Estudar Spring"));
    }

    // ✔ VALIDATION + EXCEPTION HANDLER
    @Test
    void deveFalharQuandoCamposInvalidos() throws Exception {

        mockMvc.perform(post("/adicionarTarefa")
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

    // ✔ LISTAGEM (SMOKE TEST + DB + SERIALIZATION)
    @Test
    void deveListarTarefas() throws Exception {

        mockMvc.perform(get("/listagem"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray());
    }

    // ✔ INTEGRAÇÃO REAL (FLUXO COMPLETO SIMPLES)
    @Test
    void deveCriarEDeletarTarefa() throws Exception {

        Tarefas tarefa = new Tarefas();
        tarefa.setTitulo("Deletar");
        tarefa.setDescricao("Teste");
        tarefa.setTipo("TESTE");
        tarefa.setPrioridade("ALTA");

        String response = mockMvc.perform(post("/adicionarTarefa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarefa)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Number idNumber = JsonPath.read(response, "$.data.id");
        Long id = idNumber.longValue();

        mockMvc.perform(delete("/delete/" + id))
            .andExpect(status().isOk());
    }
}
