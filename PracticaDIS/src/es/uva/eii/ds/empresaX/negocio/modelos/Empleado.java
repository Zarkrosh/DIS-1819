package es.uva.eii.ds.empresaX.negocio.modelos;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.time.LocalDate;
import java.util.TreeMap;

/**
 * @author Abel Herrero Gómez         (abeherr)
 * @author Daniel De Vicente Garrote  (dandevi)
 * @author Roberto García Antoranz    (robegar)
 */
public class Empleado {
    
    public static final String JSON_DNI = "dni";
    public static final String JSON_NOMBRE = "nombre";
    public static final String JSON_APELLIDOS = "apellidos";
    public static final String JSON_FECHA_INICIO = "fechaInicio";
    public static final String JSON_COMIENZO = "comienzo";
    public static final String JSON_FINAL_PREVISTO = "finalPrevisto";
    public static final String JSON_ROLES = "roles";
    public static final String JSON_ROL = "rol";
    public static final String JSON_VINCULACIONES = "vinculaciones";
    public static final String JSON_VINCULACION = "vinculacion";
    public static final String JSON_DISPONIBILIDADES = "disponibilidades";
    public static final String JSON_DISPONIBILIDAD = "disponibilidad";

    public static final String JSON_ERROR = "error";
    
    private String dni;
    private String nombre;
    private String apellidos;
    private LocalDate fechaInicioEnEmpresa;
    private TreeMap<LocalDate, Rol> rolesEnLaEmpresa;
    private TreeMap<LocalDate, VinculacionConLaEmpresa> estadoDeVinculacion;
    private TreeMap<LocalDate, Disponibilidad> estadoDeDisponibilidad;
    
    /**
     * Construye un objeto Empleado a partir de una cadena JSON.
     * 
     * Ejemplo de jsonString: 
     * { "dni"              : "12345678Z",
     *   "nombre"           : "Hermenegildo Manuel",
     *   "apellidos"        : "Ruipérez Núñez",
     *   "fechaInicio"      : "2014-02-25",
     *   "roles"            : [
     *      { "comienzo" : "2014-02-25", "rol" : "Dependiente" },
     *      { "comienzo" : "2015-04-14", "rol" : "Supervisor" }
     *   ],
     *   "vinculaciones"    : [
     *      { "comienzo" : "2014-02-25", "vinculacion" : "Contratado" }
     *   ],
     *   "disponibilidades" : [
     *      { "comienzo" : "2014-02-25", "disponibilidad" : "Trabajando" },
     *      { "comienzo" : "2014-06-23", "finalPrevisto" : "2014-08-29", "disponibilidad" : "Vacaciones" },
     *      { "comienzo" : "2014-08-29", "disponibilidad" : "Trabajando" },
     *      { "comienzo" : "2014-11-05", "finalPrevisto" : "2015-02-05", "disponibilidad" : "BajaTemporal" },
     *      { "comienzo" : "2015-02-12", "disponibilidad" : "Trabajando" }
     *   ]
     * }
     * 
     * @param jsonString Cadena JSON
     */
    public Empleado(String jsonString) {
        try {
            JsonObject jo = new Gson().fromJson(jsonString, JsonObject.class);
            // NOMBRE, APELLIDOS Y DNI
            nombre = jo.get(JSON_NOMBRE).getAsString();
            apellidos = jo.get(JSON_APELLIDOS).getAsString();
            dni = jo.get(JSON_DNI).getAsString();
            // FECHA DE INICIO
            String[] fechaI = jo.get(JSON_FECHA_INICIO).getAsString().split("-");
            fechaInicioEnEmpresa = LocalDate.of(
                    Integer.valueOf(fechaI[0]), // YYYY
                    Integer.valueOf(fechaI[1]), // MM
                    Integer.valueOf(fechaI[2])  // DD
            );
            
            configuraRoles(jo);
            configuraVinculaciones(jo);
            configuraDisponibilidades(jo);
        } catch(JsonSyntaxException | NumberFormatException e) {
            // Especificar excepciones
            System.out.println("[!] Excepción al crear Empleado:");
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene los roles y los añade a la lista.
     * @param jo Objeto JSON
     */
    private void configuraRoles(JsonObject jo) {
        rolesEnLaEmpresa = new TreeMap<>();
        JsonArray jRoles = jo.getAsJsonArray(JSON_ROLES);
        for(JsonElement jr : jRoles) {
            JsonObject jRol = new Gson().fromJson(jr.toString(), JsonObject.class);
            String[] fechaComienzo = jRol.get(JSON_COMIENZO).getAsString().split("-");
            LocalDate comienzo = LocalDate.of(
                Integer.valueOf(fechaComienzo[0]), // YYYY
                Integer.valueOf(fechaComienzo[1]), // MM
                Integer.valueOf(fechaComienzo[2])  // DD
            );
            Rol rol = new Rol(TipoRol.valueOf(jRol.get(JSON_ROL).getAsString()));
            rolesEnLaEmpresa.put(comienzo, rol);
        }
    }
    
    /**
     * Obtiene las vinculaciones y las añade a la lista.
     * @param jo Objeto JSON
     */
    private void configuraVinculaciones(JsonObject jo) {
        estadoDeVinculacion = new TreeMap<>();
        JsonArray jVinculaciones = jo.getAsJsonArray(JSON_VINCULACIONES);
        for(JsonElement jv : jVinculaciones) {
            JsonObject jVinculacion = new Gson().fromJson(jv.toString(), JsonObject.class);
            String[] fechaComienzo = jVinculacion.get(JSON_COMIENZO).getAsString().split("-");
            LocalDate comienzo = LocalDate.of(
                Integer.valueOf(fechaComienzo[0]), // YYYY
                Integer.valueOf(fechaComienzo[1]), // MM
                Integer.valueOf(fechaComienzo[2])  // DD
            );
            VinculacionConLaEmpresa vinculacion = new VinculacionConLaEmpresa(TipoVinculacion.valueOf(jVinculacion.get(JSON_VINCULACION).getAsString()));
            estadoDeVinculacion.put(comienzo, vinculacion);
        }
    }
    
    /**
     * Obtiene las disponibilidades y las añade a la lista.
     * @param jo Objeto JSON
     */
    private void configuraDisponibilidades(JsonObject jo) {
        estadoDeDisponibilidad = new TreeMap<>();
        JsonArray jDisponibilidades = jo.getAsJsonArray(JSON_DISPONIBILIDADES);
        for(JsonElement jd : jDisponibilidades) {
            JsonObject jDisponibilidad = new Gson().fromJson(jd.toString(), JsonObject.class);
            String[] fechaComienzo = jDisponibilidad.get(JSON_COMIENZO).getAsString().split("-");
            LocalDate comienzo = LocalDate.of(
                Integer.valueOf(fechaComienzo[0]), // YYYY
                Integer.valueOf(fechaComienzo[1]), // MM
                Integer.valueOf(fechaComienzo[2])  // DD
            );
            Disponibilidad disponibilidad = new Disponibilidad(TipoDisponibilidad.valueOf(jDisponibilidad.get(JSON_DISPONIBILIDAD).getAsString()));
            estadoDeDisponibilidad.put(comienzo, disponibilidad);
        }
    }
    

    /**
     * Devuelve el DNI del empleado, que actúa como identificador único.
     * @return DNI del empleado
     */
    public String getDni() {
        return dni;
    }
    
    /**
     * Devuelve el nombre del empleado.
     * @return Nombre del empleado
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Devuelve los apellidos del empleado.
     * @return Apellidos del empleado
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Devuelve la fecha de inicio en la empresa.
     * @return Fecha de inicio en la empresa
     */
    public LocalDate getFechaInicioEnEmpresa() {
        return fechaInicioEnEmpresa;
    }
    
    /**
     * Devuelve true si el empleado está actualmente activo, false si no.
     * @return true si está activo
     */
    public boolean estaActivo() {
        boolean activoDisponible  = estadoDeDisponibilidad.lastEntry().
                                    getValue().estaEnActivo();
        boolean activoVinculacion = estadoDeVinculacion.lastEntry().
                                    getValue().estaEnActivo();
        
        return activoDisponible && activoVinculacion;
    }
    
    /**
     * Devuelve el rol actual del empleado (el último).
     * @return Rol actual
     */
    public Rol obtenerRolActual() {
        return rolesEnLaEmpresa.lastEntry().getValue();
    }
    
}