package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Tarefas;

public interface TarefasRepo extends JpaRepository<Tarefas,Long> {

}
