package com.example.gestionpracticasfinal.controladores;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class MensajeBoxController {
    private static final Alert alert = iniciaAlert();

    private static Alert iniciaAlert() {
        Alert a = new Alert(Alert.AlertType.NONE);
        a.setHeaderText(null);
        return a;
    }
    public static void alertError(String titulo, String texto) {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(texto);
        alert.showAndWait();
    }

    public static void alertWarning(String titulo, String texto) {
        alert.setAlertType(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setContentText(texto);
        alert.showAndWait();
    }

    public static ButtonType alertConfirmar(String titulo, String texto) {
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setContentText(texto);
        alert.showAndWait();
        return alert.getResult();
    }

    public static void alertInformation(String titulo, String texto) {
        alert.setAlertType(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(texto);
        alert.showAndWait();
    }

    public static Optional<String> inputTexto(String titulo, String texto) {
        TextInputDialog td = new TextInputDialog();
        td.setTitle(titulo);
        td.setContentText(texto);
        td.setHeaderText(null);
        return td.showAndWait();
    }
}
