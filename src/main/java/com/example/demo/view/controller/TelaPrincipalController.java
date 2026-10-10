package com.example.demo.view.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.example.demo.model.Tarefas;
import com.example.demo.service.TarefaServices;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class TelaPrincipalController {

    private final TarefaServices service;
    private final ApplicationContext context;

    public TelaPrincipalController(TarefaServices service, ApplicationContext context) {
        this.service = service;
        this.context = context;
    }

    @FXML private TableView<Tarefas> tabelaTarefas;
    @FXML private TableColumn<Tarefas, String> colTitulo;
    @FXML private TableColumn<Tarefas, String> colDescricao;
    @FXML private TableColumn<Tarefas, String> colTipo;
    @FXML private TableColumn<Tarefas, String> colPrioridade;

    @FXML
    public void initialize() {
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colDescricao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescricao()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colPrioridade.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrioridade()));
        carregarTabela();
    }

    @FXML
    private void onNovaTarefa() {
        abrirCadastro(null);
    }

    @FXML
    private void onEditar() {
        Tarefas selecionada = tabelaTarefas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta(AlertType.WARNING, "Atenção", "Selecione uma tarefa para editar.");
            return;
        }
        abrirCadastro(selecionada);
    }

    @FXML
    private void onRemover() {
        Tarefas selecionada = tabelaTarefas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta(AlertType.WARNING, "Atenção", "Selecione uma tarefa para remover.");
            return;
        }

        Alert confirmacao = new Alert(AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmação");
        confirmacao.setHeaderText("Remover tarefa?");
        confirmacao.setContentText("A tarefa \"" + selecionada.getTitulo() + "\" será removida. Essa ação não pode ser desfeita.");

        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            try {
                service.deletar(selecionada.getId());
                carregarTabela();
            } catch (Exception e) {
                mostrarAlerta(AlertType.ERROR, "Erro", e.getMessage());
            }
        }
    }

    private void carregarTabela() {
        try {
            tabelaTarefas.setItems(FXCollections.observableArrayList(service.listar()));
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    private void abrirCadastro(Tarefas tarefa) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TelaCadastro.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent raiz = loader.load();

            TelaCadastroController controller = loader.getController();
            controller.setTarefa(tarefa);

            Stage janela = new Stage();
            janela.setTitle(tarefa == null ? "Nova tarefa" : "Editar tarefa");
            janela.initModality(Modality.APPLICATION_MODAL);
            janela.initOwner(tabelaTarefas.getScene().getWindow());
            janela.setScene(new Scene(raiz));
            janela.showAndWait();

            carregarTabela();
        } catch (IOException e) {
            mostrarAlerta(AlertType.ERROR, "Erro", "Não foi possível abrir a tela: " + e.getMessage());
        }
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}