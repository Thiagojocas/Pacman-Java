
package main;

import javax.swing.JFrame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class main {

    public static void main(String[] args) {

        JFrame window = new JFrame();

        window.setTitle("Pacman");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Pantalla completa, sin bordes ni barra superior
        window.setUndecorated(true);

        // Crear menú inicial
        Menu menu = new Menu(window);
        window.add(menu);

        // Usar toda la pantalla disponible
        GraphicsDevice pantalla =
                GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice();

        window.setVisible(true);
        pantalla.setFullScreenWindow(window);

        menu.requestFocusInWindow();
    }
}