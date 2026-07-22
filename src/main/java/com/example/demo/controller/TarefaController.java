package com.example.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Tarefas;
import com.example.demo.service.TarefaServices;

import jakarta.validation.Valid;


@RestController
public class TarefaController {

	@Autowired
	private TarefaServices tarefaService;

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
	public ResponseEntity<?> adicionar(@Valid @RequestBody Tarefas tarefa) {
		Tarefas response = tarefaService.adicionar(tarefa);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of("message", "Tarefa criada com sucesso", "status", 201, "data", response));
	}
	
	@PostMapping("/adicionarLista")
	public ResponseEntity<?> adicionarLista(@Valid @RequestBody List<Tarefas> tarefaLista) {
		List response = tarefaService.adicionarLista(tarefaLista);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of("message", "Múltiplas tarefas criadas com sucesso", "status", 201, "data", response));
	}

	@PutMapping("/editar/{id}")
	public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody Tarefas tarefa) {

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
