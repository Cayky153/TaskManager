package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;


import com.example.demo.exception.TarefaNaoEncontradaException;
import com.example.demo.model.Tarefas;
import com.example.demo.repository.TarefasRepo;




@Service
public class TarefaServices {

	private final TarefasRepo tarefaRepo;
	private final Counter tarefasCriadas;

	public TarefaServices(TarefasRepo tarefaRepo, MeterRegistry registry) {
		this.tarefaRepo = tarefaRepo;
		 this.tarefasCriadas = Counter.builder("tarefas_criadas_total")
	                .description("Total de tarefas criadas")
	                .register(registry);
	}

	public List<Tarefas> listar() {
		return tarefaRepo.findAll();
	}
	
	public Optional<Tarefas> listarId(Long id) {
		tarefaRepo.findById(id)
		.orElseThrow(TarefaNaoEncontradaException::new);
		return tarefaRepo.findById(id);
	}
	
	  public Tarefas adicionar(Tarefas tarefa) {
	        Tarefas salva = tarefaRepo.save(tarefa);
	        tarefasCriadas.increment();
	        return salva;
	    }
	
	  public List<Tarefas> adicionarLista(List<Tarefas> tarefas) {
		    List<Tarefas> salvas = tarefaRepo.saveAll(tarefas);
		    tarefasCriadas.increment(salvas.size());
		    return salvas;
		}

	public Tarefas editar(Long id, Tarefas nova) {
		Tarefas existente = tarefaRepo.findById(id).orElseThrow(TarefaNaoEncontradaException::new);
		existente.setTitulo(nova.getTitulo());
		existente.setDescricao(nova.getDescricao());
		existente.setTipo(nova.getTipo());
		existente.setPrioridade(nova.getPrioridade());
		return tarefaRepo.save(existente);
	}
	
	public void deletar(Long id) {
    	tarefaRepo.findById(id)
    		.orElseThrow(TarefaNaoEncontradaException::new);
    	tarefaRepo.deleteById(id); 
    	
    	}
	public void deletarTudo(){
		tarefaRepo.deleteAll();
	}
}
