package main;

public class Puntos {

    // Guarda el puntaje actual de Pac-Man.
    // Empieza en 0 porque todavía no comió ningún punto.
    private static int puntaje = 0;
    
    // Guarda la cantidad de puntos normales que todavía quedan en el mapa.
    private static int puntosRestantes = contarPuntosIniciales();
    
    // Cuenta todos los puntos normales que existen en el mapa al comenzar el juego.
    private static int contarPuntosIniciales() {

    // Variable donde vamos a guardar la cantidad de puntos encontrados.
    int cantidad = 0;

    // Recorremos todas las filas de la matriz.
    for (int fila = 0; fila < mapa.MATRIZ.length; fila++) {

        // Recorremos todas las columnas de cada fila.
        for (int columna = 0; columna < mapa.MATRIZ[0].length; columna++) {

            // En nuestro mapa, el número 2 representa un punto normal.
            if (mapa.MATRIZ[fila][columna] == 2) {

                // Encontramos un punto, así que aumentamos el contador.
                cantidad++;
            }
        }
    }

    // Devolvemos la cantidad total de puntos encontrados.
    return cantidad;
}

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
            
            // Como acabamos de comer un punto, queda uno menos en el mapa.
            puntosRestantes--;

            // Avisamos que efectivamente se comió un punto.
            return true;
        }

        // Si no había un punto, no hacemos nada.
        return false;
    }

    // Este método recibe la FILA y la COLUMNA donde se encuentra Pac-Man.
    // Se usa para comer un Power Pellet (la bolita grande).
    // Devuelve:
    // true  -> si encontró y comió un Power Pellet.
    // false -> si no había uno.
    public static boolean comerPowerPellet(int fila, int columna) {

        // Comprobamos que la posición esté dentro de los límites de la matriz.
        if (fila < 0 || fila >= mapa.MATRIZ.length ||
            columna < 0 || columna >= mapa.MATRIZ[0].length) {

            return false;
        }

        // El número 3 representa un Power Pellet.
        if (mapa.MATRIZ[fila][columna] == 3) {

            // Lo borramos del mapa, igual que hacemos con los puntos normales.
            mapa.MATRIZ[fila][columna] = 0;

            // Un Power Pellet vale más puntos que uno normal.
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