package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tablero extends JPanel {

    // ========== CONFIGURACIÓN DE TAMAÑO ==========
    public static int TILE_SIZE = 21;
    public static final int COLUMNAS = mapa.COLUMNAS;
    public static final int FILAS = mapa.FILAS;
    public static final int FILA_TUNEL = mapa.FILA_TUNEL;

    private static final int CASA_FILA = 16;
    private static final int CASA_COLUMNA = 13;

    private static final int DURACION_ASUSTADO_MS = 7000;
    private static final int TIEMPO_REAPARICION_MS = 20000;

    private static final long SALIDA_BLINKY_MS = 0;
    private static final long SALIDA_PINKY_MS = 3000;
    private static final long SALIDA_INKY_MS = 7000;
    private static final long SALIDA_CLYDE_MS = 12000;

    // ========== ESTADOS ==========
    private PacmanJugador pacman;
    private Fantasma fantasma1;
    private Fantasma fantasma2;
    private Fantasma fantasma3;
    private Fantasma fantasma4;

    private boolean juegoPausado = false;
    private long tiempoAntesPausa = 0;
    private long milisAcumuladosPausado = 0;

    // ===== SPRITES =====
    private BufferedImage spritePacmanDerechaAbierto, spritePacmanDerechaSemi;
    private BufferedImage spritePacmanIzquierdaAbierto, spritePacmanIzquierdaSemi;
    private BufferedImage spritePacmanArribaAbierto, spritePacmanArribaSemi;
    private BufferedImage spritePacmanAbajoAbierto, spritePacmanAbajoSemi;
    private BufferedImage spritePacmanCerrado;

    private BufferedImage spriteFredDerecha, spriteFredIzquierda;
    private BufferedImage spriteFredArriba, spriteFredAbajo;
    private BufferedImage spriteColmanDerecha, spriteColmanIzquierda;
    private BufferedImage spriteColmanArriba, spriteColmanAbajo;
    private BufferedImage spritePoshoDerecha, spritePoshoIzquierda;
    private BufferedImage spritePoshoArriba, spritePoshoAbajo;
    private BufferedImage spriteSopaDerecha, spriteSopaIzquierda;
    private BufferedImage spriteSopaArriba, spriteSopaAbajo;

    private BufferedImage spriteAsustadoAzul, spriteAsustadoBlanco;
    private BufferedImage spriteOjosDerecha, spriteOjosIzquierda;
    private BufferedImage spriteOjosArriba, spriteOjosAbajo;
    private BufferedImage spritePunto;
    private BufferedImage spriteBolaAzul, spriteBolaRoja, spriteBolaVerde;
    private BufferedImage spriteCorazon;

    private Timer timer;
    private boolean juegoTerminado;
    private boolean gano;
    private boolean perdio;
    private int vidas = 3;

    private long inicioJuegoMillis;
    private long finAsustadoEnMillis = 0;

    private long ultimoCambioAnimacion = 0;
    private boolean pacmanBocaAbierta = true;

    private int fantasmasComidosSeguidos = 0;

    // ========== SCATTER/CHASE ==========
    private long ultimoCambioComportamiento = 0;
    private boolean enModoPersecucion = true;
    private static final int DURACION_CHASE_MS = 20000;
    private static final int DURACION_SCATTER_MS = 7000;

    // ========== RANDOM ==========
    private Random random = new Random();

    public Tablero() {
        setPreferredSize(new Dimension(COLUMNAS * TILE_SIZE, FILAS * TILE_SIZE));
        setBackground(Color.BLACK);

        // ===== CARGA DE SPRITES =====
        try {
            spritePacmanDerechaAbierto = cargarSprite("/main/sprites/DerechaAbi.png");
            spritePacmanDerechaSemi = cargarSprite("/main/sprites/DerechaSemi.png");
            spritePacmanIzquierdaAbierto = cargarSprite("/main/sprites/IzquierdaAbi.png");
            spritePacmanIzquierdaSemi = cargarSprite("/main/sprites/IzquierdaSemi.png");
            spritePacmanArribaAbierto = cargarSprite("/main/sprites/ArribaAbi.png");
            spritePacmanArribaSemi = cargarSprite("/main/sprites/ArribaSemi.png");
            spritePacmanAbajoAbierto = cargarSprite("/main/sprites/AbajoAbi.png");
            spritePacmanAbajoSemi = cargarSprite("/main/sprites/AbajoSemi.png");
            spritePacmanCerrado = cargarSprite("/main/sprites/Cerrado.png");

            spriteFredDerecha = cargarSprite("/main/sprites/FredDerecha.png");
            spriteFredIzquierda = cargarSprite("/main/sprites/FredIzquierda.png");
            spriteFredArriba = cargarSprite("/main/sprites/FredArriba.png");
            spriteFredAbajo = cargarSprite("/main/sprites/FredAbajo.png");

            spriteColmanDerecha = cargarSprite("/main/sprites/ColmanDerecha.png");
            spriteColmanIzquierda = cargarSprite("/main/sprites/ColmanIzquierda.png");
            spriteColmanArriba = cargarSprite("/main/sprites/ColmanArriba.png");
            spriteColmanAbajo = cargarSprite("/main/sprites/ColmanAbajo.png");

            spritePoshoDerecha = cargarSprite("/main/sprites/PoshoDerecha.png");
            spritePoshoIzquierda = cargarSprite("/main/sprites/PoshoIzquierda.png");
            spritePoshoArriba = cargarSprite("/main/sprites/PoshoArriba.png");
            spritePoshoAbajo = cargarSprite("/main/sprites/PoshoAbajo.png");

            spriteSopaDerecha = cargarSprite("/main/sprites/SopaDerecha.png");
            spriteSopaIzquierda = cargarSprite("/main/sprites/SopaIzquierda.png");
            spriteSopaArriba = cargarSprite("/main/sprites/SopaArriba.png");
            spriteSopaAbajo = cargarSprite("/main/sprites/SopaAbajo.png");

            spriteAsustadoAzul = cargarSprite("/main/sprites/AsustadoAzul.png");
            spriteAsustadoBlanco = cargarSprite("/main/sprites/AsustadoBlanco.png");

            spriteOjosDerecha = cargarSprite("/main/sprites/OjosDerecha.png");
            spriteOjosIzquierda = cargarSprite("/main/sprites/OjosIzquierda.png");
            spriteOjosArriba = cargarSprite("/main/sprites/OjosArriba.png");
            spriteOjosAbajo = cargarSprite("/main/sprites/OjosAbajo.png");

            spritePunto = cargarSprite("/main/sprites/Puntos.png");
            spriteBolaAzul = cargarSprite("/main/sprites/BolaAzul.png");
            spriteBolaRoja = cargarSprite("/main/sprites/BolaRoja.png");
            spriteBolaVerde = cargarSprite("/main/sprites/BolaVerde.png");
            spriteCorazon = cargarSprite("/main/sprites/Corazon.png");

        } catch (Exception e) {
            System.out.println("Error cargando sprites: " + e.getMessage());
        }

        int filaInicial = 1;
        int columnaInicial = 1;
        pacman = new PacmanJugador(columnaInicial * TILE_SIZE, filaInicial * TILE_SIZE);

        int xCasa = CASA_COLUMNA * TILE_SIZE;
        int yCasa = CASA_FILA * TILE_SIZE;

        fantasma1 = new Fantasma(13 * TILE_SIZE, 11 * TILE_SIZE, xCasa, yCasa);
        fantasma2 = new Fantasma(12 * TILE_SIZE, 16 * TILE_SIZE, xCasa, yCasa);
        fantasma3 = new Fantasma(13 * TILE_SIZE, 16 * TILE_SIZE, xCasa, yCasa);
        fantasma4 = new Fantasma(14 * TILE_SIZE, 16 * TILE_SIZE, xCasa, yCasa);

        fantasma1.setTipo(Fantasma.TIPO_BLINKY);
        fantasma1.setSalirEnMillis(SALIDA_BLINKY_MS);
        fantasma1.setEstado(Fantasma.EN_CASA);

        fantasma2.setTipo(Fantasma.TIPO_PINKY);
        fantasma2.setSalirEnMillis(SALIDA_PINKY_MS);
        fantasma2.setEstado(Fantasma.EN_CASA);

        fantasma3.setTipo(Fantasma.TIPO_INKY);
        fantasma3.setSalirEnMillis(SALIDA_INKY_MS);
        fantasma3.setEstado(Fantasma.EN_CASA);

        fantasma4.setTipo(Fantasma.TIPO_CLYDE);
        fantasma4.setSalirEnMillis(SALIDA_CLYDE_MS);
        fantasma4.setEstado(Fantasma.EN_CASA);

        inicioJuegoMillis = System.currentTimeMillis();
        juegoTerminado = false;

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int codigo = e.getKeyCode();

                if (codigo == KeyEvent.VK_P) {
                    togglePausa();
                    return;
                }

                if (juegoPausado) {
                    return;
                }

                if (codigo == KeyEvent.VK_RIGHT) {
                    pacman.setDireccionDeseada("derecha");
                } else if (codigo == KeyEvent.VK_LEFT) {
                    pacman.setDireccionDeseada("izquierda");
                } else if (codigo == KeyEvent.VK_UP) {
                    pacman.setDireccionDeseada("arriba");
                } else if (codigo == KeyEvent.VK_DOWN) {
                    pacman.setDireccionDeseada("abajo");
                } else if (codigo == KeyEvent.VK_F) {
                    intentarRomperPared();
                }
            }
        });

        timer = new Timer(50, e -> {

            if (!juegoPausado && !juegoTerminado) {

                actualizarMovimiento();

                actualizarComportamientoFantasmas();

                actualizarFantasma(fantasma1);
                actualizarFantasma(fantasma2);
                actualizarFantasma(fantasma3);
                actualizarFantasma(fantasma4);

                verificarColisionConFantasma();
            }

            repaint();
        });
        timer.start();
    }

    // ========== SISTEMA DE PAUSA ==========
    private void togglePausa() {
        if (juegoTerminado) {
            return;
        }

        juegoPausado = !juegoPausado;

        if (juegoPausado) {
            tiempoAntesPausa = System.currentTimeMillis();
            System.out.println("⏸️ JUEGO PAUSADO - Presiona P para continuar");
        } else {
            long tiempoPausado = System.currentTimeMillis() - tiempoAntesPausa;
            milisAcumuladosPausado += tiempoPausado;

            inicioJuegoMillis += tiempoPausado;
            if (finAsustadoEnMillis > 0) {
                finAsustadoEnMillis += tiempoPausado;
            }

            System.out.println("▶️ JUEGO REANUDADO");
        }
    }

    private void actualizarComportamientoFantasmas() {
        long ahora = System.currentTimeMillis();

        if (ultimoCambioComportamiento == 0) {
            ultimoCambioComportamiento = ahora;
            return;
        }

        long transcurrido = ahora - ultimoCambioComportamiento;
        int duracionActual = enModoPersecucion ? DURACION_CHASE_MS : DURACION_SCATTER_MS;

        if (transcurrido > duracionActual) {
            enModoPersecucion = !enModoPersecucion;
            ultimoCambioComportamiento = ahora;

            String modo = enModoPersecucion ? "PERSECUCIÓN" : "DISPERSIÓN";
            System.out.println("Fantasmas cambian a modo: " + modo);
        }
    }

    private BufferedImage cargarSprite(String ruta) throws IOException {
        java.io.InputStream entrada = getClass().getResourceAsStream(ruta);

        if (entrada == null) {
            throw new IOException("No se encontró: " + ruta);
        }

        return ImageIO.read(entrada);
    }

    private void actualizarMovimiento() {
        boolean centrado = (pacman.getX() % TILE_SIZE == 0) && (pacman.getY() % TILE_SIZE == 0);

        if (centrado) {
            int fila = pacman.getY() / TILE_SIZE;
            int columna = pacman.getX() / TILE_SIZE;

            Puntos.comerPunto(fila, columna);

            if (Puntos.comerPowerPellet(fila, columna)) {
                activarModoAsustado();
            }

            if (Puntos.comerPowerPelletEspecial(fila, columna)) {
                activarModoAsustado();
                pacman.agregarCargaRomperParedes();
            }

            if (Puntos.todosLosPuntosComidos()) {
                gano = true;
                juegoTerminado = true;
                timer.stop();

                System.out.println("¡GANASTE! Pac-Man comió todos los puntos.");
            }

            if (puedeAvanzar(fila, columna, pacman.getDireccionDeseada(), null)) {
                pacman.setDireccionActual(pacman.getDireccionDeseada());
            }

            if (!puedeAvanzar(fila, columna, pacman.getDireccionActual(), null)) {
                return;
            }
        }

        moverSegunDireccionActual();
        aplicarTunel();
    }

    private void intentarRomperPared() {

        if (juegoTerminado) {
            return;
        }

        String dir = pacman.getDireccionActual();
        if (dir == null) {
            return;
        }

        int fila = pacman.getY() / TILE_SIZE;
        int columna = pacman.getX() / TILE_SIZE;

        int filaDestino = fila;
        int columnaDestino = columna;

        switch (dir) {
            case "derecha":   columnaDestino++; break;
            case "izquierda": columnaDestino--; break;
            case "arriba":    filaDestino--;    break;
            case "abajo":     filaDestino++;    break;
        }

        if (filaDestino < 0 || filaDestino >= FILAS ||
            columnaDestino < 0 || columnaDestino >= COLUMNAS) {
            return;
        }

        if (mapa.MATRIZ[filaDestino][columnaDestino] == 4) {
            return;
        }

        if (mapa.MATRIZ[filaDestino][columnaDestino] != 1) {
            return;
        }

        if (!pacman.usarCargaRomperParedes()) {
            return;
        }

        mapa.MATRIZ[filaDestino][columnaDestino] = 6;
    }

    public boolean puedeAvanzar(int fila, int columna, String direccion, Fantasma fantasma) {
        if (direccion == null) {
            return false;
        }

        int filaDestino = fila;
        int columnaDestino = columna;

        switch (direccion) {
            case "derecha":
                columnaDestino++;
                break;
            case "izquierda":
                columnaDestino--;
                break;
            case "arriba":
                filaDestino--;
                break;
            case "abajo":
                filaDestino++;
                break;
        }

        if (fila == FILA_TUNEL && (columnaDestino < 0 || columnaDestino >= COLUMNAS)) {
            return true;
        }

        if (esPared(filaDestino, columnaDestino)) {
            return false;
        }

        boolean entrandoACasa = esCasaFantasmas(filaDestino, columnaDestino)
                && !esCasaFantasmas(fila, columna);

        if (entrandoACasa) {
            boolean puedeEntrar = fantasma != null && Fantasma.COMIDO.equals(fantasma.getEstado());
            if (!puedeEntrar) {
                return false;
            }
        }

        return true;
    }

    private boolean esCasaFantasmas(int fila, int columna) {

        if (fila < 15 || fila > 17 || columna < 11 || columna > 16) {
            return false;
        }

        int tile = mapa.MATRIZ[fila][columna];

        return tile == 0 || tile == 4;
    }

    private void aplicarTunel() {
        if (pacman.getY() != FILA_TUNEL * TILE_SIZE) {
            return;
        }

        int limiteDerecho = (COLUMNAS - 1) * TILE_SIZE;

        if (pacman.getX() < 0) {
            pacman.setX(limiteDerecho);
        } else if (pacman.getX() > limiteDerecho) {
            pacman.setX(0);
        }
    }

    private void moverSegunDireccionActual() {
        String dir = pacman.getDireccionActual();
        if (dir == null) {
            return;
        }

        switch (dir) {
            case "derecha":
                pacman.moverDerecha();
                break;
            case "izquierda":
                pacman.moverIzquierda();
                break;
            case "arriba":
                pacman.moverArriba();
                break;
            case "abajo":
                pacman.moverAbajo();
                break;
        }
    }

    private void actualizarFantasma(Fantasma fantasma) {

        boolean centrado = (fantasma.getX() % TILE_SIZE == 0)
                && (fantasma.getY() % TILE_SIZE == 0);

        if (centrado) {
            actualizarEstadoFantasma(fantasma);
        }

        moverFantasmaSegunDireccion(fantasma);

        aplicarTunelFantasma(fantasma);
    }

    private void actualizarEstadoFantasma(Fantasma fantasma) {

        if (Fantasma.EN_CASA.equals(fantasma.getEstado())) {
            long transcurrido = System.currentTimeMillis() - inicioJuegoMillis;
            if (transcurrido < fantasma.getSalirEnMillis()) {
                fantasma.setDireccionActual(null);
                return;
            }
            fantasma.setEstado(Fantasma.NORMAL);
        }

        if (Fantasma.ASUSTADO.equals(fantasma.getEstado()) && yaTerminoElAsustado()) {
            fantasma.setEstado(Fantasma.NORMAL);
        }

        int fila = fantasma.getY() / TILE_SIZE;
        int columna = fantasma.getX() / TILE_SIZE;

        if (Fantasma.COMIDO.equals(fantasma.getEstado())) {
            int filaCasa = fantasma.getYCasa() / TILE_SIZE;
            int colCasa  = fantasma.getXCasa() / TILE_SIZE;

            if (fila == filaCasa && columna == colCasa) {
                fantasma.setX(fantasma.getXCasa());
                fantasma.setY(fantasma.getYCasa());

                if (System.currentTimeMillis() >= fantasma.getRevivirEnMillis()) {
                    fantasma.setEstado(Fantasma.NORMAL);
                } else {
                    fantasma.setDireccionActual(null);
                    return;
                }
            }
        }

        switch (fantasma.getEstado()) {
            case Fantasma.ASUSTADO:
                fantasma.setVelocidad(3);
                break;
            case Fantasma.COMIDO:
                fantasma.setVelocidad(7);
                break;
            default:
                fantasma.setVelocidad(7);
                break;
        }

        fantasma.setDireccionActual(elegirDireccionFantasmaIA(fila, columna, fantasma));
    }

    private boolean yaTerminoElAsustado() {
        return finAsustadoEnMillis != 0 && System.currentTimeMillis() >= finAsustadoEnMillis;
    }

    // ========== IA DE FANTASMAS MEJORADA (SIN BFS) ==========
    private String elegirDireccionFantasmaIA(int fila, int columna, Fantasma fantasma) {

        boolean comido = Fantasma.COMIDO.equals(fantasma.getEstado());
        boolean huyendo = Fantasma.ASUSTADO.equals(fantasma.getEstado());

        // Si está en la casa, salir
        if (esCasaFantasmas(fila, columna) && !comido) {
            return direccionParaSalirDeCasa(fila, columna, fantasma);
        }

        // Determinar objetivo
        int filaObjetivo;
        int columnaObjetivo;

        if (comido) {
            filaObjetivo  = fantasma.getYCasa() / TILE_SIZE;
            columnaObjetivo = fantasma.getXCasa() / TILE_SIZE;
        } else if (huyendo) {
            // Huir de Pac-Man
            int filaPac = pacman.getY() / TILE_SIZE;
            int colPac = pacman.getX() / TILE_SIZE;
            filaObjetivo = filaPac + (filaPac - fila) * 3;
            columnaObjetivo = colPac + (colPac - columna) * 3;
        } else {
            // Persecución o dispersión
            if (enModoPersecucion) {
                int[] objetivo = calcularObjetivoPersecucion(fantasma);
                filaObjetivo = objetivo[0];
                columnaObjetivo = objetivo[1];
            } else {
                int[] esquina = calcularEsquinaDispersion(fantasma);
                filaObjetivo = esquina[0];
                columnaObjetivo = esquina[1];
            }
        }

        // Elegir mejor dirección usando distancia Manhattan
        String[] direcciones = {"arriba", "abajo", "izquierda", "derecha"};
        String opuesta = direccionOpuesta(fantasma.getDireccionActual());

        String mejorDireccion = null;
        int mejorDistancia = huyendo ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (String direccion : direcciones) {

            if (direccion.equals(opuesta)) {
                continue;
            }

            if (!puedeAvanzar(fila, columna, direccion, fantasma)) {
                continue;
            }

            int[] destino = calcularDestino(fila, columna, direccion);

            int distancia = distanciaAlCuadrado(
                    destino[0], destino[1],
                    filaObjetivo, columnaObjetivo);

            boolean esMejor = huyendo ? (distancia > mejorDistancia) : (distancia < mejorDistancia);

            if (esMejor) {
                mejorDistancia = distancia;
                mejorDireccion = direccion;
            }
        }

        // Fallback: cualquier dirección válida
        if (mejorDireccion == null) {
            for (String direccion : direcciones) {
                if (puedeAvanzar(fila, columna, direccion, fantasma)) {
                    mejorDireccion = direccion;
                    break;
                }
            }
        }

        return mejorDireccion;
    }

    private int[] calcularObjetivoPersecucion(Fantasma fantasma) {

        int pf = pacman.getY() / TILE_SIZE;
        int pc = pacman.getX() / TILE_SIZE;

        String dirPac = pacman.getDireccionActual();
        int df = 0, dc = 0;
        if ("arriba".equals(dirPac))         df = -1;
        else if ("abajo".equals(dirPac))     df =  1;
        else if ("izquierda".equals(dirPac)) dc = -1;
        else if ("derecha".equals(dirPac))   dc =  1;

        if (Fantasma.TIPO_PINKY.equals(fantasma.getTipo())) {
            return new int[] { pf + 4 * df, pc + 4 * dc };
        }

        if (Fantasma.TIPO_INKY.equals(fantasma.getTipo())) {
            int filaBlinky = fantasma1.getY() / TILE_SIZE;
            int colBlinky  = fantasma1.getX() / TILE_SIZE;

            int filaFrente = pf + 2 * df;
            int colFrente  = pc + 2 * dc;

            int vf = filaFrente - filaBlinky;
            int vc = colFrente  - colBlinky;

            return new int[] { filaFrente + vf, colFrente + vc };
        }

        if (Fantasma.TIPO_CLYDE.equals(fantasma.getTipo())) {
            int filaFant = fantasma.getY() / TILE_SIZE;
            int colFant  = fantasma.getX() / TILE_SIZE;

            int dist2 = distanciaAlCuadrado(filaFant, colFant, pf, pc);

            if (dist2 > 64) {
                return new int[] { pf, pc };
            } else {
                return new int[] { 29, 1 };
            }
        }

        return new int[] { pf, pc };
    }

    private int[] calcularEsquinaDispersion(Fantasma fantasma) {
        if (Fantasma.TIPO_BLINKY.equals(fantasma.getTipo())) {
            return new int[] { 1, 26 };
        } else if (Fantasma.TIPO_PINKY.equals(fantasma.getTipo())) {
            return new int[] { 1, 1 };
        } else if (Fantasma.TIPO_INKY.equals(fantasma.getTipo())) {
            return new int[] { 29, 26 };
        } else {
            return new int[] { 29, 1 };
        }
    }

    private String direccionParaSalirDeCasa(int fila, int columna, Fantasma fantasma) {

        int colPuerta = 13;
        if (Math.abs(columna - 14) < Math.abs(columna - 13)) {
            colPuerta = 14;
        }

        if (columna < colPuerta && puedeAvanzar(fila, columna, "derecha", fantasma)) {
            return "derecha";
        }
        if (columna > colPuerta && puedeAvanzar(fila, columna, "izquierda", fantasma)) {
            return "izquierda";
        }
        if (puedeAvanzar(fila, columna, "arriba", fantasma)) {
            return "arriba";
        }

        for (String d : new String[]{"arriba", "izquierda", "derecha", "abajo"}) {
            if (puedeAvanzar(fila, columna, d, fantasma)) {
                return d;
            }
        }
        return null;
    }

    private void activarModoAsustado() {
        finAsustadoEnMillis = System.currentTimeMillis() + DURACION_ASUSTADO_MS;
        fantasmasComidosSeguidos = 0;

        ponerAsustado(fantasma1);
        ponerAsustado(fantasma2);
        ponerAsustado(fantasma3);
        ponerAsustado(fantasma4);
    }

    private void ponerAsustado(Fantasma fantasma) {
        if (Fantasma.COMIDO.equals(fantasma.getEstado())) {
            return;
        }
        fantasma.setEstado(Fantasma.ASUSTADO);
    }

    private int[] calcularDestino(int fila, int columna, String direccion) {
        int filaDestino = fila;
        int columnaDestino = columna;

        switch (direccion) {
            case "derecha":
                columnaDestino++;
                break;
            case "izquierda":
                columnaDestino--;
                break;
            case "arriba":
                filaDestino--;
                break;
            case "abajo":
                filaDestino++;
                break;
        }

        return new int[] {filaDestino, columnaDestino};
    }

    private int distanciaAlCuadrado(int fila1, int columna1, int fila2, int columna2) {
        int df = fila1 - fila2;
        int dc = columna1 - columna2;
        return df * df + dc * dc;
    }

    private String direccionOpuesta(String direccion) {
        if (direccion == null) {
            return null;
        }
        switch (direccion) {
            case "arriba":
                return "abajo";
            case "abajo":
                return "arriba";
            case "izquierda":
                return "derecha";
            case "derecha":
                return "izquierda";
        }
        return null;
    }

    private void moverFantasmaSegunDireccion(Fantasma fantasma) {

        String dir = fantasma.getDireccionActual();

        if (dir == null) {
            return;
        }

        switch (dir) {

            case "derecha":
                fantasma.moverDerecha();
                break;

            case "izquierda":
                fantasma.moverIzquierda();
                break;

            case "arriba":
                fantasma.moverArriba();
                break;

            case "abajo":
                fantasma.moverAbajo();
                break;
        }
    }

    private void aplicarTunelFantasma(Fantasma fantasma) {

        if (fantasma.getY() != FILA_TUNEL * TILE_SIZE) {
            return;
        }

        int limiteDerecho = (COLUMNAS - 1) * TILE_SIZE;

        if (fantasma.getX() < 0) {
            fantasma.setX(limiteDerecho);
        } else if (fantasma.getX() > limiteDerecho) {
            fantasma.setX(0);
        }
    }

    private void verificarColisionConFantasma() {

        if (procesarColision(fantasma1)) return;
        if (procesarColision(fantasma2)) return;
        if (procesarColision(fantasma3)) return;
        procesarColision(fantasma4);
    }

    private boolean procesarColision(Fantasma fantasma) {

        if (!hayColision(pacman, fantasma)) {
            return false;
        }

        if (Fantasma.COMIDO.equals(fantasma.getEstado())) {
            return false;
        }

        if (Fantasma.ASUSTADO.equals(fantasma.getEstado())) {
            comerFantasma(fantasma);
            return false;
        }

        perderVida();
        return true;
    }

    private boolean hayColision(PacmanJugador pacman, Fantasma fantasma) {

        return pacman.getX() < fantasma.getX() + Fantasma.TAMANO
                && pacman.getX() + PacmanJugador.TAMANO > fantasma.getX()
                && pacman.getY() < fantasma.getY() + Fantasma.TAMANO
                && pacman.getY() + PacmanJugador.TAMANO > fantasma.getY();
    }

    private void comerFantasma(Fantasma fantasma) {
        fantasmasComidosSeguidos++;

        int puntos = 200 * (int) Math.pow(2, fantasmasComidosSeguidos - 1);
        if (puntos > 1600) {
            puntos = 1600;
        }
        Puntos.sumarPuntos(puntos);

        fantasma.setEstado(Fantasma.COMIDO);
        fantasma.setRevivirEnMillis(System.currentTimeMillis() + TIEMPO_REAPARICION_MS);
    }

    private void perderVida() {
        vidas--;

        if (vidas <= 0) {
            terminarPorGameOver();
            return;
        }

        reiniciarPosiciones();
    }

    private void reiniciarPosiciones() {
        pacman.setX(1 * TILE_SIZE);
        pacman.setY(1 * TILE_SIZE);
        pacman.setDireccionActual(null);
        pacman.setDireccionDeseada(null);

        reiniciarFantasma(fantasma1, 13 * TILE_SIZE, 11 * TILE_SIZE);
        reiniciarFantasma(fantasma2, 12 * TILE_SIZE, 16 * TILE_SIZE);
        reiniciarFantasma(fantasma3, 13 * TILE_SIZE, 16 * TILE_SIZE);
        reiniciarFantasma(fantasma4, 14 * TILE_SIZE, 16 * TILE_SIZE);

        finAsustadoEnMillis = 0;
        fantasmasComidosSeguidos = 0;

        inicioJuegoMillis = System.currentTimeMillis();
    }

    private void reiniciarFantasma(Fantasma fantasma, int x, int y) {
        fantasma.setX(x);
        fantasma.setY(y);
        fantasma.setDireccionActual(null);
        fantasma.setEstado(Fantasma.EN_CASA);
        fantasma.setVelocidad(3);
        fantasma.setRevivirEnMillis(0);
    }

    private void terminarPorGameOver() {

        juegoTerminado = true;
        perdio = true;

        timer.stop();

        System.out.println("Pacman fue atrapado. Game Over.");
    }

    public boolean esPared(int fila, int columna) {
        if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS) {
            return true;
        }
        return mapa.MATRIZ[fila][columna] == 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        dibujarMapa(g);
        dibujarPacman(g);
        dibujarFantasma(g);
        dibujarPuntaje(g);
        dibujarRecordatorioHabilidad(g);
        if (juegoPausado) { dibujarPantallaPausa(g); }
        if (gano){ dibujarPantallaWin(g); }
        if (perdio){ dibujarPantallaGameOver(g); }
    }

    // ========== PANTALLA DE PAUSA ==========
    private void dibujarPantallaPausa(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.YELLOW);
        g.setFont(g.getFont().deriveFont(java.awt.Font.BOLD, 50f));

        String titulo = "PAUSADO";
        int anchoTitulo = g.getFontMetrics().stringWidth(titulo);
        int xTitulo = (getWidth() - anchoTitulo) / 2;

        g.drawString(titulo, xTitulo, 250);

        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(java.awt.Font.PLAIN, 20f));

        String instruccion = "Presiona P para continuar";
        int anchoInstr = g.getFontMetrics().stringWidth(instruccion);
        int xInstr = (getWidth() - anchoInstr) / 2;

        g.drawString(instruccion, xInstr, 320);
    }

    private void dibujarRecordatorioHabilidad(Graphics g) {

        int cargas = pacman.getCargasRomperParedes();

        if (cargas <= 0) {
            return;
        }

        int anchoPanel = 180;
        int altoPanel = 50;
        int xPanel = getWidth() - anchoPanel - 10;
        int yPanel = 10;

        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(xPanel, yPanel, anchoPanel, altoPanel, 12, 12);

        g.setColor(Color.ORANGE);
        g.drawRoundRect(xPanel, yPanel, anchoPanel, altoPanel, 12, 12);
        g.drawRoundRect(xPanel + 1, yPanel + 1, anchoPanel - 2, altoPanel - 2, 12, 12);

        g.setFont(g.getFont().deriveFont(java.awt.Font.BOLD, 14f));
        g.setColor(Color.ORANGE);
        g.drawString("[F] Romper pared", xPanel + 14, yPanel + 22);

        g.setFont(g.getFont().deriveFont(13f));
        g.setColor(Color.WHITE);
        g.drawString("Cargas: " + cargas, xPanel + 14, yPanel + 40);
    }

    private void dibujarPantallaGameOver(Graphics g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.RED);
        g.setFont(g.getFont().deriveFont(40f));

        String titulo = "GAME OVER";

        int anchoTitulo = g.getFontMetrics().stringWidth(titulo);
        int xTitulo = (getWidth() - anchoTitulo) / 2;
        int yTitulo = 230;

        g.drawString(titulo, xTitulo, yTitulo);

        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(22f));

        String textoPuntaje = "Puntaje: " + Puntos.getPuntaje();
        int anchoPuntaje = g.getFontMetrics().stringWidth(textoPuntaje);
        int xPuntaje = (getWidth() - anchoPuntaje) / 2;

        g.drawString(textoPuntaje, xPuntaje, 280);

        g.setFont(g.getFont().deriveFont(18f));

        String mensaje = "Te atraparon los fantasmas.";
        int anchoMensaje = g.getFontMetrics().stringWidth(mensaje);
        int xMensaje = (getWidth() - anchoMensaje) / 2;

        g.drawString(mensaje, xMensaje, 320);
    }

    private void dibujarPantallaWin(Graphics g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.YELLOW);
        g.setFont(g.getFont().deriveFont(40f));

        String titulo = "¡GANASTE!";

        int anchoTitulo = g.getFontMetrics().stringWidth(titulo);
        int xTitulo = (getWidth() - anchoTitulo) / 2;
        int yTitulo = 230;

        g.drawString(titulo, xTitulo, yTitulo);

        g.setFont(g.getFont().deriveFont(22f));

        String textoPuntaje = "Puntaje: " + Puntos.getPuntaje();
        int anchoPuntaje = g.getFontMetrics().stringWidth(textoPuntaje);
        int xPuntaje = (getWidth() - anchoPuntaje) / 2;

        g.drawString(textoPuntaje, xPuntaje, 280);

        g.setFont(g.getFont().deriveFont(18f));

        String mensaje = "¡Comiste todas las bolitas!";
        int anchoMensaje = g.getFontMetrics().stringWidth(mensaje);
        int xMensaje = (getWidth() - anchoMensaje) / 2;

        g.drawString(mensaje, xMensaje, 320);
    }

    private void dibujarPuntaje(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        g.drawString("Puntaje: " + Puntos.getPuntaje(), 10, 20);

        g.setFont(g.getFont().deriveFont(java.awt.Font.PLAIN, 16f));
        g.drawString("Vidas:", 10, 46);

        if (spriteCorazon != null) {
            for (int i = 0; i < vidas; i++) {
                g.drawImage(spriteCorazon, 68 + (i * 27), 24, 23, 23, this);
            }
        } else {
            g.drawString(String.valueOf(vidas), 68, 46);
        }
    }

    private void dibujarMapa(Graphics g) {
        int[][] matriz = mapa.MATRIZ;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        for (int fila = 0; fila < FILAS; fila++) {
            for (int col = 0; col < COLUMNAS; col++) {
                int tile = matriz[fila][col];
                int x = col * TILE_SIZE;
                int y = fila * TILE_SIZE;

                switch (tile) {
                    case 1:
                        g.setColor(new Color(0, 180, 255));
                        g.drawRoundRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 6, 6);
                        break;

                    case 2:
                        dibujarSpriteCentrado(g, spritePunto, x, y, 17, 19);
                        break;

                    case 3:
                        BufferedImage bolaNormal =
                                ((System.currentTimeMillis() / 250) % 2 == 0)
                                ? spriteBolaRoja : spriteBolaAzul;
                        dibujarSpriteCentrado(g, bolaNormal, x, y, 20, 21);
                        break;

                    case 4:
                        g.setColor(Color.PINK);
                        g.fillRect(x + 2, y + TILE_SIZE / 2 - 2, TILE_SIZE - 4, 4);
                        break;

                    case 5:
                        dibujarSpriteCentrado(g, spriteBolaVerde, x, y, 20, 21);
                        break;

                    case 6:
                        break;

                    case 0:
                        break;
                }
            }
        }
    }

    private void dibujarSpriteCentrado(Graphics g, BufferedImage sprite,
            int xCelda, int yCelda, int ancho, int alto) {
        if (sprite == null) {
            return;
        }

        int x = xCelda + (TILE_SIZE - ancho) / 2;
        int y = yCelda + (TILE_SIZE - alto) / 2;
        g.drawImage(sprite, x, y, ancho, alto, this);
    }

    private void dibujarPacman(Graphics g) {
        String direccion = pacman.getDireccionActual();

        if (direccion == null) {
            direccion = "derecha";
        }

        long ahora = System.currentTimeMillis();

        int frame = (int) ((ahora / 100) % 3);

        BufferedImage sprite;

        if (frame == 0) {
            sprite = spritePacmanCerrado;
        } else if (frame == 1) {
            sprite = obtenerSpritePacmanSemi(direccion);
        } else {
            sprite = obtenerSpritePacmanAbierto(direccion);
        }

        if (sprite != null) {
            g.drawImage(sprite, pacman.getX(), pacman.getY(),
                    PacmanJugador.TAMANO, PacmanJugador.TAMANO, this);
        } else {
            g.setColor(Color.YELLOW);
            g.fillOval(pacman.getX() + 1, pacman.getY() + 1,
                    PacmanJugador.TAMANO, PacmanJugador.TAMANO);
        }
    }

    private BufferedImage obtenerSpritePacmanAbierto(String direccion) {
        switch (direccion) {
            case "izquierda":
                return spritePacmanIzquierdaAbierto;
            case "arriba":
                return spritePacmanArribaAbierto;
            case "abajo":
                return spritePacmanAbajoAbierto;
            case "derecha":
            default:
                return spritePacmanDerechaAbierto;
        }
    }

    private BufferedImage obtenerSpritePacmanSemi(String direccion) {
        switch (direccion) {
            case "izquierda":
                return spritePacmanIzquierdaSemi;
            case "arriba":
                return spritePacmanArribaSemi;
            case "abajo":
                return spritePacmanAbajoSemi;
            case "derecha":
            default:
                return spritePacmanDerechaSemi;
        }
    }

    private void dibujarFantasma(Graphics g) {
        dibujarFantasmaNormal(g, fantasma1,
                spriteFredDerecha, spriteFredIzquierda,
                spriteFredArriba, spriteFredAbajo);

        dibujarFantasmaNormal(g, fantasma2,
                spriteColmanDerecha, spriteColmanIzquierda,
                spriteColmanArriba, spriteColmanAbajo);

        dibujarFantasmaNormal(g, fantasma3,
                spritePoshoDerecha, spritePoshoIzquierda,
                spritePoshoArriba, spritePoshoAbajo);

        dibujarFantasmaNormal(g, fantasma4,
                spriteSopaDerecha, spriteSopaIzquierda,
                spriteSopaArriba, spriteSopaAbajo);
    }

    private void dibujarFantasmaNormal(Graphics g, Fantasma fantasma,
            BufferedImage derecha, BufferedImage izquierda,
            BufferedImage arriba, BufferedImage abajo) {

        String estado = fantasma.getEstado();

        if (Fantasma.COMIDO.equals(estado)) {
            BufferedImage ojos = spriteOjosDerecha;
            String direccion = fantasma.getDireccionActual();

            if ("izquierda".equals(direccion)) {
                ojos = spriteOjosIzquierda;
            } else if ("arriba".equals(direccion)) {
                ojos = spriteOjosArriba;
            } else if ("abajo".equals(direccion)) {
                ojos = spriteOjosAbajo;
            }

            if (ojos != null) {
                g.drawImage(ojos,
                        fantasma.getX(),
                        fantasma.getY() + 4,
                        21,
                        12,
                        this);
            }
            return;
        }

        if (Fantasma.ASUSTADO.equals(estado)) {
            long restante = finAsustadoEnMillis - System.currentTimeMillis();
            boolean porTerminar = restante < 2000;

            boolean parpadeoBlanco =
                    porTerminar && ((System.currentTimeMillis() / 200) % 2 == 0);

            BufferedImage spriteAsustado =
                    parpadeoBlanco ? spriteAsustadoBlanco : spriteAsustadoAzul;

            if (spriteAsustado != null) {
                g.drawImage(spriteAsustado,
                        fantasma.getX(), fantasma.getY(),
                        Fantasma.TAMANO, Fantasma.TAMANO, this);
            }
            return;
        }

        String direccion = fantasma.getDireccionActual();
        BufferedImage sprite = derecha;

        if ("izquierda".equals(direccion)) {
            sprite = izquierda;
        } else if ("arriba".equals(direccion)) {
            sprite = arriba;
        } else if ("abajo".equals(direccion)) {
            sprite = abajo;
        }

        if (sprite != null) {
            g.drawImage(sprite,
                    fantasma.getX(), fantasma.getY(),
                    Fantasma.TAMANO, Fantasma.TAMANO, this);
        } else {
            Color colorNormal = Color.RED;

            if (fantasma == fantasma2) {
                colorNormal = Color.PINK;
            } else if (fantasma == fantasma3) {
                colorNormal = Color.CYAN;
            } else if (fantasma == fantasma4) {
                colorNormal = Color.ORANGE;
            }

            g.setColor(colorNormal);
            g.fillOval(fantasma.getX() + 1, fantasma.getY() + 1,
                    Fantasma.TAMANO, Fantasma.TAMANO);
        }
    }
}