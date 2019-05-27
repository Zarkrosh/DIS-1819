package es.uva.eii.ds.empresaX.persistencia;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.uva.eii.ds.empresaX.servicioscomunes.JSONHelper;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Abel Herrero Gómez (abeherr)
 * @author Daniel De Vicente Garrote (dandevi)
 * @author Roberto García Antoranz (robegar)
 */
public class FachadaPersistenciaEncargado {

    // FACTURAS PENDIENTES DE PAGO
    // Devuelve el ID del proveedor especificado
    private static final String QUERY_ID_PROVEEDOR = "SELECT cif FROM Proveedor WHERE UPPER(nombre) = (?)";
    // Devuelve el año de la primera fecha de emisión
    private static final String QUERY_MIN_ANIO_FAC = "SELECT YEAR(fechaDeEmision) FROM Factura WHERE ... = (?)";
    // Devuelve el año de la última fecha de emisión
    private static final String QUERY_MAX_ANIO_FAC = "SELECT YEAR(fechaDeEmision) FROM Factura WHERE ... = (?)";
    // Devuelve las facturas pendientes, en el rango de fechas especificado, para el proveedor especificado
    private static final String QUERY_FACTURAS_PEND = 
            "SELECT * FROM "
            + "Factura INNER JOIN PedidoAProveedor ON Factura.pedido = PedidoAProveedor.numeroDePedido "
            + "INNER JOIN proveedor ON PedidoAProveedor.proveedor = Proveedor.cif "
            + "WHERE fechaDeEmision >= (?) AND fechaDeEmision <= (?) AND enTransferencia IS NULL";
    private static final String QUERY_PLUS_PROVEEDOR = " AND proveedor = (?)";

    private static ConexionBD conectarse() throws ClassNotFoundException, SQLException {
        return ConexionBD.getInstancia();
    }
    
    /**
     * Devuelve el año de la primera factura.
     *
     * @return Año de la primera factura
     */
    public static int getMinAnioFacturas() {
        // TODO
        return 2014;
    }

    /**
     * Devuelve el año de la última factura.
     *
     * @return Año de la última factura
     */
    public static int getMaxAnioFacturas() {
        // TODO
        return 2019;
    }

    /**
     * Devuelve el id de un proveedor en la BD con el nombre especificado.
     *
     * @param proveedor Nombre de proveedor
     * @return ID del proveedor (null si no existe)
     */
    public static String getCIFProveedor(String proveedor) {
        String cif = null;

        try {
            ConexionBD conn = conectarse();
            PreparedStatement pst = conn.prepareStatement(QUERY_ID_PROVEEDOR);
            pst.setString(1, proveedor.toUpperCase());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                cif = rs.getString("cif");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(FachadaPersistenciaEncargado.class.getName()).log(Level.SEVERE, null, ex);
        }

        return cif;
    }

    /**
     * Devuelve un JSON con las facturas pendientes de pago en el rango de fechas
     * seleccionado y para el proveedor requerido.
     *
     * @param fechaI Fecha mínima de emisión
     * @param fechaF Fecha máxima de emisión
     * @param proveedor Proveedor del pedido (null para cualquiera)
     * @return Facturas que cumplen los requisitos de búsqueda (JSON)
     */
    public static String getFacturasPendientesDePago(LocalDate fechaI, LocalDate fechaF, String proveedor) {
        // Obtiene la lista de facturas
        JsonArray arrayFacturas = new JsonArray();
        
        try {
            ConexionBD conn = conectarse();
            String query = QUERY_FACTURAS_PEND;
            if(proveedor != null) {
                // Proveedor especificado
                query += QUERY_PLUS_PROVEEDOR;
            }
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setDate(1, Date.valueOf(fechaI));
            pst.setDate(2, Date.valueOf(fechaF));
            if(proveedor != null) { 
                pst.setString(3, proveedor);
            }
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                JsonObject factura = new JsonObject();
                // Atributos directos de la factura
                factura.addProperty(JSONHelper.JSON_FECHA_EMISION, rs.getDate("FECHADEEMISION").toString());
                factura.addProperty(JSONHelper.JSON_IMPORTE, rs.getDouble("IMPORTE"));
                factura.addProperty(JSONHelper.JSON_CUENTA_BANCARIA, rs.getString("CUENTABANCARIA"));
                factura.add(JSONHelper.JSON_PEDIDO, getPedido(rs)); // Añade el pedido

                // Añade la factura a la lista
                arrayFacturas.add(factura);
            }

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(FachadaPersistenciaEncargado.class.getName()).log(Level.SEVERE, null, ex);
            arrayFacturas = new JsonArray(); // Vacío
        }

        // Construye el objeto resultado
        JsonObject facturasPendientes = new JsonObject();
        facturasPendientes.add(JSONHelper.JSON_FACTURAS_PENDIENTES, arrayFacturas);
        
        return facturasPendientes.toString();
    }

    /**
     * Devuelve el objeto JSON correspondiente al pedido.
     * @param rs Resultado de la consulta
     * @return JSON del pedido
     * @throws SQLException 
     */
    private static JsonObject getPedido(ResultSet rs) throws SQLException {
        JsonObject pedido = new JsonObject();
        
        pedido.addProperty(JSONHelper.JSON_NUM_PEDIDO, rs.getLong("NUMERODEPEDIDO"));
        pedido.addProperty(JSONHelper.JSON_FECHA_REALIZACION, rs.getDate("FECHADEREALIZACION").toString());
        pedido.addProperty(JSONHelper.JSON_PENDIENTE, rs.getString("ESTAPENDIENTE"));
        // Proveedor
        pedido.add(JSONHelper.JSON_PROVEEDOR, getProveedor(rs));
        
        return pedido;
    }
    
    /**
     * Devuelve el objeto JSON correspondiente al proveedor.
     * @param rs Resultado de la consulta
     * @return JSON del proveedor
     * @throws SQLException 
     */
    private static JsonObject getProveedor(ResultSet rs) throws SQLException {
        JsonObject proveedor = new JsonObject();
        
        proveedor.addProperty(JSONHelper.JSON_NOMBRE, rs.getString("NOMBRE"));
        proveedor.addProperty(JSONHelper.JSON_TELEFONO, rs.getString("TELEFONO"));
        proveedor.addProperty(JSONHelper.JSON_EMAIL, rs.getString("EMAIL"));
        
        return proveedor;
    }
    
}
