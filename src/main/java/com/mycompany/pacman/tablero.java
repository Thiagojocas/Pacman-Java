package com.mycompany.pacman;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Tablero extends JPanel {
     
    private PacmanJugador pacman;

    public Tablero() {
        pacman = new PacmanJugador();
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    pacman.moverDerecha();
                }

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    pacman.moverIzquierda();
                }

                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    pacman.moverArriba();
                }

                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    pacman.moverAbajo();
                }

                repaint();
            }
        });

        requestFocusInWindow();
    }

    private final int[][] mapa = {
        {1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,1,1,0,1,1,0,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,1,0,1,1,1,1,0,1,0,1},
        {1,0,1,0,0,0,0,0,0,1,0,1},
        {1,0,1,0,1,1,1,1,0,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1}
    };

    private final int TAMANO_CELDA = 50;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        for (int fila = 0; fila < mapa.length; fila++) {
            for (int columna = 0; columna < mapa[fila].length; columna++) {

                if (mapa[fila][columna] == 1) {
                    g.setColor(Color.BLUE);

                    g.fillRect(
                        columna * TAMANO_CELDA,
                        fila * TAMANO_CELDA,
                        TAMANO_CELDA,
                        TAMANO_CELDA
                    );
                }
            }
        }
        g.setColor(Color.YELLOW);
        g.fillOval(pacman.getX(), pacman.getY(), 40, 40);
    }
}