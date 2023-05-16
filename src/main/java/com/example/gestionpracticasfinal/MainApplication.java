package com.example.gestionpracticasfinal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        //al comenzar la aplicacion se ejecuta la parte de gestion de alumnos
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("alumnos-view.fxml"));
        //ajuste inicial del tamaño de la ventana
        Scene scene = new Scene(fxmlLoader.load(), 920, 595);
        //se pone el titulo y de tamaño fijo a la ventana, se añade al stage y se muestra la ventana
        stage.setTitle("GESTIÓN PRACTICAS PROGRESA");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}