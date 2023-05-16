package com.example.gestionpracticasfinal.controladores;

import com.example.gestionpracticasfinal.modelos.Alumno;

import java.io.File;
import java.io.FileWriter;

public class FicherosController {
    //metodo estatico que recibe un fichero y una lista de alumnos
    public static void escribirCiclo(File file, Alumno[] lista) throws Exception {
        //crea un File writter con el fichero
        FileWriter fw = new FileWriter(file);

        //por cada alumno de la lista lo escribe en el fichero con el metodo toString
        for (Alumno a : lista) {
            fw.write(a.toString());
        }

        //se cierra el file writter
        fw.close();
    }
}
