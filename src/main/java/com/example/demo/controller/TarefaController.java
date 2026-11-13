package com.example.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.TarefaRequest;
import com.example.demo.model.Tarefas;
import com.example.demo.service.TarefaServices;

import jakarta.validation.Valid;


@RestController
public class TarefaController {

	private final TarefaServices tarefaService;

	public TarefaController(TarefaServices tarefaService) {
		this.tarefaService = tarefaService;
	}

	@GetMapping("/listar")
	public ResponseEntity<?> listar() {

		List<Tarefas> tarefasLista = tarefaService.listar();
		return ResponseEntity
				.ok(Map.of("message", "Lista de tarefas buscada com sucesso!", "status", 200, "data", tarefasLista));
	}
	
	@GetMapping("/listarPorId/{id}")
	public ResponseEntity<?> listarId(@PathVariable Long id) {

		Optional<Tarefas> tarefa = tarefaService.listarId(id);
		return ResponseEntity
				.ok(Map.of("message", "Tarefa buscada com sucesso!", "status", 200, "data", tarefa));
	}

	@PostMapping("/adicionar")
	public ResponseEntity<?> adicionar(@Valid @RequestBody TarefaRequest request) {
		  Tarefas tarefa = new Tarefas();
		    tarefa.setTitulo(request.titulo());
		    tarefa.setDescricao(request.descricao());
		    tarefa.setTipo(request.tipo());
		    tarefa.setPrioridade(request.prioridade());
		
		Tarefas response = tarefaService.adicionar(tarefa);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of("message", "Tarefa criada com sucesso", "status", 201, "data", response));
	}
	
	@PostMapping("/adicionarLista")
	public ResponseEntity<?> adicionarLista(@Valid @RequestBody List<TarefaRequest> requests) {

	    List<Tarefas> tarefas = requests.stream().map(request -> {
	        Tarefas tarefa = new Tarefas();
	        tarefa.setTitulo(request.titulo());
	        tarefa.setDescricao(request.descricao());
	        tarefa.setTipo(request.tipo());
	        tarefa.setPrioridade(request.prioridade());
	        return tarefa;
	    }).toList();

	    List<Tarefas> response = tarefaService.adicionarLista(tarefas);
	    return ResponseEntity.status(HttpStatus.CREATED)
	            .body(Map.of("message", "Múltiplas tarefas criadas com sucesso", "status", 201, "data", response));
	}

	@PutMapping("/editar/{id}")
	public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody TarefaRequest request) {
		 Tarefas tarefa = new Tarefas();
		    tarefa.setTitulo(request.titulo());
		    tarefa.setDescricao(request.descricao());
		    tarefa.setTipo(request.tipo());
		    tarefa.setPrioridade(request.prioridade());
		Tarefas novaData = tarefaService.editar(id, tarefa);
		return ResponseEntity.ok(Map.of("message", "Tarefa atualizada com sucessp", "status", 200, "data", novaData));
	}

	@DeleteMapping("/deletar/{id}")
	public ResponseEntity<?> deletar(@PathVariable Long id) {

		tarefaService.deletar(id);

		return ResponseEntity
				.ok(Map.of("message", "Tarefa deletada com sucesso", "status", 200));
	}
	@DeleteMapping("/deletarTudo")
	public ResponseEntity<?> deletarTudo() {

		tarefaService.deletarTudo();

		return ResponseEntity
				.ok(Map.of("message", "Lista deletada com sucesso", "status", 200));
	}
}
