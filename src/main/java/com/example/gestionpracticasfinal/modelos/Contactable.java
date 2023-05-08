package com.example.gestionpracticasfinal.modelos;

//clase contactable que tiene los atributos comunes de empresa y alumno
public class Contactable {
    protected int id;
    protected String nombre;
    protected String telefono;
    protected String email;

    //constructor vacio
    public Contactable() {
    }

    //constructor que pide telefono y email
    public Contactable(String telefono, String email) {
        setTelefono(telefono);
        setEmail(email);
    }
    //constructor que pide id telefono y email
    public Contactable(int id, String telefono, String email) {
        this.id = id;
        setTelefono(telefono);
        setEmail(email);
    }

    //getters y setters de la clase
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        //se comprueba si el telefono es correcto
        if (telCorrecto(telefono)) {
            this.telefono = telefono;
        }else {
            //en caso de no ser correcto se guarda un vacio en el atributo
            this.telefono = "";
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        //comprueba si el email es correcto
        if (emailCorrecto(email)) {
            if ((email.length() > 40)) {
                this.email = "";
            } else {
                this.email = email;
            }
        }else {
            //sino, se guarda un string vacio
            this.email = "";
        }
    }

    //override del tostring
    @Override
    public String toString() {
        return "Contactable{" +
                "nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    //metodo estatico que comprueba si el string pasado como parametro es un numero de telefono valido
    //devuelve un boolean
    public static boolean telCorrecto(String telefono) {
        //se asegura de que el string pasado se compruebe sin espacios
        if (telefono != null && telefono.length() > 9){
            telefono = telefono.replaceAll(" ", "");
            telefono = telefono.replaceAll("-", "");
            telefono = telefono.replaceAll("\\.", "");
        }
        //se comprueba si la longitud del string es de 9 digitos y si son solo numeros
        if (telefono != null && telefono.length() >= 9 && telefono.length() <= 15) {
            for (int i = 0; i < telefono.length(); i++) {
                if (telefono.charAt(i) < '0' || telefono.charAt(i) > '9') {
                    if (telefono.charAt(i) == '+' && i == 0) {
                        continue;
                    }
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    //se comprueba que el string pasado como parametro es un email valido al no tener espacios y tener 1 arroba
    public static boolean emailCorrecto(String email) {
        return email.indexOf('@') > -1 && email.indexOf(' ') == -1 && email.length() <= 40;
    }
}
