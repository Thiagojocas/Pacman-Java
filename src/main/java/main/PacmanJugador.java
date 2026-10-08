package main;

public class PacmanJugador {

    public static final int TAMANO = 18; // tamaño visual, se dibuja con leve inset

    private int x;
    private int y;
    private int velocidad;
    private String direccionActual;   // la que se está ejecutando de verdad
    private String direccionDeseada;  // la última tecla que tocó el jugador

    // Cuántas veces puede romper paredes con la tecla F.
    // Cada Power Pellet especial (tile 5) suma 3.
    private int cargasRomperParedes;

    // Cuántas veces puede congelar fantasmas con la tecla G.
    // Cada Power Pellet normal (tile 3) suma 1.
    private int cargasCongelar;

    public PacmanJugador(int xInicial, int yInicial) {
        x = xInicial;
        y = yInicial;
        velocidad = 7; // debe ser divisor exacto de Tablero.TILE_SIZE
        direccionActual = null;
        direccionDeseada = null;
        cargasRomperParedes = 0;
        cargasCongelar = 0;
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

    // ===== ROMPER PAREDES (tecla F) =====
    public int getCargasRomperParedes() {
        return cargasRomperParedes;
    }

    public void agregarCargaRomperParedes() {
        cargasRomperParedes++;
    }

    // Suma varias cargas de una vez.
    public void agregarCargasRomperParedes(int cantidad) {
        cargasRomperParedes += cantidad;
    }

    // Intenta consumir una carga. Devuelve true si había disponible.
    public boolean usarCargaRomperParedes() {
        if (cargasRomperParedes > 0) {
            cargasRomperParedes--;
            return true;
        }
        return false;
    }

    // ===== CONGELAR FANTASMAS (tecla G) =====
    public int getCargasCongelar() {
        return cargasCongelar;
    }

    public void agregarCargaCongelar() {
        cargasCongelar++;
    }

    // Intenta consumir una carga. Devuelve true si había disponible.
    public boolean usarCargaCongelar() {
        if (cargasCongelar > 0) {
            cargasCongelar--;
            return true;
        }
        return false;
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