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
    //objetos de fxml traidos de la ventana alumno
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
    //declaracion de TextField que guardaran el editor de los combobox
    private TextField editorCiclos;
    private TextField editorEmpresas;
    private TextField editorFiltro;
    //declarar el alumno que se traera al tratar de modificar un alumno existente
    private Alumno modificado = null;
    //declaracion de objeto practicas
    private Practica p = null;
    //declaracion de empresas asociadas a las practicas
    private Empresa empresaPracticas = null;

    //metodo initialize
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            //creacion de conexion con la base de datos
            GestionPracticasBDController.crearConexion();
            //metodo que genera y muestra los datos iniciales que tendra la tabla
            inicializaTabla();
            //metodos para configurar los combobox correctamente
            inicializaCombCiclo();
            inicializaCombEmpresas();
            inicializaCombFiltro();
        } catch (Exception e) {
            //en caso de dar un error provocado por la conexion con la bd notifica y cierra la aplicación
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error de conexion con la base de datos y no se pudo inicializar la aplicación");
            System.exit(0);
        }
        //metodos que dan formato especifico a los Textfield para los campos de horario de las practicas
        formatoHoras(hEntradaHoras);
        formatoMinutos(hEntradaMin);
        formatoHoras(hSalidaH);
        formatoMinutos(hSalidaMin);
    }

    private void inicializaCombFiltro() throws Exception {
        //primero se consulta los ciclos que hay en la base de datos
        String[] lista = GestionPracticasBDController.consultaCiclosNombres();
        //se asigna la lista al combobox correspondiente
        combFiltroCiclo.setItems(FXCollections.observableArrayList(lista));
        //inicialmente se ponen los mismos objetos en el combobox y su listview
        lvFiltroCiclo.setItems(combFiltroCiclo.getItems());
        //se comprueba si hay algun ciclo inicialmente, si no lo hay desactiva el combobox que se usa para filtrar
        //la tabla
        if (lvFiltroCiclo.getItems().size() == 0) {
            combFiltroCiclo.setDisable(true);
        }
        //se guarda el editor del combobox, su parte TextField
        editorFiltro = combFiltroCiclo.getEditor();
        //en el editor se añade un evento a onKeyReleased
        editorFiltro.setOnKeyReleased(event -> {
            //metodo que configura el combobox con su listview para que al actuar sobre el editor haga acciones sobre el
            //list view
            configuraBuscaEnCombobox(event, lvFiltroCiclo, combFiltroCiclo);
            //acciones especificas de este combobox
            if (event.getCode() != KeyCode.UP && event.getCode() != KeyCode.DOWN && event.getCode() != KeyCode.ENTER) {
                //cuando se escribe filtra y unicamente muestra los ciclos en el list view
                String busca = editorFiltro.getText();
                lvFiltroCiclo.setItems(buscaCiclos(busca));
            }else if (event.getCode() == KeyCode.ENTER) {
                //al pulsar enter llama al metodo que recoge la seleccion del listview y la pone en su combobox
                getSeleccionLv(lvFiltroCiclo, combFiltroCiclo);
                //al ser el combobox de filtrar por ciclo llama al metodo que filtra la tabla
                filtraTabla();
                //una vez hecho todo despues de seleccionar algo de la lista reinicia sus elementos al total
                lvFiltroCiclo.setItems(combFiltroCiclo.getItems());
            }
        });
        //evento activado al clickar sobre el editor para que se muestre y pueda clickar la listview
        editorFiltro.setOnMouseClicked(event -> {
            lvFiltroCiclo.setVisible(true);
            lvFiltroCiclo.toFront();
        });
        //evento para que en caso de que se haga click o deseleccione el combobox se oculte la lista
        combFiltroCiclo.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !lvFiltroCiclo.isFocused()) {
                lvFiltroCiclo.toBack();
                lvFiltroCiclo.setVisible(false);
            }
        });
    }

    //inicializacion de combobox empresas y su lista correspondiente
    private void inicializaCombEmpresas() throws Exception {
        String[] lista = Contactable.contactableArrayNombres(GestionPracticasBDController.consultaEmpresas());
        combEmpresa.setItems(FXCollections.observableArrayList(lista));
        lvEmpresas.setItems(combEmpresa.getItems());
        editorEmpresas = combEmpresa.getEditor();
        editorEmpresas.setOnKeyReleased(event -> {
            configuraBuscaEnCombobox(event, lvEmpresas, combEmpresa);
            //acciones especificas de combEmpresas
            if (event.getCode() != KeyCode.UP && event.getCode() != KeyCode.DOWN && event.getCode() != KeyCode.ENTER) {
                //recoge lo escrito en el editor y muestra las empresas que coincidan con lo filtrado
                String busca = editorEmpresas.getText();
                lvEmpresas.setItems(buscaEmpresas(busca));
            }
            if (event.getCode() == KeyCode.ENTER) {
                //recoge el string de la lista que se puso en el combobox y lo guarda
                String selectedItem = getSeleccionLv(lvEmpresas, combEmpresa);
                //llama al metodo que muestra los datos de la empresa en los TextField que le corresponden
                //pasandole el nombre de la empresa
                muestraEmpresa(selectedItem);
                //reinicia los elementos en la list view de empresas a todas las empresas
                lvEmpresas.setItems(combEmpresa.getItems());
            }
        });
        //eventos para hacer visible la lista al clickar el editor o ocultar la lista al deseleccionar el combobox
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

    //metodo que da formato de horas a un TextField
    private void formatoHoras(TextField numTextField) {
        //formato de texto para que acepte solo numeros del 0 al 23
        String regex = "^(0?[0-9]|1[0-9]|2[0-3])?$";
        //textformatter para string que llama al metodo que aplica el formato
        TextFormatter<String> formatter = new TextFormatter<>(change -> metodoFormato(regex, change));
        //se asigna el textformatter al TextField
        numTextField.setTextFormatter(formatter);
    }

    //metodo que tambien asigna formato pero para los minutos
    private void formatoMinutos(TextField numTextField) {
        //permite numeros del 0 al 59
        String regex = "^(0?[0-9]|[1-5][0-9])?$";
        TextFormatter<String> formatter = new TextFormatter<>(change -> metodoFormato(regex, change));
        numTextField.setTextFormatter(formatter);
    }

    //metodo que usaran los textformatter
    private static TextFormatter.Change metodoFormato(String regex, TextFormatter.Change change) {
        //guarda el nuevo texto que se intenta poner
        String newText = change.getControlNewText();
        //si coincide con el formato lo devuelve si no devuelve null
        if (newText.matches(regex)) {
            return change;
        } else {
            return null;
        }
    }

    //inicialización del combobox que permite elegir ciclos para los alumnos
    private void inicializaCombCiclo() throws Exception {
        String[] ciclos = GestionPracticasBDController.consultaCiclosNombres();
        combCiclo.setItems(FXCollections.observableArrayList(ciclos));
        //en este combobox se añade la opcion de crear un nuevo ciclo al inicio del combobox
        combCiclo.getItems().add(0, "+ Alta Nuevo Ciclo");
        lvCiclos.setItems(combCiclo.getItems());
        editorCiclos = combCiclo.getEditor();
        editorCiclos.setOnKeyReleased(event -> {
            configuraBuscaEnCombobox(event, lvCiclos, combCiclo);
            if (event.getCode() != KeyCode.UP && event.getCode() != KeyCode.DOWN && event.getCode() != KeyCode.ENTER) {
                //a diferencia del combobox de filtrado este llama al metodo que agrega la opcion de dar de alta
                //un nuevo ciclo
                String busca = editorCiclos.getText();
                lvCiclos.setItems(buscaCiclos(busca));
                agregaOpcionAltaCiclo();
            }else if (event.getCode() == KeyCode.ENTER) {
                //al pulsar enter se selecciona lo elegido en la lista y se llama a alta ciclo que solo actua
                //si se escoge la opcion de añadir ciclo
                getSeleccionLv(lvCiclos, combCiclo);
                altaCiclo();
                //se reinicia la lista mostrada
                lvCiclos.setItems(combCiclo.getItems());
            }
        });
        //eventos para que la lista se muestre y oculte
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
        //en caso de que la lista este mostrando todos los ciclos posibles se agrega el alta nuevo ciclo
        //al inicio del list view
        if (lvCiclos.getItems().size() == combCiclo.getItems().size() - 1) {
            lvCiclos.getItems().add(0, "+ Alta Nuevo Ciclo");
        } else {
            //en caso contrario se agrega al final
            lvCiclos.getItems().add("+ Alta Nuevo Ciclo");
        }
    }

    //metodo que configura las opciones que todos los combobox y sus listview tienen en comun
    private void configuraBuscaEnCombobox(KeyEvent event, ListView<String> lv, ComboBox<String> comb) {
        if (event.getCode() == KeyCode.UP) {
            //si se pulsa flexa arriba recoge el indice que habia seleccionado
            int selectedIndex = lv.getSelectionModel().getSelectedIndex();
            //mientras el indice no sea el limite superior lo desplaza una posicion hacia atras y
            //lleva el deslizable ahi, en caso de que haya por haber muchos elementos
            if (selectedIndex > 0) {
                lv.getSelectionModel().select(selectedIndex - 1);
                lv.scrollTo(selectedIndex - 1);
            }
        } else if (event.getCode() == KeyCode.DOWN) {
            //si se pulsa la flecha hacia abajo hace el proceso inverso
            int selectedIndex = lv.getSelectionModel().getSelectedIndex();
            //mientras no sea el limite final de la lista va desplazando el indice seleccionado y moviendo el deslizable
            //automaticamente en caso de que haya
            if (selectedIndex < lv.getItems().size() - 1) {
                lv.getSelectionModel().select(selectedIndex + 1);
                lv.scrollTo(selectedIndex + 1);
            }
        } else if (event.getCode() == KeyCode.ENTER) {
            //cuando se pulsa enter, se deselecciona, se esconde la lista original del combobox
            //se hace invisible el list view y se pone al fondo para que no interfiera con otros elementos que pueda
            //haber
            root.requestFocus();
            comb.hide();
            lv.setVisible(false);
            lv.toBack();
        } else {
            //si solamente se esta escribiendo en el editor del combobox, se esconde la lista original del combobox
            //se hace visible la lista y se pone al frente para poder seleccionar
            comb.hide();
            lv.setVisible(true);
            lv.toFront();
        }
    }

    //metodo que recoge la seleccion del listview y lo pone como valor en el combobox
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

    //metodo para mostrar empresas de las practicas
    private void muestraEmpresa(String n) {
        //hace visible la parte de datos de la empresa
        paneEmpresa.setVisible(true);
        try {
            //recoge la empresa de la base de datos y rellena los TextField necesarios
            empresaPracticas = GestionPracticasBDController.consultaEmpresa(n);
            rellenaDatosEmpresa(empresaPracticas);
        } catch (Exception e) {
            //en caso de dar un error en la consulta se notifica
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error de conexion con la base de datos y no se pudo consultar la empresa");
        }
    }

    //metodo que filtra las empresas que se muestran en el combobox
    private ObservableList<String> buscaEmpresas(String busca) {
        //instancia de lista observable y declaracion de lista de empresas
        ObservableList<String> lista = FXCollections.observableArrayList();
        String[] empresas;

        try {
            //siempre que haya empresas
            if (GestionPracticasBDController.numEmpresas() > 0) {
                //guarda los nombres de todas las empresas mediante un metodo estatico de contactable
                //que recibe el array de objetos empresa de la base de datos
                empresas = Contactable.contactableArrayNombres(GestionPracticasBDController.consultaBuscaEmpresas(busca));
                //mientras haya empresas se asignan al observablearraylist
                if (empresas.length > 0) {
                    lista.setAll(empresas);
                } else {
                    //en caso de no haber se muestra sin resultados en la lista
                    lista.add("Sin resultados");
                }
            }
        } catch (Exception e) {
            //si hay un error con las consultas a la bd se notifica al usuario
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error con la base de datos y no se pudo consultar las empresas");
        }

        return lista;
    }

    //metodo que da de alta nuevos ciclos
    private void altaCiclo() {
        //se activa solo si la opcion escogida en el comb es la de añadir un nuevo ciclo
        if (combCiclo.getValue().equals("+ Alta Nuevo Ciclo")) {
            //pide al usuario un nombre para el nuevo ciclo
            Optional<String> r = MensajeBoxController.inputTexto("ALTA CICLO",
                    "Introduce el nombre del nuevo ciclo que quieres dar de alta");
            //siempre que se haya dado un nombre y pulsado ok actua
            if (r.isPresent() && !r.get().equals("")) {
                try {
                    //inserta un ciclo en la base de datos, en caso de poder lo notifica
                    if (GestionPracticasBDController.altaCiclo(r.get())) {
                        MensajeBoxController.alertInformation("ALTA CICLO",
                                "El ciclo introducido fue añadido correctamente");
                        //comprueba si el combobox de filtros estaba desactivado debido a que no habia ciclos
                        if (combFiltroCiclo.isDisable()) {
                            combFiltroCiclo.setDisable(false);
                        }
                    } else {
                        //en caso de que la funcion sea false lo mas seguro es que se trato de repetir nombre
                        //y avisa al usuario
                        MensajeBoxController.alertWarning("CICLO NO AÑADIDO",
                                "El ciclo introducido ya existe");
                    }
                } catch (Exception e) {
                    //en caso de ocurrir un error de conexion con la base de datos lo notifica
                    MensajeBoxController.alertError("ERROR CONEXION BD",
                            "Hubo un error al conectar con la base de datos y no se añadio" +
                                    " el ciclo");
                }
            } else if (r.isPresent()) {
                //en caso de no recibir ningun nombre lo notifica
                MensajeBoxController.alertWarning("CICLO NO AÑADIDO",
                        "Para añadir un ciclo debe introducir un nombre");
            }
            //finalmente se reinicia la seleccion del combobox de ciclos
            combCiclo.setValue("");
            lvCiclos.setItems(buscaCiclos(""));
            lvCiclos.getItems().add(0, "+ Alta Nuevo Ciclo");
            lvFiltroCiclo.setItems(buscaCiclos(""));
            combCiclo.setItems(lvCiclos.getItems());
            combFiltroCiclo.setItems(lvFiltroCiclo.getItems());
        }
    }

    //metodo que busca ciclos filtrando su nombre
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

    //cuando se hace click al combobox de ciclos
    public void onClickCombCiclos(MouseEvent event) {
        //se guarda el texto del editor
        String buscaC = editorCiclos.getText();
        if (!isValorEnComb(buscaC, combCiclo)) {
            //si no es un valor que pertenezca al combobox busca ciclos con coincidencias y agrega la opcion de alta ciclo
            lvCiclos.setItems(buscaCiclos(buscaC));
            agregaOpcionAltaCiclo();
        }
        //se esconde la lista original del combobox y se muestra o oculta la list view segun como estaba antes
        combCiclo.hide();
        lvCiclos.setVisible(!lvCiclos.isVisible());
        //segun si se este mostrando o ocultando envia hacia atras o hacia adelante para que no interfiera con
        //el resto de elementos del formulario
        if (lvCiclos.isVisible()) {
            lvCiclos.toFront();
        } else {
            lvCiclos.toBack();
        }
    }

    //metodo que comprueba si un estring pertenece a un combobox
    private boolean isValorEnComb(String st, ComboBox<String> comb) {
        for (String res : comb.getItems()) {
            if (st != null && st.equalsIgnoreCase(res)) {
                return true;
            }
        }
        return false;
    }

    //al clickar la lista de ciclos
    public void onClickListCiclos(MouseEvent event) {
        //recoge el valor seleccionado
        String selectedItem = lvCiclos.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            //mientras no sea null lo coloca en el combobox, se hace invisible la lista se
            //pone al frente el boton de generar email para que la lista no interfiera con el y comprueba si se
            //debe activar el alta de ciclo
            combCiclo.getSelectionModel().select(selectedItem);
            lvCiclos.setVisible(false);
            btnGeneraEmail.toFront();
            altaCiclo();
            root.requestFocus();
        }
    }

    //si se hace click en comb empresas actua
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

    //si se hace click en el list view de empresas actua
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

    //si se hace click al comb filtro por ciclos actua
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

    //si se hace click a la list view del filtro actua
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

    //metodo que inicializa la tabla de alumnos
    private void inicializaTabla() {
        //se le asocian los atributos col1 col2 y col3 de DatosTabla a cada columna, ademas se asocian
        //los botones de datostabla
        colNombre.setCellValueFactory(new PropertyValueFactory<>("col1"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("col2"));
        colCiclo.setCellValueFactory(new PropertyValueFactory<>("col3"));
        colConsulta.setCellValueFactory(new PropertyValueFactory<>("btnConsultar"));
        colModifica.setCellValueFactory(new PropertyValueFactory<>("btnModificar"));
        colElimina.setCellValueFactory(new PropertyValueFactory<>("btnEliminar"));
        //ha que no se puedan mover de lugar las columnas
        colNombre.setReorderable(false);
        colApellidos.setReorderable(false);
        colCiclo.setReorderable(false);
        colConsulta.setReorderable(false);
        colModifica.setReorderable(false);
        colElimina.setReorderable(false);
        //llama al metodo que muestra todos los alumnos de la base de datos y que determina si la tabla sera visible o no
        muestraTodosAlumnosTabla();
    }

    //metodo que recoge una lista con todos los alumnos de la base de datos
    private void muestraTodosAlumnosTabla() {
        try {
            Alumno[] lista = GestionPracticasBDController.consultaAlumnos();
            //a la tabla se le asignan los elementos hechos observablelist de DatosTabla usando
            //el metodo getalumnos()
            tabAlumnos.setItems(getAlumnos(lista));
            //se determina si la tabla sera visible con el metodo tablaVisible
            tablaVisible();
        } catch (Exception e) {
            //si hay algun error lo notifica
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error de conexion con la base de datos y no se pudo consultar los alumnos");
        }
    }

    //metodo que determina si se muestra la tabla o no
    private void tablaVisible() {
        //comprueba si hay alumnos en la tabla, si no los hay avisa ocultandola y mostrando la etiqueta
        //que pide alumnos
        boolean visible = tabAlumnos.getItems().size() > 0;
        tabAlumnos.setVisible(visible);
        lbNoAlumnos.setVisible(!visible);
    }

    //metodo que pasado un array de alumnos devuelve un observable list de datostabla
    public ObservableList<DatosTabla> getAlumnos(Alumno[] lista) {
        //se vacia la tabla
        tabAlumnos.getItems().clear();
        //se crea el output
        ObservableList<DatosTabla> output = FXCollections.observableArrayList();
        //por cada alumno se crea un DatosTabla con el constructor que requiere del alumno y de 3 eventos que recogen
        // a este mismo alumno y se añaden a la salida
        for (Alumno a : lista) {
            output.add(new DatosTabla(a, onClickBtnConsulta(a), onClickBtnModifica(a), onClickBtnElimina(a)));
        }
        //se devuelve el observable
        return output;
    }

    //evento para el boton consultar, pone la etiqueta a ver alumno, rellena los datos con el alumno que le corresponde
    //y activa el modo solo consultar
    public EventHandler<MouseEvent> onClickBtnConsulta(Alumno a) {
        return event -> {
            lbAccionAlumno.setText("Ver Alumno");
            rellenaDatosAlumno(a);
            modoConsultar(true);
        };
    }

    //metodo que pone modo solo consultar, recibido un booleano habilita o deshabilita o hace visibles o invisibles
    //las partes necesarias del formulario
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

    //evento de boton modifica
    public EventHandler<MouseEvent> onClickBtnModifica(Alumno a) {
        return event -> {
            //cambia el texto del boton de practicas, para que en caso de que tenga sea para quitarle las practicas
            //y no para cancelar su creacion, añade la etiqueta de modificacion
            btnCancelaPracticas.setText("Eliminar");
            lbAccionAlumno.setText("Modificar Alumno");
            //desactiva el modo consultar
            modoConsultar(false);
            //rellena los datos pasandole el alumno
            rellenaDatosAlumno(a);
            //pone en el objeto global modificado el alumno recibido
            modificado = a;
        };
    }

    //metodo rellenar datos alumno que coloca todos los datos que tiene
    private void rellenaDatosAlumno(Alumno a) {
        txtNombreAlumno.setText(a.getNombre());
        txtApellidosAlumno.setText(a.getApellidos());
        txtTelefonoAlumno.setText(a.getTelefono());
        txtEmail.setText(a.getEmail());
        try {
            //colocacion del ciclo al que pertenece consultando a la base de datos por el nombre pasandole el id del ciclo
            combCiclo.setValue(GestionPracticasBDController.consultaNombreCiclo(a.getIdCiclo()));
            //se recoge las practicas del alumno pasandole el id del alumno
            p = GestionPracticasBDController.consultaPractica(a.getId());
            //si hay practicas las muestra, sino devuelve la opcion de añadir practicas a su estado original
            if (p != null) {
                rellenaPracticas();
            } else {
                reiniciaRellenarPracticas();
            }
        } catch (Exception e) {
            //se notifica si ocurre cualquier error
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error al conectar con la base de datos y no se pudo" +
                            " consultar la practica del alumno");
        }
    }

    //metodo rellenar practicas que coloca y hace visible todos los datos necesarios de las practicas
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

    //evento asignado al boton eliminar de cada alumno
    public EventHandler<MouseEvent> onClickBtnElimina(Alumno a) {
        return event -> {
            //pide confirmacion de eliminacion al usuario
            ButtonType r = MensajeBoxController.alertConfirmar("ELIMINAR ALUMNO",
                    "Esta totalmente seguro de que desea eliminar al alumno " + a.getNombre() + " " +
                            a.getApellidos() + "?");
            //si se confirma que quiere eliminar
            if (r == ButtonType.OK) {
                try {
                    //elimina al alumno seleccionado pasandole la id
                    GestionPracticasBDController.eliminaAlumno(a.getId());
                    //reinicia los alumnos mostrados en la tabla
                    muestraTodosAlumnosTabla();
                    //en caso de que al eliminar el alumno haya sido el mismo que estuviese siendo consultado
                    //o modificado se reinician los datos a rellenar y el modo a Añadir
                    if (modificado != null && modificado.equals(a)) {
                        reiniciaRellenarAlumno();
                    } else if (lbAccionAlumno.getText().equals("Ver Alumno")) {
                        Alumno aux = new Alumno(0, txtNombreAlumno.getText(), txtApellidosAlumno.getText(),
                                txtTelefonoAlumno.getText(), txtEmail.getText(),
                                GestionPracticasBDController.consultaIdCiclo(combCiclo.getValue()));
                        if (aux.equals(a)) {
                            reiniciaRellenarAlumno();
                        } else {
                            reiniciaFiltros();
                        }
                    } else {
                        reiniciaFiltros();
                    }
                } catch (Exception e) {
                    //ante cualquier error se notifica
                    MensajeBoxController.alertError("ERROR CONEXION BD",
                            "Hubo un error de conexion con la base de datos y el alumno no fue eliminado");
                }
            }
        };
    }

    //al hacer click sobre root se le hace focus
    public void onClickRoot(MouseEvent event) {
        root.requestFocus();
    }

    //metodo que reinicia todos los datos de alumno y practicas pone el modo a añadir alumno y
    //modificado a null ademas de modo consulta a false
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

    //metodo que reinicia las practicas
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

    //metodo que rellena los datos de una empresa, si se le pasa un valor nulo los vacia
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

    //metodo para reiniciar filtros posibles
    private void reiniciaFiltros() {
        cbFiltroNombre.setSelected(false);
        cbFiltroApellidos.setSelected(false);
        cbFiltroCiclo.setSelected(false);
        txtFiltroNombre.setText("");
        txtFiltroApellidos.setText("");
        combFiltroCiclo.setValue("");
    }

    //al hacer click sobre el pane de empresas cambia a esa escena
    public void onClickChangeToEmpresas(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "empresas-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }

    //al hacer click a la ventana de exportar un ciclo cambia a esa escena
    public void onClickChangeToExportCiclo(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "exportciclo-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }

    //al hacer click en el boton generar email comprueba que todos los datos necesarios esten rellenos
    public void onClickGenerarEmail(MouseEvent event) {
        if (txtNombreAlumno.getText() != null && !txtNombreAlumno.getText().equals("") &&
                txtApellidosAlumno.getText() != null && !txtApellidosAlumno.getText().equals("") &&
                txtTelefonoAlumno.getText() != null && !txtTelefonoAlumno.getText().equals("")) {
            if (telCorrecto()) {
                Alumno a = new Alumno();
                a.setNombre(txtNombreAlumno.getText());
                a.setApellidos(txtApellidosAlumno.getText());
                a.setTelefono(txtTelefonoAlumno.getText());

                //si todos los datos son correctos rellena el campo de email con un valor dado por el valor devuelto
                //en un procedimiento de la base de datos
                try {
                    txtEmail.setText(GestionPracticasBDController.generaEmail(a));
                } catch (Exception e) {
                    //si ocurre un error notifica
                    MensajeBoxController.alertError("ERROR CONEXION BD",
                            "Hubo un error al conectar con la base de datos y no se pudo generar el email");
                }
            }
        } else {
            //si faltan campos para poder generar notifica
            MensajeBoxController.alertInformation("FALTAN CAMPOS",
                    "Debes rellenar nombre, apellidos y telefono para poder generar un email");
        }
    }

    //metodo que comprueba si el telefono introducido es correcto, si no lo es notifica
    public boolean telCorrecto() {
        if (!Contactable.telCorrecto(txtTelefonoAlumno.getText())) {
            MensajeBoxController.alertWarning("DATOS INCORRECTOS",
                    "Introduce un numero de telefono valido (9 numeros, pueden estar separados" +
                            " por espacios, guiones o puntos))");
            return false;
        }
        return true;
    }

    //metodo que comprueba si el email es correcto, si no lo es notifica
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

    //metodo de cancelar, reinicia todos los campos
    public void onClickCancelaTodo() {
        reiniciaRellenarAlumno();
    }

    //metodo guardar el alumno y sus practicas
    public void onClickGuardarAlumnoPracticas() {
        //primero comprueba si ya se calculo la fecha de fin de las practicas, en caso de que no la calcula
        if (txtFechaFin.getText() == null || txtFechaFin.getText().equals("")) {
            fechaFinPracticas();
        }
        //si sigue estando vacio es porque faltan datos, en caso de ser relleno continua
        if (txtFechaFin.getText() != null && !txtFechaFin.getText().equals("") && empresaPracticas != null &&
                todoRellenoAlumno()) {
            //si p no es null
            if (p != null) {
                //si las practicas son null inserta el alumno en la base de datos
                if (modificado == null) {
                    try {
                        //si no se insertan las practicas del alumno y al alumno de seguido se tratara de
                        //hacer rollback
                        GestionPracticasBDController.conexion.setAutoCommit(false);
                        insertaAlumno(false);
                        //recoge la ultima id insertada que en este caso seria la del alumno
                        int id_alumno = GestionPracticasBDController.ultimaId();
                        //setea la id de alumno y empresa de las practicas
                        p.setId_alumno(id_alumno);
                        p.setId_empresa(empresaPracticas.getId());
                        //se insertan las practicas
                        GestionPracticasBDController.insertarPracticas(p);
                        GestionPracticasBDController.conexion.commit();
                        GestionPracticasBDController.conexion.setAutoCommit(true);
                        //se reinician todos los campos
                        reiniciaRellenarAlumno();
                    } catch (Exception e) {
                        //se notifica de posibles errores
                        MensajeBoxController.alertError("ERROR CONEXION BD",
                                "Hubo un error de conexion con la base de datos y no se pudo modificar las " +
                                        "practicas");
                        try {
                            //rollback en caso de no hacer todo seguido
                            GestionPracticasBDController.conexion.rollback();
                            GestionPracticasBDController.conexion.setAutoCommit(true);
                        } catch (Exception ignored){}
                    }
                } else {
                    //en caso de que modificado no sea null se llama a modificar alumno
                    modificaAlumno(false);
                    try {
                        //se pone la id del alumno y de la empresa
                        p.setId_alumno(modificado.getId());
                        p.setId_empresa(empresaPracticas.getId());
                        //se comprueba si ya habian practicas añadidas para ese alumno, si no las habia se insertan
                        //nuevas, si ya las habia se modifican
                        if (GestionPracticasBDController.consultaPractica(p.getId_alumno()) == null) {
                            GestionPracticasBDController.insertarPracticas(p);
                        } else {
                            GestionPracticasBDController.actualizaPractica(p);
                        }
                        //se reinician los campos
                        reiniciaRellenarAlumno();
                    }catch (Exception e) {
                        //se notifica de cualquier error
                        MensajeBoxController.alertError("ERROR CONEXION BD",
                                "Hubo un error de conexion con la base de datos y no se pudo guardar" +
                                        " la practicas");
                    }
                }
            }
        } else if (txtFechaFin.getText() != null && !txtFechaFin.getText().equals("") && !todoRellenoAlumno()) {
            //en caso de no poder entrar porque falten datos se avisa
            MensajeBoxController.alertWarning("ALUMNO NO VALIDO",
                    "Debes rellenar todos los campos para poder guardar el alumno y sus practicas");
        }
    }

    //al pulsar guardar un alumno
    public void onClickGuardarAlumno(MouseEvent event) {
        //se comprueba que no falten datos
        if (todoRellenoAlumno()) {
            //si modificado es null es porque se esta guardando un nuevo alumno
            if (modificado == null) {
                //se comprueba si el panel de practicas fue añadido se pregunta si quiere
                //guardar unicamente el alumno y no las practicas, para algo son botones separados
                if (!panePracticas.isVisible() || confirmarGuardaSoloAlumno()) {
                    //se llama a insertar el alumno
                    insertaAlumno(true);
                }
            } else {
                //en caso de que modificado no sea null se modifica el alumno
                if (!panePracticas.isVisible() || confirmarGuardaSoloAlumno()) {
                    modificaAlumno(true);
                }
            }
        } else {
            //si faltan campos se avisa
            MensajeBoxController.alertWarning("ALUMNO NO GUARDADO",
                    "Debes rellenar todos los campos para poder guardar el alumno");
        }
    }

    //metodo para modificar un alumno
    private void modificaAlumno(boolean aux) {
        try {
            //si todos los datos son correctos
            if (telCorrecto() && mailCorrecto()) {
                //se rellena el modificado con los nuevos datos
                recogeDatosAlumno(modificado);
                //se intenta actualizar el alumno
                if (GestionPracticasBDController.actualizaAlumno(modificado)) {
                    //en caso de poder se actualiza la tabla
                    muestraTodosAlumnosTabla();
                } else {
                    //en caso de no poder lo mas seguro es que haya un alumno repetido y se avisa
                    MensajeBoxController.alertWarning("ALUMNO NO MODIFICADO",
                            "El alumno tiene datos repetidos con otro alumno, asi que no se " +
                                    "modificaron sus datos");
                }
                //segun si el booleano pasado se reinician los campos a rellenar direcatmente o no
                if (aux) {
                    reiniciaRellenarAlumno();
                }
            }
        } catch (Exception e) {
            //ante cualquier error se notifica
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error con la base de datos y no se modifico el alumno");
        }
    }

    //metodo que pregunta por confirmacion de solo guardar alumno
    private boolean confirmarGuardaSoloAlumno() {
        ButtonType r = MensajeBoxController.alertConfirmar("GUARDAR ALUMNO",
                "Se guardara unicamente el alumno, no sus practicas" +
                        "¿Esta seguro de que desea guardar solamente el alumno?");

        //devuelve la resupuesta del usuario
        return r == ButtonType.OK;
    }

    //metodo para insertar nuevos alumnos
    private void insertaAlumno(boolean reinicia) {
        try {
            //comprueba que todos los datos sean correctos
            if (telCorrecto() && mailCorrecto()) {
                //en caso de serlo se crea un nuevo alumno y se rellenan sus datos
                Alumno a = new Alumno();
                recogeDatosAlumno(a);
                //su se pudo insertar se actualiza la tabla
                if (GestionPracticasBDController.insertarAlumno(a)) {
                    muestraTodosAlumnosTabla();
                } else {
                    //si no se pudo es seguramente por datos repetidos con otro alumno, se notifica
                    MensajeBoxController.alertWarning("ALUMNO NO INSERTADO",
                            "El alumno tiene datos repetidos con otro alumno, asi que no se inserto");
                }
                //segun el booleano se reinicia todo directamente o no
                if (reinicia) {
                    reiniciaRellenarAlumno();
                }
            }
        } catch (Exception e) {
            //en caso de error se notifica
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error de conexion con la base de datos y no se pudo añadir el alumno");
        }
    }

    //metodo que recoge los datos de los campos de alumno y los guarda en el alumno pasado
    private void recogeDatosAlumno(Alumno a) throws Exception {
        a.setNombre(txtNombreAlumno.getText());
        a.setApellidos(txtApellidosAlumno.getText());
        a.setTelefono(txtTelefonoAlumno.getText());
        a.setEmail(txtEmail.getText());
        a.setIdCiclo(GestionPracticasBDController.consultaIdCiclo(combCiclo.getValue()));
    }

    //metodo que comprueba si todos los datos del alumno fueron rellenados
    private boolean todoRellenoAlumno() {
        return txtNombreAlumno.getText() != null && !txtNombreAlumno.getText().equals("") &&
                txtApellidosAlumno.getText() != null && !txtApellidosAlumno.getText().equals("") &&
                txtTelefonoAlumno.getText() != null && !txtTelefonoAlumno.getText().equals("") &&
                txtEmail.getText() != null && !txtEmail.getText().equals("") &&
                isValorEnComb(combCiclo.getValue(), combCiclo);
    }

    //metodo para añadir practicas
    public void onClickAnadePracticas(MouseEvent event) {
        //siempre que haya empresas
        if (combEmpresa.getItems().size() > 0) {
            //muestra el panel de empresas y oculta este boton
            btnAnadePracticas.setVisible(false);
            panePracticas.setVisible(true);
        } else {
            //en caso de no haber empresas lo notifica
            MensajeBoxController.alertWarning("SIN EMPRESAS",
                    "Para poder añadir practicas debes tener al menos 1 empresa añadida");
        }
    }

    //metodos para que al rellenar las horas de las practicas de los alumnos pasen al siguiente campo de minutos u horas
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

    //metodo que crea practicas y asigna valores de hora entrada y salida asi como fecha de inicio para luego calcular
    //la fecha de finalizacion y mostrarla
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
                //en caso de haber una excepcion por los metodos propios del objeto practicas la muestra al usuario
                MensajeBoxController.alertWarning("DATOS INCORRECTOS",
                        e.getMessage());
                //pone fecha fin a nada y p a nulo
                txtFechaFin.setText("");
                p = null;
            }
        } else {
            //si no estan todos los campos rellenos se notifica
            MensajeBoxController.alertWarning("COMPLETA TODOS LOS CAMPOS",
                    "Debes rellenar los datos necesarios para las practicas");
        }
    }

    //metodo para comprobar si todos los campos de horario y fecha inicio estan rellenos
    private boolean isFechaHorariosRellenos() {
        return hEntradaHoras.getText() != null && !hEntradaHoras.getText().equals("") &&
                hEntradaMin.getText() != null && !hEntradaMin.getText().equals("") &&
                hSalidaH.getText() != null && !hSalidaH.getText().equals("") &&
                hSalidaMin.getText() != null && !hSalidaMin.getText().equals("") &&
                dtpFechaIni.getValue() != null;
    }

    //metodo para el boton que genera la fecha de finalizacion
    public void onClickGenerarFecha(MouseEvent event) {
        fechaFinPracticas();
    }

    //metodo que se asigna al boton cancela las practicas
    public void onClickCancelaPracticas(MouseEvent event) {
        if (modificado == null || btnCancelaPracticas.getText().equals("Cancelar")) {
            //si el valor es cancelar sin mas se reinician los datos
            reiniciaRellenarPracticas();
        } else {
            //si no se pide confirmacion de la eliminacion de las practicas
            ButtonType r = MensajeBoxController.alertConfirmar("ELIMINAR PRACTICAS",
                    "Esta seguro de que desea eliminar las practicas del alumno " + modificado.getNombre() + "?");
            if (r == ButtonType.OK) {
                //en caso de querer eliminar se quitan de la base de datos pasando la id del alumno modificado y se
                //reinician los campos de datos de las practicas
                try {
                    GestionPracticasBDController.eliminaPractica(modificado.getId());
                    reiniciaRellenarPracticas();
                } catch (Exception e) {
                    //en caso de error notifica
                    MensajeBoxController.alertError("ERROR CONEXION BD",
                            "Hubo un error con la base de datos y no pudo eliminarse la practica");
                }
            }
        }
    }

    //metodo para el boton terminar consulta, simplemente reinicia
    public void onClickTerminaConsulta(MouseEvent event) {
        reiniciaRellenarAlumno();
    }

    //metodo que filtra los datos que se muestran en la tabla
    private void filtraTabla() {
        String buscaNom, buscaApe, buscaCiclo;
        //segun si estan rellenos los campos de filtros y seleccionados en checkbox se asigna valor o null a las
        //variables de filtrado
        buscaNom = cbFiltroNombre.isSelected() && txtFiltroNombre != null &&
                !txtFiltroNombre.getText().equals("") ? txtFiltroNombre.getText() : null;
        buscaApe = cbFiltroApellidos.isSelected() && txtFiltroApellidos != null &&
                !txtFiltroApellidos.getText().equals("") ? txtFiltroApellidos.getText() : null;
        buscaCiclo = cbFiltroCiclo.isSelected() && combFiltroCiclo.getValue() != null &&
                !combFiltroCiclo.getValue().equals("") ? combFiltroCiclo.getValue() : null;

        try {
            //con que una sola de ellas no sea null
            if (buscaNom != null || buscaApe != null || buscaCiclo != null) {
                //se rellena un array de alumnos haciendo una consulta con los filtros pasados
                Alumno[] filtrados = GestionPracticasBDController.consultaBuscaAlumnos(buscaNom, buscaApe, buscaCiclo);
                //se ponen los alumnos recogidos en la tabla
                tabAlumnos.setItems(getAlumnos(filtrados));
            } else if (cbFiltroNombre.isSelected() || cbFiltroApellidos.isSelected() || cbFiltroCiclo.isSelected()) {
                //si alguno de los cb esta seleccionado pero los valores son todos nulos se muestra toda la tabla
                muestraTodosAlumnosTabla();
            } else {
                //si esta todo deseleccionado se muestra la tabla completa
                muestraTodosAlumnosTabla();
            }
        }catch (Exception e) {
            //ante cualquier error se notifica
            MensajeBoxController.alertError("ERROR DB", "Hubo un error con la base de datos" +
                    " al filtrar las empresas");
        }
    }

    //metodo que se asigna a los 3 checkbox de filtros
    public void onSelectCbFiltro(Event event) {
        filtraTabla();
    }

    //metodo que se asigna a los 2 TextField de filtros
    public void onKeyReleasedFiltro(KeyEvent event) {
        filtraTabla();
    }
}