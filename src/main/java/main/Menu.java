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
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

public class Menu extends JPanel {

    private JFrame ventana;
    private Image imagenMenu;

    public Menu(JFrame ventana) {

        this.ventana = ventana;
        
        try {
    imagenMenu = ImageIO.read(
        getClass().getResource("/main/sprites/Menu.png")
    );
    } catch (Exception e) {
        System.out.println("No se pudo cargar la imagen del menú");
        e.printStackTrace();
    }

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
                Sonido.detener(Sonido.AMBIENTE);
            }
        });
        add(btnJugar);
        
        JButton btnControles = new JButton("CONTROLES");
        
        btnControles.addActionListener(e -> mostrarControles());

        btnControles.setBorderPainted(false);
        btnControles.setBackground(new Color(255, 200, 0));
        btnControles.setForeground(Color.BLACK);
        btnControles.setFont(new Font("Arial", Font.BOLD, 22));
        btnControles.setFocusPainted(false);

        add(btnControles);

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
           // Música de ambiente mientras estás en el menú.
        Sonido.detener(Sonido.GAME_OVER);
        Sonido.reproducirLoop(Sonido.AMBIENTE);  
    
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

    if (imagenMenu != null) {
        g.drawImage(
            imagenMenu,
            0, 0,
            getWidth(), getHeight(),
            this
        );
    }
}
    
@Override
public void doLayout() {
    int anchoBoton = 200;
    int altoBoton = 50;
    int espacio = 20;

    int centroX = getWidth() / 2;
    int yPrimeraFila = (int) (getHeight() * 0.74);

    // Botones de arriba, uno al lado del otro
    int xJugar = centroX - anchoBoton - espacio / 2;
    int xControles = centroX + espacio / 2;

    // Botón de abajo, centrado
    int xSalir = centroX - anchoBoton / 2;
    int ySalir = yPrimeraFila + altoBoton + 15;

    for (java.awt.Component componente : getComponents()) {
        if (componente instanceof JButton) {
            JButton boton = (JButton) componente;

            if (boton.getText().equals("JUGAR")
                    || boton.getText().equals("EMPEZAR")) {
                boton.setBounds(
                    xJugar, yPrimeraFila,
                    anchoBoton, altoBoton
                );

            } else if (boton.getText().equals("CONTROLES")) {
                boton.setBounds(
                    xControles, yPrimeraFila,
                    anchoBoton, altoBoton
                );

            } else if (boton.getText().equals("SALIR")) {
                boton.setBounds(
                    xSalir, ySalir,
                    anchoBoton, altoBoton
                );
            }
        }
    }
}

private void mostrarControles() {
    ventana.remove(this);

    JPanel panelControles = new JPanel();
    panelControles.setBackground(Color.BLACK);
    panelControles.setLayout(null);

    JLabel titulo = new JLabel("CONTROLES", JLabel.CENTER);
    titulo.setForeground(Color.YELLOW);
    titulo.setFont(new Font("Arial", Font.BOLD, 40));
    titulo.setBounds(0, 100, ventana.getWidth(), 60);
    panelControles.add(titulo);

    String[] instrucciones = {
        "FLECHAS: mover a Pac-Man",
        "F: romper paredes (3 cargas)",
        "G: congelar fantasmas (1 carga)",
        "P: pausar el juego",
        "Come todos los puntos para ganar",
        "¡Cuidado con los fantasmas!"
    };

    int y = 220;

    for (String texto : instrucciones) {
        JLabel linea = new JLabel(texto, JLabel.CENTER);
        linea.setForeground(Color.WHITE);
        linea.setFont(new Font("Arial", Font.PLAIN, 22));
        linea.setBounds(0, y, ventana.getWidth(), 35);
        panelControles.add(linea);
        y += 45;
    }

    JButton btnVolver = new JButton("VOLVER");
    btnVolver.setFont(new Font("Arial", Font.BOLD, 18));
    btnVolver.setBounds(
        ventana.getWidth() / 2 - 100,
        y + 30,
        200,
        50
    );

    btnVolver.addActionListener(e -> {
        ventana.remove(panelControles);
        ventana.add(this);
        ventana.revalidate();
        ventana.repaint();
    });

    panelControles.add(btnVolver);

    ventana.add(panelControles);
    ventana.revalidate();
    ventana.repaint();
}

}