package com.example.gestionpracticasfinal.controladores;

import com.example.gestionpracticasfinal.MainApplication;
import com.example.gestionpracticasfinal.modelos.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
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
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class AlumnosViewController implements Initializable {
    @FXML
    private AnchorPane root;
    @FXML
    private AnchorPane paneEmpresa;
    @FXML
    private AnchorPane panePracticas;
    @FXML
    private TableView<DatosTabla> tabAlumnos;
    @FXML
    private TableColumn<DatosTabla, String> colNombre;
    @FXML
    private TableColumn<DatosTabla, String> colApellidos;
    @FXML
    private TableColumn<DatosTabla, String> colCiclo;
    @FXML
    private TableColumn<DatosTabla, String> colElimina;
    @FXML
    private TableColumn<DatosTabla, String> colModifica;
    @FXML
    private TableColumn<DatosTabla, String> colConsulta;
    @FXML
    private TextField txtNombreAlumno;
    @FXML
    private TextField txtApellidosAlumno;
    @FXML
    private TextField txtTelefonoAlumno;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField hEntradaHoras;
    @FXML
    private TextField hEntradaMin;
    @FXML
    private TextField hSalidaH;
    @FXML
    private TextField hSalidaMin;
    @FXML
    private TextField txtFechaFin;
    @FXML
    private TextField txtEmailEmpresa;
    @FXML
    private TextField txtTelEmp;
    @FXML
    private TextField txtContactoEmp;
    @FXML
    private TextField txtDirEmp;
    @FXML
    private TextField txtFiltroApellidos;
    @FXML
    private TextField txtFiltroNombre;
    @FXML
    private DatePicker dtpFechaIni;
    @FXML
    private Label lbNoAlumnos;
    @FXML
    private Label lbAccionAlumno;
    @FXML
    private ComboBox<String> combCiclo;
    @FXML
    private ComboBox<String> combFiltroCiclo;
    @FXML
    private ComboBox<String> combEmpresa;
    @FXML
    private Button btnGeneraEmail;
    @FXML
    private Button btnGuardaAlumno;
    @FXML
    private Button btnCancelarAlumno;
    @FXML
    private Button btnVolverConsulta;
    @FXML
    private Button btnAnadePracticas;
    @FXML
    private Button btnCancelaPracticas;
    @FXML
    private Button btnConfirmarPracticas;
    @FXML
    private Button btnGeneraFfin;
    @FXML
    private CheckBox cbFiltroNombre;
    @FXML
    private CheckBox cbFiltroApellidos;
    @FXML
    private CheckBox cbFiltroCiclo;
    @FXML
    private ListView<String> lvCiclos;
    @FXML
    private ListView<String> lvEmpresas;
    @FXML
    private ListView<String> lvFiltroCiclo;
    private TextField editorCiclos;
    private TextField editorEmpresas;
    private TextField editorFiltro;
    private Alumno modificado = null;
    private Practica p = null;
    private Empresa empresaPracticas = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            GestionPracticasBDController.crearConexion();
            inicializaTabla();
            inicializaCombCiclo();
            inicializaCombEmpresas();
            inicializaCombFiltro();
        } catch (Exception e) {
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error de conexion con la base de datos y no se pudo inicializar la aplicación");
            Stage st = (Stage) root.getScene().getWindow();
            st.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
            st.close();
        }
        formatoHoras(hEntradaHoras);
        formatoMinutos(hEntradaMin);
        formatoHoras(hSalidaH);
        formatoMinutos(hSalidaMin);
    }

    private void inicializaCombFiltro() throws Exception {
        String[] lista = GestionPracticasBDController.consultaCiclosNombres();
        combFiltroCiclo.setItems(FXCollections.observableArrayList(lista));
        lvFiltroCiclo.setItems(combFiltroCiclo.getItems());
        editorFiltro = combFiltroCiclo.getEditor();
        editorFiltro.setOnKeyReleased(event -> {
            configuraBuscaEnCombobox(event, lvFiltroCiclo, combFiltroCiclo);
            if (event.getCode() != KeyCode.UP && event.getCode() != KeyCode.DOWN && event.getCode() != KeyCode.ENTER) {
                String busca = editorFiltro.getText();
                lvFiltroCiclo.setItems(buscaCiclos(busca));
            }else if (event.getCode() == KeyCode.ENTER) {
                getSeleccionLv(lvFiltroCiclo, combFiltroCiclo);
                filtraTabla();
                lvFiltroCiclo.setItems(combFiltroCiclo.getItems());
            }
        });
        editorFiltro.setOnMouseClicked(event -> {
            lvFiltroCiclo.setVisible(true);
            lvFiltroCiclo.toFront();
        });
        combFiltroCiclo.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !lvFiltroCiclo.isFocused()) {
                lvFiltroCiclo.toBack();
                lvFiltroCiclo.setVisible(false);
            }
        });
    }

    private void inicializaCombEmpresas() throws Exception {
        String[] lista = Contactable.contactableArrayNombres(GestionPracticasBDController.consultaEmpresas());
        combEmpresa.setItems(FXCollections.observableArrayList(lista));
        lvEmpresas.setItems(combEmpresa.getItems());
        editorEmpresas = combEmpresa.getEditor();
        editorEmpresas.setOnKeyReleased(event -> {
            configuraBuscaEnCombobox(event, lvEmpresas, combEmpresa);
            if (event.getCode() != KeyCode.UP && event.getCode() != KeyCode.DOWN && event.getCode() != KeyCode.ENTER) {
                String busca = editorEmpresas.getText();
                lvEmpresas.setItems(buscaEmpresas(busca));
            }
            if (event.getCode() == KeyCode.ENTER) {
                String selectedItem = getSeleccionLv(lvEmpresas, combEmpresa);
                muestraEmpresa(selectedItem);
                lvEmpresas.setItems(combEmpresa.getItems());
            }
        });
        editorEmpresas.setOnMouseClicked(event -> {
            lvEmpresas.setVisible(true);
            lvEmpresas.toFront();
        });
        combEmpresa.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !lvEmpresas.isFocused()) {
                lvEmpresas.toBack();
                lvEmpresas.setVisible(false);
            }
        });
    }

    private void formatoHoras(TextField numTextField) {
        String regex = "^(0?[0-9]|1[0-9]|2[0-3])?$";
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches(regex)) {
                return change;
            } else {
                return null;
            }
        });
        numTextField.setTextFormatter(formatter);
    }

    private void formatoMinutos(TextField numTextField) {
        String regex = "^(0?[0-9]|[1-5][0-9])?$";
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches(regex)) {
                return change;
            } else {
                return null;
            }
        });
        numTextField.setTextFormatter(formatter);
    }

    private void inicializaCombCiclo() throws Exception {
        String[] ciclos = GestionPracticasBDController.consultaCiclosNombres();
        combCiclo.setItems(FXCollections.observableArrayList(ciclos));
        combCiclo.getItems().add(0, "+ Alta Nuevo Ciclo");
        lvCiclos.setItems(combCiclo.getItems());
        editorCiclos = combCiclo.getEditor();
        editorCiclos.setOnKeyReleased(event -> {
            configuraBuscaEnCombobox(event, lvCiclos, combCiclo);
            if (event.getCode() != KeyCode.UP && event.getCode() != KeyCode.DOWN && event.getCode() != KeyCode.ENTER) {
                String busca = editorCiclos.getText();
                lvCiclos.setItems(buscaCiclos(busca));
                agregaOpcionAltaCiclo();
            }else if (event.getCode() == KeyCode.ENTER) {
                getSeleccionLv(lvCiclos, combCiclo);
                altaCiclo();
                lvCiclos.setItems(combCiclo.getItems());
            }
        });
        editorCiclos.setOnMouseClicked(event -> {
            lvCiclos.setVisible(true);
            lvCiclos.toFront();
        });
        combCiclo.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !lvCiclos.isFocused()) {
                lvCiclos.toBack();
                lvCiclos.setVisible(false);
            }
        });
    }

    private void agregaOpcionAltaCiclo() {
        if (lvCiclos.getItems().size() == combCiclo.getItems().size() - 1) {
            lvCiclos.getItems().add(0, "+ Alta Nuevo Ciclo");
        } else {
            lvCiclos.getItems().add("+ Alta Nuevo Ciclo");
        }
    }

    private void configuraBuscaEnCombobox(KeyEvent event, ListView<String> lv, ComboBox<String> comb) {
        if (event.getCode() == KeyCode.UP) {
            int selectedIndex = lv.getSelectionModel().getSelectedIndex();
            if (selectedIndex > 0) {
                lv.getSelectionModel().select(selectedIndex - 1);
                lv.scrollTo(selectedIndex - 1);
            }
        } else if (event.getCode() == KeyCode.DOWN) {
            int selectedIndex = lv.getSelectionModel().getSelectedIndex();
            if (selectedIndex < lv.getItems().size() - 1) {
                lv.getSelectionModel().select(selectedIndex + 1);
                lv.scrollTo(selectedIndex + 1);
            }
        } else if (event.getCode() == KeyCode.ENTER) {
            root.requestFocus();
            comb.hide();
            lv.setVisible(false);
            lv.toBack();
        } else {
            comb.hide();
            lv.setVisible(true);
            lv.toFront();
        }
    }

    private static String getSeleccionLv(ListView<String> lv, ComboBox<String> comb) {
        String selectedItem = lv.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            lv.getSelectionModel().select(0);
            selectedItem = lv.getSelectionModel().getSelectedItem();
        }
        if (selectedItem != null) {
            comb.getSelectionModel().select(selectedItem);
        }
        return selectedItem;
    }

    private void muestraEmpresa(String n) {
        paneEmpresa.setVisible(true);
        try {
            empresaPracticas = GestionPracticasBDController.consultaEmpresa(n);
            rellenaDatosEmpresa(empresaPracticas);
        } catch (Exception e) {
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error de conexion con la base de datos y no se pudo consultar la empresa");
        }
    }

    private ObservableList<String> buscaEmpresas(String busca) {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String[] empresas;

        try {
            if (GestionPracticasBDController.numEmpresas() > 0) {
                empresas = Contactable.contactableArrayNombres(GestionPracticasBDController.consultaBuscaEmpresas(busca));
                if (empresas.length > 0) {
                    lista.setAll(empresas);
                }
            }
        } catch (Exception e) {
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error con la base de datos y no se pudo consultar las empresas");
        }

        return lista;
    }

    private void altaCiclo() {
        if (combCiclo.getValue().equals("+ Alta Nuevo Ciclo")) {
            Optional<String> r = MensajeBoxController.inputTexto("ALTA CICLO",
                    "Introduce el nombre del nuevo ciclo que quieres dar de alta");
            if (r.isPresent() && !r.get().equals("")) {
                try {
                    if (GestionPracticasBDController.altaCiclo(r.get())) {
                        MensajeBoxController.alertInformation("ALTA CICLO",
                                "El ciclo introducido fue añadido correctamente");
                    } else {
                        MensajeBoxController.alertWarning("CICLO NO AÑADIDO",
                                "El ciclo introducido ya existe");
                    }
                } catch (Exception e) {
                    MensajeBoxController.alertError("ERROR CONEXION BD",
                            "Hubo un error al conectar con la base de datos y no se añadio" +
                                    " el ciclo");
                }
            } else if (r.isPresent()) {
                MensajeBoxController.alertWarning("CICLO NO AÑADIDO",
                        "Para añadir un ciclo debe introducir un nombre");
            }
            combCiclo.setValue("");
            lvCiclos.setItems(buscaCiclos(""));
            lvCiclos.getItems().add(0, "+ Alta Nuevo Ciclo");
            lvFiltroCiclo.setItems(buscaCiclos(""));
            combCiclo.setItems(lvCiclos.getItems());
            combFiltroCiclo.setItems(lvFiltroCiclo.getItems());
        }
    }

    private ObservableList<String> buscaCiclos(String buscaC) {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String[] ciclos;

        try {
            if (GestionPracticasBDController.numCiclos() > 0) {
                ciclos = GestionPracticasBDController.buscaCiclosNombre(buscaC);
                if (ciclos.length > 0) {
                    lista.setAll(ciclos);
                }
            }
        } catch (Exception e) {
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error con la base de datos y no se pudo consultar los ciclos");
        }

        return lista;
    }

    public void onClickCombCiclos(MouseEvent event) {
        String buscaC = editorCiclos.getText();
        if (!isValorEnComb(buscaC, combCiclo)) {
            lvCiclos.setItems(buscaCiclos(buscaC));
            agregaOpcionAltaCiclo();
        }
        combCiclo.hide();
        lvCiclos.setVisible(!lvCiclos.isVisible());
        if (lvCiclos.isVisible()) {
            lvCiclos.toFront();
        } else {
            lvCiclos.toBack();
        }
    }

    private boolean isValorEnComb(String st, ComboBox<String> comb) {
        for (String res : comb.getItems()) {
            if (st != null && st.equalsIgnoreCase(res)) {
                return true;
            }
        }
        return false;
    }

    public void onClickListCiclos(MouseEvent event) {
        String selectedItem = lvCiclos.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            combCiclo.getSelectionModel().select(selectedItem);
            lvCiclos.setVisible(false);
            btnGeneraEmail.toFront();
            altaCiclo();
            root.requestFocus();
        }
    }

    public void onClickCombEmpresas(MouseEvent event) {
        String busca = editorEmpresas.getText();
        if (!isValorEnComb(busca, combEmpresa)) {
            lvEmpresas.setItems(buscaEmpresas(busca));
        }
        combEmpresa.hide();
        lvEmpresas.setVisible(!lvEmpresas.isVisible());
        if (lvEmpresas.isVisible()) {
            lvEmpresas.toFront();
        } else {
            lvEmpresas.toBack();
        }
    }

    public void onClickListEmpresas(MouseEvent event) {
        String selectedItem = lvEmpresas.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            combEmpresa.getSelectionModel().select(selectedItem);
            lvEmpresas.setVisible(false);
            paneEmpresa.toFront();
            muestraEmpresa(selectedItem);
            root.requestFocus();
        }
    }

    public void onClickCombFiltros(MouseEvent event) {
        String busca = editorFiltro.getText();
        if (!isValorEnComb(busca, combFiltroCiclo)) {
            lvFiltroCiclo.setItems(buscaEmpresas(busca));
        }
        combFiltroCiclo.hide();
        lvFiltroCiclo.setVisible(!lvFiltroCiclo.isVisible());
        if (lvFiltroCiclo.isVisible()) {
            lvFiltroCiclo.toFront();
        } else {
            lvFiltroCiclo.toBack();
        }
    }

    public void onClickListFiltros(MouseEvent event) {
        String selectedItem = lvFiltroCiclo.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            combFiltroCiclo.getSelectionModel().select(selectedItem);
            lvFiltroCiclo.setVisible(false);
            lvFiltroCiclo.toBack();
            filtraTabla();
            root.requestFocus();
        }
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
        muestraTodosAlumnosTabla();
        tablaVisible();
    }

    private void muestraTodosAlumnosTabla() {
        try {
            Alumno[] lista = GestionPracticasBDController.consultaAlumnos();
            tabAlumnos.setItems(getAlumnos(lista));
            tablaVisible();
        } catch (Exception e) {
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error de conexion con la base de datos y no se pudo consultar los alumnos");
        }
    }

    private void tablaVisible() {
        boolean visible = tabAlumnos.getItems().size() > 0;
        tabAlumnos.setVisible(visible);
        lbNoAlumnos.setVisible(!visible);
    }

    public ObservableList<DatosTabla> getAlumnos(Alumno[] lista) {
        tabAlumnos.getItems().clear();
        ObservableList<DatosTabla> output = FXCollections.observableArrayList();
        for (Alumno a : lista) {
            output.add(new DatosTabla(a, onClickBtnConsulta(a), onClickBtnModifica(a), onClickBtnElimina(a)));
        }
        return output;
    }

    public EventHandler<MouseEvent> onClickBtnConsulta(Alumno a) {
        return event -> {
            lbAccionAlumno.setText("Ver Alumno");
            rellenaDatosAlumno(a);
            modoConsultar(true);
        };
    }

    private void modoConsultar(boolean b) {
        txtNombreAlumno.setDisable(b);
        txtApellidosAlumno.setDisable(b);
        txtTelefonoAlumno.setDisable(b);
        txtEmail.setDisable(b);
        combCiclo.setDisable(b);
        btnGeneraEmail.setVisible(!b);
        btnCancelarAlumno.setVisible(!b);
        btnGuardaAlumno.setVisible(!b);
        btnVolverConsulta.setVisible(b);
        hEntradaHoras.setDisable(b);
        hEntradaMin.setDisable(b);
        hSalidaH.setDisable(b);
        hSalidaMin.setDisable(b);
        dtpFechaIni.setDisable(b);
        combEmpresa.setDisable(b);
        btnCancelaPracticas.setVisible(!b);
        btnConfirmarPracticas.setVisible(!b);
        btnAnadePracticas.setVisible(!b);
        btnGeneraFfin.setVisible(!b);
        txtFechaFin.setDisable(b);
    }

    public EventHandler<MouseEvent> onClickBtnModifica(Alumno a) {
        return event -> {
            btnCancelaPracticas.setText("Eliminar");
            lbAccionAlumno.setText("Modificar Alumno");
            modoConsultar(false);
            rellenaDatosAlumno(a);
            modificado = a;
        };
    }

    private void rellenaDatosAlumno(Alumno a) {
        txtNombreAlumno.setText(a.getNombre());
        txtApellidosAlumno.setText(a.getApellidos());
        txtTelefonoAlumno.setText(a.getTelefono());
        txtEmail.setText(a.getEmail());
        try {
            combCiclo.setValue(GestionPracticasBDController.consultaNombreCiclo(a.getIdCiclo()));
            p = GestionPracticasBDController.consultaPractica(a.getId());
            if (p != null) {
                rellenaPracticas();
            } else {
                reiniciaRellenarPracticas();
            }
        } catch (Exception e) {
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error al conectar con la base de datos y no se pudo" +
                            " consultar la practica del alumno");
        }
    }

    private void rellenaPracticas() throws Exception {
        empresaPracticas = GestionPracticasBDController.consultaEmpresa(p.getId_empresa());
        panePracticas.setVisible(true);
        paneEmpresa.setVisible(true);
        btnAnadePracticas.setVisible(false);
        hEntradaHoras.setText(String.valueOf(p.gethEntrada().getHour()));
        hEntradaMin.setText(String.valueOf(p.gethEntrada().getMinute()));
        hSalidaH.setText(String.valueOf(p.gethSalida().getHour()));
        hSalidaMin.setText(String.valueOf(p.gethSalida().getMinute()));
        dtpFechaIni.setValue(p.getfInicio());
        txtFechaFin.setText(p.getfFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        combEmpresa.getSelectionModel().select(empresaPracticas.getNombre());
        rellenaDatosEmpresa(empresaPracticas);
    }

    public EventHandler<MouseEvent> onClickBtnElimina(Alumno a) {
        return event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("ELIMINAR ALUMNO");
            alert.setContentText("Esta totalmente seguro de que desea eliminar al alumno " + a.getNombre() + " " +
                    a.getApellidos() + "?");
            alert.setHeaderText(null);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                try {
                    GestionPracticasBDController.eliminaAlumno(a.getId());
                    muestraTodosAlumnosTabla();
                    tablaVisible();
                    //hacer luego lo mismo que en empresas controler
                    reiniciaRellenarAlumno();
                } catch (Exception e) {
                    MensajeBoxController.alertError("ERROR CONEXION BD",
                            "Hubo un error de conexion con la base de datos y el alumno no fue eliminado");
                }
            }
        };
    }

    public void onClickRoot(MouseEvent event) {
        root.requestFocus();
    }

    private void reiniciaRellenarAlumno() {
        reiniciaFiltros();
        lbAccionAlumno.setText("Añadir Alumno");
        txtNombreAlumno.setText("");
        txtApellidosAlumno.setText("");
        txtTelefonoAlumno.setText("");
        combCiclo.setValue("");
        txtEmail.setText("");
        btnGeneraEmail.setVisible(true);
        btnGuardaAlumno.setVisible(true);
        btnCancelarAlumno.setVisible(true);
        btnVolverConsulta.setVisible(false);
        reiniciaRellenarPracticas();
        modificado = null;
        modoConsultar(false);
    }

    private void reiniciaRellenarPracticas() {
        btnAnadePracticas.setVisible(true);
        btnCancelaPracticas.setText("Cancelar");
        hEntradaHoras.setText("");
        hEntradaMin.setText("");
        hSalidaH.setText("");
        hSalidaMin.setText("");
        dtpFechaIni.setValue(null);
        txtFechaFin.setText("");
        combEmpresa.setValue("");
        btnCancelaPracticas.setVisible(true);
        btnConfirmarPracticas.setVisible(true);
        empresaPracticas = null;
        rellenaDatosEmpresa(null);
        panePracticas.setVisible(false);
        p = null;
    }

    private void rellenaDatosEmpresa(Empresa emp) {
        if (emp == null) {
            emp = new Empresa();
            paneEmpresa.setVisible(false);
        }
        txtEmailEmpresa.setText(emp.getEmail());
        txtTelEmp.setText(emp.getTelefono());
        txtContactoEmp.setText(emp.getPersonaContacto());
        txtDirEmp.setText(emp.getDireccion());
    }

    private void reiniciaFiltros() {
        cbFiltroNombre.setSelected(false);
        cbFiltroApellidos.setSelected(false);
        cbFiltroCiclo.setSelected(false);
        txtFiltroNombre.setText("");
        txtFiltroApellidos.setText("");
        combFiltroCiclo.setValue("");
    }

    public void onClickChangeToEmpresas(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "empresas-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }

    public void onClickChangeToExportCiclo(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "exportciclo-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }

    public void onClickGenerarEmail(MouseEvent event) {
        if (txtNombreAlumno.getText() != null && !txtNombreAlumno.getText().equals("") &&
                txtApellidosAlumno.getText() != null && !txtApellidosAlumno.getText().equals("") &&
                txtTelefonoAlumno.getText() != null && !txtTelefonoAlumno.getText().equals("")) {
            if (telCorrecto()) {
                Alumno a = new Alumno();
                a.setNombre(txtNombreAlumno.getText());
                a.setApellidos(txtApellidosAlumno.getText());
                a.setTelefono(txtTelefonoAlumno.getText());

                try {
                    txtEmail.setText(GestionPracticasBDController.generaEmail(a));
                } catch (Exception e) {
                    MensajeBoxController.alertError("ERROR CONEXION BD",
                            "Hubo un error al conectar con la base de datos y no se pudo generar el email");
                }
            }
        } else {
            MensajeBoxController.alertInformation("FALTAN CAMPOS",
                    "Debes rellenar nombre, apellidos y telefono para poder generar un email");
        }
    }

    public boolean telCorrecto() {
        if (!Contactable.telCorrecto(txtTelefonoAlumno.getText())) {
            MensajeBoxController.alertWarning("DATOS INCORRECTOS",
                    "Introduce un numero de telefono valido (9 numeros, pueden estar separados" +
                            " por espacios, guiones o puntos))");
            return false;
        }
        return true;
    }

    public boolean mailCorrecto() throws Exception {
        if (!Contactable.emailCorrecto(txtEmail.getText())) {
            MensajeBoxController.alertWarning("DATOS INCORRECTOS",
                    "Introduce un email valido, debe tener 1 '@'");
            return false;
        } else if (GestionPracticasBDController.existeCorreo(txtEmail.getText()) && modificado == null) {
            MensajeBoxController.alertWarning("EMAIL NO VALIDO",
                    "El email introducido ya pertenece a otro alumno");
            return false;
        }
        return true;
    }

    public void onClickCancelaTodo() {
        reiniciaRellenarAlumno();
    }

    public void onClickGuardarAlumnoPracticas() {
        if (txtFechaFin.getText() == null || txtFechaFin.getText().equals("")) {
            fechaFinPracticas();
        }
        if (txtFechaFin.getText() != null && !txtFechaFin.getText().equals("") && empresaPracticas != null &&
                todoRellenoAlumno()) {
            if (p != null) {
                if (modificado == null) {
                    insertaAlumno(false);
                    try {
                        int id_alumno = GestionPracticasBDController.ultimaId();
                        p.setId_alumno(id_alumno);
                        p.setId_empresa(empresaPracticas.getId());
                        GestionPracticasBDController.insertarPracticas(p);
                        reiniciaRellenarAlumno();
                    } catch (Exception e) {
                        MensajeBoxController.alertError("ERROR CONEXION BD",
                                "Hubo un error de conexion con la base de datos y no se pudo modificar las " +
                                        "practicas");
                    }
                } else {
                    modificaAlumno(false);
                    try {
                        p.setId_alumno(modificado.getId());
                        p.setId_empresa(empresaPracticas.getId());
                        if (GestionPracticasBDController.consultaPractica(p.getId_alumno()) == null) {
                            GestionPracticasBDController.insertarPracticas(p);
                        } else {
                            GestionPracticasBDController.actualizaPractica(p);
                        }
                        reiniciaRellenarAlumno();
                    }catch (Exception e) {
                        MensajeBoxController.alertError("ERROR CONEXION BD",
                                "Hubo un error de conexion con la base de datos y no se pudo guardar" +
                                        " la practicas");
                    }
                }
            }
        } else if (txtFechaFin.getText() != null && !txtFechaFin.getText().equals("") && !todoRellenoAlumno()) {
            MensajeBoxController.alertWarning("ALUMNO NO VALIDO",
                    "Debes rellenar todos los campos para poder guardar el alumno y sus practicas");
        }
    }

    public void onClickGuardarAlumno(MouseEvent event) {
        if (todoRellenoAlumno()) {
            if (modificado == null) {
                if (!panePracticas.isVisible() || confirmarGuardaSoloAlumno()) {
                    insertaAlumno(true);
                }
            } else {
                if (!panePracticas.isVisible() || confirmarGuardaSoloAlumno()) {
                    modificaAlumno(true);
                }
            }
        } else {
            MensajeBoxController.alertWarning("ALUMNO NO GUARDADO",
                    "Debes rellenar todos los campos para poder guardar el alumno");
        }
    }

    private void modificaAlumno(boolean aux) {
        try {
            if (telCorrecto() && mailCorrecto()) {
                recogeDatosAlumno(modificado);
                if (GestionPracticasBDController.actualizaAlumno(modificado)) {
                    muestraTodosAlumnosTabla();
                } else {
                    MensajeBoxController.alertWarning("ALUMNO NO MODIFICADO",
                            "El alumno tiene datos repetidos con otro alumno, asi que no se " +
                                    "modificaron sus datos");
                }
                if (aux) {
                    reiniciaRellenarAlumno();
                }
            }
        } catch (Exception e) {
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error con la base de datos y no se modifico el alumno");
        }
    }

    private boolean confirmarGuardaSoloAlumno() {
        ButtonType r = MensajeBoxController.alertConfirmar("GUARDAR ALUMNO",
                "Se guardara unicamente el alumno, no sus practicas" +
                        "¿Esta seguro de que desea guardar solamente el alumno?");

        return r == ButtonType.OK;
    }

    private void insertaAlumno(boolean reinicia) {
        try {
            if (telCorrecto() && mailCorrecto()) {
                Alumno a = new Alumno();
                recogeDatosAlumno(a);
                if (GestionPracticasBDController.insertarAlumno(a)) {
                    muestraTodosAlumnosTabla();
                } else {
                    MensajeBoxController.alertWarning("ALUMNO NO INSERTADO",
                            "El alumno tiene datos repetidos con otro alumno, asi que no se inserto");
                }
                if (reinicia) {
                    reiniciaRellenarAlumno();
                }
            }
        } catch (Exception e) {
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error de conexion con la base de datos y no se pudo añadir el alumno");
        }
    }

    private void recogeDatosAlumno(Alumno a) throws Exception {
        a.setNombre(txtNombreAlumno.getText());
        a.setApellidos(txtApellidosAlumno.getText());
        a.setTelefono(txtTelefonoAlumno.getText());
        a.setEmail(txtEmail.getText());
        a.setIdCiclo(GestionPracticasBDController.consultaIdCiclo(combCiclo.getValue()));
    }

    private boolean todoRellenoAlumno() {
        return txtNombreAlumno.getText() != null && !txtNombreAlumno.getText().equals("") &&
                txtApellidosAlumno.getText() != null && !txtApellidosAlumno.getText().equals("") &&
                txtTelefonoAlumno.getText() != null && !txtTelefonoAlumno.getText().equals("") &&
                txtEmail.getText() != null && !txtEmail.getText().equals("") &&
                isValorEnComb(combCiclo.getValue(), combCiclo);
    }

    public void onClickAnadePracticas(MouseEvent event) {
        if (combEmpresa.getItems().size() > 0) {
            btnAnadePracticas.setVisible(false);
            panePracticas.setVisible(true);
        } else {
            MensajeBoxController.alertWarning("SIN EMPRESAS",
                    "Para poder añadir practicas debes tener al menos 1 empresa añadida");
        }
    }

    public void onKeyReleasedHEntrada(KeyEvent event) {
        if (event.getCode() != KeyCode.RIGHT && event.getCode() != KeyCode.LEFT) {
            if (hEntradaHoras.getText().length() == 2) {
                hEntradaMin.requestFocus();
            }
        }
    }

    public void onKeyReleasedMinEntrada(KeyEvent event) {
        if (event.getCode() != KeyCode.RIGHT && event.getCode() != KeyCode.LEFT) {
            if (hEntradaMin.getText().length() == 2) {
                hSalidaH.requestFocus();
            }
        }
    }

    public void onKeyReleasedHSalida(KeyEvent event) {
        if (event.getCode() != KeyCode.RIGHT && event.getCode() != KeyCode.LEFT) {
            if (hSalidaH.getText().length() == 2) {
                hSalidaMin.requestFocus();
            }
        }
    }

    public void fechaFinPracticas() {
        if (isFechaHorariosRellenos()) {
            try {
                p = new Practica();
                p.sethEntrada(LocalTime.of(Integer.parseInt(hEntradaHoras.getText()),
                        Integer.parseInt(hEntradaMin.getText())));
                p.sethSalida(LocalTime.of(Integer.parseInt(hSalidaH.getText()),
                        Integer.parseInt(hSalidaMin.getText())));
                p.setfInicio(dtpFechaIni.getValue());
                p.calculaFfin();
                txtFechaFin.setText(p.getfFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } catch (Exception e) {
                MensajeBoxController.alertWarning("DATOS INCORRECTOS",
                        e.getMessage());
                txtFechaFin.setText("");
                p = null;
            }
        } else {
            MensajeBoxController.alertWarning("COMPLETA TODOS LOS CAMPOS",
                    "Debes rellenar los datos necesarios para las practicas");
        }
    }

    private boolean isFechaHorariosRellenos() {
        return hEntradaHoras.getText() != null && !hEntradaHoras.getText().equals("") &&
                hEntradaMin.getText() != null && !hEntradaMin.getText().equals("") &&
                hSalidaH.getText() != null && !hSalidaH.getText().equals("") &&
                hSalidaMin.getText() != null && !hSalidaMin.getText().equals("") &&
                dtpFechaIni.getValue() != null;
    }

    public void onClickGenerarFecha(MouseEvent event) {
        fechaFinPracticas();
    }

    public void onClickCancelaPracticas(MouseEvent event) {
        if (modificado == null || btnCancelaPracticas.getText().equals("Cancelar")) {
            reiniciaRellenarPracticas();
        } else {
            ButtonType r = MensajeBoxController.alertConfirmar("ELIMINAR PRACTICAS",
                    "Esta seguro de que desea eliminar las practicas del alumno " + modificado.getNombre() + "?");
            if (r == ButtonType.OK) {
                try {
                    GestionPracticasBDController.eliminaPractica(modificado.getId());
                    reiniciaRellenarPracticas();
                } catch (Exception e) {
                    MensajeBoxController.alertError("ERROR CONEXION BD",
                            "Hubo un error con la base de datos y no pudo eliminarse la practica");
                }
            }
        }
    }

    public void onClickTerminaConsulta(MouseEvent event) {
        reiniciaRellenarAlumno();
    }

    private void filtraTabla() {
        String buscaNom, buscaApe, buscaCiclo;
        buscaNom = cbFiltroNombre.isSelected() && txtFiltroNombre != null &&
                !txtFiltroNombre.getText().equals("") ? txtFiltroNombre.getText() : null;
        buscaApe = cbFiltroApellidos.isSelected() && txtFiltroApellidos != null &&
                !txtFiltroApellidos.getText().equals("") ? txtFiltroApellidos.getText() : null;
        buscaCiclo = cbFiltroCiclo.isSelected() && combFiltroCiclo.getValue() != null &&
                !combFiltroCiclo.getValue().equals("") ? combFiltroCiclo.getValue() : null;

        try {
            if (buscaNom != null || buscaApe != null || buscaCiclo != null) {
                Alumno[] filtrados = GestionPracticasBDController.consultaBuscaAlumnos(buscaNom, buscaApe, buscaCiclo);
                tabAlumnos.setItems(getAlumnos(filtrados));
            } else if (cbFiltroNombre.isSelected() || cbFiltroApellidos.isSelected() || cbFiltroCiclo.isSelected()) {
                muestraTodosAlumnosTabla();
            } else {
                muestraTodosAlumnosTabla();
            }
        }catch (Exception e) {
            MensajeBoxController.alertError("ERROR DB", "Hubo un error con la base de datos" +
                    " al filtrar las empresas");
        }
    }

    public void onSelectCbFiltro(Event event) {
        filtraTabla();
    }

    public void onKeyReleasedFiltro(KeyEvent event) {
        filtraTabla();
    }
}