package com.example.gestionpracticasfinal.controladores;

import com.example.gestionpracticasfinal.MainApplication;
import com.example.gestionpracticasfinal.modelos.Contactable;
import com.example.gestionpracticasfinal.modelos.DatosTabla;
import com.example.gestionpracticasfinal.modelos.Empresa;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EmpresasViewController implements Initializable {
    @FXML private AnchorPane root;
    @FXML private TableView<DatosTabla> tabEmpresas;
    @FXML private TableColumn<DatosTabla, String> colCodigo;
    @FXML private TableColumn<DatosTabla, String> colNombre;
    @FXML private TableColumn<DatosTabla, String> colConsulta;
    @FXML private TableColumn<DatosTabla, String> colModifica;
    @FXML private TableColumn<DatosTabla, String> colElimina;
    @FXML private Label lbNoEmpresas;
    @FXML private TextField txtNombreEmpresa;
    @FXML private TextField txtContactoEmpresa;
    @FXML private TextField txtNumTel;
    @FXML private TextField txtEmailEmpresa;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtFiltrarNombre;
    @FXML private Label lbTituloAccion;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnTerminaConsulta;
    @FXML private CheckBox cbFiltrarNombre;
    private Empresa modificado = null;

    public void onClickChangeToAlumnos(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "alumnos-view.fxml"));
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializaTabla();
    }

    private void inicializaTabla() {
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
        muestraTodasEmpresasTabla();
        tablaVisible();
    }

    private void muestraTodasEmpresasTabla() {
        try {
            Empresa[] lista = GestionPracticasBDController.consultaEmpresas();
            tabEmpresas.setItems(getEmpresasTabla(lista));
        } catch (Exception e) {
            MensajeBoxController.alertError("ERROR DB",
                    "Hubo un error de conexion con la base de datos");
        }
    }

    public void tablaVisible() {
        boolean visible = tabEmpresas.getItems().size() > 0;
        tabEmpresas.setVisible(visible);
        lbNoEmpresas.setVisible(!visible);
    }

    public ObservableList<DatosTabla> getEmpresasTabla(Empresa[] lista) {
        tabEmpresas.getItems().clear();
        ObservableList<DatosTabla> output = FXCollections.observableArrayList();
        for (Empresa emp : lista) {
            output.add(new DatosTabla(emp, consultaEmpresa(emp), modificaEmpresa(emp), eliminaEmpresa(emp)));
        }
        return output;
    }

    public EventHandler<MouseEvent> consultaEmpresa(Empresa emp) {
        return event -> {
            lbTituloAccion.setText("Ver Empresa");
            btnTerminaConsulta.setVisible(true);
            btnGuardar.setVisible(false);
            btnCancelar.setVisible(false);
            txtDatosDisable(true);
            setTxtDatosEmpresa(emp);
        };
    }

    public EventHandler<MouseEvent> modificaEmpresa(Empresa emp) {
        return event -> {
            modificado = emp;
            lbTituloAccion.setText("Modificar Empresa");
            btnTerminaConsulta.setVisible(false);
            btnGuardar.setVisible(true);
            btnCancelar.setVisible(true);
            txtDatosDisable(false);
            setTxtDatosEmpresa(modificado);
        };
    }

    public EventHandler<MouseEvent> eliminaEmpresa(Empresa emp) {
        return event -> {
            ButtonType r = MensajeBoxController.alertConfirmar("ELIMINAR EMPRESA",
                    "Esta totalmente seguro de que desea eliminar la empresa " + emp.getNombre() + "?");
            if (r == ButtonType.OK)  {
                try {
                    GestionPracticasBDController.eliminaEmpresa(emp.getId());
                    muestraTodasEmpresasTabla();
                    tablaVisible();
                    if (modificado != null && modificado.equals(emp)) {
                        reiniciaDatosRellenar();
                    } else if (lbTituloAccion.getText().equals("Ver Empresa")) {
                        Empresa aux = new Empresa(0, txtNombreEmpresa.getText(), txtContactoEmpresa.getText(),
                                txtNumTel.getText(), txtEmailEmpresa.getText(), txtDireccion.getText());
                        if (aux.equals(emp)) {
                            reiniciaDatosRellenar();
                        } else {
                            quitaFiltros();
                        }
                    } else {
                        quitaFiltros();
                    }
                } catch (Exception e) {
                    MensajeBoxController.alertError("ERROR BD",
                            "Hubo un error con la base de datos al eliminar la empresa");
                }
            }
        };
    }

    public void onClickReinicia(MouseEvent event) {
        reiniciaDatosRellenar();
    }

    private void reiniciaDatosRellenar() {
        lbTituloAccion.setText("Añadir Empresa");
        vaciaTxtDatos();
        btnTerminaConsulta.setVisible(false);
        btnGuardar.setVisible(true);
        btnCancelar.setVisible(true);
        txtDatosDisable(false);
        quitaFiltros();
        modificado = null;
    }

    private void quitaFiltros() {
        cbFiltrarNombre.setSelected(false);
        txtFiltrarNombre.setText("");
    }

    public void setTxtDatosEmpresa(Empresa emp) {
        txtNombreEmpresa.setText(emp.getNombre());
        txtContactoEmpresa.setText(emp.getPersonaContacto());
        txtNumTel.setText(emp.getTelefono());
        txtEmailEmpresa.setText(emp.getEmail());
        txtDireccion.setText(emp.getDireccion());
    }

    public void txtDatosDisable(boolean v) {
        txtNombreEmpresa.setDisable(v);
        txtContactoEmpresa.setDisable(v);
        txtNumTel.setDisable(v);
        txtEmailEmpresa.setDisable(v);
        txtDireccion.setDisable(v);
    }

    public void onClickGuardaEmpresa() {
        Empresa emp = recogeDatosEmpresa();

        if (emp.isTodoRellenoMenosId()) {
            if (modificado == null) {
                try {
                    GestionPracticasBDController.insertarEmpresa(emp);
                    muestraTodasEmpresasTabla();
                    tablaVisible();
                    reiniciaDatosRellenar();
                }catch (Exception e) {
                    MensajeBoxController.alertWarning("EMPRESA REPETIDA",
                            "La empresa no fue añadida, el nombre ya existe en la lista de empresas");
                }
            } else {
                if (!modificado.equals(emp)) {
                    emp.setId(modificado.getId());
                    try {
                        GestionPracticasBDController.actualizaEmpresa(emp);
                        muestraTodasEmpresasTabla();
                    }catch (Exception e) {
                        MensajeBoxController.alertError("ERROR BD",
                                "Hubo un error con la base de datos al modificar la empresa");
                    }
                }
                reiniciaDatosRellenar();
            }
        } else {
            if (emp.isRellenoNomConDir() && !txtNumTel.getText().equals("") && !txtEmailEmpresa.getText().equals("")) {
                telefonoEmailCorrecto(emp);
            } else {
                MensajeBoxController.alertWarning("COMPLETE TODOS LOS CAMPOS",
                        "Faltan datos por rellenar para poder guardar la empresa");
            }

        }
    }

    public void telefonoEmailCorrecto(Empresa empresa) {
        if (!Contactable.telCorrecto(empresa.getTelefono())) {
            MensajeBoxController.alertWarning("TELEFONO NO VALIDO",
                    "Introduzca un número de telefono valido (9 numeros, pueden estar separados" +
                            " por espacios, guiones o puntos)");
        }
        if (!Contactable.emailCorrecto(empresa.getEmail())) {
            MensajeBoxController.alertWarning("CORREO NO VALIDO",
                    "El correo electronico debe tener 1 '@'");
        }
    }

    public void vaciaTxtDatos() {
        txtNombreEmpresa.setText("");
        txtContactoEmpresa.setText("");
        txtNumTel.setText("");
        txtEmailEmpresa.setText("");
        txtDireccion.setText("");
    }

    public Empresa recogeDatosEmpresa() {
        Empresa e = new Empresa();

        e.setNombre(txtNombreEmpresa.getText());
        e.setPersonaContacto(txtContactoEmpresa.getText());
        e.setTelefono(txtNumTel.getText());
        e.setEmail(txtEmailEmpresa.getText());
        e.setDireccion(txtDireccion.getText());

        return e;
    }

    public void onClickCheckFiltro(MouseEvent event) {
        filtraTabla();
        try {
            if (!cbFiltrarNombre.isSelected() &&
                    tabEmpresas.getItems().size() != GestionPracticasBDController.numEmpresas()) {
                muestraTodasEmpresasTabla();
            }
        }catch (Exception e) {
            MensajeBoxController.alertError("ERROR BD", "Hubo un error de conexion con la base de datos");
        }
    }

    public void onKeyReleasedFiltro(KeyEvent event) {
        filtraTabla();
    }

    private void filtraTabla() {
        try {
            if (cbFiltrarNombre.isSelected() &&
                    txtFiltrarNombre != null && !txtFiltrarNombre.getText().equals("")) {
                Empresa[] lista = GestionPracticasBDController.consultaBuscaEmpresas(
                        txtFiltrarNombre.getText());
                tabEmpresas.setItems(getEmpresasTabla(lista));
            } else if (cbFiltrarNombre.isSelected() && (txtFiltrarNombre == null
                    || txtFiltrarNombre.getText().equals(""))) {
                muestraTodasEmpresasTabla();
            }
        }catch (Exception e) {
            MensajeBoxController.alertError("ERROR DB", "Hubo un error con la base de datos" +
                    " al filtrar las empresas");
        }
    }
}
