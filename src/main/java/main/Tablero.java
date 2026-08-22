package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;

public class Tablero extends JPanel {

    // Tamaño ajustado para pantalla 1366x768 (Juana Manso 11,6")
    public static final int TILE_SIZE = 21;
    public static final int COLUMNAS = 28;
    public static final int FILAS = 31;

    private PacmanJugador pacman;

    public Tablero() {
        setPreferredSize(new Dimension(COLUMNAS * TILE_SIZE, FILAS * TILE_SIZE));
        setBackground(Color.BLACK);

        // Posición inicial: fila 1, columna 1 (celda libre del mapa)
        int xInicial = 1 * TILE_SIZE + 2;
        int yInicial = 1 * TILE_SIZE + 2;
        pacman = new PacmanJugador(xInicial, yInicial);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int codigo = e.getKeyCode();

                if (codigo == KeyEvent.VK_RIGHT) {
                    intentarMover("derecha");
                } else if (codigo == KeyEvent.VK_LEFT) {
                    intentarMover("izquierda");
                } else if (codigo == KeyEvent.VK_UP) {
                    intentarMover("arriba");
                } else if (codigo == KeyEvent.VK_DOWN) {
                    intentarMover("abajo");
                }

                repaint();
            }
        });
    }

    // Calcula la posicion siguiente segun la direccion, verifica colision
    // contra la matriz del mapa y recien ahi mueve a pacman de verdad.
    private void intentarMover(String direccion) {
        int vel = pacman.getVelocidad();
        int nuevoX = pacman.getX();
        int nuevoY = pacman.getY();

        switch (direccion) {
            case "derecha":
                nuevoX += vel;
                break;
            case "izquierda":
                nuevoX -= vel;
                break;
            case "arriba":
                nuevoY -= vel;
                break;
            case "abajo":
                nuevoY += vel;
                break;
        }

        if (!colisionaConPared(nuevoX, nuevoY)) {
            switch (direccion) {
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
    }

    // Revisa las 4 esquinas del cuadrado de pacman contra la matriz de mapa
    private boolean colisionaConPared(int x, int y) {
        int tam = PacmanJugador.TAMANO;

        int[][] esquinas = {
            {x, y},
            {x + tam - 1, y},
            {x, y + tam - 1},
            {x + tam - 1, y + tam - 1}
        };

        for (int[] esquina : esquinas) {
            int columna = esquina[0] / TILE_SIZE;
            int fila = esquina[1] / TILE_SIZE;
            if (esPared(fila, columna)) {
                return true;
            }
        }
        return false;
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
        g.fillOval(pacman.getX(), pacman.getY(), PacmanJugador.TAMANO, PacmanJugador.TAMANO);
    }
}