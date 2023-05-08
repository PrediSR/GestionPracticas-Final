package com.example.gestionpracticasfinal;

import com.example.gestionpracticasfinal.controladores.GestionPracticasBDController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("alumnos-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 920, 595);
        stage.setTitle("GESTIÓN PRACTICAS PROGRESA");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}