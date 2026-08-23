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

    public Tablero() {
        setPreferredSize(new Dimension(COLUMNAS * TILE_SIZE, FILAS * TILE_SIZE));
        setBackground(Color.BLACK);

        // Posicion inicial: DEBE ser multiplo de TILE_SIZE para que el
        // sistema de alineacion a la grilla funcione desde el arranque.
        int filaInicial = 1;
        int columnaInicial = 1;
        pacman = new PacmanJugador(columnaInicial * TILE_SIZE, filaInicial * TILE_SIZE);

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
        Timer timer = new Timer(50, e -> {
            actualizarMovimiento();
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
}