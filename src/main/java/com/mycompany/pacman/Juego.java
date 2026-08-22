package com.mycompany.pacman;

import javax.swing.JFrame;

public class Juego extends JFrame {

    public Juego() {
        setTitle("Pac-Man");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        Tablero tablero = new Tablero();
        add(tablero);
        
        setVisible(true);
    }
}