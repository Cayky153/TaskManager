package com.example.demo.view.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.example.demo.model.Tarefas;
import com.example.demo.service.TarefaServices;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@Component
@Scope("prototype")
public class TelaCadastroController {

    private final TarefaServices service;
    private Tarefas tarefaEmEdicao;

    public TelaCadastroController(TarefaServices service) {
        this.service = service;
    }

    @FXML private Label lblTituloTela;
    @FXML private TextField tfTitulo;
    @FXML private TextArea taDescricao;
    @FXML private TextField tfTipo;
    @FXML private TextField tfPrioridade;

   
    public void setTarefa(Tarefas tarefa) {
        this.tarefaEmEdicao = tarefa;
        if (tarefa != null) {
            lblTituloTela.setText("Editar tarefa");
            tfTitulo.setText(tarefa.getTitulo());
            taDescricao.setText(tarefa.getDescricao());
            tfTipo.setText(tarefa.getTipo());
            tfPrioridade.setText(tarefa.getPrioridade());
        }
    }

    @FXML
    private void onSalvar() {
        if (tfTitulo.getText() == null || tfTitulo.getText().isBlank()) {
            aviso("Título é obrigatório");
            return;
        }
        if (taDescricao.getText() == null || taDescricao.getText().isBlank()) {
            aviso("Descrição é obrigatória");
            return;
        }
        if (tfTipo.getText() == null || tfTipo.getText().isBlank()) {
            aviso("Tipo é obrigatório");
            return;
        }
        if (tfPrioridade.getText() == null || tfPrioridade.getText().isBlank()) {
            aviso("Prioridade é obrigatória");
            return;
        }

        Tarefas dados = new Tarefas();
        dados.setTitulo(tfTitulo.getText().trim());
        dados.setDescricao(taDescricao.getText().trim());
        dados.setTipo(tfTipo.getText().trim());
        dados.setPrioridade(tfPrioridade.getText().trim());

        try {
            if (tarefaEmEdicao == null) {
                service.adicionar(dados);
            } else {
                service.editar(tarefaEmEdicao.getId(), dados);
            }
            fechar();
        } catch (Exception e) {
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Erro");
            alerta.setHeaderText(null);
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();
        }
    }

    @FXML
    private void onCancelar() {
        fechar();
    }

    private void aviso(String mensagem) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle("Atenção");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void fechar() {
        ((Stage) tfTitulo.getScene().getWindow()).close();
    }
}