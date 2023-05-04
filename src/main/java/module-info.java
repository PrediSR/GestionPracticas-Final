module com.example.gestionpracticasfinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.gestionpracticasfinal to javafx.fxml;
    exports com.example.gestionpracticasfinal;
    exports com.example.gestionpracticasfinal.Controladores;
    opens com.example.gestionpracticasfinal.Controladores to javafx.fxml;
}