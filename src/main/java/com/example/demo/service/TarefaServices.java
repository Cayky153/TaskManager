package com.example.demo.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Tarefas;
import com.example.demo.TarefasRepo;

@Service
public class TarefaServices {

	@Autowired
	private TarefasRepo tarefaRepo;

	public Tarefas salvar(Tarefas t) {
		return tarefaRepo.save(t);
	}

	public List<Tarefas> listar() {
		return tarefaRepo.findAll();
	}

	public List<Tarefas> deletar(Long id) {
    	tarefaRepo.findById(id)
    		.orElseThrow(()-> new RuntimeException("Tarefa não encontrada"));
    	tarefaRepo.deleteById(id); 
    	return tarefaRepo.findAll();
    	}

	public Tarefas editar(Long id, Tarefas nova) {
		Tarefas existente = tarefaRepo.findById(id).orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
		existente.setTitulo(nova.getTitulo());
		existente.setDescricao(nova.getDescricao());
		existente.setTipo(nova.getTipo());
		existente.setPrioridade(nova.getPrioridade());
		return tarefaRepo.save(existente);
	}

	public List<Tarefas> salvarLista(List<Tarefas> tarefas) {
		return tarefaRepo.saveAll(tarefas);
	}
}
