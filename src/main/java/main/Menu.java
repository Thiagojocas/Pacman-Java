package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Menu extends JPanel {

    private JFrame ventana;

    public Menu(JFrame ventana) {

        this.ventana = ventana;

        int ancho = mapa.COLUMNAS * Tablero.TILE_SIZE;
        int alto = mapa.FILAS * Tablero.TILE_SIZE;
        setPreferredSize(new Dimension(ancho, alto));
        setBackground(Color.BLACK);

        setLayout(null);

        // ----- BOTÓN JUGAR -----
        JButton btnJugar = new JButton("JUGAR");
        btnJugar.setBounds(ancho / 2 - 100, 470, 200, 50);
        btnJugar.setOpaque(true);
        btnJugar.setBorderPainted(false);
        btnJugar.setBackground(new Color(255, 200, 0));
        btnJugar.setForeground(Color.BLACK);
        btnJugar.setFont(new Font("Arial", Font.BOLD, 22));
        btnJugar.setFocusPainted(false);
        btnJugar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarJuego();
            }
        });
        add(btnJugar);

        // ----- BOTÓN SALIR -----
        JButton btnSalir = new JButton("SALIR");
        btnSalir.setBounds(ancho / 2 - 100, 535, 200, 50);
        btnSalir.setOpaque(true);
        btnSalir.setBorderPainted(false);
        btnSalir.setBackground(new Color(180, 30, 30));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFont(new Font("Arial", Font.BOLD, 22));
        btnSalir.setFocusPainted(false);
        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        add(btnSalir);
    }

    private void iniciarJuego() {
        // Dejamos todo limpio para una partida nueva.
        mapa.reiniciar();
        Puntos.reiniciar();

        Tablero tablero = new Tablero(ventana);

        ventana.remove(this);
        ventana.add(tablero);
        ventana.revalidate();
        ventana.repaint();

        tablero.requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int ancho = getWidth();

        // ----- TÍTULO -----
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        String titulo = "PAC-MANIA";
        int anchoTitulo = g.getFontMetrics().stringWidth(titulo);
        g.drawString(titulo, (ancho - anchoTitulo) / 2, 130);

        // ----- PAC-MAN DECORATIVO -----
        g.setColor(Color.YELLOW);
        g.fillArc(ancho / 2 - 30, 155, 60, 60, 30, 300);

        // ----- SUBTÍTULO -----
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        String subtitulo = "Proyecto hecho por - Lautaro chamorro, Tiziano entivero"
                + " y Thiago castillo ";
        int anchoSub = g.getFontMetrics().stringWidth(subtitulo);
        g.drawString(subtitulo, (ancho - anchoSub) / 2, 260);

        // ----- HEADER CONTROLES -----
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        String controles = "CONTROLES";
        int anchoControles = g.getFontMetrics().stringWidth(controles);
        g.drawString(controles, (ancho - anchoControles) / 2, 310);

        // ----- LÍNEAS DE INSTRUCCIONES -----
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));

        String[] lineas = {
            "Flechas: mover a Pac-Man",
            "F: romper pared (bolas verdes de arriba: 3 cargas)",
            "G: congelar fantasmas (bolas de abajo: 1 carga)",
            "Come todos los puntos para ganar",
            "¡Cuidado con los fantasmas!"
        };

        int y = 345;
        for (String linea : lineas) {
            int anchoLinea = g.getFontMetrics().stringWidth(linea);
            g.drawString(linea, (ancho - anchoLinea) / 2, y);
            y += 22;
        }
    }
}