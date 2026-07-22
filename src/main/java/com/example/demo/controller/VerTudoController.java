package com.example.demo.TarefaControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.TarefasRepo;
import com.example.demo.Services.TarefaServices;
import com.example.demo.Tarefas;

@RestController
public class VerTudoController {
	
	@Autowired
	private TarefaServices tarefaService;
	
	@GetMapping("/listagem")
	public ResponseEntity<?> VerTudoController() {

		List<Tarefas> tarefasLista = tarefaService.listar();
		return ResponseEntity
				.ok(Map.of("message", "Lista de tarefas buscada com sucesso!", "status", 200, "data", tarefasLista));
	}
}