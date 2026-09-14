package main;

public class Fantasma {

    public static final int TAMANO = 18;

    // Los tres estados posibles de un fantasma.
    // NORMAL   -> persigue a Pac-Man, si lo toca Pac-Man pierde una vida.
    // ASUSTADO -> Pac-Man comió un Power Pellet; el fantasma huye y si
    //             Pac-Man lo toca, se lo come.
    // COMIDO   -> son solo los "ojos" volviendo a la casa para revivir.
    public static final String NORMAL = "normal";
    public static final String ASUSTADO = "asustado";
    public static final String COMIDO = "comido";

    private int x;
    private int y;
    private int velocidad;
    private String direccionActual;
    private String estado;

    // Posición (en píxeles) del punto dentro de la casa al que el
    // fantasma vuelve cuando Pac-Man se lo come.
    private int xCasa;
    private int yCasa;

    // Momento (System.currentTimeMillis()) a partir del cual este
    // fantasma puede volver a ser "normal" despues de haber sido
    // comido. Se usa para el tiempo de reaparición.
    private long revivirEnMillis;

    public Fantasma(int xInicial, int yInicial, int xCasa, int yCasa) {
        x = xInicial;
        y = yInicial;
        this.xCasa = xCasa;
        this.yCasa = yCasa;
        velocidad = 3; // mismo valor que Pacman, debe ser divisor de TILE_SIZE
        direccionActual = null;
        estado = NORMAL;
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

    // La velocidad cambia según el estado: más lenta si está asustado,
    // más rápida si son los ojos volviendo a la casa. SIEMPRE debe ser
    // un divisor de Tablero.TILE_SIZE (1, 3, 7 o 21) para no romper el
    // sistema de alineación a la grilla.
    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public String getDireccionActual() {
        return direccionActual;
    }

    public void setDireccionActual(String direccionActual) {
        this.direccionActual = direccionActual;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getXCasa() {
        return xCasa;
    }

    public int getYCasa() {
        return yCasa;
    }

    public long getRevivirEnMillis() {
        return revivirEnMillis;
    }

    public void setRevivirEnMillis(long revivirEnMillis) {
        this.revivirEnMillis = revivirEnMillis;
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