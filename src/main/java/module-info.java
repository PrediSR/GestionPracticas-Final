module com.example.gestionpracticasfinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.gestionpracticasfinal to javafx.fxml;
    exports com.example.gestionpracticasfinal;
    exports com.example.gestionpracticasfinal.controladores;
    opens com.example.gestionpracticasfinal.controladores to javafx.fxml;
    exports com.example.gestionpracticasfinal.modelos;
    opens com.example.gestionpracticasfinal.modelos to javafx.fxml;
}