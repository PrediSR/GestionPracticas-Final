package com.example.gestionpracticasfinal.controladores;

import com.example.gestionpracticasfinal.modelos.*;

import java.sql.*;
import java.util.ArrayList;

//Clase que conecta con la base de datos y devuelve la informacion consultada asi como actualiza, inserta o elimina
//lo que se le pida mediante sus metodos estaticos
public class GestionPracticasBDController {
    //Declaración de objeto de clase connection
    public static Connection conexion;

    //metodo para establecer la conexion con la base de datos, debe llamarse primero para poder usar el resto de metodos
    public static void crearConexion() throws Exception {
        String bd = "jdbc:mysql://localhost:3306/gestionpracticasdb";
        conexion = DriverManager.getConnection(bd, "root", "root");
    }

    //funcion que recibe un string con el nombre de un ciclo para insertarlo en la tabla de ciclos, devuelve verdadero
    //si pudo insertarla y falso en caso contrario
    public static boolean altaCiclo(String ciclo) throws Exception {
        //variable que sera devuelta inicializada en false
        boolean out = false;
        //consulta a realizar
        String consulta = "insert into ciclo values (default, ?);";
        //prepared statement con la consulta
        PreparedStatement ps = conexion.prepareStatement(consulta);

        //se introduce en la interrogante de la consulta el nombre del ciclo
        ps.setString(1, ciclo);
        //en caso de no insertar filas o dar error por nombre duplicado pone la salida a false
        try {
            //se ejecuta la insercion del ciclo en la tabla
            int n = ps.executeUpdate();
            out = n != 0;
        }catch (SQLException ignored) {}

        //cierra el preparedstatement y se devuelve out
        ps.close();
        return out;
    }

    //metodo que devuelve un array de string con los nombres de los ciclos que hay en la tabla
    public static String[] consultaCiclosNombres() throws Exception {
        //se crea el arraylist que guarda los nombres
        ArrayList<String> lista = new ArrayList<>();
        //consulta a realizar
        String consulta = "select nombre from ciclo;";
        //instancia de statement, ejecucion de la consulta y guardar los resultados en un resulset
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(consulta);

        //se añade cada nombre devuelto por la consulta en el resulset
        while (rs.next()) {
            lista.add(rs.getString(1));
        }

        //conversion del arraylist a un array
        String[] out = new String[lista.size()];
        out = lista.toArray(out);
        //se cierran rs y st y se devuelve el array de strings
        rs.close();
        st.close();
        return out;
    }

    public static String[] buscaCiclosNombre(String nombre) throws Exception {
        ArrayList<String> lista = new ArrayList<>();
        String consulta = "select nombre from ciclo where nombre like ?;";
        PreparedStatement ps = conexion.prepareStatement(consulta);
        ps.setString(1, "%" + nombre + "%");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(rs.getString(1));
        }

        String[] out = new String[lista.size()];
        out = lista.toArray(out);
        rs.close();
        ps.close();
        return out;
    }

    //metodo para obtener el id de un ciclo pasando un nombre
    public static int consultaIdCiclo(String nombre) throws Exception {
        //preparacion de la consulta e insercion de la variable de nombre en ella
        String consulta = "select id from ciclo where nombre = ?;";
        PreparedStatement ps = conexion.prepareStatement(consulta);
        ps.setString(1, nombre);

        //ejecucion de la consulta y si devuelve un valor se guarda en la variable de salida, sino se pone -10
        ResultSet rs = ps.executeQuery();
        int out = rs.next() ? rs.getInt(1) : -10;

        //se cierran ps y rs y se devuelve la salida
        ps.close();
        rs.close();
        return out;
    }

    //consulta el nombre de un ciclo al pasarle su id
    public static String consultaNombreCiclo (int id) throws Exception {
        //preparacion de la consulta
        String consulta = "select nombre from ciclo where id = ?;";
        PreparedStatement ps = conexion.prepareStatement(consulta);
        ps.setInt(1, id);

        //ejecucion y recogida del resultado de la consulta en caso de que haya, sino se pone out a null
        ResultSet rs = ps.executeQuery();
        String out = rs.next() ? rs.getString(1) : null;

        //cierra ps y rs y se devuelve out
        ps.close();
        rs.close();
        return out;
    }

    public static int numCiclos() throws Exception {
        int output = 0;
        String consulta = "select count(*) from ciclo;";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(consulta);

        while (rs.next()) {
            output = rs.getInt(1);
        }

        st.close();
        rs.close();
        return output;
    }

    //metodo para insertar alumnos en la tabla de alumnos pasandole un alumno
    public static boolean insertarAlumno(Alumno a) throws Exception {
        //variable que determina la salida con las filas afectadas
        int out;
        //preparacion de la consulta
        String consulta = "insert into alumno values (default, ?, ?, ?, ?, ?);";
        PreparedStatement ps = conexion.prepareStatement(consulta);

        //set de todos los datos del alumno en los ? de la consulta
        ps.setString(1, a.getNombre());
        ps.setString(2, a.getApellidos());
        ps.setString(3, a.getTelefono());
        ps.setString(4, a.getEmail());
        ps.setInt(5, a.getIdCiclo());

        //al ejecutar la consulta se guarda las filas afectadas
        try {
            out = ps.executeUpdate();
            //en caso de tirar una excepcion se pone out a -1
        }catch (Exception e) {
            out = -1;
        }

        //devuelve si out es mayor que 0
        return out > 0;
    }

    //metodo que devuelve un string con un email generado pasando un alumno mediante la llamada a un procedimiento almacenado
    public static String generaEmail(Alumno a) throws Exception {
        //variable que recogera el resultado
        String out;
        //consulta a realizar
        String consulta = "call generaEmail(?, ?, ?, 'cieep.com', ?)";
        //se guarda en un callablestatement la preparacion de la llamada al procedimiento
        CallableStatement cs = conexion.prepareCall(consulta);

        //se hace set a las ? necesarias para ejecutar el procedimiento
        cs.setString(1, a.getNombre());
        cs.setString(2, a.getApellidos());
        cs.setString(3, a.getTelefono());
        //se ejecuta el procedimiento
        cs.execute();
        //se recoge la variable de salida del procedimiento
        out = cs.getString(4);

        //se cierra cs y se devuelve out
        cs.close();
        return out;
    }

    //metodo que devuelve un booleano segun si el string pasado ya existe como correo en las tablas alumno y empresa
    public static boolean existeCorreo(String email) throws Exception {
        //variable que sera devuelta
        boolean out;
        //se prepara la consulta
        String consulta = "select existeCorreo(?)";
        PreparedStatement ps = conexion.prepareStatement(consulta);

        ps.setString(1, email);
        //se recoge el resultado
        ResultSet rs = ps.executeQuery();
        rs.next();
        //se guarda en out el resultado
        out = rs.getBoolean(1);

        //se cierran rs y ps y se devuelve out
        rs.close();
        ps.close();
        return out;
    }

    //metodo que recibe un nombre y la id de un ciclo para buscar alumnos que coincidan en la tabla
    public static Alumno[] consultaBuscaAlumnos(String nombre, String apellidos, String ciclo) throws Exception {
        ArrayList<Alumno> lista = new ArrayList<>();
        int id_ciclo = consultaIdCiclo(ciclo), posApe = 1, posCiclo = 1;
        String consulta = consultaBusqueda(nombre, apellidos, id_ciclo);
        PreparedStatement ps = conexion.prepareStatement(consulta);

        if (nombre != null) {
            ps.setString(1, "%" + nombre + "%");
            posApe++;
            posCiclo++;
        }
        if (apellidos != null) {
            ps.setString(posApe, "%" + apellidos + "%");
            posCiclo++;
        }
        if (ciclo != null) {
            ps.setInt(posCiclo, id_ciclo);
        }
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(new Alumno(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getInt(6)));
        }
        Alumno[] out = new Alumno[lista.size()];
        out = lista.toArray(out);

        ps.close();
        rs.close();
        return out;
    }

    private static String consultaBusqueda(String nombre, String apellidos, int ciclo) {
        String out = "select * from alumno where ";
        out = nombre != null ? out.concat("nombre like ?") : out;
        out = nombre != null && apellidos != null || nombre != null && ciclo != -10 ?
                out.concat(" and ") : out;
        out = apellidos != null ? out.concat("apellidos like ?") : out;
        out = apellidos != null && ciclo != -10 ? out.concat(" and ") : out;
        out = ciclo != -10 ? out.concat("id_ciclo = ?") : out;
        out = out.concat(";");
        return out;
    }

    //devuelve todos los alumnos que pertenezcan a un ciclo
    public static Alumno[] consultaAlumnosPorIdCiclo(int idC) throws Exception {
        //se prepara la consulta
        String consulta = "select id, nombre, apellidos, telefono, email from alumno where id_ciclo = ?;";
        PreparedStatement ps = conexion.prepareStatement(consulta);
        ps.setInt(1, idC);
        //se guarda el resultado de la ejecucion
        ResultSet rs = ps.executeQuery();

        //se devuelve el resultado de llamar al metodo getAlumnos
        return getAlumnos(idC, ps, rs);
    }

    public static Alumno[] consultaAlumnosPorNombreCiclo(String nombreC) throws Exception {
        int idC = consultaIdCiclo(nombreC);
        return consultaAlumnosPorIdCiclo(idC);
    }

    public static Alumno[] consultaAlumnos() throws Exception {
        ArrayList<Alumno> lista = new ArrayList<>();
        //se prepara la consulta
        String consulta = "select * from alumno;";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(consulta);
        //por cada resultado del rs pasado se guarda un alumno en la lista
        while (rs.next()) {
            lista.add(new Alumno(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getInt(6)));
        }

        //se guarda el arraylist en un array de alumnos
        Alumno[] out = new Alumno[lista.size()];
        out = lista.toArray(out);
        //se cierran rs y ps y se devuelve out
        rs.close();
        st.close();
        return out;
    }

    //metodo que recibe un resulset e idC para guardar los alumnos obtenidos en un array que sera devuelto, tambien
    //recibe el ps para cerrarlo
    private static Alumno[] getAlumnos(int idC, PreparedStatement ps, ResultSet rs) throws Exception {
        //instancia de arraylist de alumnos
        ArrayList<Alumno> lista = new ArrayList<>();
        //por cada resultado del rs pasado se guarda un alumno en la lista
        while (rs.next()) {
            lista.add(new Alumno(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), idC));
        }

        //se guarda el arraylist en un array de alumnos
        Alumno[] out = new Alumno[lista.size()];
        out = lista.toArray(out);
        //se cierran rs y ps y se devuelve out
        rs.close();
        ps.close();
        return out;
    }

    public static int ultimaId() throws SQLException {
        int out = -1;
        String consulta = "select last_insert_id();";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(consulta);

        while (rs.next()) {
            out = rs.getInt(1);
        }

        rs.close();
        st.close();
        return out;
    }

    //metodo que actualiza un alumno de la tabla alumnos pasandole un objeto alumno
    public static boolean actualizaAlumno(Alumno a) throws Exception {
        boolean out = false;
        //se prepara la consulta
        String consulta = "update alumno set nombre = ?, apellidos = ?, telefono = ?, email = ?, id_ciclo = ? where id = ?;";
        PreparedStatement ps = conexion.prepareStatement(consulta);

        ps.setString(1, a.getNombre());
        ps.setString(2, a.getApellidos());
        ps.setString(3, a.getTelefono());
        ps.setString(4, a.getEmail());
        //el alumno que se pase tendra la misma id que la fila a modificar de la tabla
        ps.setInt(5, a.getIdCiclo());
        ps.setInt(6, a.getId());
        //variable de salida que devuelve true si se pudo modificar una fila
        try {
            out = ps.executeUpdate() > 0;
        }catch (Exception ignored) {}
        //se cierra ps y devuelve out
        ps.close();
        return out;
    }

    //metodo que elimina un alumno pasando su id
    public static void eliminaAlumno(int id) throws Exception {
        //se prepara la consulta
        String consulta = "delete from alumno where id = ?;";
        PreparedStatement ps = conexion.prepareStatement(consulta);

        ps.setInt(1, id);
        //se guarda si hubo cambios en la tabla
        ps.executeUpdate();

        //se cierra ps y devuelve out
        ps.close();
    }

    //metodo para insertar una empresa en la tabla de empresas pasando un objeto de la clase
    public static void insertarEmpresa(Empresa e) throws Exception {
        //se prepara la consulta
        String consulta = "insert into empresa values (default, ?, ?, ?, ?, ?);";
        PreparedStatement ps = conexion.prepareStatement(consulta);
        //llamada a metodo que insertara los datos de la empresa en la consulta
        setAtributosEmpresa(e, ps);
        //se ejecuta la consulta y se guarda en out si pudo realizar la insercion
        ps.executeUpdate();

        //se cierra ps y se devuelve out
        ps.close();
    }

    //metodo que devuelve un array con todas las empresas de la tabla
    public static Empresa[] consultaEmpresas() throws Exception {
        //realizacion de la consulta y guardar el resultado en un resulset
        String consulta = "select * from empresa";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(consulta);

        //se guarda las empresas en un array llamando al metodo getempresas pasandole el resulset
        Empresa[] out = getEmpresas(rs);
        //se cierra st y se devuelve el array
        st.close();
        return out;
    }

    public static Empresa consultaEmpresa(String nombre) throws Exception {
        Empresa out = null;
        String consulta = "select id, persona_contacto, telefono, email, direccion from empresa where nombre = ?";
        PreparedStatement ps = conexion.prepareStatement(consulta);
        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            out = new Empresa(rs.getInt(1), nombre, rs.getString(2),
                    rs.getString(3), rs.getString(4), rs.getString(5));
        }

        ps.close();
        rs.close();
        return out;
    }

    public static Empresa consultaEmpresa(int id) throws Exception {
        Empresa out = null;
        String consulta = "select nombre, persona_contacto, telefono, email, direccion from empresa where id = ?";
        PreparedStatement ps = conexion.prepareStatement(consulta);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            out = new Empresa(id, rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getString(4), rs.getString(5));
        }

        ps.close();
        rs.close();
        return out;
    }

    //metodo que devuelve cuantas empresas hay en total
    public static int numEmpresas() throws Exception {
        //realizacion de la consulta y guardado de resultado
        String consulta = "select count(*) from empresa";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(consulta);

        //se recoge el resultado en una variable out
        rs.next();
        int out = rs.getInt(1);

        //se cierra rs y st y se devuelve out
        rs.close();
        st.close();
        return out;
    }

    //metodo que devuelve un array de empresas que coincidan con el nombre pasado como parametro
    public static Empresa[] consultaBuscaEmpresas(String nombre) throws Exception {
        //se prepara la consulta
        String consulta = "select * from empresa where nombre like ?";
        PreparedStatement ps = conexion.prepareStatement(consulta);
        ps.setString(1, "%" + nombre + "%");
        //se guarda resultados
        ResultSet rs = ps.executeQuery();

        Empresa[] out = getEmpresas(rs);
        //se cierra ps y se devuelve el array
        ps.close();
        return out;
    }

    //metodo que pasandole un resulset almacena las empresas que tenga en un array que devolvera
    private static Empresa[] getEmpresas(ResultSet rs) throws Exception {
        //arraylist de empresas
        ArrayList<Empresa> lista = new ArrayList<>();
        //bucle que guardara cada empresa del resulset
        while (rs.next()) {
            lista.add(new Empresa(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)));
        }
        //se convierte el arraylist a array
        Empresa[] out = new Empresa[lista.size()];
        out = lista.toArray(out);
        //se cierra el resulset y se devuelve el array de empresas
        rs.close();
        return out;
    }

    //metodo que actualiza una empresa pasandole un objeto empresa
    public static void actualizaEmpresa(Empresa e) throws Exception {
        //se prepara la consulta
        String consulta = "update empresa set nombre = ?, persona_contacto = ?, telefono = ?, email = ?, direccion = ? " +
                "where id = ?;";
        PreparedStatement ps = conexion.prepareStatement(consulta);
        //se hace set a los atributos de la empresa con el metodo
        setAtributosEmpresa(e, ps);
        //se hace set de la id
        ps.setInt(6, e.getId());
        //se guarda en out si hubo filas afectadas
        ps.executeUpdate();

        //se cierra el ps y devuelve out
        ps.close();
    }

    // metodo que pasando un objeto empresa y un prepared statement se setean los atributos en las posiciones
    private static void setAtributosEmpresa(Empresa e, PreparedStatement ps) throws Exception {
        ps.setString(1, e.getNombre());
        ps.setString(2, e.getPersonaContacto());
        ps.setString(3, e.getTelefono());
        ps.setString(4, e.getEmail());
        ps.setString(5, e.getDireccion());
    }

    //metodo que elimina una empresa pasandole un id
    public static void eliminaEmpresa(int id) throws Exception {
        //se prepara la consulta
        String consulta = "delete from empresa where id = ?;";
        PreparedStatement ps = conexion.prepareStatement(consulta);

        ps.setInt(1, id);
        //se guarda si hubo cambios en la tabla en out
        ps.executeUpdate();

        //se cierra ps y se devuelve out
        ps.close();
    }

    public static void insertarPracticas(Practica p) throws SQLException {
        String consulta = "insert into practicas_alumno values (?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = conexion.prepareStatement(consulta);

        ps.setInt(1,p.getId_alumno());
        ps.setInt(2,p.getId_empresa());
        ps.setTime(3, Time.valueOf(p.gethEntrada()));
        ps.setTime(4, Time.valueOf(p.gethSalida()));
        ps.setDate(5, Date.valueOf(p.getfInicio()));
        ps.setDate(6, Date.valueOf(p.getfFin()));

        ps.executeUpdate();
        ps.close();
    }

    public static Practica consultaPractica(int id) throws Exception {
        Practica out = null;
        String consulta = "select id_empresa, horario_Inicio, horario_Fin, f_Inicio, f_Fin from practicas_alumno" +
                " where id_alumno = ?";
        PreparedStatement ps = conexion.prepareStatement(consulta);
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            out = new Practica(id, rs.getInt(1), rs.getTime(2).toLocalTime(),
                    rs.getTime(3).toLocalTime(), rs.getDate(4).toLocalDate(),
                    rs.getDate(5).toLocalDate());
        }

        ps.close();
        rs.close();
        return out;
    }

    public static void eliminaPractica(int id) throws Exception {
        String consulta = "delete from practicas_alumno where id_alumno = ?";
        PreparedStatement ps = conexion.prepareStatement(consulta);
        ps.setInt(1,id);

        ps.executeUpdate();

        ps.close();
    }

    public static void actualizaPractica(Practica p) throws Exception {
        String consulta = "update practicas_alumno set id_empresa = ?, horario_Inicio = ?, horario_Fin = ?, " +
                "f_Inicio = ?, f_Fin = ? where id_alumno = ?";
        PreparedStatement ps = conexion.prepareStatement(consulta);

        ps.setInt(1, p.getId_empresa());
        ps.setTime(2, Time.valueOf(p.gethEntrada()));
        ps.setTime(3, Time.valueOf(p.gethSalida()));
        ps.setDate(4, Date.valueOf(p.getfInicio()));
        ps.setDate(5, Date.valueOf(p.getfFin()));
        ps.setInt(6, p.getId_alumno());
        ps.executeUpdate();

        ps.close();
    }
}
