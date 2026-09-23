package main;

public class Puntos {

    // Guarda el puntaje actual de Pac-Man.
    // Empieza en 0 porque todavía no comió ningún punto.
    private static int puntaje = 0;
    
    // Guarda la cantidad de puntos normales que todavía quedan en el mapa.
    private static int puntosRestantes = contarPuntosIniciales();
    
    // Cuenta todos los puntos normales que existen en el mapa al comenzar el juego.
    private static int contarPuntosIniciales() {

        int cantidad = 0;

        for (int fila = 0; fila < mapa.MATRIZ.length; fila++) {
            for (int columna = 0; columna < mapa.MATRIZ[0].length; columna++) {
                if (mapa.MATRIZ[fila][columna] == 2) {
                    cantidad++;
                }
            }
        }

        return cantidad;
    }

    // Este método recibe la FILA y la COLUMNA donde se encuentra Pac-Man.
    // Devuelve:
    // true  -> si encontró y comió un punto.
    // false -> si no había un punto.
    public static boolean comerPunto(int fila, int columna) {

        if (fila < 0 || fila >= mapa.MATRIZ.length ||
            columna < 0 || columna >= mapa.MATRIZ[0].length) {

            return false;
        }

        if (mapa.MATRIZ[fila][columna] == 2) {

            mapa.MATRIZ[fila][columna] = 0;

            puntaje += 10;
            
            puntosRestantes--;

            return true;
        }

        return false;
    }

    // Power Pellet NORMAL (tile 3). Solo activa el modo asustado.
    public static boolean comerPowerPellet(int fila, int columna) {

        if (fila < 0 || fila >= mapa.MATRIZ.length ||
            columna < 0 || columna >= mapa.MATRIZ[0].length) {

            return false;
        }

        if (mapa.MATRIZ[fila][columna] == 3) {

            mapa.MATRIZ[fila][columna] = 0;

            puntaje += 50;

            return true;
        }

        return false;
    }

    // Power Pellet ESPECIAL (tile 5). Activa el modo asustado Y da una
    // carga para romper paredes con la tecla F.
    // No cuenta para la condición de victoria.
    public static boolean comerPowerPelletEspecial(int fila, int columna) {

        if (fila < 0 || fila >= mapa.MATRIZ.length ||
            columna < 0 || columna >= mapa.MATRIZ[0].length) {

            return false;
        }

        if (mapa.MATRIZ[fila][columna] == 5) {

            mapa.MATRIZ[fila][columna] = 0;

            puntaje += 50;

            return true;
        }

        return false;
    }

    // Permite sumar puntos "sueltos", como los que se ganan al comerse
    // a un fantasma asustado.
    public static void sumarPuntos(int cantidad) {
        puntaje += cantidad;
    }

    // Este método permite que otras clases puedan consultar cuánto puntaje tiene actualmente Pac-Man. 
    public static int getPuntaje() {
        return puntaje;
    }

    // Devuelve la cantidad de puntos normales que todavía quedan en el mapa.
    public static int getPuntosRestantes() {
        return puntosRestantes;
    }

    // Comprueba si Pac-Man ya comió todos los puntos.
    public static boolean todosLosPuntosComidos() {
        return puntosRestantes == 0;
    }
}