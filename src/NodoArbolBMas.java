import java.util.ArrayList;
import java.util.List;


public class NodoArbolBMas {

    // ===================== Atributos =====================

    /** Indica si el nodo es una hoja (true) o un nodo interno (false). */
    private boolean esHoja;

    /** Claves del nodo, siempre en orden ascendente. */
    private List<Integer> claves;

    /** Hijos del nodo. Vacía si el nodo es una hoja. */
    private List<NodoArbolBMas> hijos;

    /** Datos asociados a cada clave. Solo existe en las hojas; null en nodos internos. */
    private List<String> datos;

    /** Referencia a la hoja siguiente. Solo se usa en hojas; null en nodos internos. */
    private NodoArbolBMas siguiente;

    // ===================== Constructor =====================

    /**
     * Crea un nodo vacío.
     *
     * @param esHoja true si el nodo será una hoja (y por lo tanto almacenará datos).
     */
    public NodoArbolBMas(boolean esHoja) {
        this.esHoja = esHoja;
        this.claves = new ArrayList<>();
        this.hijos = new ArrayList<>();
        // Solo las hojas almacenan datos; los nodos internos no los necesitan
        this.datos = esHoja ? new ArrayList<>() : null;
        // Al crearse, ninguna hoja tiene todavía una hoja siguiente
        this.siguiente = null;
    }

    // ===================== Getters =====================

    public boolean esHoja() {
        return esHoja;
    }

    public List<Integer> getClaves() {
        return claves;
    }

    public List<NodoArbolBMas> getHijos() {
        return hijos;
    }

    public List<String> getDatos() {
        return datos;
    }

    public NodoArbolBMas getSiguiente() {
        return siguiente;
    }

    // ===================== Setters =====================

    public void setClaves(List<Integer> claves) {
        this.claves = claves;
    }

    public void setHijos(List<NodoArbolBMas> hijos) {
        this.hijos = hijos;
    }

    /**
     * Asigna la lista de datos. Se ignora en nodos internos para mantener
     * la regla de que solo las hojas almacenan datos.
     */
    public void setDatos(List<String> datos) {
        if (esHoja) {
            this.datos = datos;
        }
    }

    /**
     * Asigna la hoja siguiente. Se ignora en nodos internos para mantener
     * la regla de que su referencia siguiente es siempre null.
     */
    public void setSiguiente(NodoArbolBMas siguiente) {
        if (esHoja) {
            this.siguiente = siguiente;
        }
    }
}
