package main;

public class Fantasma {

    public static final int TAMANO = 18;

    private int x;
    private int y;
    private int velocidad;
    private String direccionActual;

    public Fantasma(int xInicial, int yInicial) {
        x = xInicial;
        y = yInicial;
        velocidad = 3; // mismo valor que Pacman, debe ser divisor de TILE_SIZE
        direccionActual = null;
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