package com.example.demo.TarefaControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Tarefas;
import com.example.demo.TarefasRepo;
import com.example.demo.Services.TarefaServices;

@RestController
public class DeletarController {
	
	@Autowired
	private TarefaServices tarefaService;
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deletarTarefa(@PathVariable Long id){
	
		
		List<Tarefas> tarefasDeletada = tarefaService.deletar(id);	
		
		
		return ResponseEntity.ok(Map.of(
	                "message", "Tarefa deletada com sucesso",
	                "status", 200,
	                "data", tarefasDeletada
	        ));
	}
}