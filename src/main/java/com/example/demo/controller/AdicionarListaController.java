package com.example.demo.TarefaControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Tarefas;
import com.example.demo.TarefasRepo;
import com.example.demo.Services.TarefaServices;

import jakarta.validation.Valid;

@RestController
public class AdicionarListaController {

	@Autowired
	private TarefaServices tarefaService;
	
	@PostMapping("/adicionarListaTarefa")
	public ResponseEntity<?> criarTarefa(@Valid @RequestBody List<Tarefas> tarefaLista) {
		List response= tarefaService.salvarLista(tarefaLista);
		return ResponseEntity.status(HttpStatus.CREATED).body(
				Map.of(
					"message","Múltiplas tarefas criadas com sucesso",
					"status", 201,
					"data", response
				)
			);	
	
}
}