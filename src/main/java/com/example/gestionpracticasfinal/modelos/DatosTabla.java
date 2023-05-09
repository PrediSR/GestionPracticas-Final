package com.example.gestionpracticasfinal.modelos;

import com.example.gestionpracticasfinal.controladores.GestionPracticasBDController;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;

public class DatosTabla {
    private final int BTNIMGSIZE = 22;
    private final File HOJAESTILOS = new File("src/main/resources/com/example/gestionpracticasfinal/css/estilos.css");;
    private String col1;
    private String col2;
    private String col3;
    private String col4;
    private Button btnEliminar;
    private Button btnModificar;
    private Button btnConsultar;

    public DatosTabla(Alumno a) {
        col1 = a.nombre;
        col2 = a.getApellidos();
        col3 = a.telefono;
        col4 = a.email;
    }

    public DatosTabla(Alumno a, EventHandler<MouseEvent> eModifica) {
        col1 = a.nombre;
        col2 = a.getApellidos();
        try {
            col3 = GestionPracticasBDController.consultaNombreCiclo(a.getIdCiclo());
        } catch (Exception e) {
            col3 = "Error, idCiclo: " + a.getIdCiclo();
        }
        inicializaConsulta();
        inicializaModifica(eModifica);
        inicializaElimina();
    }

    public DatosTabla(Empresa emp) {
        col1 = emp.id + "";
        col2 = emp.nombre;
        inicializaConsulta();
        inicializaModifica(event -> {});
        inicializaElimina();
    }

    private void inicializaConsulta() {
        btnConsultar = disenoBaseBtn("com/example/gestionpracticasfinal/img/consultaBtn.png",
                "boton-consultar");
        btnConsultar.setOnMouseEntered(event -> {
            ImageView img = new ImageView("com/example/gestionpracticasfinal/img/consultaBtn.gif");
            img.setFitWidth(BTNIMGSIZE);
            img.setFitHeight(BTNIMGSIZE);
            btnConsultar.setGraphic(img);
        });

        btnConsultar.setOnMouseExited(event -> {
            ImageView img = new ImageView("com/example/gestionpracticasfinal/img/consultaBtn.png");
            img.setFitWidth(BTNIMGSIZE);
            img.setFitHeight(BTNIMGSIZE);
            btnConsultar.setGraphic(img);
        });
    }

    private void inicializaElimina() {
        btnEliminar = disenoBaseBtn("com/example/gestionpracticasfinal/img/eliminaBtn.png",
                "boton-eliminar");
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
    }

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
        btnModificar = disenoBaseBtn("com/example/gestionpracticasfinal/img/modificaBtn.png",
                "boton-modificar");

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

        btnModificar.setOnMouseClicked(evento);
    }

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
