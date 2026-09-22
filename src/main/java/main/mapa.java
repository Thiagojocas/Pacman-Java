package main;

/*
 * MAPA DE PAC-MANIA
 *
 * 28 columnas x 31 filas
 *
 * 0 = espacio vacío (interior casa fantasmas)
 * 1 = pared
 * 2 = punto normal
 * 3 = Power Pellet
 * 4 = puerta de la casa de los fantasmas
 *
 * Los túneles están en la fila 14.
 *
 * CORRECCIONES ACUMULADAS:
 *  - Fila 6,  col 14: 2 -> 1 (punto aislado).
 *  - Filas 16-18: casa de fantasmas cerrada (costados y fondo).
 *  - Fila 22, col 14: 1 -> 2 (conexión vertical inferior).
 *  - Fila 25, col 14: 1 -> 2 (conexión vertical inferior).
 *  - Fila 20, col 20: 2 -> 1 (aislada).
 *  - Fila 20, col 23: 2 -> 1 (colgante).
 *  - Fila 21, col 19: 2 -> 1 (par aislado).
 *  - Fila 22, col 19: 2 -> 1 (par aislado).
 *  - Fila 25, col 21: 2 -> 1 (colgante).
 *
 *  - Fila 7,  col 11: 1 -> 2 (abre salida a la fila 6 col 11).
 *  - Fila 7,  col 17: 1 -> 2 (abre salida a la fila 6 col 17).
 *  - Fila 8,  col 13: 1 -> 2 (une las filas 8 y 9 con salida).
 *  - Fila 8,  col 14: 1 -> 2 (idem).
 *  - Fila 9,  col 21: 2 -> 1 (stub sin salida).
 *  - Fila 15, col 10: 2 -> 1 (corredor sin salida).
 *  - Fila 16, col 10: 2 -> 1 (idem).
 *  - Fila 17, col 10: 2 -> 1 (idem).
 *  - Fila 19, col 11: 2 -> 1 (stub).
 *  - Fila 19, col 14: 2 -> 1 (stub).
 *  - Fila 19, col 21: 2 -> 1 (stub).
 *  - Fila 20, col 22: 1 -> 2 (abre salida a las filas 19 y 21).
 *  - Fila 20, col 25: 1 -> 2 (abre salida a las filas 19 y 21).
 *  - Fila 28, col  6: 1 -> 2 (abre salida a la fila 27).
 *  - Fila 28, col 11: 1 -> 2 (abre salida a las filas 27 y 29).
 */

public class mapa {

    public static final int COLUMNAS = 28;
    public static final int FILAS = 31;

    public static final int FILA_TUNEL = 14;

    public static final int[][] MATRIZ = {

        // FILA 0
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},

        // FILA 1
        {1,2,2,2,2,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,1},

        // FILA 2
        {1,2,1,1,1,1,2,1,1,1,1,2,1,1,2,1,1,1,1,2,1,1,1,1,1,1,2,1},

        // FILA 3 - POWER PELLETS
        {1,3,1,1,1,1,2,1,1,1,1,2,1,1,2,1,1,1,1,2,1,1,1,1,1,1,3,1},

        // FILA 4
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},

        // FILA 5
        {1,2,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,2,1},

        // FILA 6
        {1,2,2,2,2,2,2,1,1,2,2,2,1,1,1,1,1,2,2,2,1,1,2,2,2,2,2,1},

        // FILA 7 - (col 11 y col 17 ahora abren salida a la fila 6)
        {1,1,1,1,1,1,2,1,1,1,2,2,1,1,1,1,1,2,1,2,1,1,2,1,1,1,1,1},

        // FILA 8 - (col 13 y col 14 ahora conectan con la fila 9)
        {1,1,1,1,1,1,2,1,1,1,2,2,2,2,2,1,1,2,2,2,1,1,2,1,1,1,1,1},

        // FILA 9 - (col 21 convertida en pared: era stub sin salida)
        {1,2,2,2,2,2,2,1,1,1,1,2,1,1,2,1,1,2,1,1,1,1,2,2,2,2,2,1},

        // FILA 10
        {1,2,1,1,1,1,1,1,1,1,1,2,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1},

        // FILA 11
        {1,2,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,1,1,1,1,1,2,1},

        // FILA 12
        {1,2,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,2,1},

        // FILA 13
        {1,2,2,2,2,2,2,1,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,1},

        // FILA 14 - TÚNEL
        {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},

        // FILA 15 - CASA / PUERTA (col 10 convertida en pared: corredor sin salida)
        {1,2,1,1,1,1,2,1,1,1,1,1,1,4,4,1,1,2,1,1,2,1,1,1,1,1,2,1},

        // FILA 16 - INTERIOR CASA (col 10 convertida en pared)
        {1,2,1,1,1,1,2,1,1,1,1,1,0,0,0,0,1,2,1,1,2,1,1,1,1,1,2,1},

        // FILA 17 - INTERIOR CASA (col 10 convertida en pared)
        {1,2,2,2,2,2,2,1,1,1,1,1,0,0,0,0,1,2,1,1,2,2,2,2,2,2,2,1},

        // FILA 18 - FONDO CASA
        {1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,2,1},

        // FILA 19 - (cols 11, 14 y 21 convertidas en pared: eran stubs)
        {1,2,2,2,2,2,2,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,2,2,2,2,2,1},

        // FILA 20 - (cols 22 y 25 abren salida hacia filas 19 y 21)
        {1,1,1,2,1,1,2,1,1,1,2,2,2,2,2,2,2,2,1,1,1,1,2,1,1,2,1,1},

        // FILA 21 - POWER PELLETS
        {1,3,2,2,1,1,2,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,2,1,1,2,3,1},

        // FILA 22
        {1,2,1,1,1,1,2,1,1,1,2,2,2,1,2,2,2,2,1,1,1,1,2,1,1,1,2,1},

        // FILA 23
        {1,2,1,1,1,1,2,1,1,1,1,2,1,1,2,1,1,2,1,1,1,1,2,1,1,1,2,1},

        // FILA 24
        {1,2,2,2,2,2,2,1,1,2,2,2,1,1,2,1,1,2,2,2,1,1,2,2,2,2,2,1},

        // FILA 25
        {1,2,1,1,1,1,2,1,1,2,1,1,1,1,2,1,1,1,2,1,1,1,1,1,1,1,2,1},

        // FILA 26
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},

        // FILA 27
        {1,2,1,1,1,1,2,1,1,1,1,2,1,1,2,1,1,2,1,1,1,1,2,1,1,1,2,1},

        // FILA 28 - POWER PELLETS (cols 6 y 11 abren salida)
        {1,3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,2,2,2,2,1,2,2,3,1},

        // FILA 29
        {1,2,2,2,2,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,1},

        // FILA 30
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
}