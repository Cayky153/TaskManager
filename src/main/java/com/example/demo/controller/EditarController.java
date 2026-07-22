package com.example.demo.TarefaControllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Tarefas;
import com.example.demo.TarefasRepo;
import com.example.demo.Services.TarefaServices;

import jakarta.validation.Valid;


@RestController
public class EditarController {
	
	@Autowired
	private TarefaServices tarefaService;
	
	@PutMapping("/editar/{id}")
	public ResponseEntity<?> editarTarefa(@PathVariable Long id,@Valid @RequestBody Tarefas tarefa) {
		
		
		Tarefas novaData = tarefaService.editar(id, tarefa);
			return ResponseEntity.ok(Map.of(
	                "message", "Tarefa atualizada com sucessp",
	                "status", 200,
	                "data", novaData
	        ));
	}
}