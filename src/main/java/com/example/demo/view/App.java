package com.example.demo.view;

import java.io.IOException;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.example.demo.DemoApplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(DemoApplication.class).headless(false).run();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TelaPrincipal.fxml"));
        loader.setControllerFactory(context::getBean);
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Task Manager");
        stage.show();
    }

    @Override
    public void stop() {
        context.close();
    }
}