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
    //objetos traidos de fxml de empresas-view
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
    //declaracion de empresa modificada
    private Empresa modificado = null;

    //al hacer click en Gestionar alumnos cambia de escena
    public void onClickChangeToAlumnos(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "alumnos-view.fxml"));
        Scene scene =new Scene(loader.load());
        stage.setScene(scene);
    }

    //al hacer click en exportar ciclos cambia de ciclos
    public void onClickChangeToExportCiclo(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "exportciclo-view.fxml"));
        Scene scene =new Scene(loader.load());
        stage.setScene(scene);
    }

    //metodo inicializacion que solamente inicializa la tabla de empresas
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializaTabla();
    }

    //metodo que asigna los valores de las columnas de la tabla a los atributos de DatosTabla
    private void inicializaTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("col1"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("col2"));
        colConsulta.setCellValueFactory(new PropertyValueFactory<>("btnConsultar"));
        colModifica.setCellValueFactory(new PropertyValueFactory<>("btnModificar"));
        colElimina.setCellValueFactory(new PropertyValueFactory<>("btnEliminar"));
        //se impide que las columnas puedan cambiarse de posicion
        colCodigo.setReorderable(false);
        colNombre.setReorderable(false);
        colConsulta.setReorderable(false);
        colModifica.setReorderable(false);
        colElimina.setReorderable(false);
        //se muestran todas las empresas y se determina si la tabla sera visible
        muestraTodasEmpresasTabla();
        tablaVisible();
    }

    //metodo que consulta en la bd todas las empresas que hay y las coloca en la tabla con el metodo getEmpresasTabla
    //pasandole la lista
    private void muestraTodasEmpresasTabla() {
        try {
            Empresa[] lista = GestionPracticasBDController.consultaEmpresas();
            tabEmpresas.setItems(getEmpresasTabla(lista));
        } catch (Exception e) {
            //ante cualquier error se notifica
            MensajeBoxController.alertError("ERROR DB",
                    "Hubo un error de conexion con la base de datos");
        }
    }

    //metodo que solo muestra la tabla si hay empresas en la bd
    public void tablaVisible() {
        boolean visible = tabEmpresas.getItems().size() > 0;
        tabEmpresas.setVisible(visible);
        lbNoEmpresas.setVisible(!visible);
    }

    //metodo que convierte una lista de empresas en un observable list de datos tabla
    public ObservableList<DatosTabla> getEmpresasTabla(Empresa[] lista) {
        tabEmpresas.getItems().clear();
        ObservableList<DatosTabla> output = FXCollections.observableArrayList();
        for (Empresa emp : lista) {
            //se usa el constructor de empresas de datos tabla pasando los eventos de cada boton
            output.add(new DatosTabla(emp, consultaEmpresa(emp), modificaEmpresa(emp), eliminaEmpresa(emp)));
        }
        return output;
    }

    //evento de boton consulta
    public EventHandler<MouseEvent> consultaEmpresa(Empresa emp) {
        return event -> {
            //pone ver empresa en la accion actual, hace invisibles los botones guardar y cancelar y muestra
            //un boton para dejar de consultar
            lbTituloAccion.setText("Ver Empresa");
            btnTerminaConsulta.setVisible(true);
            btnGuardar.setVisible(false);
            btnCancelar.setVisible(false);
            //deshabilita todos los TextField
            txtDatosDisable(true);
            //rellena todos los TextField con loos datos de la empresa pasada
            setTxtDatosEmpresa(emp);
        };
    }

    //evento para modifcar una empresa
    public EventHandler<MouseEvent> modificaEmpresa(Empresa emp) {
        return event -> {
            //asigna a modificado la empresa pasada
            modificado = emp;
            //pone la accion como modificar, quita terminar consulta de la vista y poner guardar y cancelar
            lbTituloAccion.setText("Modificar Empresa");
            btnTerminaConsulta.setVisible(false);
            btnGuardar.setVisible(true);
            btnCancelar.setVisible(true);
            //habilita los TextField
            txtDatosDisable(false);
            //coloca los datos de la empresa a modificar en los TextField
            setTxtDatosEmpresa(modificado);
        };
    }

    //boton eliminar empresa
    public EventHandler<MouseEvent> eliminaEmpresa(Empresa emp) {
        return event -> {
            //pregunta si esta seguro de eliminar
            ButtonType r = MensajeBoxController.alertConfirmar("ELIMINAR EMPRESA",
                    "Esta totalmente seguro de que desea eliminar la empresa " + emp.getNombre() + "?");
            if (r == ButtonType.OK)  {
                try {
                    //en caso de estarlo trata de eliminar la empresa de la base de datos pasando su id
                    GestionPracticasBDController.eliminaEmpresa(emp.getId());
                    //muestra nuevamente todas las empresas y determina si es visible la tabla
                    muestraTodasEmpresasTabla();
                    tablaVisible();
                    //en caso de que la empresa eliminada no fuese la que estuviese consultandose o modificandose
                    //no se reinician los TextField, si lo era, se reinician
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
                    //ante cualquier error notifica
                    MensajeBoxController.alertError("ERROR BD",
                            "Hubo un error con la base de datos al eliminar la empresa");
                }
            }
        };
    }

    //al pulsar el boton cancelar o terminar consulta se reinician los datos a rellenar
    public void onClickReinicia(MouseEvent event) {
        reiniciaDatosRellenar();
    }

    //metodo que reinicia los datos a rellenar, quita los filtros y pone modificad a null
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

    //metodo que deselecciona el cb de filtrado y vacia el Textfield
    private void quitaFiltros() {
        cbFiltrarNombre.setSelected(false);
        txtFiltrarNombre.setText("");
    }

    //metodo que pasada una empresa rellena los campos de datos con sus datos
    public void setTxtDatosEmpresa(Empresa emp) {
        txtNombreEmpresa.setText(emp.getNombre());
        txtContactoEmpresa.setText(emp.getPersonaContacto());
        txtNumTel.setText(emp.getTelefono());
        txtEmailEmpresa.setText(emp.getEmail());
        txtDireccion.setText(emp.getDireccion());
    }

    //metodo que deshabilita los datos para que no se puedan editar al consultar
    public void txtDatosDisable(boolean v) {
        txtNombreEmpresa.setDisable(v);
        txtContactoEmpresa.setDisable(v);
        txtNumTel.setDisable(v);
        txtEmailEmpresa.setDisable(v);
        txtDireccion.setDisable(v);
    }

    //metodo para guardar una empresa
    public void onClickGuardaEmpresa() {
        //se recogen los datos de la empresa
        Empresa emp = recogeDatosEmpresa();

        //se comprueba si se rellenaron todos los datos
        if (emp.isTodoRellenoMenosId()) {
            //si modificado es null se insertara la nueva empresa en la base de datos
            if (modificado == null) {
                try {
                    GestionPracticasBDController.insertarEmpresa(emp);
                    //se muestra toda la tabla y se determina si sera visible
                    muestraTodasEmpresasTabla();
                    tablaVisible();
                    //se reinician los datos introducidos
                    reiniciaDatosRellenar();
                }catch (Exception e) {
                    //en caso de tirar una excepcion posiblemente sea por tratar de repetir una empresa
                    //en ese caso se notifica
                    MensajeBoxController.alertWarning("EMPRESA REPETIDA",
                            "La empresa no fue añadida, el nombre ya existe en la lista de empresas");
                }
            } else {
                //si modificado no es null significa que esta modificandose una empresa
                //siempre que modificado y empresa rellenada no sean iguales se modifica la empresa
                if (!modificado.equals(emp)) {
                    //se setea la id de la nueva empresa a la misma que la de modificado
                    emp.setId(modificado.getId());
                    try {
                        //se modifica la empresa pasandola
                        GestionPracticasBDController.actualizaEmpresa(emp);
                        //se actualiza la tabla
                        muestraTodasEmpresasTabla();
                    }catch (Exception e) {
                        //en caso de error se notifica
                        MensajeBoxController.alertWarning("EMPRESA REPETIDA",
                                "El nombre introducido ya existe en la lista de empresas");
                    }
                }
                //se reinician todos los datos a rellenar
                reiniciaDatosRellenar();
            }
        } else {
            //si a la empresa le faltaban datos por rellenar avisa
            if (emp.isRellenoNomConDir() && !txtNumTel.getText().equals("") && !txtEmailEmpresa.getText().equals("")) {
                telefonoEmailCorrecto(emp);
            } else {
                MensajeBoxController.alertWarning("COMPLETE TODOS LOS CAMPOS",
                        "Faltan datos por rellenar para poder guardar la empresa");
            }

        }
    }

    //metodo que comprueba que el telefono y el email introducidos sean correctos, si no lo son avisa
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

    //metodo que vacia los datos a rellenar
    public void vaciaTxtDatos() {
        txtNombreEmpresa.setText("");
        txtContactoEmpresa.setText("");
        txtNumTel.setText("");
        txtEmailEmpresa.setText("");
        txtDireccion.setText("");
    }

    //metodo que devuelve una empresa con todos los datos en los campos
    public Empresa recogeDatosEmpresa() {
        Empresa e = new Empresa();

        e.setNombre(txtNombreEmpresa.getText());
        e.setPersonaContacto(txtContactoEmpresa.getText());
        e.setTelefono(txtNumTel.getText());
        e.setEmail(txtEmailEmpresa.getText());
        e.setDireccion(txtDireccion.getText());

        return e;
    }

    //metodo que se activa al pulsar el checkbox de filtrado
    public void onClickCheckFiltro(MouseEvent event) {
        //llama al filtro
        filtraTabla();
        try {
            //en caso de que no este seleccionado y el numero de empresas sea diferente al numero total
            //en la base de datos se trata de actualizar la tabla
            if (!cbFiltrarNombre.isSelected() &&
                    tabEmpresas.getItems().size() != GestionPracticasBDController.numEmpresas()) {
                muestraTodasEmpresasTabla();
            }
        }catch (Exception e) {
            //ante cualquier error notifica
            MensajeBoxController.alertError("ERROR BD", "Hubo un error de conexion con la base de datos");
        }
    }

    //metodo asignado al Textfield de filtrado, llama al filtrar tabla
    public void onKeyReleasedFiltro(KeyEvent event) {
        filtraTabla();
    }

    //filtrar tabla consulta empresas que coincidan con caracteres pasados en la base de datos
    private void filtraTabla() {
        try {
            //siempre que este seleccionado y haya texto en el TextField de filtro
            if (cbFiltrarNombre.isSelected() &&
                    txtFiltrarNombre != null && !txtFiltrarNombre.getText().equals("")) {
                //llena una lista de empresas consultando en la base de datos pasando el string de txt
                Empresa[] lista = GestionPracticasBDController.consultaBuscaEmpresas(
                        txtFiltrarNombre.getText());
                //rellena la tabla con la lista
                tabEmpresas.setItems(getEmpresasTabla(lista));
            } else if (cbFiltrarNombre.isSelected() && (txtFiltrarNombre == null
                    || txtFiltrarNombre.getText().equals(""))) {
                //en caso de que el cb este seleccionado pero el TextField de filtrado no tenga nada se muestran
                //todas las empresas
                muestraTodasEmpresasTabla();
            }
        }catch (Exception e) {
            //ante cualquier error notifica
            MensajeBoxController.alertError("ERROR DB", "Hubo un error con la base de datos" +
                    " al filtrar las empresas");
        }
    }
}
