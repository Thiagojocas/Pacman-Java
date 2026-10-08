package main;

import javax.swing.JFrame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class main {

    public static void main(String[] args) {

        JFrame window = new JFrame();

        window.setTitle("Pacman");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ========== VENTANA REDIMENSIONABLE ==========
        window.setResizable(true);

        // Tamaño inicial
        window.setSize(588, 651);  // 28*21 x 31*21 (tamaño original)

        // Crear menú inicial
        Menu menu = new Menu(window);
        window.add(menu);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // ========== ESCUCHAR CAMBIOS DE TAMAÑO ==========
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ajustarTamanoJuego(window);
            }
        });

        menu.requestFocusInWindow();
    }

    /**
     * Ajusta el TILE_SIZE según el tamaño de la ventana.
     * Solo se aplica fuera de la partida y en múltiplos de 21, para que
     * las velocidades (7 y 3) sigan alineando con la grilla.
     */
    private static void ajustarTamanoJuego(JFrame window) {

        // No tocar el TILE_SIZE mientras se está jugando.
        if (window.getContentPane().getComponentCount() > 0
                && window.getContentPane().getComponent(0) instanceof Tablero) {
            return;
        }

        int anchoDispositivo = window.getContentPane().getWidth();
        int altoDispositivo = window.getContentPane().getHeight();

        int tileSizePorAncho = anchoDispositivo / Tablero.COLUMNAS;
        int tileSizePorAlto = altoDispositivo / Tablero.FILAS;

        int nuevoTileSize = Math.min(tileSizePorAncho, tileSizePorAlto);

        // Redondear hacia abajo a múltiplo de 21 (mínimo 21).
        nuevoTileSize = (nuevoTileSize / 21) * 21;
        if (nuevoTileSize < 21) {
            nuevoTileSize = 21;
        }

        if (nuevoTileSize != Tablero.TILE_SIZE) {
            Tablero.TILE_SIZE = nuevoTileSize;

            if (window.getContentPane().getComponentCount() > 0) {
                var panel = window.getContentPane().getComponent(0);
                panel.setPreferredSize(
                    new java.awt.Dimension(
                        Tablero.COLUMNAS * Tablero.TILE_SIZE,
                        Tablero.FILAS * Tablero.TILE_SIZE
                    )
                );
                window.revalidate();
                window.repaint();
            }
        }
    }
}