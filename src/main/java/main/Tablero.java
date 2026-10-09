package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tablero extends JPanel {

    // NO es final: main.java lo modifica al redimensionar la ventana.
    public static int TILE_SIZE = 21;
    public static final int COLUMNAS = mapa.COLUMNAS;
    public static final int FILAS = mapa.FILAS;
    public static final int FILA_TUNEL = mapa.FILA_TUNEL;

    // Celda dentro de la casa de los fantasmas a la que vuelven cuando
    // Pac-Man se los come (ahí "revive" y vuelve a salir).
    private static final int CASA_FILA = 16;
    private static final int CASA_COLUMNA = 13;

    // Cuánto dura el efecto de un Power Pellet (en milisegundos).
    private static final int DURACION_ASUSTADO_MS = 7000;

    // Cuánto dura la habilidad de congelar fantasmas (tecla G).
    private static final int DURACION_CONGELADO_MS = 4000;

    // Nuevos poderes especiales: Rayo Veloz y Rey del Laberinto.
    private static final int DURACION_PODER_ESPECIAL_MS = 15000;
    private static final int VELOCIDAD_NORMAL_PACMAN = 7;
    // Se conserva la velocidad base para no desalinear a Pac-Man de la grilla.
    // El Rayo Veloz aumenta la velocidad actualizando el movimiento dos veces por tick.

    // Cuántas cargas de romper paredes da cada Power Pellet especial (tile 5).
    private static final int CARGAS_POR_PELLET_ESPECIAL = 3;

    // Cuánto tiene que esperar un fantasma adentro de la casa, despues
    // de que Pac-Man se lo comió, antes de poder volver a salir.
    private static final int TIEMPO_REAPARICION_MS = 10000;

    // Tiempos de salida escalonada (en milisegundos desde el inicio del juego)
    private static final long SALIDA_BLINKY_MS = 0;      // Sale inmediato
    private static final long SALIDA_PINKY_MS = 3000;    // Sale a los 3 segundos
    private static final long SALIDA_INKY_MS = 7000;     // Sale a los 7 segundos
    private static final long SALIDA_CLYDE_MS = 12000;   // Sale a los 12 segundos

    // Ventana principal (se usa para volver al menú).
    private JFrame ventana;

    private PacmanJugador pacman;
    private Fantasma fantasma1;
    private Fantasma fantasma2;
    private Fantasma fantasma3;
    private Fantasma fantasma4;

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
    private boolean juegoTerminado; // Indica si el juego ya terminó.
    private boolean juegoPausado = false;
    private JButton btnVolverMenu;
    private long inicioPausaMillis = 0;
    private boolean gano; // true = ganó | false = todavía no ganó.
    private boolean perdio; // true = chocó con un fantasma y perdió todas las vidas.
    private int vidas = 3; // El juego comienza con 3 vidas.

    // Momento en que arrancó el juego.
    private long inicioJuegoMillis;

    // Momento (System.currentTimeMillis()) en el que termina el modo
    // asustado. 0 significa que el modo asustado no está activo.
    private long finAsustadoEnMillis = 0;

    // Momento en el que termina el congelamiento de fantasmas.
    // 0 significa que no hay congelamiento activo.
    private long finCongeladoEnMillis = 0;

    // Finalización de los dos poderes nuevos.
    private long finRayoVelozEnMillis = 0;
    private long finReyLaberintoEnMillis = 0;

    // Cuántos fantasmas seguidos se comió Pac-Man con el Power Pellet
    // actual. Sirve para el puntaje en cadena: 200, 400, 800, 1600.
    private int fantasmasComidosSeguidos = 0;

    public Tablero(JFrame ventana) {
        this.ventana = ventana;
        setLayout(null); // Necesario para ubicar el botón con coordenadas.

        setPreferredSize(new Dimension(
        COLUMNAS * TILE_SIZE,
        FILAS * TILE_SIZE + 60
        ));
        setBackground(Color.BLACK);

        // ===== CARGA DE SPRITES =====
        // Todos los recursos están dentro de:
        // src/main/resources/main/sprites/
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

        // Posicion inicial: DEBE ser multiplo de TILE_SIZE para que el
        // sistema de alineacion a la grilla funcione desde el arranque.
        int filaInicial = 1;
        int columnaInicial = 1;
        pacman = new PacmanJugador(columnaInicial * TILE_SIZE, filaInicial * TILE_SIZE);

        // Punto de la casa al que vuelven los fantasmas cuando los comen.
        int xCasa = CASA_COLUMNA * TILE_SIZE;
        int yCasa = CASA_FILA * TILE_SIZE;

        // Los fantasmas 2, 3 y 4 arrancan dentro de la casa (fila 16).
        // Blinky arranca afuera, listo para salir de inmediato.
        fantasma1 = new Fantasma(13 * TILE_SIZE, 11 * TILE_SIZE, xCasa, yCasa);
        fantasma2 = new Fantasma(12 * TILE_SIZE, 16 * TILE_SIZE, xCasa, yCasa);
        fantasma3 = new Fantasma(13 * TILE_SIZE, 16 * TILE_SIZE, xCasa, yCasa);
        fantasma4 = new Fantasma(14 * TILE_SIZE, 16 * TILE_SIZE, xCasa, yCasa);

        // Asignar tipo a cada fantasma y su tiempo de salida
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

        // Registrar cuándo arrancó el juego
        inicioJuegoMillis = System.currentTimeMillis();

        juegoTerminado = false;

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int codigo = e.getKeyCode();
                if (codigo == KeyEvent.VK_P) {
                    if (!juegoPausado) {
                        juegoPausado = true;
                        inicioPausaMillis = System.currentTimeMillis();
                        timer.stop();
                        btnVolverMenu.setVisible(true);
                    } else {
                        long tiempoPausado = System.currentTimeMillis() - inicioPausaMillis;

                        // Congelar los tiempos de los poderes y efectos activos.
                        if (finAsustadoEnMillis != 0) finAsustadoEnMillis += tiempoPausado;
                        if (finCongeladoEnMillis != 0) finCongeladoEnMillis += tiempoPausado;
                        if (finRayoVelozEnMillis != 0) finRayoVelozEnMillis += tiempoPausado;
                        if (finReyLaberintoEnMillis != 0) finReyLaberintoEnMillis += tiempoPausado;

                        juegoPausado = false;
                        btnVolverMenu.setVisible(false);
                        timer.start();
                        repaint();
                    }
                    repaint();
                    return;
                }

                // El Timer decide cuando aplicar la dirección (al llegar al centro de una celda).
                if (codigo == KeyEvent.VK_RIGHT) {
                    pacman.setDireccionDeseada("derecha");
                } else if (codigo == KeyEvent.VK_LEFT) {
                    pacman.setDireccionDeseada("izquierda");
                } else if (codigo == KeyEvent.VK_UP) {
                    pacman.setDireccionDeseada("arriba");
                } else if (codigo == KeyEvent.VK_DOWN) {
                    pacman.setDireccionDeseada("abajo");
                } else if (codigo == KeyEvent.VK_F) {
                    // Habilidad de romper paredes.
                    intentarRomperPared();
                } else if (codigo == KeyEvent.VK_G) {
                    // Habilidad de congelar fantasmas.
                    intentarCongelarFantasmas();
                }
            }
        });

        timer = new Timer(50, e -> {

            if (!juegoTerminado) {

                actualizarPoderesEspeciales();
                actualizarMovimiento();
                // El Rayo Veloz duplica los pasos de movimiento, sin cambiar el tamaño
                // de cada paso; así Pac-Man sigue alineado con las celdas y no atraviesa paredes.
                if (rayoVelozActivo()) {
                    actualizarMovimiento();
                }

                // Actualizamos los cuatro fantasmas.
                actualizarFantasma(fantasma1);
                actualizarFantasma(fantasma2);
                actualizarFantasma(fantasma3);
                actualizarFantasma(fantasma4);

                verificarColisionConFantasma();
            }

            repaint();
        });
        timer.start();
        
        btnVolverMenu = new JButton("VOLVER AL MENÚ");
        btnVolverMenu.setFont(new Font("Arial", Font.BOLD, 16));
        // Anclado al borde derecho del componente, no al centro del laberinto.
        // Se recalcula al cambiar el tamaño de la ventana.
        btnVolverMenu.setBounds(
            Math.max(10, getWidth() - 190),
            Math.max(10, getHeight() / 2 - 25),
            180,
            50
        );
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                btnVolverMenu.setLocation(
                    Math.max(10, getWidth() - btnVolverMenu.getWidth() - 10),
                    Math.max(10, getHeight() / 2 - btnVolverMenu.getHeight() / 2)
                );
            }
        });
        // Mismo estilo rojo que el botón SALIR del menú.
        btnVolverMenu.setBackground(new Color(180, 30, 30));
        btnVolverMenu.setForeground(Color.WHITE);
        btnVolverMenu.setOpaque(true);
        btnVolverMenu.setFocusPainted(false);
        btnVolverMenu.setVisible(false);

        btnVolverMenu.addActionListener(e -> volverAlMenu());

        add(btnVolverMenu);
        
    }

    // Carga un sprite desde src/main/resources.
    private BufferedImage cargarSprite(String ruta) throws IOException {
        java.io.InputStream entrada = getClass().getResourceAsStream(ruta);

        if (entrada == null) {
            throw new IOException("No se encontró: " + ruta);
        }

        return ImageIO.read(entrada);
    }

    // Logica central del movimiento por celdas
    private void actualizarMovimiento() {
        if (juegoPausado || juegoTerminado) {
            return;
        }
        boolean centrado = (pacman.getX() % TILE_SIZE == 0) && (pacman.getY() % TILE_SIZE == 0);

        if (centrado) {
            int fila = pacman.getY() / TILE_SIZE;
            int columna = pacman.getX() / TILE_SIZE;

            // Pacman come el punto de la celda actual
            Puntos.comerPunto(fila, columna);

            // Power Pellet normal (tile 3): activa el modo asustado
            // Y da 1 carga de congelar fantasmas (tecla G).
            if (Puntos.comerPowerPellet(fila, columna)) {
                activarModoAsustado();
                pacman.agregarCargaCongelar();
            }

            // Power Pellet especial (tile 5): activa el modo asustado
            // Y da 3 cargas para romper paredes (tecla F).
            if (Puntos.comerPowerPelletEspecial(fila, columna)) {
                activarModoAsustado();
                pacman.agregarCargasRomperParedes(CARGAS_POR_PELLET_ESPECIAL);
            }
            
            // Superbolita 7: activa el Rayo Veloz.
            if (Puntos.comerRayoVeloz(fila, columna)) {
                activarRayoVeloz();
            }

            // Superbolita 8: activa el Rey del Laberinto.
            if (Puntos.comerReyDelLaberinto(fila, columna)) {
                activarReyDelLaberinto();
            }

            // Comprobamos si ya no queda ningún punto en el mapa.
            if (Puntos.todosLosPuntosComidos()) {

                gano = true;
                juegoTerminado = true;
                timer.stop();

                // Botón para volver al menú en la pantalla de victoria.
                mostrarBotonVolverAlMenu();

                System.out.println("¡GANASTE! Pac-Man comió todos los puntos.");
                return;
            }

            // Si el jugador pidio girar y ese camino esta libre, se adopta ahora.
            if (puedeAvanzar(fila, columna, pacman.getDireccionDeseada(), null)) {
                pacman.setDireccionActual(pacman.getDireccionDeseada());
            }

            // Si la direccion actual choca con pared, Pacman se frena
            // exactamente centrado en la celda.
            if (!puedeAvanzar(fila, columna, pacman.getDireccionActual(), null)) {
                return;
            }
        }

        moverSegunDireccionActual();
        aplicarTunel();
    }

    // Activa Rayo Veloz durante 6 segundos.
    private void activarRayoVeloz() {
        if (juegoTerminado) return;
        long ahora = System.currentTimeMillis();
        finRayoVelozEnMillis = ahora + DURACION_PODER_ESPECIAL_MS;
        // Los poderes especiales no se acumulan: activar uno cancela el otro.
        finReyLaberintoEnMillis = 0;
        pacman.setVelocidad(VELOCIDAD_NORMAL_PACMAN);
    }

    // Activa invencibilidad y huida de los fantasmas durante 6 segundos.
    private void activarReyDelLaberinto() {
        if (juegoTerminado) return;
        long ahora = System.currentTimeMillis();
        finReyLaberintoEnMillis = ahora + DURACION_PODER_ESPECIAL_MS;
        // Los poderes especiales no se acumulan: activar uno cancela el otro.
        finRayoVelozEnMillis = 0;
        pacman.setVelocidad(VELOCIDAD_NORMAL_PACMAN);
    }

    private boolean rayoVelozActivo() {
        return finRayoVelozEnMillis != 0
                && System.currentTimeMillis() < finRayoVelozEnMillis;
    }

    private boolean reyDelLaberintoActivo() {
        return finReyLaberintoEnMillis != 0
                && System.currentTimeMillis() < finReyLaberintoEnMillis;
    }

    // Actualiza vencimientos para restaurar el movimiento normal al terminar.
    private void actualizarPoderesEspeciales() {
        long ahora = System.currentTimeMillis();
        if (finRayoVelozEnMillis != 0 && ahora >= finRayoVelozEnMillis) {
            finRayoVelozEnMillis = 0;
            pacman.setVelocidad(VELOCIDAD_NORMAL_PACMAN);
        }
        if (finReyLaberintoEnMillis != 0 && ahora >= finReyLaberintoEnMillis) {
            finReyLaberintoEnMillis = 0;
        }
    }

    // Habilidad de romper paredes con la tecla F.
    // Rompe la celda que está delante de Pac-Man, en la
    // dirección en la que se está moviendo.
    private void intentarRomperPared() {

        if (juegoTerminado) {
            return;
        }

        // Necesita una dirección actual para saber hacia dónde romper.
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

        // 1) No puede romper fuera del mapa (borde).
        if (filaDestino < 0 || filaDestino >= FILAS ||
            columnaDestino < 0 || columnaDestino >= COLUMNAS) {
            return;
        }

        // 2) No puede romper puertas de la casa de fantasmas.
        if (mapa.MATRIZ[filaDestino][columnaDestino] == 4) {
            return;
        }

        // 3) Solo se rompen paredes (tile 1).
        if (mapa.MATRIZ[filaDestino][columnaDestino] != 1) {
            return;
        }

        // 4) Necesita una carga disponible.
        if (!pacman.usarCargaRomperParedes()) {
            return;
        }

        // 5) Romper la pared: pasa a tile 6 (transitable, no cuenta
        //    para ganar y no rompe la lógica de la casa).
        mapa.MATRIZ[filaDestino][columnaDestino] = 6;
    }

    // Habilidad de congelar fantasmas con la tecla G.
    // Consume 1 carga y congela a todos los fantasmas durante unos segundos.
    private void intentarCongelarFantasmas() {

        if (juegoTerminado) {
            return;
        }

        // Si ya están congelados, no gastamos otra carga.
        if (estanCongelados()) {
            return;
        }

        if (!pacman.usarCargaCongelar()) {
            return;
        }

        finCongeladoEnMillis = System.currentTimeMillis() + DURACION_CONGELADO_MS;
    }

    // Indica si el congelamiento sigue activo.
    private boolean estanCongelados() {
        return finCongeladoEnMillis != 0 && System.currentTimeMillis() < finCongeladoEnMillis;
    }

    // Un fantasma está congelado si el efecto está activo y no es un par de ojos.
    private boolean estaCongelado(Fantasma fantasma) {
        return estanCongelados() && !Fantasma.COMIDO.equals(fantasma.getEstado());
    }

    // Revisa si desde (fila, columna) se puede avanzar un paso en esa direccion.
    private boolean puedeAvanzar(int fila, int columna, String direccion, Fantasma fantasma) {
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

        // Tunel: en la fila habilitada, dejar "salir" del mapa por los bordes.
        if (fila == FILA_TUNEL && (columnaDestino < 0 || columnaDestino >= COLUMNAS)) {
            return true;
        }

        if (esPared(filaDestino, columnaDestino)) {
            return false;
        }

        // Solo dejamos ENTRAR a la casa de los fantasmas (desde afuera)
        // a un fantasma que está en estado "comido".
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

    // Indica si la celda (fila, columna) forma parte de la casa de los fantasmas.
    private boolean esCasaFantasmas(int fila, int columna) {

        if (fila < 15 || fila > 17 || columna < 11 || columna > 16) {
            return false;
        }

        int tile = mapa.MATRIZ[fila][columna];

        // 4 = puerta
        // 0 = interior de la casa
        // 6 = pared rota -> no cuenta como casa

        return tile == 0 || tile == 4;
    }

    // Si Pacman cruzo el borde del mapa por el tunel, lo reaparece del otro lado
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

    // Actualiza el movimiento de un fantasma.
    private void actualizarFantasma(Fantasma fantasma) {

        // Fantasma congelado: no decide ni se mueve. Al descongelarse
        // sigue exactamente desde donde estaba.
        if (estaCongelado(fantasma)) {
            return;
        }

        boolean centrado = (fantasma.getX() % TILE_SIZE == 0)
                && (fantasma.getY() % TILE_SIZE == 0);

        if (centrado) {
            actualizarEstadoFantasma(fantasma);
        }

        moverFantasmaSegunDireccion(fantasma);

        aplicarTunelFantasma(fantasma);
    }

    // Decide en qué estado queda el fantasma y hacia dónde se mueve
    // despues. Se llama UNICAMENTE cuando el fantasma está centrado.
    private void actualizarEstadoFantasma(Fantasma fantasma) {

        // SALIDA ESCALONADA: mientras el fantasma esté EN_CASA y
        // todavía no le toque su turno, lo dejamos quieto.
        if (Fantasma.EN_CASA.equals(fantasma.getEstado())) {
            long transcurrido = System.currentTimeMillis() - inicioJuegoMillis;
            if (transcurrido < fantasma.getSalirEnMillis()) {
                fantasma.setDireccionActual(null);
                return;
            }
            fantasma.setEstado(Fantasma.NORMAL);
        }

        // Si el modo asustado ya terminó, este fantasma deja de estar asustado.
        if (Fantasma.ASUSTADO.equals(fantasma.getEstado()) && yaTerminoElAsustado()) {
            fantasma.setEstado(Fantasma.NORMAL);
        }

        int fila = fantasma.getY() / TILE_SIZE;
        int columna = fantasma.getX() / TILE_SIZE;

        // LLEGADA A LA CASA (ojos volviendo).
        if (Fantasma.COMIDO.equals(fantasma.getEstado())) {
            int filaCasa = fantasma.getYCasa() / TILE_SIZE;
            int colCasa  = fantasma.getXCasa() / TILE_SIZE;

            if (fila == filaCasa && columna == colCasa) {
                // Snap exacto para evitar desalineaciones.
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

        // Ajustamos la velocidad según el estado ACTUAL.
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

        // La IA decide la mejor dirección para ESTE fantasma.
        fantasma.setDireccionActual(elegirDireccionFantasma(fila, columna, fantasma));
    }

    // Indica si ya se cumplió el tiempo de efecto del último Power Pellet.
    private boolean yaTerminoElAsustado() {
        return finAsustadoEnMillis != 0 && System.currentTimeMillis() >= finAsustadoEnMillis;
    }

    // IA DE FANTASMAS
    private String elegirDireccionFantasma(int fila, int columna, Fantasma fantasma) {

        boolean comido = Fantasma.COMIDO.equals(fantasma.getEstado());
        boolean huyendo = Fantasma.ASUSTADO.equals(fantasma.getEstado())
                || reyDelLaberintoActivo();

        // CASO 1: dentro de la casa y no es un par de ojos. Forzamos la salida.
        if (esCasaFantasmas(fila, columna) && !comido) {
            return direccionParaSalirDeCasa(fila, columna, fantasma);
        }

        // Determinamos el objetivo según estado y tipo.
        int filaObjetivo;
        int columnaObjetivo;

        if (comido) {
            filaObjetivo  = fantasma.getYCasa() / TILE_SIZE;
            columnaObjetivo = fantasma.getXCasa() / TILE_SIZE;
        } else if (huyendo) {
            filaObjetivo  = pacman.getY() / TILE_SIZE;
            columnaObjetivo = pacman.getX() / TILE_SIZE;
        } else {
            int[] objetivo = calcularObjetivoPersecucion(fantasma);
            filaObjetivo  = objetivo[0];
            columnaObjetivo = objetivo[1];
        }

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

        // Fallback: si solo puede volver por donde vino, lo permitimos.
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

    // Objetivo de persecución según el TIPO de fantasma.
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
            }
            return new int[] { 29, 1 };
        }

        return new int[] { pf, pc };
    }

    // Ruta forzada para SALIR DE LA CASA.
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

    // Se llama cuando Pac-Man come un Power Pellet.
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

    // Mueve al fantasma recibido según su dirección actual.
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

    // Permite que el fantasma atraviese el túnel.
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

    // Comprueba si Pac-Man chocó con alguno de los cuatro fantasmas.
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

        // El Rey del Laberinto evita perder vidas mientras dura.
        if (reyDelLaberintoActivo()) {
            return false;
        }

        // Un fantasma congelado no hace daño.
        if (estaCongelado(fantasma)) {
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

    // Pac-Man se come a un fantasma asustado.
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

    // Le hace perder una vida a Pac-Man.
    private void perderVida() {
        vidas--;

        if (vidas <= 0) {
            terminarPorGameOver();
            return;
        }

        reiniciarPosiciones();
    }

    // Vuelve a Pac-Man y a los fantasmas a sus posiciones y estado iniciales.
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
        finCongeladoEnMillis = 0;
        finRayoVelozEnMillis = 0;
        finReyLaberintoEnMillis = 0;
        pacman.setVelocidad(VELOCIDAD_NORMAL_PACMAN);
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

    // Termina el juego cuando Pac-Man se queda sin vidas.
    private void terminarPorGameOver() {

        juegoTerminado = true;
        perdio = true;

        timer.stop();

         Sonido.detenerTodos();
        Sonido.reproducir(Sonido.GAME_OVER);
        // Botón para volver al menú principal.
        mostrarBotonVolverAlMenu();

        System.out.println("Pacman fue atrapado. Game Over.");
    }

    // Botón que aparece en las pantallas de Game Over y de victoria.
    private void mostrarBotonVolverAlMenu() {
        JButton btnMenu = new JButton("VOLVER AL MENÚ");
        btnMenu.setBounds(getWidth() / 2 - 120, 360, 240, 50);
        btnMenu.setOpaque(true);
        btnMenu.setBorderPainted(false);
        btnMenu.setBackground(new Color(255, 200, 0));
        btnMenu.setForeground(Color.BLACK);
        btnMenu.setFont(new Font("Arial", Font.BOLD, 20));
        btnMenu.setFocusPainted(false);
        btnMenu.addActionListener(e -> volverAlMenu());
        add(btnMenu);

        revalidate();
        repaint();
    }

    // Reemplaza el tablero por el menú principal.
    private void volverAlMenu() {
        timer.stop();

        Menu menu = new Menu(ventana);

        ventana.remove(this);
        ventana.add(menu);
        ventana.revalidate();
        ventana.repaint();

        menu.requestFocusInWindow();
    }

    public boolean esPared(int fila, int columna) {
        if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS) {
            return true;
        }
        // Solo el tile 1 es pared. El 6 (pared rota) no es pared.
        return mapa.MATRIZ[fila][columna] == 1;
    }

    



@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    g.setColor(Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight());

    int anchoJuego = COLUMNAS * TILE_SIZE;
    int altoJuego = FILAS * TILE_SIZE + 60;

    int desplazamientoX = Math.max(0, (getWidth() - anchoJuego) / 2);
    int desplazamientoY = Math.max(0, (getHeight() - altoJuego) / 2);

    Graphics gJuego = g.create();
    gJuego.translate(desplazamientoX, desplazamientoY);

    // Información superior
    dibujarPuntaje(gJuego);
    dibujarRecordatorioHabilidad(gJuego);

    // Laberinto y personajes, debajo de la franja superior
    Graphics gLaberinto = gJuego.create();
    gLaberinto.translate(0, 60);

    dibujarMapa(gLaberinto);
    dibujarPacman(gLaberinto);
    dibujarFantasma(gLaberinto);

    gLaberinto.dispose();
    gJuego.dispose();

    if (gano) {
        dibujarPantallaWin(g);
    }

    if (perdio) {
        dibujarPantallaGameOver(g);
    }
}

    // Recordatorio visual de las habilidades.
    // Se muestra en la esquina superior derecha, un panel por habilidad,
    // y solo si hay cargas disponibles.
    
    
    
private void dibujarRecordatorioHabilidad(Graphics g) {
    // Habilidades con cargas: lado izquierdo.
    int yPanel = 5;

    int cargasPared = pacman.getCargasRomperParedes();
    if (cargasPared > 0) {
        dibujarPanelHabilidad(
                g, 10, yPanel,
                "[F] Romper paredes",
                cargasPared, Color.ORANGE
        );
        yPanel += 28;
    }

    int cargasHielo = pacman.getCargasCongelar();
    if (cargasHielo > 0) {
        dibujarPanelHabilidad(
                g, 10, yPanel,
                "[G] Congelar",
                cargasHielo, Color.CYAN
        );
    }

    // Poderes temporales: lado derecho.
    int xPoder = COLUMNAS * TILE_SIZE - 200;
    int yPoder = 5;

    if (rayoVelozActivo()) {
        dibujarPanelHabilidad(
                g, xPoder, yPoder,
                "RAYO VELOZ",
                (int) segundosRestantes(finRayoVelozEnMillis),
                Color.RED
        );
        yPoder += 28;
    }

    if (reyDelLaberintoActivo()) {
        dibujarPanelHabilidad(
                g, xPoder, yPoder,
                "REY DEL LABERINTO",
                (int) segundosRestantes(finReyLaberintoEnMillis),
                Color.RED
        );
    }
}

    private long segundosRestantes(long finEnMillis) {
        return Math.max(0, (finEnMillis - System.currentTimeMillis() + 999) / 1000);
    }

    // Dibuja un panelcito con el nombre de la habilidad y sus cargas.
    private void dibujarPanelHabilidad(Graphics g, int xPanel, int yPanel,
        String nombre, int cargas, Color colorBorde) {

        int anchoPanel = 190;
        int altoPanel = 26;

        // Fondo negro semitransparente para que se lea bien.
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(xPanel, yPanel, anchoPanel, altoPanel, 12, 12);

        // Borde del color de la habilidad
        g.setColor(colorBorde);
        g.drawRoundRect(xPanel, yPanel, anchoPanel, altoPanel, 12, 12);
        g.drawRoundRect(xPanel + 1, yPanel + 1, anchoPanel - 2, altoPanel - 2, 12, 12);

        
        g.setFont(g.getFont().deriveFont(java.awt.Font.BOLD, 13f));
        g.setColor(colorBorde);
        g.drawString(nombre + " x" + cargas, xPanel + 10, yPanel + 20);
    }

    // Dibuja la pantalla que aparece cuando Pac-Man pierde todas sus vidas.
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

    // Dibuja la pantalla que aparece cuando Pac-Man gana.
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
        g.setFont(g.getFont().deriveFont(
                java.awt.Font.BOLD, 18f));

        String textoPuntaje = "Puntaje: " + Puntos.getPuntaje();
        java.awt.FontMetrics fm = g.getFontMetrics();
        int xPuntaje = (COLUMNAS * TILE_SIZE - fm.stringWidth(textoPuntaje)) / 2;

        g.drawString(textoPuntaje, xPuntaje, 22);

        g.setFont(g.getFont().deriveFont(
                java.awt.Font.PLAIN, 16f));

        String textoVidas = "Vidas:";
        fm = g.getFontMetrics();

        int anchoVidas = fm.stringWidth(textoVidas);
        int anchoCorazones = vidas > 0 ? vidas * 23 + (vidas - 1) * 4 : 0;
        int espacio = 8;
        int anchoTotal = anchoVidas + espacio + anchoCorazones;

        int xInicio = (COLUMNAS * TILE_SIZE - anchoTotal) / 2;

        g.drawString(textoVidas, xInicio, 48);

        if (spriteCorazon != null) {
            for (int i = 0; i < vidas; i++) {
                g.drawImage(
                        spriteCorazon,
                        xInicio + anchoVidas + espacio + i * 27,
                        29, 23, 23, this);
            }
        } else {
            g.drawString(
                    String.valueOf(vidas),
                    xInicio + anchoVidas + espacio,
                    48);
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
                        // Power Pellet normal: alterna entre las bolas roja y azul.
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
                        // Power Pellet especial: bola verde.
                        dibujarSpriteCentrado(g, spriteBolaVerde, x, y, 20, 21);
                        break;
                        
                        case 7:
                        // Rayo Veloz: bolita amarilla con un rayo.
                        g.setColor(Color.YELLOW);
                        g.fillOval(x + 3, y + 3, TILE_SIZE - 6, TILE_SIZE - 6);

                        g.setColor(Color.BLACK);
                        g.drawLine(x + 12, y + 5, x + 8, y + 11);
                        g.drawLine(x + 8, y + 11, x + 13, y + 11);
                        g.drawLine(x + 13, y + 11, x + 10, y + 16);
                        break;

                    case 8:
                        // Rey del Laberinto: bolita naranja.
                        g.setColor(Color.ORANGE);
                        g.fillOval(x + 3, y + 3, TILE_SIZE - 6, TILE_SIZE - 6);
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

        // 3 estados visuales: cerrado -> semi -> abierto.
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

        // Tinte de hielo sobre los fantasmas congelados.
        dibujarHielo(g, fantasma1);
        dibujarHielo(g, fantasma2);
        dibujarHielo(g, fantasma3);
        dibujarHielo(g, fantasma4);
    }

    // Dibuja un recuadro celeste semitransparente sobre un fantasma congelado.
    private void dibujarHielo(Graphics g, Fantasma fantasma) {
        if (!estaCongelado(fantasma)) {
            return;
        }

        g.setColor(new Color(120, 220, 255, 140));
        g.fillRoundRect(fantasma.getX(), fantasma.getY(),
                Fantasma.TAMANO, Fantasma.TAMANO, 8, 8);

        g.setColor(Color.WHITE);
        g.drawRoundRect(fantasma.getX(), fantasma.getY(),
                Fantasma.TAMANO, Fantasma.TAMANO, 8, 8);
    }

    private void dibujarFantasmaNormal(Graphics g, Fantasma fantasma,
            BufferedImage derecha, BufferedImage izquierda,
            BufferedImage arriba, BufferedImage abajo) {

        String estado = fantasma.getEstado();

        // Fantasma comido: solo se ven los ojos.
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

        // Fantasma asustado.
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

        // Fantasma normal.
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