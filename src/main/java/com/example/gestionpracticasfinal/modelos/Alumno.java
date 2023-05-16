package com.example.gestionpracticasfinal.modelos;

//clase que hereda de contactable para hacer los alumnos
public class Alumno extends Contactable{
    //atributos especificos de la clase alumno
    private String apellidos;
    private int idCiclo;

    //constructor vacio
    public Alumno() {
    }

    //constructor que pide todos los datos del alumno menos id
    public Alumno(String nombre, String apellidos, String telefono, String email, int idCiclo) {
        //llamada al constructor de la clase padre pasandole el nombre telefono e email
        super(telefono, email);
        setNombre(nombre);
        //asignar a los atributos apellidos y ciclo sus datos
        setApellidos(apellidos);
        this.idCiclo = idCiclo;
    }

    //constructor que pide todos los datos del alumno
    public Alumno(int id, String nombre, String apellidos, String telefono, String email, int idCiclo) {
        //llamada al constructor de la clase padre pasandole el id telefono e email
        super(id, telefono, email);
        //asignar a los atributos nombre, apellidos y ciclo sus datos
        setNombre(nombre);
        setApellidos(apellidos);
        this.idCiclo = idCiclo;
    }

    //override al setNombre de la clase Contactable para controlar el maximo de caracteres que admite
    @Override
    public void setNombre(String nombre) {
        if (nombre.length() <= 40) {
            super.setNombre(nombre);
        }else {
            super.setNombre("");
        }
    }

    //getters y setters de la clase
    public String getApellidos() {
        return apellidos;
    }

    //se controlan los caracteres maximos del apellido
    public void setApellidos(String apellidos) {
        if (apellidos.length() <= 40) {
            this.apellidos = apellidos;
        }else {
            this.apellidos = "";
        }
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public void setIdCiclo(int idCiclo) {
        this.idCiclo = idCiclo;
    }

    //override del to string que separa los atributos con ; y poder exportarlos
    @Override
    public String toString() {
        return id + ";" + nombre + ";" + apellidos + ";" + telefono + ";" + email;
    }

    //override del equals para comparar si 2 alumnos son iguales por todos sus datos menos la id
    @Override
    public boolean equals(Object obj) {
        Alumno a;

        if (obj instanceof Alumno) {
            a = (Alumno) obj;

            return nombre.equalsIgnoreCase(a.nombre) && apellidos.equalsIgnoreCase(a.apellidos) &&
                    telefono.equals(a.telefono) && email.equalsIgnoreCase(a.email) && idCiclo == a.idCiclo;
        }

        return false;
    }
}
