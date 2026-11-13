package com.example.demo.IntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.model.Tarefas;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class TarefaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Long criarTarefa(String titulo) throws Exception {

        Tarefas tarefa = new Tarefas();
        tarefa.setTitulo(titulo);
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
        return idNumber.longValue();
    }
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
    void deveCriarListaDeTarefas() throws Exception {

        Tarefas t1 = new Tarefas();
        t1.setTitulo("Lista 1");
        t1.setDescricao("Teste");
        t1.setTipo("TESTE");
        t1.setPrioridade("ALTA");

        Tarefas t2 = new Tarefas();
        t2.setTitulo("Lista 2");
        t2.setDescricao("Teste");
        t2.setTipo("TESTE");
        t2.setPrioridade("BAIXA");

        mockMvc.perform(post("/adicionarLista")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(t1, t2))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(201))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].titulo").value("Lista 1"))
            .andExpect(jsonPath("$.data[1].titulo").value("Lista 2"));
    } 
    @Test
    void deveEditarTarefa() throws Exception {

    	Long id = criarTarefa("Antes");

        Tarefas editada = new Tarefas();
        editada.setTitulo("Depois");
        editada.setDescricao("Descricao nova");
        editada.setTipo("NOVO");
        editada.setPrioridade("BAIXA");

        mockMvc.perform(put("/editar/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editada)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.titulo").value("Depois"))
            .andExpect(jsonPath("$.data.descricao").value("Descricao nova"))
            .andExpect(jsonPath("$.data.tipo").value("NOVO"))
            .andExpect(jsonPath("$.data.prioridade").value("BAIXA"));
    }

    @Test
    void deveFalharAoEditarComCamposInvalidos() throws Exception {



        Long id = criarTarefa("Antes");


        mockMvc.perform(put("/editar/" + id)
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
    void deveFalharAoEditarIdInexistente() throws Exception {

        Tarefas editada = new Tarefas();
        editada.setTitulo("Nao existe");
        editada.setDescricao("Teste");
        editada.setTipo("TESTE");
        editada.setPrioridade("ALTA");

        mockMvc.perform(put("/editar/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editada)))
        	.andExpect(status().isNotFound());
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
    void deveListarTarefaPorId() throws Exception {

    	Long id = criarTarefa("Buscar por id");

        mockMvc.perform(get("/listarPorId/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.id").value(id))
            .andExpect(jsonPath("$.data.titulo").value("Buscar por id"));
    }

    @Test
    void deveFalharAoListarPorIdInexistente() throws Exception {

        mockMvc.perform(get("/listarPorId/999999"))
            .andExpect(status().isNotFound());
    }

    
    @Test
    void deveCriarEDeletarTarefa() throws Exception {

    	Long id = criarTarefa("Deletar");

        mockMvc.perform(delete("/deletar/" + id))
            .andExpect(status().isOk());
    }
    
    @Test
    void deveDeletarTodasAsTarefas() throws Exception {

        Tarefas tarefa = new Tarefas();
        tarefa.setTitulo("Apagar");
        tarefa.setDescricao("Teste");
        tarefa.setTipo("TESTE");
        tarefa.setPrioridade("ALTA");

        mockMvc.perform(post("/adicionar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarefa)))
            .andExpect(status().isCreated());

        mockMvc.perform(delete("/deletarTudo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200));

        mockMvc.perform(get("/listar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isEmpty());
    }
}
