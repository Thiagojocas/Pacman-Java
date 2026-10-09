package main;

/*
 * MAPA DE PAC-MANIA
 *
 * 28 columnas x 31 filas
 *
 * 0 = espacio vacío (interior casa fantasmas)
 * 1 = pared
 * 2 = punto normal
 * 3 = Power Pellet (asusta fantasmas)
 * 4 = puerta de la casa de los fantasmas
 * 5 = Power Pellet ESPECIAL (asusta + da 1 carga de "romper paredes")
 * 6 = pared rota (queda transitable, no cuenta para ganar)
 *
 * Los túneles están en la fila 14.
 *
 * Los Power Pellets especiales (5) son los de la fila 3 (arriba).
 */

public class mapa {

    public static final int COLUMNAS = 28;
    public static final int FILAS = 31;

    public static final int FILA_TUNEL = 14;

    public static final int[][] MATRIZ = {

        // FILA 0
    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},

    // FILA 1
    {1,2,2,2,2,2,2,2,2,2,2,2,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},

    // FILA 2
    {1,5,1,1,1,1,1,1,1,1,1,2,1,2,1,1,1,1,1,2,1,1,1,1,1,1,5,1},

    // FILA 3 - PASILLO SUPERIOR
    {1,2,2,2,2,2,2,2,2,2,2,2,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},

    // FILA 4
    {1,2,1,1,1,1,1,2,1,1,1,2,1,2,1,1,1,2,1,2,1,1,1,1,1,1,2,1},

    // FILA 5
    {1,2,2,2,2,2,1,2,2,2,2,2,2,2,2,2,1,2,2,2,1,2,2,2,2,2,2,1},

    // FILA 6
    {1,2,1,1,1,2,1,1,1,2,1,1,1,1,1,2,1,1,1,2,1,1,1,1,1,2,1,1},

    // FILA 7
    {1,2,1,7,1,2,2,2,1,2,2,2,2,2,1,2,2,2,1,2,2,2,1,8,1,2,1,1},

    // FILA 8 - SUPERBOLITAS DE PODERES
    {1,2,1,1,1,2,1,2,1,1,1,2,2,2,1,2,1,2,1,1,1,2,1,1,1,2,1,1},

    // FILA 9
    {1,2,1,1,1,2,1,2,1,1,1,2,1,2,1,2,1,2,1,1,1,2,1,1,1,2,1,1},

    // FILA 10
    {1,2,2,2,2,2,1,2,2,2,2,2,1,2,2,2,1,2,2,2,2,2,1,2,2,2,2,1},

    // FILA 11
    {1,2,1,1,1,2,1,1,1,2,1,1,1,1,1,2,1,1,1,2,1,1,1,1,1,1,2,1},

    // FILA 12
    {1,2,2,2,1,2,2,2,1,2,2,2,2,2,1,2,2,2,1,2,2,2,2,2,1,2,2,1},

    // FILA 13
    {1,1,1,1,1,1,1,2,1,2,1,1,1,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1},

        // FILA 14 - TÚNEL
    {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},

    // FILA 15 - CASA DE LOS FANTASMAS Y PUERTA
    {1,2,1,1,1,1,2,1,1,1,1,1,1,4,4,1,1,2,1,1,2,1,1,1,1,1,2,1},

    // FILA 16 - INTERIOR DE LA CASA
    {1,2,1,1,1,1,2,1,1,1,1,1,0,0,0,0,1,2,1,1,2,1,1,1,1,1,2,1},

    // FILA 17 - INTERIOR DE LA CASA
    {1,2,2,2,2,2,2,1,1,1,1,1,0,0,0,0,1,2,1,1,2,2,2,2,2,2,2,1},

    // FILA 18 - FONDO DE LA CASA
    {1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,2,1},

    // FILA 19
    {1,2,1,2,1,1,1,2,1,1,2,2,2,2,2,2,2,2,1,1,1,2,1,1,2,1,1,1},

    // FILA 20
    {1,2,2,2,1,2,2,2,1,2,2,2,2,2,2,2,2,2,1,2,2,2,1,2,2,2,2,1},

    // FILA 21 
    {1,2,2,2,1,1,2,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,2,1,1,2,2,1},

    // FILA 22
    {1,2,1,1,1,2,2,2,1,2,2,2,2,1,2,2,2,2,1,1,1,2,2,1,1,1,2,1},

    // FILA 23
    {1,2,1,1,1,2,1,2,1,1,1,2,1,1,2,1,1,2,1,1,1,2,2,1,1,1,2,1},

    // FILA 24
    {1,2,2,2,2,2,1,2,2,2,2,2,1,1,2,1,1,2,2,2,1,2,2,2,2,2,2,1},

    // FILA 25
    {1,2,1,1,1,2,1,1,1,2,1,1,1,1,2,1,1,1,2,1,1,1,1,1,1,1,2,1},

    // FILA 26
    {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},

    // FILA 27
    {1,2,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,2,1,1,1,2,1,1,1,2,1,1},

    // FILA 28 - POWER PELLETS NORMALES
    {1,3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,2,2,2,2,1,2,2,3,1},

    // FILA 29
    {1,2,2,2,2,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,1},

    // FILA 30 - BORDE INFERIOR
    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    // ===== REINICIO DEL MAPA =====
    // Copia del mapa original, para poder reiniciar la partida.
    // IMPORTANTE: tiene que estar DESPUÉS de MATRIZ (orden de inicialización estática).
    private static final int[][] ORIGINAL = copiar(MATRIZ);

    private static int[][] copiar(int[][] origen) {
        int[][] copia = new int[origen.length][];
        for (int i = 0; i < origen.length; i++) {
            copia[i] = origen[i].clone();
        }
        return copia;
    }

    // Restaura el mapa a su estado inicial (puntos, paredes, pellets).
    public static void reiniciar() {
        for (int i = 0; i < ORIGINAL.length; i++) {
            MATRIZ[i] = ORIGINAL[i].clone();
        }
    }
}