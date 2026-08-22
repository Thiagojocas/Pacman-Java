package main;

public class PacmanJugador {

    public static final int TAMANO = 18; // debe ser menor al TILE_SIZE del tablero

    private int x;
    private int y;
    private int velocidad;
    private String direccion;

    public PacmanJugador(int xInicial, int yInicial) {
        x = xInicial;
        y = yInicial;
        velocidad = 4;
        direccion = "derecha";
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void moverDerecha() {
        x = x + velocidad;
        direccion = "derecha";
    }

    public void moverIzquierda() {
        x = x - velocidad;
        direccion = "izquierda";
    }

    public void moverArriba() {
        y = y - velocidad;
        direccion = "arriba";
    }

    public void moverAbajo() {
        y = y + velocidad;
        direccion = "abajo";
    }
}