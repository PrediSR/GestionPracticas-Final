package com.example.gestionpracticasfinal.controladores;

import com.example.gestionpracticasfinal.MainApplication;
import com.example.gestionpracticasfinal.modelos.Alumno;
import com.example.gestionpracticasfinal.modelos.DatosTabla;
import com.example.gestionpracticasfinal.modelos.Empresa;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EmpresasViewController implements Initializable {
    @FXML private TableView<DatosTabla> tabEmpresas;
    @FXML private TableColumn<DatosTabla, String> colCodigo;
    @FXML private TableColumn<DatosTabla, String> colNombre;
    @FXML private TableColumn<DatosTabla, String> colConsulta;
    @FXML private TableColumn<DatosTabla, String> colModifica;
    @FXML private TableColumn<DatosTabla, String> colElimina;

    public void OnClickChangeToAlumnos(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("alumnos-view.fxml"));
            Stage stage = (Stage) tabEmpresas.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 920, 595);
            stage.setScene(scene);
        }catch (Exception e) {

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("col1"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("col2"));
        colConsulta.setCellValueFactory(new PropertyValueFactory<>("btnConsultar"));
        colModifica.setCellValueFactory(new PropertyValueFactory<>("btnModificar"));
        colElimina.setCellValueFactory(new PropertyValueFactory<>("btnEliminar"));
        colCodigo.setReorderable(false);
        colNombre.setReorderable(false);
        colConsulta.setReorderable(false);
        colModifica.setReorderable(false);
        colElimina.setReorderable(false);
        try {
            tabEmpresas.setItems(getEmpresas());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ObservableList<DatosTabla> getEmpresas() throws Exception {
        ObservableList<DatosTabla> output = FXCollections.observableArrayList();
        Empresa[] lista = GestionPracticasBDController.consultaEmpresas();
        for (Empresa emp : lista) {
            output.add(new DatosTabla(emp));
        }
        return output;
    }
}