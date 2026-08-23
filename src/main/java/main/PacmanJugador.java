package main;

public class PacmanJugador {

    public static final int TAMANO = 18; // tamaño visual, se dibuja con leve inset

    private int x;
    private int y;
    private int velocidad;
    private String direccionActual;   // la que se está ejecutando de verdad
    private String direccionDeseada;  // la última tecla que tocó el jugador

    public PacmanJugador(int xInicial, int yInicial) {
        x = xInicial;
        y = yInicial;
        velocidad = 7; // debe ser divisor exacto de Tablero.TILE_SIZE
        direccionActual = null;
        direccionDeseada = null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public String getDireccionActual() {
        return direccionActual;
    }

    public void setDireccionActual(String direccionActual) {
        this.direccionActual = direccionActual;
    }

    public String getDireccionDeseada() {
        return direccionDeseada;
    }

    public void setDireccionDeseada(String direccionDeseada) {
        this.direccionDeseada = direccionDeseada;
    }

    public void moverDerecha() {
        x = x + velocidad;
    }

    public void moverIzquierda() {
        x = x - velocidad;
    }

    public void moverArriba() {
        y = y - velocidad;
    }

    public void moverAbajo() {
        y = y + velocidad;
    }
}