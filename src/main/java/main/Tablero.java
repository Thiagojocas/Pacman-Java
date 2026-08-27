package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tablero extends JPanel {

    public static final int TILE_SIZE = 21;
    public static final int COLUMNAS = 28;
    public static final int FILAS = 31;
    public static final int FILA_TUNEL = 14; // fila con abertura en columna 0 y 27

    private PacmanJugador pacman;
    private Fantasma fantasma;
    private Timer timer;
    private boolean juegoTerminado; // Indica si el juego ya terminó.
    private boolean gano; // Indica si el jugador ganó. true = ganó | false = todavía no ganó.

    public Tablero() {
        setPreferredSize(new Dimension(COLUMNAS * TILE_SIZE, FILAS * TILE_SIZE));
        setBackground(Color.BLACK);

        // Posicion inicial: DEBE ser multiplo de TILE_SIZE para que el
        // sistema de alineacion a la grilla funcione desde el arranque.
        int filaInicial = 1;
        int columnaInicial = 1;
        pacman = new PacmanJugador(columnaInicial * TILE_SIZE, filaInicial * TILE_SIZE);

        // Punto de partida del fantasma: pasillo abierto justo arriba
        // de la puerta de la casa de fantasmas.
        int filaFantasma = 11;
        int columnaFantasma = 13;
        fantasma = new Fantasma(columnaFantasma * TILE_SIZE, filaFantasma * TILE_SIZE);

        juegoTerminado = false;

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int codigo = e.getKeyCode();

                // Ya NO movemos aca. Solo guardamos la intencion del jugador;
                // el Timer decide cuando aplicarla (al llegar al centro de una celda).
                if (codigo == KeyEvent.VK_RIGHT) {
                    pacman.setDireccionDeseada("derecha");
                } else if (codigo == KeyEvent.VK_LEFT) {
                    pacman.setDireccionDeseada("izquierda");
                } else if (codigo == KeyEvent.VK_UP) {
                    pacman.setDireccionDeseada("arriba");
                } else if (codigo == KeyEvent.VK_DOWN) {
                    pacman.setDireccionDeseada("abajo");
                }
            }
        });

        // Game loop: se ejecuta cada 50ms (20 veces por segundo)
        timer = new Timer(50, e -> {
            if (!juegoTerminado) {
                actualizarMovimiento();
                actualizarFantasma();
                verificarColisionConFantasma();
            }
            repaint();
        });
        timer.start();
    }

    // Logica central del movimiento por celdas
    private void actualizarMovimiento() {
        boolean centrado = (pacman.getX() % TILE_SIZE == 0) && (pacman.getY() % TILE_SIZE == 0);

        if (centrado) {
            int fila = pacman.getY() / TILE_SIZE;
            int columna = pacman.getX() / TILE_SIZE;
            
            // Pacman come el punto de la celda actual
            Puntos.comerPunto(fila, columna);
            
            // Pac-Man acaba de comer el punto de esta celda.

            // Comprobamos si ya no queda ningún punto en el mapa.
            if (Puntos.todosLosPuntosComidos()) {
                
                // Indicamos que el jugador ganó.
                gano = true;

                // Indicamos que el juego terminó.
                juegoTerminado = true;

                // Detenemos el Timer.
                // Al detenerlo, Pac-Man y los fantasmas dejan de moverse.
                timer.stop();

                // Por ahora mostramos un mensaje en la consola.
                // Más adelante lo reemplazaremos por nuestra pantalla WIN.
                System.out.println("¡GANASTE! Pac-Man comió todos los puntos.");
            }

            // Si el jugador pidio girar y ese camino esta libre, se adopta ahora.
            // Esto es lo que permite doblar justo en las esquinas, no antes ni despues.
            if (puedeAvanzar(fila, columna, pacman.getDireccionDeseada())) {
                pacman.setDireccionActual(pacman.getDireccionDeseada());
            }

            // Si la direccion actual choca con pared, Pacman se frena
            // exactamente centrado en la celda (nunca queda a mitad de camino).
            if (!puedeAvanzar(fila, columna, pacman.getDireccionActual())) {
                return;
            }
        }

        moverSegunDireccionActual();
        aplicarTunel();
    }

    // Revisa si desde (fila, columna) se puede avanzar un paso en esa direccion
    private boolean puedeAvanzar(int fila, int columna, String direccion) {
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

        // Tunel: en la fila habilitada, dejar "salir" del mapa por los bordes
        // en vez de bloquear como si fuera pared.
        if (fila == FILA_TUNEL && (columnaDestino < 0 || columnaDestino >= COLUMNAS)) {
            return true;
        }

        return !esPared(filaDestino, columnaDestino);
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

    // Logica central del movimiento del fantasma: misma idea de "centrado
    // en la celda" que usa Pacman, pero la direccion la decide la IA.
    private void actualizarFantasma() {
        boolean centrado = (fantasma.getX() % TILE_SIZE == 0) && (fantasma.getY() % TILE_SIZE == 0);

        if (centrado) {
            int fila = fantasma.getY() / TILE_SIZE;
            int columna = fantasma.getX() / TILE_SIZE;
            fantasma.setDireccionActual(elegirDireccionFantasma(fila, columna));
        }

        moverFantasmaSegunDireccion();
        aplicarTunelFantasma();
    }

    // IA de persecucion: de las direcciones libres, evita revertir el
    // camino (salvo que sea la unica opcion) y elige la que mas acerca
    // al fantasma a la celda actual de Pacman.
    private String elegirDireccionFantasma(int fila, int columna) {
        String[] direcciones = {"arriba", "abajo", "izquierda", "derecha"};
        String opuesta = direccionOpuesta(fantasma.getDireccionActual());

        int filaPacman = pacman.getY() / TILE_SIZE;
        int columnaPacman = pacman.getX() / TILE_SIZE;

        String mejorDireccion = null;
        int mejorDistancia = Integer.MAX_VALUE;

        for (String direccion : direcciones) {
            if (direccion.equals(opuesta)) {
                continue;
            }
            if (!puedeAvanzar(fila, columna, direccion)) {
                continue;
            }

            int[] destino = calcularDestino(fila, columna, direccion);
            int distancia = distanciaAlCuadrado(destino[0], destino[1], filaPacman, columnaPacman);

            if (distancia < mejorDistancia) {
                mejorDistancia = distancia;
                mejorDireccion = direccion;
            }
        }

        // Callejon sin salida: la unica opcion era revertir, se permite.
        if (mejorDireccion == null) {
            for (String direccion : direcciones) {
                if (puedeAvanzar(fila, columna, direccion)) {
                    mejorDireccion = direccion;
                    break;
                }
            }
        }

        return mejorDireccion;
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

    private void moverFantasmaSegunDireccion() {
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

    // Mismo wraparound del tunel, aplicado al fantasma
    private void aplicarTunelFantasma() {
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

    // Colision por superposicion de rectangulos (mas confiable que comparar
    // celdas exactas, porque detecta el choque aunque no esten perfectamente
    // alineados al centro de la celda en el mismo instante).
    private void verificarColisionConFantasma() {
        boolean colisionan =
            pacman.getX() < fantasma.getX() + Fantasma.TAMANO &&
            pacman.getX() + PacmanJugador.TAMANO > fantasma.getX() &&
            pacman.getY() < fantasma.getY() + Fantasma.TAMANO &&
            pacman.getY() + PacmanJugador.TAMANO > fantasma.getY();

        if (colisionan) {
            juegoTerminado = true;
            timer.stop();
            System.out.println("Pacman fue atrapado. Game Over.");
        }
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
        if (gano){dibujarPantallaWin(g);}
        }
    // Dibuja la pantalla que aparece cuando el jugador gana.
        private void dibujarPantallaWin(Graphics g) {

            // Fondo negro para cubrir el tablero.
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Color del texto.
            g.setColor(Color.YELLOW);

            // Tamaño de la letra.
            g.setFont(g.getFont().deriveFont(32f));

            // Mensaje principal.
            g.drawString("¡GANASTE!", 250, 250);

            // Mostramos el puntaje final.
            g.setFont(g.getFont().deriveFont(20f));
            g.drawString("Puntaje: " + Puntos.getPuntaje(), 260, 290);
        }
    
        private void dibujarPuntaje(Graphics g) {
            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(18f));
            g.drawString("Puntaje: " + Puntos.getPuntaje(), 10, 20);
    }

    private void dibujarMapa(Graphics g) {
        int[][] matriz = mapa.MATRIZ;

        for (int fila = 0; fila < FILAS; fila++) {
            for (int col = 0; col < COLUMNAS; col++) {
                int tile = matriz[fila][col];
                int x = col * TILE_SIZE;
                int y = fila * TILE_SIZE;

                switch (tile) {
                    case 1: // pared
                        g.setColor(Color.BLUE);
                        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        break;
                    case 2: // pellet
                        g.setColor(Color.WHITE);
                        g.fillOval(x + TILE_SIZE / 2 - 2, y + TILE_SIZE / 2 - 2, 4, 4);
                        break;
                    case 3: // power pellet
                        g.setColor(Color.WHITE);
                        g.fillOval(x + TILE_SIZE / 2 - 5, y + TILE_SIZE / 2 - 5, 10, 10);
                        break;
                    case 4: // puerta casa fantasmas
                        g.setColor(Color.PINK);
                        g.fillRect(x, y + TILE_SIZE / 2 - 1, TILE_SIZE, 3);
                        break;
                    // case 0: vacio, no se dibuja nada
                }
            }
        }
    }

    private void dibujarPacman(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(pacman.getX() + 1, pacman.getY() + 1, PacmanJugador.TAMANO, PacmanJugador.TAMANO);
    }

    private void dibujarFantasma(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(fantasma.getX() + 1, fantasma.getY() + 1, Fantasma.TAMANO, Fantasma.TAMANO);
    }
}