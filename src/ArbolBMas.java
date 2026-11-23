import java.util.ArrayList;
import java.util.List;


public class ArbolBMas {

    // ===================== Atributos =====================

    /** Número máximo de hijos por nodo (m). */
    private final int orden;

    /** Raíz del árbol. Al inicio es una hoja vacía. */
    private NodoArbolBMas raiz;

    // ===================== Constructor =====================

    /**
     * Crea un árbol B+ vacío.
     *
     * @param orden número máximo de hijos por nodo; debe ser al menos 3.
     */
    public ArbolBMas(int orden) {
        if (orden < 3) {
            throw new IllegalArgumentException("El orden de un árbol B+ debe ser al menos 3.");
        }
        this.orden = orden;
        raiz = new NodoArbolBMas(true);
    }

    public int getOrden() {
        return orden;
    }

    /** Indica si el árbol no contiene ningún elemento. */
    public boolean estaVacio() {
        return raiz.esHoja() && raiz.getClaves().isEmpty();
    }

    /** Cantidad mínima de claves que debe tener un nodo que no sea la raíz: ⌈m/2⌉ - 1. */
    private int minimoClaves() {
        return (orden + 1) / 2 - 1;
    }

    // ===================== Inserción =====================

    /**
     * Inserta una clave con su dato asociado.
     *
     * @param clave clave a insertar.
     * @param dato  dato que se guardará en la hoja junto con la clave.
     * @return true si se insertó; false si la clave ya existía.
     */
    public boolean insertar(int clave, String dato) {
        // Las claves son únicas: si ya existe, no se inserta
        if (buscar(clave)) {
            return false;
        }

        // Se inserta primero de forma recursiva
        insertarRecursivo(this.raiz, clave, dato);

        // Se verifica si la raíz se desbordó; si es así, el árbol crece un nivel
        if (this.raiz.getClaves().size() == orden) {
            NodoArbolBMas nuevaRaiz = new NodoArbolBMas(false);
            nuevaRaiz.getHijos().add(this.raiz);
            dividirHijo(nuevaRaiz, 0);
            this.raiz = nuevaRaiz;
        }
        return true;
    }

    // Inserción recursiva de una clave
    // Permite que los nodos se desborden temporalmente y los divide al regresar de la recursión
    private void insertarRecursivo(NodoArbolBMas nodo, int clave, String dato) {
        int i = nodo.getClaves().size() - 1;

        if (nodo.esHoja()) {
            while (i >= 0 && clave < nodo.getClaves().get(i)) {
                i--;
            }
            // La clave y su dato se insertan en la misma posición para mantenerlos alineados
            nodo.getClaves().add(i + 1, clave);
            nodo.getDatos().add(i + 1, dato);
        } else {
            while (i >= 0 && clave < nodo.getClaves().get(i)) {
                i--;
            }
            i++;
            NodoArbolBMas hijo = nodo.getHijos().get(i);

            // Llamada recursiva para insertar la clave primero en el hijo correspondiente
            insertarRecursivo(hijo, clave, dato);

            // Evaluación de desbordamiento al retornar de la recursividad
            if (hijo.getClaves().size() == orden) {
                dividirHijo(nodo, i);
            }
        }
    }

    // Dividir un nodo que está lleno
    private void dividirHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas nodoLleno = padre.getHijos().get(indice);
        NodoArbolBMas nuevoNodo = new NodoArbolBMas(nodoLleno.esHoja());
        int mitad = orden / 2;

        if (nodoLleno.esHoja()) {
            int total = nodoLleno.getClaves().size();

            // En nodos hoja, la mitad derecha de las claves y de los datos pasa al nuevo nodo
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad, total)));
            nuevoNodo.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(mitad, total)));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));
            nodoLleno.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(0, mitad)));

            // Se actualiza la lista enlazada de hojas: nodoLleno -> nuevoNodo -> antiguo siguiente
            nuevoNodo.setSiguiente(nodoLleno.getSiguiente());
            nodoLleno.setSiguiente(nuevoNodo);

            // El padre recibe como separador una copia de la primera clave del nuevo nodo
            int claveMedia = nuevoNodo.getClaves().getFirst();
            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        } else {
            // En nodos internos, la clave media sube y se elimina del hijo
            int claveMedia = nodoLleno.getClaves().get(mitad);
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad + 1, nodoLleno.getClaves().size())));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));

            // Mover los hijos del nodo dividido
            nuevoNodo.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(mitad + 1, nodoLleno.getHijos().size())));
            nodoLleno.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(0, mitad + 1)));

            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        }
    }

    // ===================== Impresión =====================

    // Impresión del árbol como parte de la interfaz pública del árbol
    public void imprimirArbol() {
        if (estaVacio()) {
            System.out.println("(El árbol está vacío)");
            return;
        }
        imprimirNodo(raiz, "", true);
    }

    // Impresión recursiva privada de los nodos a partir de uno inicial
    private void imprimirNodo(NodoArbolBMas nodo, String indentacion, boolean esUltimo) {
        System.out.println(indentacion + "+- " + (nodo.esHoja() ? "Hoja > " : "Interno > ") + nodo.getClaves());
        indentacion += esUltimo ? "   " : "|  ";
        for (int i = 0; i < nodo.getHijos().size(); i++) {
            imprimirNodo(nodo.getHijos().get(i), indentacion, i == (nodo.getHijos().size() - 1));
        }
    }

    // ===================== Búsqueda =====================

    // Búsqueda de una clave como parte de la interfaz pública del árbol
    public boolean buscar(int clave) {
        return buscarNodo(raiz, clave);
    }

    // Búsqueda recursiva privada de una clave a partir de un nodo inicial
    private boolean buscarNodo(NodoArbolBMas nodo, int clave) {
        int i = 0;
        while (i < nodo.getClaves().size() && clave > nodo.getClaves().get(i)) {
            i++;
        }
        if (i < nodo.getClaves().size() && clave == nodo.getClaves().get(i)) {
            return true;
        }
        if (nodo.esHoja()) {
            return false;
        } else {
            return buscarNodo(nodo.getHijos().get(i), clave);
        }
    }

    /**
     * Obtiene el dato asociado a una clave.
     *
     * @return el dato, o null si la clave no existe.
     */
    public String obtenerDato(int clave) {
        NodoArbolBMas hoja = buscarHoja(clave);
        int posicion = hoja.getClaves().indexOf(clave);
        return posicion >= 0 ? hoja.getDatos().get(posicion) : null;
    }

    /**
     * Desciende desde la raíz hasta la hoja en la que se encuentra (o debería encontrarse)
     * la clave. Un separador igual a la clave dirige la búsqueda hacia la derecha, porque
     * cada separador es la menor clave de su subárbol derecho.
     */
    private NodoArbolBMas buscarHoja(int clave) {
        NodoArbolBMas actual = raiz;
        while (!actual.esHoja()) {
            actual = actual.getHijos().get(indiceHijo(actual, clave));
        }
        return actual;
    }

    /** Índice del hijo de un nodo interno por el que se debe descender para una clave. */
    private int indiceHijo(NodoArbolBMas nodo, int clave) {
        int i = 0;
        while (i < nodo.getClaves().size() && clave >= nodo.getClaves().get(i)) {
            i++;
        }
        return i;
    }

    // ===================== Eliminación =====================

    /**
     * Elimina una clave (y su dato) del árbol, rebalanceándolo si es necesario.
     *
     * @param clave clave a eliminar.
     * @return true si se eliminó; false si la clave no existía.
     */
    public boolean eliminar(int clave) {
        if (!buscar(clave)) {
            return false;
        }

        eliminarRecursivo(raiz, clave);

        // Si la raíz interna se quedó sin claves, su único hijo pasa a ser la nueva raíz
        // y el árbol disminuye un nivel de altura
        if (!raiz.esHoja() && raiz.getClaves().isEmpty()) {
            raiz = raiz.getHijos().getFirst();
        }
        return true;
    }

    // Eliminación recursiva: se borra en la hoja y al regresar de la recursión
    // se corrigen los nodos que quedaron con menos claves de las permitidas
    private void eliminarRecursivo(NodoArbolBMas nodo, int clave) {
        if (nodo.esHoja()) {
            int posicion = nodo.getClaves().indexOf(clave);
            nodo.getClaves().remove(posicion);
            nodo.getDatos().remove(posicion);
            return;
        }

        int i = indiceHijo(nodo, clave);
        NodoArbolBMas hijo = nodo.getHijos().get(i);

        // Llamada recursiva para eliminar primero en el hijo correspondiente
        eliminarRecursivo(hijo, clave);

        // Evaluación de subdesbordamiento al retornar de la recursividad
        if (hijo.getClaves().size() < minimoClaves()) {
            rebalancearHijo(nodo, i);
        }

        // La clave eliminada pudo haber sido usada como separador en este nodo
        actualizarSeparadores(nodo);
    }

    /**
     * Corrige un hijo con menos claves de las permitidas. Primero intenta pedir una
     * clave prestada a un hermano adyacente que tenga claves de sobra; si ninguno
     * puede prestar, fusiona el hijo con uno de sus hermanos.
     */
    private void rebalancearHijo(NodoArbolBMas padre, int indice) {
        List<NodoArbolBMas> hijos = padre.getHijos();
        NodoArbolBMas hermanoIzquierdo = indice > 0 ? hijos.get(indice - 1) : null;
        NodoArbolBMas hermanoDerecho = indice < hijos.size() - 1 ? hijos.get(indice + 1) : null;
        NodoArbolBMas hijo = hijos.get(indice);

        if (hermanoIzquierdo != null && hermanoIzquierdo.getClaves().size() > minimoClaves()) {
            prestarDeIzquierda(hermanoIzquierdo, hijo);
        } else if (hermanoDerecho != null && hermanoDerecho.getClaves().size() > minimoClaves()) {
            prestarDeDerecha(hijo, hermanoDerecho);
        } else if (hermanoIzquierdo != null) {
            fusionar(padre, indice - 1);
        } else {
            fusionar(padre, indice);
        }
    }

    // Mueve el último elemento del hermano izquierdo al inicio del hijo
    private void prestarDeIzquierda(NodoArbolBMas izquierdo, NodoArbolBMas hijo) {
        if (hijo.esHoja()) {
            hijo.getClaves().addFirst(izquierdo.getClaves().removeLast());
            hijo.getDatos().addFirst(izquierdo.getDatos().removeLast());
        } else {
            // El hijo prestado se coloca al inicio; la nueva clave separa ese hijo
            // del que antes era el primero
            NodoArbolBMas hijoPrestado = izquierdo.getHijos().removeLast();
            izquierdo.getClaves().removeLast();
            hijo.getClaves().addFirst(primeraClave(hijo.getHijos().getFirst()));
            hijo.getHijos().addFirst(hijoPrestado);
        }
    }

    // Mueve el primer elemento del hermano derecho al final del hijo
    private void prestarDeDerecha(NodoArbolBMas hijo, NodoArbolBMas derecho) {
        if (hijo.esHoja()) {
            hijo.getClaves().addLast(derecho.getClaves().removeFirst());
            hijo.getDatos().addLast(derecho.getDatos().removeFirst());
        } else {
            NodoArbolBMas hijoPrestado = derecho.getHijos().removeFirst();
            derecho.getClaves().removeFirst();
            hijo.getClaves().addLast(primeraClave(hijoPrestado));
            hijo.getHijos().addLast(hijoPrestado);
        }
    }

    /**
     * Fusiona el hijo en la posición indiceIzquierdo con su hermano derecho.
     * El nodo derecho desaparece y el padre pierde el separador entre ambos.
     */
    private void fusionar(NodoArbolBMas padre, int indiceIzquierdo) {
        NodoArbolBMas izquierdo = padre.getHijos().get(indiceIzquierdo);
        NodoArbolBMas derecho = padre.getHijos().get(indiceIzquierdo + 1);

        if (izquierdo.esHoja()) {
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getDatos().addAll(derecho.getDatos());
            // Se salta la hoja eliminada en la lista enlazada de hojas
            izquierdo.setSiguiente(derecho.getSiguiente());
        } else {
            // En nodos internos se necesita un separador entre los hijos de ambos nodos
            izquierdo.getClaves().add(primeraClave(derecho));
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getHijos().addAll(derecho.getHijos());
        }

        padre.getClaves().remove(indiceIzquierdo);
        padre.getHijos().remove(indiceIzquierdo + 1);
    }

    /**
     * Recalcula los separadores de un nodo interno: cada clave i pasa a ser la menor
     * clave del subárbol del hijo i + 1. Así se descartan separadores obsoletos
     * (por ejemplo, una clave que acaba de eliminarse).
     */
    private void actualizarSeparadores(NodoArbolBMas nodo) {
        for (int i = 1; i < nodo.getHijos().size(); i++) {
            nodo.getClaves().set(i - 1, primeraClave(nodo.getHijos().get(i)));
        }
    }

    /** Menor clave de un subárbol: la primera clave de su hoja más a la izquierda. */
    private int primeraClave(NodoArbolBMas nodo) {
        while (!nodo.esHoja()) {
            nodo = nodo.getHijos().getFirst();
        }
        return nodo.getClaves().getFirst();
    }

    // ===================== Recorrido por rango =====================

    /**
     * Busca la clave inicial y muestra los datos de los siguientes n elementos en orden
     * ascendente, empezando por esa clave (o por la primera clave mayor si no existe).
     * Usa la lista enlazada de hojas, por lo que después de la búsqueda inicial no es
     * necesario volver a recorrer el árbol desde la raíz.
     *
     * @param claveInicial clave desde la cual comienza el recorrido.
     * @param n            cantidad de elementos a mostrar.
     * @return cantidad de elementos que se mostraron (puede ser menor que n si se
     *         llega al final del árbol).
     */
    public int recorrerRango(int claveInicial, int n) {
        NodoArbolBMas hoja = buscarHoja(claveInicial);

        // Posición de la primera clave mayor o igual que la clave inicial dentro de la hoja
        int posicion = 0;
        while (posicion < hoja.getClaves().size() && hoja.getClaves().get(posicion) < claveInicial) {
            posicion++;
        }

        int mostrados = 0;
        while (hoja != null && mostrados < n) {
            if (posicion < hoja.getClaves().size()) {
                System.out.printf("  %3d. Clave: %-8d Dato: %s%n",
                        mostrados + 1, hoja.getClaves().get(posicion), hoja.getDatos().get(posicion));
                mostrados++;
                posicion++;
            } else {
                // Se terminó la hoja actual: se continúa con la hoja siguiente
                hoja = hoja.getSiguiente();
                posicion = 0;
            }
        }
        return mostrados;
    }

    /** Muestra todos los elementos en orden, recorriendo únicamente la lista enlazada de hojas. */
    public void imprimirHojas() {
        NodoArbolBMas hoja = raiz;
        while (!hoja.esHoja()) {
            hoja = hoja.getHijos().getFirst();
        }

        StringBuilder salida = new StringBuilder();
        while (hoja != null) {
            for (int i = 0; i < hoja.getClaves().size(); i++) {
                salida.append(hoja.getClaves().get(i)).append(" (").append(hoja.getDatos().get(i)).append(")");
                salida.append(i < hoja.getClaves().size() - 1 ? ", " : "");
            }
            hoja = hoja.getSiguiente();
            if (hoja != null) {
                salida.append("  ->  ");
            }
        }
        System.out.println(salida.isEmpty() ? "(El árbol está vacío)" : salida);
    }
}
