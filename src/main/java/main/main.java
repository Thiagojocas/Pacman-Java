package main;

import javax.swing.JFrame;

public class main {

    public static void main(String[] args) {

        JFrame window = new JFrame();

        window.setTitle("Pacman");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Primero mostramos el MENÚ. Cuando el jugador aprieta "JUGAR",
        // el propio menú reemplaza este panel por un Tablero nuevo.
        Menu menu = new Menu(window);
        window.add(menu);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Le pasamos el foco al menú para que los botones funcionen bien.
        menu.requestFocusInWindow();
    }
}