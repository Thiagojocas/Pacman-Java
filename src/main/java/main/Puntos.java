package main;

public class Puntos {

    public static boolean comerPunto(int fila, int columna) {

        if (fila < 0 || fila >= mapa.MATRIZ.length ||
            columna < 0 || columna >= mapa.MATRIZ[0].length) {
            return false;
        }

        if (mapa.MATRIZ[fila][columna] == 2) {
            mapa.MATRIZ[fila][columna] = 0;
            return true;
        }

        return false;
    }
}