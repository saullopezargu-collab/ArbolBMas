import java.util.Scanner;


public class Main {

    /** Orden que se usa si el usuario no indica otro. */
    private static final int ORDEN_POR_DEFECTO = 4;

    /** Lector de la entrada estándar, compartido por todo el programa. */
    private static final Scanner ENTRADA = new Scanner(System.in);

    public static void main(String[] args) {
        mostrarBienvenida();

        // 1. Se configura el árbol con el orden elegido por el usuario
        ArbolBMas arbol = new ArbolBMas(leerOrden());

        // 2. Se ejecuta el menú hasta que el usuario decida salir
        menu(arbol);

        System.out.println("\n¡Gracias por usar el programa! Hasta luego.");
    }

    // ===================== Menú principal =====================

    /** Muestra el menú en un ciclo y ejecuta la opción elegida por el usuario. */
    private static void menu(ArbolBMas arbol) {
        int opcion;
        do {
            System.out.println();
            System.out.println("==================== MENÚ PRINCIPAL ====================");
            System.out.println("  Árbol B+ de orden " + arbol.getOrden());
            System.out.println("--------------------------------------------------------");
            System.out.println("  1. Insertar un registro");
            System.out.println("  2. Eliminar un registro");
            System.out.println("  3. Buscar un registro");
            System.out.println("  4. Recorrer un rango (n elementos desde una clave)");
            System.out.println("  5. Ver la estructura del árbol");
            System.out.println("  6. Ver todos los registros (recorrido de hojas)");
            System.out.println("  7. Cargar registros de ejemplo");
            System.out.println("  0. Salir");
            System.out.println("========================================================");
            opcion = leerEntero("Elija una opción: ");
            System.out.println();

            switch (opcion) {
                case 1 -> opcionInsertar(arbol);
                case 2 -> opcionEliminar(arbol);
                case 3 -> opcionBuscar(arbol);
                case 4 -> opcionRecorrerRango(arbol);
                case 5 -> opcionVerArbol(arbol);
                case 6 -> opcionVerRegistros(arbol);
                case 7 -> opcionCargarEjemplo(arbol);
                case 0 -> { /* Se sale del ciclo */ }
                default -> System.out.println("Opción no válida. Escriba un número del 0 al 7.");
            }

            if (opcion != 0) {
                pausar();
            }
        } while (opcion != 0);
    }

    // ===================== Opciones del menú =====================

    private static void opcionInsertar(ArbolBMas arbol) {
        System.out.println("--- Insertar un registro ---");
        int clave = leerEntero("Clave (número entero): ");
        if (arbol.buscar(clave)) {
            System.out.println("La clave " + clave + " ya existe con el dato \""
                    + arbol.obtenerDato(clave) + "\". Las claves no pueden repetirse.");
            return;
        }
        String dato = leerTexto("Dato asociado (texto): ");
        arbol.insertar(clave, dato);
        System.out.println("Registro insertado: " + clave + " -> \"" + dato + "\".");
    }

    private static void opcionEliminar(ArbolBMas arbol) {
        System.out.println("--- Eliminar un registro ---");
        if (avisarSiVacio(arbol)) {
            return;
        }
        int clave = leerEntero("Clave a eliminar: ");
        String dato = arbol.obtenerDato(clave);
        if (arbol.eliminar(clave)) {
            System.out.println("Registro eliminado: " + clave + " -> \"" + dato + "\".");
            System.out.println("Estructura después de la eliminación:");
            arbol.imprimirArbol();
        } else {
            System.out.println("La clave " + clave + " no existe en el árbol.");
        }
    }

    private static void opcionBuscar(ArbolBMas arbol) {
        System.out.println("--- Buscar un registro ---");
        if (avisarSiVacio(arbol)) {
            return;
        }
        int clave = leerEntero("Clave a buscar: ");
        if (arbol.buscar(clave)) {
            System.out.println("Encontrado: " + clave + " -> \"" + arbol.obtenerDato(clave) + "\".");
        } else {
            System.out.println("La clave " + clave + " no existe en el árbol.");
        }
    }

    private static void opcionRecorrerRango(ArbolBMas arbol) {
        System.out.println("--- Recorrer un rango ---");
        if (avisarSiVacio(arbol)) {
            return;
        }
        int clave = leerEntero("Clave inicial: ");
        int n = leerEnteroPositivo("Cantidad de elementos a mostrar (n): ");

        System.out.println("Registros a partir de la clave " + clave + ":");
        int mostrados = arbol.recorrerRango(clave, n);

        if (mostrados == 0) {
            System.out.println("  No hay registros con clave mayor o igual que " + clave + ".");
        } else if (mostrados < n) {
            System.out.println("Se llegó al final del árbol: solo había " + mostrados + " registro(s).");
        }
    }

    private static void opcionVerArbol(ArbolBMas arbol) {
        System.out.println("--- Estructura del árbol ---");
        arbol.imprimirArbol();
    }

    private static void opcionVerRegistros(ArbolBMas arbol) {
        System.out.println("--- Todos los registros, hoja por hoja ---");
        arbol.imprimirHojas();
    }

    /** Inserta un conjunto de registros de ejemplo para probar rápidamente el árbol. */
    private static void opcionCargarEjemplo(ArbolBMas arbol) {
        int[] claves = {10, 20, 5, 6, 15, 30, 25, 40, 45, 50, 35, 12};
        String[] datos = {"Ana", "Bruno", "Carla", "Diego", "Elena", "Fabián",
                "Gabriela", "Héctor", "Irene", "Jorge", "Karla", "Luis"};

        int insertados = 0;
        for (int i = 0; i < claves.length; i++) {
            if (arbol.insertar(claves[i], datos[i])) {
                insertados++;
            }
        }
        System.out.println("Se cargaron " + insertados + " registro(s) de ejemplo"
                + (insertados < claves.length ? " (las claves repetidas se omitieron)." : "."));
        arbol.imprimirArbol();
    }

    // ===================== Utilidades de entrada y salida =====================

    private static void mostrarBienvenida() {
        System.out.println("========================================================");
        System.out.println("          SIMULADOR DE ÁRBOL B+ - Estructuras de Datos");
        System.out.println("========================================================");
        System.out.println("Este programa guarda registros (clave numérica + dato) en un");
        System.out.println("árbol B+, como lo hacen las bases de datos y los sistemas");
        System.out.println("de archivos. Los datos se guardan solo en las hojas, que");
        System.out.println("están enlazadas entre sí, y el árbol se rebalancea solo.");
        System.out.println();
    }

    /** Pide el orden del árbol; Enter usa el valor por defecto. */
    private static int leerOrden() {
        while (true) {
            System.out.print("Orden del árbol (máximo de hijos por nodo, mínimo 3) [Enter = "
                    + ORDEN_POR_DEFECTO + "]: ");
            String linea = leerLinea();
            if (linea.isEmpty()) {
                return ORDEN_POR_DEFECTO;
            }
            try {
                int orden = Integer.parseInt(linea);
                if (orden >= 3) {
                    return orden;
                }
                System.out.println("El orden debe ser al menos 3.");
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un número entero.");
            }
        }
    }

    /** Lee un número entero, repitiendo la pregunta hasta que la entrada sea válida. */
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = leerLinea();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida: escriba un número entero.");
            }
        }
    }

    /** Lee un número entero mayor que cero. */
    private static int leerEnteroPositivo(String mensaje) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor > 0) {
                return valor;
            }
            System.out.println("El número debe ser mayor que cero.");
        }
    }

    /** Lee un texto no vacío. */
    private static String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = leerLinea();
            if (!linea.isEmpty()) {
                return linea;
            }
            System.out.println("El dato no puede estar vacío.");
        }
    }

    /** Lee una línea de la consola; si la entrada se cerró, termina el programa de forma ordenada. */
    private static String leerLinea() {
        if (!ENTRADA.hasNextLine()) {
            System.out.println("\nEntrada finalizada. Cerrando el programa.");
            System.exit(0);
        }
        return ENTRADA.nextLine().trim();
    }

    /** Muestra un mensaje de verificación de si el árbol está vacío. */
    private static boolean avisarSiVacio(ArbolBMas arbol) {
        if (arbol.estaVacio()) {
            System.out.println("El árbol está vacío. Inserte registros (opción 1) o cargue el ejemplo (opción 7).");
            return true;
        }
        return false;
    }

    private static void pausar() {
        System.out.print("\nPresione Enter para continuar...");
        leerLinea();
    }
}
