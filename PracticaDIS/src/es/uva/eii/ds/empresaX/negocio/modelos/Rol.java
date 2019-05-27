package es.uva.eii.ds.empresaX.negocio.modelos;

/**
 * @author Abel Herrero Gómez         (abeherr)
 * @author Daniel De Vicente Garrote  (dandevi)
 * @author Roberto García Antoranz    (robegar)
 */
public class Rol {

    private TipoEstadoPeido tipo;
    
    public Rol(TipoEstadoPeido tipo){
        this.tipo = tipo;
    }

    public TipoEstadoPeido getTipo() {
        return tipo;
    }

    public void setTipo(TipoEstadoPeido tipo) {
        this.tipo = tipo;
    }
    
}
