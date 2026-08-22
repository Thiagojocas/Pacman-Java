package com.mycompany.pacman;

public class PacmanJugador {

    private int x;
    private int y;
    private int velocidad;
    private String direccion;

    public PacmanJugador() {
        x = 100;
        y = 100;
        velocidad = 5;
        direccion = "derecha";
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public void moverDerecha() {
    x = x + velocidad;
    }

    public void moverIzquierda() {
        x = x - velocidad;
    }

    public void moverArriba() {
        y = y - velocidad;
    }

    public void moverAbajo() {
        y = y + velocidad;
    }
}