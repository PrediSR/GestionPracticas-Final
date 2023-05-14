package com.example.gestionpracticasfinal.controladores;

import com.example.gestionpracticasfinal.MainApplication;
import com.example.gestionpracticasfinal.modelos.Alumno;
import com.example.gestionpracticasfinal.modelos.DatosTabla;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
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
    @FXML private ListView<String> lvCombCiclos;
    @FXML
    private Button btnExportar;
    @FXML
    private Label lbNoHayAlumnos;
    private TextField editor;


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
        inicializaCombCiclos();
    }

    private void configuraBusquedaComb(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            int selectedIndex = lvCombCiclos.getSelectionModel().getSelectedIndex();
            if (selectedIndex > 0) {
                lvCombCiclos.getSelectionModel().select(selectedIndex - 1);
                lvCombCiclos.scrollTo(selectedIndex - 1);
            }
        } else if (event.getCode() == KeyCode.DOWN) {
            int selectedIndex = lvCombCiclos.getSelectionModel().getSelectedIndex();
            if (selectedIndex < lvCombCiclos.getItems().size() - 1) {
                lvCombCiclos.getSelectionModel().select(selectedIndex + 1);
                lvCombCiclos.scrollTo(selectedIndex + 1);
            }
        } else if (event.getCode() == KeyCode.ENTER) {
            String selectedItem = lvCombCiclos.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                lvCombCiclos.getSelectionModel().select(0);
                selectedItem = lvCombCiclos.getSelectionModel().getSelectedItem();
            }
            if (selectedItem != null) {
                combCiclo.getSelectionModel().select(selectedItem);
                muestraTabla();
            }
            combCiclo.hide();
            lvCombCiclos.setVisible(false);
            btnExportar.toFront();
            btnExportar.requestFocus();
            lvCombCiclos.setItems(combCiclo.getItems());
        } else {
            // Filter the items based on user input
            String buscaC = editor.getText();
            lvCombCiclos.setItems(buscaCiclos(buscaC));
            combCiclo.hide();
            lvCombCiclos.setVisible(true);
            btnExportar.toBack();
        }
    }

    private void inicializaCombCiclos() {
        try {
            String[] ciclos = GestionPracticasBDController.consultaCiclosNombres();
            if (ciclos.length > 0) {
                combCiclo.setItems(FXCollections.observableArrayList(ciclos));
            } else {
                combCiclo.setItems(FXCollections.observableArrayList(
                        "No hay ciclos añadidos"));
            }
            lvCombCiclos.setItems(combCiclo.getItems());
        } catch (Exception e) {
            MensajeBoxController.alertError("Error al consultar",
                    "Hubo un error con la base de datos y no pudo realizarse la consulta de ciclos");
        }
        editor = combCiclo.getEditor();
        editor.setOnKeyReleased(this::configuraBusquedaComb);
        editor.setOnMouseClicked(event -> {
            lvCombCiclos.setVisible(true);
            btnExportar.toBack();
        });
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

    public void muestraTabla() {
        String ciclo = combCiclo.getValue();

        try {
            if (isValorEnCombCiclos(ciclo)) {
                if (!ciclo.equals("No hay ciclos añadidos") && !ciclo.equals("sin resultados")) {
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
                } else {
                    quitaTabla();
                }
            } else {
                quitaTabla();
            }
        } catch (Exception e) {
            MensajeBoxController.alertError("Error al consultar el ciclo",
                    "No se ha podido hacer la consulta de alumnos que se exportaran");
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

    public ObservableList<String> buscaCiclos(String buscaC) {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String[] ciclos;

        try {
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
        }catch (Exception e) {
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error con la base de datos y no se pudo consultar los ciclos");
        }

        return lista;
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
                    MensajeBoxController.alertInformation("CICLO EXPORTADO",
                            "El ciclo seleccionado fue exportado correctamente");
                    reiniciaSeleccion();
                } catch (Exception e) {
                    MensajeBoxController.alertError("ERROR AL EXPORTAR",
                            "Hubo un error al exportar el ciclo y no pudo crearse el fichero");
                }
            }
        } else {
            MensajeBoxController.alertWarning("CICLO NO VALIDO",
                    "El ciclo seleccionado no tiene alumnos para exportar a un fichero");
        }
    }

    public void onClickCombCiclos(MouseEvent event) {
        String buscaC = editor.getText();
        if (!isValorEnCombCiclos(buscaC)) {
            lvCombCiclos.setItems(buscaCiclos(buscaC));
        }
        combCiclo.hide();
        lvCombCiclos.setVisible(!lvCombCiclos.isVisible());
        if (lvCombCiclos.isVisible()) {
            btnExportar.toBack();
        } else {
            btnExportar.toFront();
            btnExportar.requestFocus();
        }
    }

    public void onClickListCiclos(MouseEvent event) {
        String selectedItem = lvCombCiclos.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            combCiclo.getSelectionModel().select(selectedItem);
            lvCombCiclos.setVisible(false);
            btnExportar.toFront();
            btnExportar.requestFocus();
            muestraTabla();
        }
    }

    private void reiniciaSeleccion() {
        combCiclo.setValue(null);
        lvCombCiclos.setItems(combCiclo.getItems());
        lvCombCiclos.setVisible(false);
        quitaTabla();
    }

    private void quitaTabla() {
        tabAlumnosExport.setVisible(false);
        lbNoHayAlumnos.setVisible(false);
        btnExportar.setDisable(true);
    }

    public void onClickUnselect(MouseEvent event) {
        btnExportar.requestFocus();
        lvCombCiclos.setVisible(false);
        btnExportar.toFront();
    }
}
