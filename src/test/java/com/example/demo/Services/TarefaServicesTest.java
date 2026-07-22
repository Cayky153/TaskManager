package com.example.demo.Services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.Tarefas;
import com.example.demo.repository.TarefasRepo;
import com.example.demo.service.TarefaServices;

@ExtendWith(MockitoExtension.class)
public class TarefaServicesTest {
	
	@InjectMocks
	TarefaServices service;
	
	@Mock
	TarefasRepo repository;
	
	Tarefas tarefa;
	
	Tarefas tarefaEditada;
	
	List<Tarefas> tarefasArray;
	
	@BeforeEach
	public void setUp() {
		   tarefa = new Tarefas();
		   tarefa.setId(1L);
		    tarefa.setTitulo("Lavar a louça");
		    tarefa.setDescricao("A pia cheia de louça o banheiro parece o de butiquim");
		    tarefa.setTipo("em andamento");
		    tarefa.setPrioridade("Alta prioridade");
		    
		    tarefasArray = List.of(tarefa, tarefa);
		   
		   	tarefaEditada= new Tarefas();
		    tarefaEditada.setTitulo("Treinar");
		    tarefaEditada.setDescricao("Projeto verao");
		    tarefaEditada.setTipo("A fazer");
		    tarefaEditada.setPrioridade("Alta prioridade");
	}	
	
	@Test
	void ListarSucess() {
		List<Tarefas> lista = List.of(tarefa);
		when(repository.findAll()).thenReturn(lista);
		
		List<Tarefas> serviceListar = service.listar();
		
		assertEquals(lista,serviceListar);
		verify(repository).findAll();
		verifyNoMoreInteractions(repository);
		
	}
	@Test
	void SalvarSucess() {
		
		
		when(repository.save(tarefa)).thenReturn(tarefa);
		
		Tarefas serviceAdicionar = service.adicionar(tarefa);
		
		assertEquals(tarefa,serviceAdicionar);
		verify(repository).save(tarefa);
		verifyNoMoreInteractions(repository);
		
	}
	@Test
	void SalvarListaSucess() {
		
		
		when(repository.saveAll(tarefasArray)).thenReturn(tarefasArray);
		
		List<Tarefas> serviceAdicionarLista = service.adicionarLista(tarefasArray);
		
		assertEquals(tarefasArray,serviceAdicionarLista);
		verify(repository).saveAll(tarefasArray);
		verifyNoMoreInteractions(repository);
		
	}
	
	@Test
	void DeletarSucess() {
		
		
		long tarefaId = tarefa.getId();
		when(repository.findById(tarefaId)).thenReturn(Optional.of(tarefa));
		
		service.deletar(tarefaId);
		
		verify(repository).findById(tarefaId);
		verify(repository).deleteById(tarefaId);
		verify(repository).findAll();
		verifyNoMoreInteractions(repository);
		
	}
	@Test
	void DeletarFailure() {
		
		long tarefaId = tarefa.getId();
		when(repository.findById(tarefaId)).thenReturn(Optional.empty());
		
		RuntimeException e = assertThrows(RuntimeException.class, () -> {
		    service.deletar(tarefaId);
		});
		assertThat(e.getMessage())
	    .isEqualTo("Tarefa não encontrada");
		verify(repository).findById(tarefaId);
		verifyNoMoreInteractions(repository);
	}
	@Test
	void EditarSucess() {
		
		
		long tarefaId = tarefa.getId();
		when(repository.findById(tarefaId)).thenReturn(Optional.of(tarefa));
		when(repository.save(tarefa)).thenReturn(tarefa);
		Tarefas serviceEditar = service.editar(tarefaId,tarefaEditada);
		

	    assertEquals(tarefaEditada.getTitulo(), serviceEditar.getTitulo());
	    assertEquals(tarefaEditada.getDescricao(), serviceEditar.getDescricao());
	    assertEquals(tarefaEditada.getTipo(), serviceEditar.getTipo());
	    assertEquals(tarefaEditada.getPrioridade(), serviceEditar.getPrioridade());
		
		verify(repository).findById(tarefaId);
		verify(repository).save(tarefa);
		verifyNoMoreInteractions(repository);
		
	}
	
	@Test
	void EditarFailure() {
		
		long tarefaId = tarefa.getId();
		when(repository.findById(tarefaId)).thenReturn(Optional.empty());
		
		RuntimeException e = assertThrows(RuntimeException.class, () -> {
		    service.editar(tarefaId,tarefaEditada);
		});
		assertThat(e.getMessage())
	    .isEqualTo("Tarefa não encontrada");
		verify(repository).findById(tarefaId);
		verifyNoMoreInteractions(repository);
	}
}
