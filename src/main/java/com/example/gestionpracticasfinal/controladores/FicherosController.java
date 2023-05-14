package com.example.gestionpracticasfinal.controladores;

import com.example.gestionpracticasfinal.modelos.Alumno;

import java.io.File;
import java.io.FileWriter;

public class FicherosController {
    public static void escribirCiclo(File file, Alumno[] lista) throws Exception {
        FileWriter fw = new FileWriter(file);

        for (Alumno a : lista) {
            fw.write(a.toString());
        }

        fw.close();
    }
}
