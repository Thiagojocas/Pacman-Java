package main;

import javax.swing.JFrame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class main {

    public static void main(String[] args) {

        JFrame window = new JFrame();

        window.setTitle("Pacman - Presiona P para pausar");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ========== VENTANA REDIMENSIONABLE ==========
        window.setResizable(true);
        
        // Tamaño inicial (puede cambiar)
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
     * Ajusta el TILE_SIZE basado en el tamaño de la ventana.
     * Se calcula cuál es el mayor TILE_SIZE que mantiene la proporción 28x31.
     */
    private static void ajustarTamanoJuego(JFrame window) {
        int anchoDispositivo = window.getContentPane().getWidth();
        int altoDispositivo = window.getContentPane().getHeight();

        // Calcular el máximo TILE_SIZE que cabe en la pantalla
        int tileSizePorAncho = anchoDispositivo / Tablero.COLUMNAS;
        int tileSizePorAlto = altoDispositivo / Tablero.FILAS;

        // Usar el menor de los dos para mantener proporciones
        int nuevoTileSize = Math.min(tileSizePorAncho, tileSizePorAlto);

        // Asegurarse de que sea mínimo 10 para que sea jugable
        if (nuevoTileSize < 10) {
            nuevoTileSize = 10;
        }

        // Solo actualizar si cambió significativamente
        if (Math.abs(nuevoTileSize - Tablero.TILE_SIZE) > 1) {
            Tablero.TILE_SIZE = nuevoTileSize;
            
            System.out.println("📏 TILE_SIZE ajustado a: " + nuevoTileSize + 
                             " (Ventana: " + anchoDispositivo + "x" + altoDispositivo + ")");

            // Recalcular preferredSize del panel actual
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