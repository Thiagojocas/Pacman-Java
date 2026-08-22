package main;

import javax.swing.JFrame;

public class main {

    public static void main(String[] args) {

        JFrame window = new JFrame();

        window.setTitle("Pacman");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        Tablero tablero = new Tablero();
        window.add(tablero);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Se pide el foco despues de mostrar la ventana para que
        // el KeyListener del tablero funcione desde el arranque.
        tablero.requestFocusInWindow();
    }
}