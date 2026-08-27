package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tablero extends JPanel {

    public static final int TILE_SIZE = 21;
    public static final int COLUMNAS = 28;
    public static final int FILAS = 31;
    public static final int FILA_TUNEL = 14; // fila con abertura en columna 0 y 27

    private PacmanJugador pacman;
    private Fantasma fantasma1;
    private Fantasma fantasma2;
    private Fantasma fantasma3;
    private Fantasma fantasma4;
    private Timer timer;
    private boolean juegoTerminado; // Indica si el juego ya terminó.
    private boolean gano; // Indica si el jugador ganó. true = ganó | false = todavía no ganó.
    private boolean perdio; // Indica si el jugador choco con algun fantasma. true = perdio | false = no perdio.

    public Tablero() {
        setPreferredSize(new Dimension(COLUMNAS * TILE_SIZE, FILAS * TILE_SIZE));
        setBackground(Color.BLACK);

        // Posicion inicial: DEBE ser multiplo de TILE_SIZE para que el
        // sistema de alineacion a la grilla funcione desde el arranque.
        int filaInicial = 1;
        int columnaInicial = 1;
        pacman = new PacmanJugador(columnaInicial * TILE_SIZE, filaInicial * TILE_SIZE);

        // Fantasma 1
        fantasma1 = new Fantasma(13 * TILE_SIZE, 11 * TILE_SIZE);

        // Fantasma 2
        fantasma2 = new Fantasma(14 * TILE_SIZE, 13 * TILE_SIZE);

        // Fantasma 3
        fantasma3 = new Fantasma(12 * TILE_SIZE, 13 * TILE_SIZE);

        // Fantasma 4
        fantasma4 = new Fantasma(15 * TILE_SIZE, 13 * TILE_SIZE);

        juegoTerminado = false;

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int codigo = e.getKeyCode();

                // Ya NO movemos aca. Solo guardamos la intencion del jugador;
                // el Timer decide cuando aplicarla (al llegar al centro de una celda).
                if (codigo == KeyEvent.VK_RIGHT) {
                    pacman.setDireccionDeseada("derecha");
                } else if (codigo == KeyEvent.VK_LEFT) {
                    pacman.setDireccionDeseada("izquierda");
                } else if (codigo == KeyEvent.VK_UP) {
                    pacman.setDireccionDeseada("arriba");
                } else if (codigo == KeyEvent.VK_DOWN) {
                    pacman.setDireccionDeseada("abajo");
                }
            }
        });

        timer = new Timer(50, e -> {

            if (!juegoTerminado) {

                actualizarMovimiento();

                // Actualizamos los cuatro fantasmas.
                actualizarFantasma(fantasma1);
                actualizarFantasma(fantasma2);
                actualizarFantasma(fantasma3);
                actualizarFantasma(fantasma4);

                verificarColisionConFantasma();
            }

            repaint();
        });
        timer.start();
    }

    // Logica central del movimiento por celdas
    private void actualizarMovimiento() {
        boolean centrado = (pacman.getX() % TILE_SIZE == 0) && (pacman.getY() % TILE_SIZE == 0);

        if (centrado) {
            int fila = pacman.getY() / TILE_SIZE;
            int columna = pacman.getX() / TILE_SIZE;
            
            // Pacman come el punto de la celda actual
            Puntos.comerPunto(fila, columna);
            
            // Pac-Man acaba de comer el punto de esta celda.

            // Comprobamos si ya no queda ningún punto en el mapa.
            if (Puntos.todosLosPuntosComidos()) {
                
                // Indicamos que el jugador ganó.
                gano = true;

                // Indicamos que el juego terminó.
                juegoTerminado = true;

                // Detenemos el Timer.
                // Al detenerlo, Pac-Man y los fantasmas dejan de moverse.
                timer.stop();

                // Por ahora mostramos un mensaje en la consola.
                // Más adelante lo reemplazaremos por nuestra pantalla WIN.
                System.out.println("¡GANASTE! Pac-Man comió todos los puntos.");
            }

            // Si el jugador pidio girar y ese camino esta libre, se adopta ahora.
            // Esto es lo que permite doblar justo en las esquinas, no antes ni despues.
            if (puedeAvanzar(fila, columna, pacman.getDireccionDeseada())) {
                pacman.setDireccionActual(pacman.getDireccionDeseada());
            }

            // Si la direccion actual choca con pared, Pacman se frena
            // exactamente centrado en la celda (nunca queda a mitad de camino).
            if (!puedeAvanzar(fila, columna, pacman.getDireccionActual())) {
                return;
            }
        }

        moverSegunDireccionActual();
        aplicarTunel();
    }

    // Revisa si desde (fila, columna) se puede avanzar un paso en esa direccion
    private boolean puedeAvanzar(int fila, int columna, String direccion) {
        if (direccion == null) {
            return false;
        }

        int filaDestino = fila;
        int columnaDestino = columna;

        switch (direccion) {
            case "derecha":
                columnaDestino++;
                break;
            case "izquierda":
                columnaDestino--;
                break;
            case "arriba":
                filaDestino--;
                break;
            case "abajo":
                filaDestino++;
                break;
        }

        // Tunel: en la fila habilitada, dejar "salir" del mapa por los bordes
        // en vez de bloquear como si fuera pared.
        if (fila == FILA_TUNEL && (columnaDestino < 0 || columnaDestino >= COLUMNAS)) {
            return true;
        }

        return !esPared(filaDestino, columnaDestino);
    }

    // Si Pacman cruzo el borde del mapa por el tunel, lo reaparece del otro lado
    private void aplicarTunel() {
        if (pacman.getY() != FILA_TUNEL * TILE_SIZE) {
            return;
        }

        int limiteDerecho = (COLUMNAS - 1) * TILE_SIZE;

        if (pacman.getX() < 0) {
            pacman.setX(limiteDerecho);
        } else if (pacman.getX() > limiteDerecho) {
            pacman.setX(0);
        }
    }

    private void moverSegunDireccionActual() {
        String dir = pacman.getDireccionActual();
        if (dir == null) {
            return;
        }

        switch (dir) {
            case "derecha":
                pacman.moverDerecha();
                break;
            case "izquierda":
                pacman.moverIzquierda();
                break;
            case "arriba":
                pacman.moverArriba();
                break;
            case "abajo":
                pacman.moverAbajo();
                break;
        }
    }

    // Actualiza el movimiento de UN fantasma.
// Recibimos como parámetro cuál de los fantasmas queremos mover.
private void actualizarFantasma(Fantasma fantasma) {

    // Comprobamos si el fantasma está exactamente centrado en una celda.
    boolean centrado = (fantasma.getX() % TILE_SIZE == 0)
            && (fantasma.getY() % TILE_SIZE == 0);

    // Si está centrado, es momento de decidir hacia dónde va.
    if (centrado) {

        // Convertimos su posición en píxeles a fila y columna del mapa.
        int fila = fantasma.getY() / TILE_SIZE;
        int columna = fantasma.getX() / TILE_SIZE;

        // La IA decide la mejor dirección para ESTE fantasma.
        fantasma.setDireccionActual(
                elegirDireccionFantasma(fila, columna, fantasma)
        );
    }

    // Movemos el fantasma en la dirección que decidió la IA.
    moverFantasmaSegunDireccion(fantasma);

    // Aplicamos el túnel a ESTE fantasma.
    aplicarTunelFantasma(fantasma);
}

    // Decide hacia dónde debe ir UN fantasma para acercarse a Pac-Man.
private String elegirDireccionFantasma(
        int fila,
        int columna,
        Fantasma fantasma) {

    // Posibles direcciones.
    String[] direcciones = {
        "arriba",
        "abajo",
        "izquierda",
        "derecha"
    };

    // Calculamos cuál sería la dirección contraria a la actual.
    // Esto evita que el fantasma esté dando vueltas hacia atrás
    // constantemente.
    String opuesta = direccionOpuesta(
            fantasma.getDireccionActual()
    );

    // Obtenemos la posición actual de Pac-Man.
    int filaPacman = pacman.getY() / TILE_SIZE;
    int columnaPacman = pacman.getX() / TILE_SIZE;

    String mejorDireccion = null;
    int mejorDistancia = Integer.MAX_VALUE;

    // Probamos las cuatro direcciones posibles.
    for (String direccion : direcciones) {

        // Evitamos volver por donde venía el fantasma,
        // salvo que sea la única opción.
        if (direccion.equals(opuesta)) {
            continue;
        }

        // Si hay una pared, descartamos esa dirección.
        if (!puedeAvanzar(fila, columna, direccion)) {
            continue;
        }

        // Calculamos la celda a la que llegaría.
        int[] destino = calcularDestino(
                fila,
                columna,
                direccion
        );

        // Calculamos qué tan cerca queda de Pac-Man.
        int distancia = distanciaAlCuadrado(
                destino[0],
                destino[1],
                filaPacman,
                columnaPacman
        );

        // Si esta dirección acerca más al fantasma,
        // la guardamos como la mejor.
        if (distancia < mejorDistancia) {
            mejorDistancia = distancia;
            mejorDireccion = direccion;
        }
    }

    // Si quedó encerrado y la única posibilidad es volver atrás,
    // permitimos la dirección contraria.
    if (mejorDireccion == null) {

        for (String direccion : direcciones) {

            if (puedeAvanzar(fila, columna, direccion)) {
                mejorDireccion = direccion;
                break;
            }
        }
    }

    return mejorDireccion;
}

    private int[] calcularDestino(int fila, int columna, String direccion) {
        int filaDestino = fila;
        int columnaDestino = columna;

        switch (direccion) {
            case "derecha":
                columnaDestino++;
                break;
            case "izquierda":
                columnaDestino--;
                break;
            case "arriba":
                filaDestino--;
                break;
            case "abajo":
                filaDestino++;
                break;
        }

        return new int[] {filaDestino, columnaDestino};
    }

    private int distanciaAlCuadrado(int fila1, int columna1, int fila2, int columna2) {
        int df = fila1 - fila2;
        int dc = columna1 - columna2;
        return df * df + dc * dc;
    }

    private String direccionOpuesta(String direccion) {
        if (direccion == null) {
            return null;
        }
        switch (direccion) {
            case "arriba":
                return "abajo";
            case "abajo":
                return "arriba";
            case "izquierda":
                return "derecha";
            case "derecha":
                return "izquierda";
        }
        return null;
    }

    // Mueve al fantasma recibido según su dirección actual.
private void moverFantasmaSegunDireccion(Fantasma fantasma) {

    // Obtenemos la dirección que decidió la IA.
    String dir = fantasma.getDireccionActual();

    // Si todavía no tiene dirección, no hacemos nada.
    if (dir == null) {
        return;
    }

    switch (dir) {

        case "derecha":
            fantasma.moverDerecha();
            break;

        case "izquierda":
            fantasma.moverIzquierda();
            break;

        case "arriba":
            fantasma.moverArriba();
            break;

        case "abajo":
            fantasma.moverAbajo();
            break;
    }
}

    // Permite que el fantasma atraviese el túnel
// y aparezca del otro lado del mapa.
private void aplicarTunelFantasma(Fantasma fantasma) {

    // El túnel solamente existe en esta fila.
    if (fantasma.getY() != FILA_TUNEL * TILE_SIZE) {
        return;
    }

    int limiteDerecho = (COLUMNAS - 1) * TILE_SIZE;

    // Si salió por la izquierda, aparece a la derecha.
    if (fantasma.getX() < 0) {
        fantasma.setX(limiteDerecho);

    // Si salió por la derecha, aparece a la izquierda.
    } else if (fantasma.getX() > limiteDerecho) {
        fantasma.setX(0);
    }
}

    // Colision por superposicion de rectangulos (mas confiable que comparar
    // celdas exactas, porque detecta el choque aunque no esten perfectamente
    // alineados al centro de la celda en el mismo instante).
    // Comprueba si Pac-Man chocó con alguno de los cuatro fantasmas.
    private void verificarColisionConFantasma() {

        // Comprobamos la colisión con el fantasma 1.
        if (hayColision(pacman, fantasma1)) {
            terminarPorGameOver();
            return;
        }

        // Comprobamos la colisión con el fantasma 2.
        if (hayColision(pacman, fantasma2)) {
            terminarPorGameOver();
            return;
        }

        // Comprobamos la colisión con el fantasma 3.
        if (hayColision(pacman, fantasma3)) {
            terminarPorGameOver();
            return;
        }

        // Comprobamos la colisión con el fantasma 4.
        if (hayColision(pacman, fantasma4)) {
            terminarPorGameOver();
        }
    }
    
    // Comprueba si Pac-Man y un fantasma están chocando.
    private boolean hayColision(PacmanJugador pacman, Fantasma fantasma) {

        // Comparamos los rectángulos que ocupan Pac-Man y el fantasma.
        return pacman.getX() < fantasma.getX() + Fantasma.TAMANO
                && pacman.getX() + PacmanJugador.TAMANO > fantasma.getX()
                && pacman.getY() < fantasma.getY() + Fantasma.TAMANO
                && pacman.getY() + PacmanJugador.TAMANO > fantasma.getY();
    }
    
    // Termina el juego cuando Pac-Man es atrapado por un fantasma.
    private void terminarPorGameOver() {

        // Indicamos que el juego terminó.
        juegoTerminado = true;

        // Indicamos que el jugador perdió.
        perdio = true;

        // Detenemos el Timer para que Pac-Man y los fantasmas dejen de moverse.
        timer.stop();

        // Por ahora mostramos el mensaje en la consola.
        // Más adelante vamos a crear la pantalla GAME OVER.
        System.out.println("Pacman fue atrapado. Game Over.");
    }

    public boolean esPared(int fila, int columna) {
        if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS) {
            return true;
        }
        return mapa.MATRIZ[fila][columna] == 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        dibujarMapa(g);
        dibujarPacman(g);
        dibujarFantasma(g);
        dibujarPuntaje(g);
        if (gano){dibujarPantallaWin(g);}
        }
    // Dibuja la pantalla que aparece cuando Pac-Man gana.
    private void dibujarPantallaWin(Graphics g) {

        // Pintamos todo el tablero de negro.
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Elegimos el color amarillo para el título.
        g.setColor(Color.YELLOW);

        // Elegimos el tamaño de la letra.
        g.setFont(g.getFont().deriveFont(40f));

        String titulo = "¡GANASTE!";

        // Calculamos cuánto mide el texto.
        int anchoTitulo = g.getFontMetrics().stringWidth(titulo);

        // Calculamos la posición X para centrarlo.
        int xTitulo = (getWidth() - anchoTitulo) / 2;

        // Posición vertical del título.
        int yTitulo = 230;

        // Dibujamos el título.
        g.drawString(titulo, xTitulo, yTitulo);

        // Cambiamos el tamaño de la letra.
        g.setFont(g.getFont().deriveFont(22f));

        String textoPuntaje = "Puntaje: " + Puntos.getPuntaje();

        // Calculamos cuánto mide el texto del puntaje.
        int anchoPuntaje = g.getFontMetrics().stringWidth(textoPuntaje);

        // Lo centramos horizontalmente.
        int xPuntaje = (getWidth() - anchoPuntaje) / 2;

        // Dibujamos el puntaje.
        g.drawString(textoPuntaje, xPuntaje, 280);

        g.setFont(g.getFont().deriveFont(18f));

        String mensaje = "¡Comiste todas las bolitas!";

        int anchoMensaje = g.getFontMetrics().stringWidth(mensaje);

        int xMensaje = (getWidth() - anchoMensaje) / 2;

        g.drawString(mensaje, xMensaje, 320);
    }
    
        private void dibujarPuntaje(Graphics g) {
            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(18f));
            g.drawString("Puntaje: " + Puntos.getPuntaje(), 10, 20);
    }

    private void dibujarMapa(Graphics g) {
        int[][] matriz = mapa.MATRIZ;

        for (int fila = 0; fila < FILAS; fila++) {
            for (int col = 0; col < COLUMNAS; col++) {
                int tile = matriz[fila][col];
                int x = col * TILE_SIZE;
                int y = fila * TILE_SIZE;

                switch (tile) {
                    case 1: // pared
                        g.setColor(Color.BLUE);
                        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        break;
                    case 2: // pellet
                        g.setColor(Color.WHITE);
                        g.fillOval(x + TILE_SIZE / 2 - 2, y + TILE_SIZE / 2 - 2, 4, 4);
                        break;
                    case 3: // power pellet
                        g.setColor(Color.WHITE);
                        g.fillOval(x + TILE_SIZE / 2 - 5, y + TILE_SIZE / 2 - 5, 10, 10);
                        break;
                    case 4: // puerta casa fantasmas
                        g.setColor(Color.PINK);
                        g.fillRect(x, y + TILE_SIZE / 2 - 1, TILE_SIZE, 3);
                        break;
                    // case 0: vacio, no se dibuja nada
                }
            }
        }
    }

    private void dibujarPacman(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(pacman.getX() + 1, pacman.getY() + 1, PacmanJugador.TAMANO, PacmanJugador.TAMANO);
    }

    // Dibuja los cuatro fantasmas en el tablero.
    private void dibujarFantasma(Graphics g) {

        // -----------------------------------------
        // FANTASMA 1
        // -----------------------------------------
        g.setColor(Color.RED);
        g.fillOval(
                fantasma1.getX() + 1,
                fantasma1.getY() + 1,
                Fantasma.TAMANO,
                Fantasma.TAMANO
        );

        // -----------------------------------------
        // FANTASMA 2
        // -----------------------------------------
        g.setColor(Color.PINK);
        g.fillOval(
                fantasma2.getX() + 1,
                fantasma2.getY() + 1,
                Fantasma.TAMANO,
                Fantasma.TAMANO
        );

        // -----------------------------------------
        // FANTASMA 3
        // -----------------------------------------
        g.setColor(Color.CYAN);
        g.fillOval(
                fantasma3.getX() + 1,
                fantasma3.getY() + 1,
                Fantasma.TAMANO,
                Fantasma.TAMANO
        );

        // -----------------------------------------
        // FANTASMA 4
        // -----------------------------------------
        g.setColor(Color.ORANGE);
        g.fillOval(
                fantasma4.getX() + 1,
                fantasma4.getY() + 1,
                Fantasma.TAMANO,
                Fantasma.TAMANO
        );
    }
}