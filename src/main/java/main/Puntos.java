package main;

public class Puntos {

    // Guarda el puntaje actual de Pac-Man.
    // Empieza en 0 porque todavía no comió ningún punto.
    private static int puntaje = 0;

    // Este método recibe la FILA y la COLUMNA donde se encuentra Pac-Man.
    // Devuelve:
    // true  -> si encontró y comió un punto.
    // false -> si no había un punto.
    public static boolean comerPunto(int fila, int columna) {

        // Comprobamos que la posición esté dentro de los límites de nuestra matriz para evitar errores.
        if (fila < 0 || fila >= mapa.MATRIZ.length ||
            columna < 0 || columna >= mapa.MATRIZ[0].length) {

            return false;
        }

        // En nuestro mapa:
        // 0 = vacío
        // 1 = pared
        // 2 = punto normal
        // 3 = Power Pellet
        //
        // Entonces preguntamos si en la posición de Pac-Man hay un punto normal.
        if (mapa.MATRIZ[fila][columna] == 2) {

            // Cambiamos el 2 por 0.
            // De esta manera el punto desaparece del mapa.
            mapa.MATRIZ[fila][columna] = 0;

            // Cada punto comido suma 10 al puntaje.
            puntaje += 10;

            // Avisamos que efectivamente se comió un punto.
            return true;
        }

        // Si no había un punto, no hacemos nada.
        return false;
    }

    // Este método permite que otras clases puedan consultar cuánto puntaje tiene actualmente Pac-Man. 
    public static int getPuntaje() {
        return puntaje;
    }
}