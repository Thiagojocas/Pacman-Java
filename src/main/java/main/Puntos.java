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

    // Reinicia puntaje y contador de puntos para una partida nueva.
    // Llamarlo SIEMPRE después de mapa.reiniciar().
    public static void reiniciar() {
        puntaje = 0;
        puntosRestantes = contarPuntosIniciales();
    }

    // true -> si encontró y comió un punto. false -> si no había un punto.
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

    // Power Pellet normal (tile 3). Solo activa el modo asustado.
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

    // Power Pellet especial (tile 5). Activa el modo asustado Y da una
    // carga para romper paredes con la tecla F.
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

    // Permite sumar puntos "sueltos", como los de comer un fantasma asustado.
    public static void sumarPuntos(int cantidad) {
        puntaje += cantidad;
    }

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
    
    // Superbolita 7: Rayo Veloz.
public static boolean comerRayoVeloz(int fila, int columna) {
    if (mapa.MATRIZ[fila][columna] == 7) {
        mapa.MATRIZ[fila][columna] = 0;
        puntaje += 100;
        return true;
    }
    return false;
}

// Superbolita 8: Rey del Laberinto.
public static boolean comerReyDelLaberinto(int fila, int columna) {
    if (mapa.MATRIZ[fila][columna] == 8) {
        mapa.MATRIZ[fila][columna] = 0;
        puntaje += 100;
        return true;
    }
    return false;
}
    
}