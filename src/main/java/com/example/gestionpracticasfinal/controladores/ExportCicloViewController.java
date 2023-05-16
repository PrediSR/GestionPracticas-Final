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
    //objetos definidos por exportciclo-view.fxml
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
    //se declara el TextField que guardara el editor del combCiclos
    private TextField editor;

    //metodo asignado a Gestion de Alumnos que cambiara la escena
    public void onClickChangeToAlumnos(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "alumnos-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }

    //metodo asignado a gestion de empresas que cambia de escena
    public void onClickChangeToEmpresas(MouseEvent event) throws Exception {
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(
                "empresas-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }

    //metodo initialize
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //lama al metodo que inicializa la tabla de alumnos y el combobox de ciclos
        inicializaTabla();
        inicializaCombCiclos();
    }

    //evento que se asigna al editor del combobox para poder filtrar correctamente los ciclos
    //el escribir en su listview
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
            //se setean los elementos de la list view a los devueltos buscando coincidencias con el texto recogido
            //del editor
            String buscaC = editor.getText();
            lvCombCiclos.setItems(buscaCiclos(buscaC));
            combCiclo.hide();
            lvCombCiclos.setVisible(true);
            btnExportar.toBack();
        }
    }

    //inicializacion de comb ciclos
    private void inicializaCombCiclos() {
        try {
            //se recogen los nombres de todos los ciclos de la base de datos
            String[] ciclos = GestionPracticasBDController.consultaCiclosNombres();
            //siempre que sean mayor que 0 se asignan al combobox
            if (ciclos.length > 0) {
                combCiclo.setItems(FXCollections.observableArrayList(ciclos));
            } else {
                //en caso de no serlo se añade no hay ciclos añadidos al combobox
                combCiclo.setItems(FXCollections.observableArrayList(
                        "No hay ciclos añadidos"));
            }
            //se setean los elementos del combobox al listview
            lvCombCiclos.setItems(combCiclo.getItems());
        } catch (Exception e) {
            //en caso de error se notifica
            MensajeBoxController.alertError("Error al consultar",
                    "Hubo un error con la base de datos y no pudo realizarse la consulta de ciclos");
        }
        //se asigna el editor de combciclo a editor
        editor = combCiclo.getEditor();
        //se le añade un evento que llama al metodo configuraBusquedaComb
        editor.setOnKeyReleased(this::configuraBusquedaComb);
        //se añade un evento on click que muestra el listview y se pone atras el boton exportar para que no interfiera
        editor.setOnMouseClicked(event -> {
            lvCombCiclos.setVisible(true);
            btnExportar.toBack();
        });
    }

    //metodo que inicializa la tabla de alumnos, asignando los atributos de DatosTabla a cada columna y desactivando
    //que se puedan reordenar
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

    //metodo mostrar tabla
    public void muestraTabla() {
        //se recoge el valor que tiene combciclo
        String ciclo = combCiclo.getValue();

        try {
            //siempre que es un valor en comciclos permite seguir
            if (isValorEnCombCiclos(ciclo)) {
                //siempre que el valor no sea no hay ciclos o sin resultados sigue
                if (!ciclo.equals("No hay ciclos añadidos") && !ciclo.equals("sin resultados")) {
                    //crea una lista DatosTabla llamando a getAlumnosCiclo
                    ObservableList<DatosTabla> lista = getAlumnosCiclo(ciclo);
                    //si la lista es mayor que 0
                    if (lista.size() > 0) {
                        //oculta la etiqueta de no hay alumnos, asigna la lista a la tabla y la muestra
                        lbNoHayAlumnos.setVisible(false);
                        tabAlumnosExport.setItems(lista);
                        tabAlumnosExport.setVisible(true);
                    } else {
                        //en caso contrario muestra no hay alumnos y oculta la tabla
                        tabAlumnosExport.setVisible(false);
                        lbNoHayAlumnos.setVisible(true);
                    }
                    //habilita el boton de exportar una vez elegido un ciclo
                    btnExportar.setDisable(false);
                } else {
                    //llama al metodo quitar tabla
                    quitaTabla();
                }
            } else {
                //llama a quitar tabla
                quitaTabla();
            }
        } catch (Exception e) {
            //en caso de error notifica
            MensajeBoxController.alertError("Error al consultar el ciclo",
                    "No se ha podido hacer la consulta de alumnos que se exportaran");
        }

    }

    //metodo que comprueba si el valor pasado esta dentro de los valores del combciclo
    private boolean isValorEnCombCiclos(String st) {
        for (String res : combCiclo.getItems()) {
            if (st != null && st.equalsIgnoreCase(res)) {
                return true;
            }
        }
        return false;
    }

    //metodo que devuelve un observable de string buscando ciclos en la base de datos
    public ObservableList<String> buscaCiclos(String buscaC) {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String[] ciclos;

        try {
            //si hay ciclos
            if (GestionPracticasBDController.numCiclos() > 0) {
                //busca coincidencias con el string pasado
                ciclos = GestionPracticasBDController.buscaCiclosNombre(buscaC);
                //si hay mas de 0 ciclos los muestra
                if (ciclos.length > 0) {
                    lista.setAll(ciclos);
                } else {
                    //si no muestra sin resultados
                    lista.add("sin resultados");
                }
            } else {
                //en caso de que no haya dice no hay ciclos añadidos
                lista.add("No hay ciclos añadidos");
            }
        }catch (Exception e) {
            //en caso de error notifica
            MensajeBoxController.alertError("ERROR CONEXION BD",
                    "Hubo un error con la base de datos y no se pudo consultar los ciclos");
        }

        return lista;
    }

    //metodo que recoge todos los alumnos de un ciclo pasado como variable y los devuelve como DatosTabla
    public ObservableList<DatosTabla> getAlumnosCiclo(String ciclo) throws Exception {
        //se hace la consulta
        Alumno[] lista = GestionPracticasBDController.consultaAlumnosPorNombreCiclo(ciclo);
        ObservableList<DatosTabla> output = FXCollections.observableArrayList();
        //se rellena el observable por cada alumno con un objeto datos tabla constructor de solo alumno
        for (Alumno a : lista) {
            output.add(new DatosTabla(a));
        }
        return output;
    }

    //al pulsar el boton de exportar
    public void onClickExportButton(MouseEvent event) {
        //se recoge el ciclo
        String ciclo = combCiclo.getValue();
        //si la tabla de alumnos es visible
        if (tabAlumnosExport.isVisible()) {
            //se crea un fileChooser y se pide que escoga donde quiere guardar el archivo
            FileChooser fc = new FileChooser();
            fc.setTitle("Elige el destino del ciclo exportado");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT", "*.txt"));
            fc.setInitialFileName(ciclo + "-Export");

            //se crea un file con el resultado
            File file = fc.showSaveDialog(null);

            //siempre que no sea null
            if (file != null) {
                try {
                    //se recoge la lista de alumnos
                    Alumno[] lista = GestionPracticasBDController.consultaAlumnosPorNombreCiclo(ciclo);
                    //se llama a ficheroscontroller y se escriben los datos de los alumnos en el fichero
                    FicherosController.escribirCiclo(file, lista);
                    //se notifica de que fue exportado correctamente
                    MensajeBoxController.alertInformation("CICLO EXPORTADO",
                            "El ciclo seleccionado fue exportado correctamente");
                    //se reinicia la seleccion
                    reiniciaSeleccion();
                } catch (Exception e) {
                    //en caso de error notifica
                    MensajeBoxController.alertError("ERROR AL EXPORTAR",
                            "Hubo un error al exportar el ciclo y no pudo crearse el fichero");
                }
            }
        } else {
            //en caso de que la tabla no sea visible es porque no hay alumnos en ese ciclo
            MensajeBoxController.alertWarning("CICLO NO VALIDO",
                    "El ciclo seleccionado no tiene alumnos para exportar a un fichero");
        }
    }

    //al hacer click a combCiclo
    public void onClickCombCiclos(MouseEvent event) {
        String buscaC = editor.getText();
        //se comprueba si el texto de combCiclos esta en su lista de valores
        if (!isValorEnCombCiclos(buscaC)) {
            //si no lo estan se setea listview a buscaCiclos() pasandole el texto
            lvCombCiclos.setItems(buscaCiclos(buscaC));
        }
        //se oculta la lista de comciclo
        combCiclo.hide();
        //cada vez que se hace click se cambia la visibilidad de la lista
        lvCombCiclos.setVisible(!lvCombCiclos.isVisible());
        //segun si es visible o no se cambia si esta al frente o detras para no interferir con el boton de exportar
        if (lvCombCiclos.isVisible()) {
            btnExportar.toBack();
        } else {
            btnExportar.toFront();
            btnExportar.requestFocus();
        }
    }

    //al hacer click en la lista de ciclos
    public void onClickListCiclos(MouseEvent event) {
        //recoge el valor seleccionado
        String selectedItem = lvCombCiclos.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            //si el string elegido de la lista no es null se asigna al combobox
            combCiclo.getSelectionModel().select(selectedItem);
            //se hace la lista invisible y se pone el boton exportar por delante
            lvCombCiclos.setVisible(false);
            btnExportar.toFront();
            btnExportar.requestFocus();
            //se llama a muestraTabla
            muestraTabla();
        }
    }

    //metodo que reinicia la seleccion de combciclos
    private void reiniciaSeleccion() {
        combCiclo.setValue(null);
        lvCombCiclos.setItems(combCiclo.getItems());
        lvCombCiclos.setVisible(false);
        quitaTabla();
    }

    //metodo que quita la tabla de la vista
    private void quitaTabla() {
        tabAlumnosExport.setVisible(false);
        lbNoHayAlumnos.setVisible(false);
        btnExportar.setDisable(true);
    }

    //Evento que deselecciona el combobox y quita el list view de la vista ademas de traer el boton exportar al frente
    //cuando se hace clic sobre root
    public void onClickUnselect(MouseEvent event) {
        btnExportar.requestFocus();
        lvCombCiclos.setVisible(false);
        btnExportar.toFront();
    }
}
