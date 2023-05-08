package com.example.gestionpracticasfinal.controladores;

import com.example.gestionpracticasfinal.MainApplication;
import com.example.gestionpracticasfinal.modelos.Alumno;
import com.example.gestionpracticasfinal.modelos.DatosTabla;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AlumnosViewController implements Initializable {
    @FXML private AnchorPane root;
    @FXML private TableView<DatosTabla> tabAlumnos;
    @FXML private TableColumn<DatosTabla, String> colNombre;
    @FXML private TableColumn<DatosTabla, String> colApellidos;
    @FXML private TableColumn<DatosTabla, String> colCiclo;
    @FXML private TableColumn<DatosTabla, String> colElimina;
    @FXML private TableColumn<DatosTabla, String> colModifica;
    @FXML private TableColumn<DatosTabla, String> colConsulta;
    @FXML private TextField txtNombreAlumno;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conectaBD();
        inicializaTabla();
    }

    private void inicializaTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("col1"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("col2"));
        colCiclo.setCellValueFactory(new PropertyValueFactory<>("col3"));
        colConsulta.setCellValueFactory(new PropertyValueFactory<>("btnConsultar"));
        colModifica.setCellValueFactory(new PropertyValueFactory<>("btnModificar"));
        colElimina.setCellValueFactory(new PropertyValueFactory<>("btnEliminar"));
        colNombre.setReorderable(false);
        colApellidos.setReorderable(false);
        colCiclo.setReorderable(false);
        colConsulta.setReorderable(false);
        colModifica.setReorderable(false);
        colElimina.setReorderable(false);
        try {
            tabAlumnos.setItems(getAlumnos());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void conectaBD() {
        try {
            GestionPracticasBDController.crearConexion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ObservableList<DatosTabla> getAlumnos() throws Exception {
        tabAlumnos.getItems().clear();
        ObservableList<DatosTabla> output = FXCollections.observableArrayList();
        Alumno[] lista = GestionPracticasBDController.consultaAlumnos();
        for (Alumno a : lista) {
            output.add(new DatosTabla(a, onClickBtnModifica(a)));
        }
        return output;
    }

    public EventHandler<MouseEvent> onClickBtnModifica(Alumno a) {
        return event -> {
            txtNombreAlumno.setText(a.getNombre());
        };
    }

    public void onClickChangeToEmpresas(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "empresas-view.fxml"));
        Scene scene =new Scene(loader.load());
        stage.setScene(scene);
    }

    public void onClickChangeToExportCiclo(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "exportciclo-view.fxml"));
        Scene scene =new Scene(loader.load());
        stage.setScene(scene);
    }
}