package com.example.gestionpracticasfinal.modelos;

public class Empresa extends Contactable {
    //Atributos de la clase
    private String personaContacto;
    private String direccion;

    //constructor vacio
    public Empresa() {
    }

    //constructor que pide todos los atributos
    public Empresa(int id, String nombre, String personaContacto, String telefono, String email, String direccion) {
        super(id, telefono, email);
        this.personaContacto = personaContacto;
        this.direccion = direccion;
        setNombre(nombre);
    }

    //override de setNombre para poner limite de caracteres en 50
    @Override
    public void setNombre(String nombre) {
        if (nombre.length() <= 50) {
            super.setNombre(nombre);
        }else {
            super.setNombre("");
        }
    }

    //getters y setters de la clase
    public String getPersonaContacto() {
        return personaContacto;
    }

    public void setPersonaContacto(String personaContacto) {
        if (personaContacto.length() > 70) {
            this.personaContacto = "";
        } else {
            this.personaContacto = personaContacto;
        }
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        if (direccion.length() > 100) {
            this.direccion = "";
        } else {
            this.direccion = direccion;
        }
    }
}