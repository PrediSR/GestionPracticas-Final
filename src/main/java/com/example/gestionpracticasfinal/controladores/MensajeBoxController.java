package com.example.gestionpracticasfinal.controladores;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

//clase estatica que guarda metodos para llamar a alerts
public class MensajeBoxController {
    private static final Alert alert = iniciaAlert();

    //metodo privado que inicializa la alerta con header a null
    private static Alert iniciaAlert() {
        Alert a = new Alert(Alert.AlertType.NONE);
        a.setHeaderText(null);
        return a;
    }
    //metodo que configura y muestra una alerta de error
    public static void alertError(String titulo, String texto) {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(texto);
        alert.showAndWait();
    }

    //metodo que configura y muestra una alerta de advertencia
    public static void alertWarning(String titulo, String texto) {
        alert.setAlertType(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setContentText(texto);
        alert.showAndWait();
    }

    //metodo que configura y muestra una alerta que pide que se confirme la accion
    public static ButtonType alertConfirmar(String titulo, String texto) {
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setContentText(texto);
        alert.showAndWait();
        return alert.getResult();
    }

    //metodo que configura y muestra una alerta de informacion
    public static void alertInformation(String titulo, String texto) {
        alert.setAlertType(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(texto);
        alert.showAndWait();
    }

    //metodo que muestra una alerta que pide que se introduzca el nombre de un nuevo ciclo
    //y devuelve el valor
    public static Optional<String> inputTexto(String titulo, String texto) {
        TextInputDialog td = new TextInputDialog();
        td.setTitle(titulo);
        td.setContentText(texto);
        td.setHeaderText(null);
        return td.showAndWait();
    }
}
