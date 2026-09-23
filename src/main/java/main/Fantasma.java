package main;

public class Fantasma {

    public static final int TAMANO = 18;

    // Estados posibles de un fantasma.
    public static final String EN_CASA = "en_casa";      // Esperando en la casa antes de salir
    public static final String NORMAL = "normal";        // Persigue a Pac-Man
    public static final String ASUSTADO = "asustado";    // Pac-Man comió Power Pellet, huye
    public static final String COMIDO = "comido";        // Ojos volviendo a la casa

    // Tipos de fantasmas (con sus personalidades distintas)
    public static final String TIPO_BLINKY = "blinky";   // Rojo: persigue directo
    public static final String TIPO_PINKY = "pinky";     // Rosa: apunta 4 celdas adelante
    public static final String TIPO_INKY = "inky";       // Cyan: impredecible, reflejo
    public static final String TIPO_CLYDE = "clyde";     // Naranja: persigue/scatter

    private int x;
    private int y;
    private int velocidad;
    private String direccionActual;
    private String estado;
    private String tipo;                    // NUEVO: identifica qué fantasma es
    private long salirEnMillis;            // NUEVO: cuándo le toca salir de la casa

    // Posición (en píxeles) del punto dentro de la casa
    private int xCasa;
    private int yCasa;

    // Momento para volver a ser "normal" después de ser comido
    private long revivirEnMillis;

    public Fantasma(int xInicial, int yInicial, int xCasa, int yCasa) {
        x = xInicial;
        y = yInicial;
        this.xCasa = xCasa;
        this.yCasa = yCasa;
        velocidad = 3;
        direccionActual = null;
        estado = EN_CASA;               // MODIFICADO: ahora empieza EN_CASA
        tipo = TIPO_BLINKY;             // Por defecto Blinky, se cambia con setTipo()
        salirEnMillis = 0;              // Se asigna en Tablero
    }

    // Getters y Setters existentes
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

    public void setVelocidad(int velocidad) {
        this.velocidad = 7;
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

    // NUEVOS: Getters y Setters para tipo y salirEnMillis
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public long getSalirEnMillis() {
        return salirEnMillis;
    }

    public void setSalirEnMillis(long salirEnMillis) {
        this.salirEnMillis = salirEnMillis;
    }

    // Métodos de movimiento
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