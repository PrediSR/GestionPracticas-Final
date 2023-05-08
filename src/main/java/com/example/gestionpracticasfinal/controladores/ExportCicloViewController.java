package com.example.gestionpracticasfinal.controladores;

import com.example.gestionpracticasfinal.MainApplication;
import com.example.gestionpracticasfinal.modelos.DatosTabla;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ExportCicloViewController implements Initializable {
    @FXML private AnchorPane root;
    @FXML private TableView<DatosTabla> tabAlumnosExport;
    @FXML private TableColumn<DatosTabla, String> colNombre;
    @FXML private TableColumn<DatosTabla, String> colApellidos;
    @FXML private TableColumn<DatosTabla, String> colTelefono;
    @FXML private TableColumn<DatosTabla, String> colEmail;
    @FXML private Label lbAlumnosExport;
    @FXML private ComboBox<String> combCiclo;
    @FXML private Button btnExportar;


    public void onClickChangeToAlumnos(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "alumnos-view.fxml"));
        Scene scene =new Scene(loader.load());
        stage.setScene(scene);
    }

    public void onClickChangeToEmpresas(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "empresas-view.fxml"));
        Scene scene =new Scene(loader.load());
        stage.setScene(scene);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializaTabla();
        inicializaCombCiclos();
    }

    private void inicializaCombCiclos() {
        try {
            String[] ciclos = GestionPracticasBDController.consultaCiclosNombres();
            combCiclo.setItems(FXCollections.observableArrayList(ciclos));
        }catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al consultar");
            alert.setContentText("Hubo un error con la base de datos y no" +
                    "pudo realizarse la consulta");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    private void inicializaTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("col1"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("col2"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("col3"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("col4"));
        colNombre.setReorderable(false);
        colApellidos.setReorderable(false);
        colTelefono.setReorderable(false);
        colEmail.setReorderable(false);
    }
}
