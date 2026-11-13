package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.exception.TarefaNaoEncontradaException;
import com.example.demo.model.Tarefas;
import com.example.demo.repository.TarefasRepo;

@Service
public class TarefaServices {

	private final TarefasRepo tarefaRepo;

	public TarefaServices(TarefasRepo tarefaRepo) {
		this.tarefaRepo = tarefaRepo;
	}

	public List<Tarefas> listar() {
		return tarefaRepo.findAll();
	}
	
	public Optional<Tarefas> listarId(Long id) {
		tarefaRepo.findById(id)
		.orElseThrow(TarefaNaoEncontradaException::new);
		return tarefaRepo.findById(id);
	}
	
	public <T extends Tarefas> T adicionar(T t) {
		return tarefaRepo.save(t);
	}
	
	public <T extends Tarefas> List<T> adicionarLista(List<T> tarefas) {
		return tarefaRepo.saveAll(tarefas);
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
