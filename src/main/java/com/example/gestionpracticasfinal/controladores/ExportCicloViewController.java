package com.example.gestionpracticasfinal.controladores;

import com.example.gestionpracticasfinal.MainApplication;
import com.example.gestionpracticasfinal.modelos.Alumno;
import com.example.gestionpracticasfinal.modelos.DatosTabla;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ExportCicloViewController implements Initializable {
    @FXML
    private AnchorPane root;
    @FXML
    private TableView<DatosTabla> tabAlumnosExport;
    @FXML
    private TableColumn<DatosTabla, String> colNombre;
    @FXML
    private TableColumn<DatosTabla, String> colApellidos;
    @FXML
    private TableColumn<DatosTabla, String> colTelefono;
    @FXML
    private TableColumn<DatosTabla, String> colEmail;
    @FXML
    private ComboBox<String> combCiclo;
    @FXML
    private Button btnExportar;
    @FXML
    private Label lbNoHayAlumnos;


    public void onClickChangeToAlumnos(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "alumnos-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }

    public void onClickChangeToEmpresas(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "empresas-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializaTabla();
        actualizaCombCiclos();
        prueba();
    }

    private void prueba() {
        combCiclo.addEventFilter(Event.ANY, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {

            }
        });
    }

    private void actualizaCombCiclos() {
        try {
            String[] ciclos = GestionPracticasBDController.consultaCiclosNombres();
            if (ciclos.length > 0) {
                combCiclo.setItems(FXCollections.observableArrayList(ciclos));
            } else {
                combCiclo.setItems(FXCollections.observableArrayList(
                        "No hay ciclos añadidos"));
            }
            if (combCiclo.getItems().size() < 6) {
                combCiclo.setVisibleRowCount(combCiclo.getItems().size());
            } else {
                combCiclo.setVisibleRowCount(5);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al consultar");
            alert.setContentText("Hubo un error con la base de datos y no" +
                    "pudo realizarse la consulta de ciclos");
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

    public void onCombCicloSelected(Event event) {
        String ciclo = combCiclo.getValue();

        try {
            System.out.println(event.getTarget());
            if (isValorEnCombCiclos(ciclo)) {
                if (!ciclo.equals("No hay ciclos añadidos")) {
                    ObservableList<DatosTabla> lista = getAlumnosCiclo(ciclo);
                    if (lista.size() > 0) {
                        lbNoHayAlumnos.setVisible(false);
                        tabAlumnosExport.setItems(lista);
                        tabAlumnosExport.setVisible(true);
                    } else {
                        tabAlumnosExport.setVisible(false);
                        lbNoHayAlumnos.setVisible(true);
                    }
                    btnExportar.setDisable(false);
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al consultar el ciclo");
            alert.setContentText("No se ha podido hacer la consulta de alumnos" +
                    " que se exportaran");
            alert.setHeaderText(null);
            alert.showAndWait();
        }

    }

    private boolean isValorEnCombCiclos(String st) {
        for (String res : combCiclo.getItems()) {
            if (st != null && st.equalsIgnoreCase(res)) {
                return true;
            }
        }
        return false;
    }

    public void onKeyPressedBuscaCiclo(KeyEvent event) throws Exception {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String[] ciclos;
        String buscaC = combCiclo.getEditor().getText();

        if (!buscaC.equals("")) {
            if (GestionPracticasBDController.numCiclos() > 0) {
                ciclos = GestionPracticasBDController.buscaCiclosNombre(buscaC);
                if (ciclos.length > 0) {
                    lista.setAll(ciclos);
                } else {
                    lista.add("sin resultados");
                }
            } else {
                lista.add("No hay ciclos añadidos");
            }
            if (lista.size() < 6) {
                combCiclo.setVisibleRowCount(lista.size());
            } else {
                combCiclo.setVisibleRowCount(5);
            }
            combCiclo.setItems(lista);
            combCiclo.show();
        } else {
            actualizaCombCiclos();
            combCiclo.hide();
        }
    }

    public ObservableList<DatosTabla> getAlumnosCiclo(String ciclo) throws Exception {
        Alumno[] lista = GestionPracticasBDController.consultaAlumnosPorNombreCiclo(ciclo);
        ObservableList<DatosTabla> output = FXCollections.observableArrayList();
        for (Alumno a : lista) {
            output.add(new DatosTabla(a));
        }
        return output;
    }

    public void onClickExportButton(MouseEvent event) {
        String ciclo = combCiclo.getValue();
        if (tabAlumnosExport.isVisible()) {
            FileChooser fc = new FileChooser();
            fc.setTitle("Elige el destino del ciclo exportado");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT", "*.txt"));
            fc.setInitialFileName(ciclo + "-Export");

            File file = fc.showSaveDialog(null);

            if (file != null) {
                try {
                    Alumno[] lista = GestionPracticasBDController.consultaAlumnosPorNombreCiclo(ciclo);
                    FicherosController.escribirCiclo(file, lista);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("CICLO EXPORTADO");
                    alert.setContentText("El ciclo seleccionado fue exportado correctamente");
                    alert.showAndWait();

                    reiniciaSeleccion();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setTitle("ERROR AL EXPORTAR");
                    alert.setContentText("Hubo un error al exportar el ciclo y no pudo" +
                            "crearse el fichero");
                    alert.showAndWait();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setTitle("CICLO NO VALIDO");
            alert.setContentText("El ciclo seleccionado no tiene alumnos para exportar" +
                    " a un fichero");
            alert.showAndWait();
        }
    }

    private void reiniciaSeleccion() {
        combCiclo.setValue("");
        tabAlumnosExport.setVisible(false);
        lbNoHayAlumnos.setVisible(false);
    }
}
