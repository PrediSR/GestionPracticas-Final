package com.example.gestionpracticasfinal.modelos;

import com.example.gestionpracticasfinal.controladores.GestionPracticasBDController;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;

//clase usada para rellenar los datos de las tablas
public class DatosTabla {
    //atributo int final para determinar el tamaño de los botones apareceran en la tabla en caso de haber
    private final int BTNIMGSIZE = 22;
    //atributo final que recoge el css de estilos para asignar una clase a los botones que tienen las tablas
    private final File HOJAESTILOS = new File("src/main/resources/com/example/gestionpracticasfinal/css/estilos.css");
    //atributos que guardan valores de las columnas de la tabla
    private String col1;
    private String col2;
    private String col3;
    private String col4;
    //atributos tipo button que seran los botones de modificar consultar y eliminar de la tabla
    private Button btnEliminar;
    private Button btnModificar;
    private Button btnConsultar;

    //constructor que unicamente recibe un alumno, asigna sus atributos a las columnas
    public DatosTabla(Alumno a) {
        col1 = a.nombre;
        col2 = a.getApellidos();
        col3 = a.telefono;
        col4 = a.email;
    }

    //constructor que recibe 1 alumno y 3 eventos, en este caso agrega los nombre apellidos y ciclos
    //a las 3 columnas y luego inicializa los bonotes
    public DatosTabla(Alumno a, EventHandler<MouseEvent> eConsulta, EventHandler<MouseEvent> eModifica,
                      EventHandler<MouseEvent> eElimina) {
        col1 = a.nombre;
        col2 = a.getApellidos();
        try {
            col3 = GestionPracticasBDController.consultaNombreCiclo(a.getIdCiclo());
        } catch (Exception e) {
            col3 = "Error, idCiclo: " + a.getIdCiclo();
        }
        inicializaConsulta(eConsulta);
        inicializaModifica(eModifica);
        inicializaElimina(eElimina);
    }

    //metodo que recibe una empresa y 3 eventos, da valor a 2 columnas e inicializa los botones
    public DatosTabla(Empresa emp, EventHandler<MouseEvent> cons, EventHandler<MouseEvent> modf,
                      EventHandler<MouseEvent> eli) {
        col1 = String.valueOf(emp.id);
        col2 = emp.nombre;
        inicializaConsulta(cons);
        inicializaModifica(modf);
        inicializaElimina(eli);
    }

    //inicializacion del boton consulta
    private void inicializaConsulta(EventHandler<MouseEvent> e) {
        //configuracion inicial del boton llamando a disenoBaseBtno
        btnConsultar = disenoBaseBtn("com/example/gestionpracticasfinal/img/consultaBtn.png",
                "boton-consultar");
        //evento asignado al entrar el raton en el area del boton, carga la animacion de este asignandole
        //un gif como imagen de tamaño definido como la variable final
        btnConsultar.setOnMouseEntered(event -> {
            ImageView img = new ImageView("com/example/gestionpracticasfinal/img/consultaBtn.gif");
            img.setFitWidth(BTNIMGSIZE);
            img.setFitHeight(BTNIMGSIZE);
            btnConsultar.setGraphic(img);
        });

        //evento para que al salir el raton del area del boton se cargue de nuevo la imagen png estatica en este
        btnConsultar.setOnMouseExited(event -> {
            ImageView img = new ImageView("com/example/gestionpracticasfinal/img/consultaBtn.png");
            img.setFitWidth(BTNIMGSIZE);
            img.setFitHeight(BTNIMGSIZE);
            btnConsultar.setGraphic(img);
        });
        //se le agrega el evento pasado como argumento del metodo como un mouseclicked
        btnConsultar.setOnMouseClicked(e);
    }

    private void inicializaElimina(EventHandler<MouseEvent> e) {
        //estilo base
        btnEliminar = disenoBaseBtn("com/example/gestionpracticasfinal/img/eliminaBtn.png",
                "boton-eliminar");
        //eventos para animacion al estar sobre el boton
        btnEliminar.setOnMouseEntered(event -> {
            ImageView img = new ImageView("com/example/gestionpracticasfinal/img/eliminaBtn.gif");
            img.setFitWidth(BTNIMGSIZE);
            img.setFitHeight(BTNIMGSIZE);
            btnEliminar.setGraphic(img);
        });

        btnEliminar.setOnMouseExited(event -> {
            ImageView img = new ImageView("com/example/gestionpracticasfinal/img/eliminaBtn.png");
            img.setFitWidth(BTNIMGSIZE);
            img.setFitHeight(BTNIMGSIZE);
            btnEliminar.setGraphic(img);
        });
        //se agrega su evento
        btnEliminar.setOnMouseClicked(e);
    }

    //metodo que devuelve un boton con un estilo base pasada una imagen y una clase css como strings
    private Button disenoBaseBtn(String stImg, String stClase) {
        ImageView img = new ImageView(stImg);
        img.setFitHeight(BTNIMGSIZE);
        img.setFitWidth(BTNIMGSIZE);
        Button btn = new Button(null, img);
        btn.getStylesheets().add(HOJAESTILOS.toURI().toString());
        btn.getStyleClass().add(stClase);
        return btn;
    }

    private void inicializaModifica(EventHandler<MouseEvent> evento) {
        //estilo base
        btnModificar = disenoBaseBtn("com/example/gestionpracticasfinal/img/modificaBtn.png",
                "boton-modificar");

        //eventos para animacion al estar sobre el boton
        btnModificar.setOnMouseEntered(event -> {
            ImageView img = new ImageView("com/example/gestionpracticasfinal/img/modificaBtn.gif");
            img.setFitWidth(BTNIMGSIZE);
            img.setFitHeight(BTNIMGSIZE);
            btnModificar.setGraphic(img);
        });

        btnModificar.setOnMouseExited(event -> {
            ImageView img = new ImageView("com/example/gestionpracticasfinal/img/modificaBtn.png");
            img.setFitWidth(BTNIMGSIZE);
            img.setFitHeight(BTNIMGSIZE);
            btnModificar.setGraphic(img);
        });

        //se agrega su evento especifico
        btnModificar.setOnMouseClicked(evento);
    }

    //getters y setters de la clase
    public String getCol4() {
        return col4;
    }

    public void setCol4(String col4) {
        this.col4 = col4;
    }

    public Button getBtnConsultar() {
        return btnConsultar;
    }

    public void setBtnConsultar(Button btnConsultar) {
        this.btnConsultar = btnConsultar;
    }

    public Button getBtnModificar() {
        return btnModificar;
    }

    public void setBtnModificar(Button btnModificar) {
        this.btnModificar = btnModificar;
    }

    public Button getBtnEliminar() {
        return btnEliminar;
    }

    public void setBtnEliminar(Button btnEliminar) {
        this.btnEliminar = btnEliminar;
    }

    public String getCol1() {
        return col1;
    }

    public void setCol1(String col1) {
        this.col1 = col1;
    }

    public String getCol2() {
        return col2;
    }

    public void setCol2(String col2) {
        this.col2 = col2;
    }

    public String getCol3() {
        return col3;
    }

    public void setCol3(String col3) {
        this.col3 = col3;
    }
}
