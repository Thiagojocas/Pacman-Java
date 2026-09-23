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
    public static final int COLUMNAS = mapa.COLUMNAS;
    public static final int FILAS = mapa.FILAS;
    public static final int FILA_TUNEL = mapa.FILA_TUNEL;
    
    

    // Celda dentro de la casa de los fantasmas a la que vuelven cuando
    // Pac-Man se los come (ahí "revive" y vuelve a salir).
    private static final int CASA_FILA = 16;
    private static final int CASA_COLUMNA = 13;

    // Cuánto dura el efecto de un Power Pellet (en milisegundos).
    private static final int DURACION_ASUSTADO_MS = 7000;

    // Cuánto tiene que esperar un fantasma adentro de la casa, despues
    // de que Pac-Man se lo comió, antes de poder volver a salir.
    private static final int TIEMPO_REAPARICION_MS = 20000;

      // NUEVO: Tiempos de salida escalonada (en milisegundos desde el inicio del juego)  
    private static final long SALIDA_BLINKY_MS = 0;      // Sale inmediato  
    private static final long SALIDA_PINKY_MS = 3000;    // Sale a los 3 segundos  
    private static final long SALIDA_INKY_MS = 7000;     // Sale a los 7 segundos  
    private static final long SALIDA_CLYDE_MS = 12000;   // Sale a los 12 segundos  
    
    
    private PacmanJugador pacman;
    private Fantasma fantasma1;
    private Fantasma fantasma2;
    private Fantasma fantasma3;
    private Fantasma fantasma4;
    private Timer timer;
    private boolean juegoTerminado; // Indica si el juego ya terminó.
    private boolean gano; // Indica si el jugador ganó. true = ganó | false = todavía no ganó.
    private boolean perdio; // Indica si el jugador choco con algun fantasma. true = perdio | false = no perdio.
    private int vidas = 3; // Cantidad de vidas que tiene Pac-Man. El juego comienza con 3 vidas.

    // Momento (System.currentTimeMillis()) en el que termina el modo
    // asustado. 0 significa que el modo asustado no está activo.
     // NUEVO: Guardar el momento en que arrancó el juego  
    private long inicioJuegoMillis;  
    
    private long finAsustadoEnMillis = 0;

    // Cuántos fantasmas seguidos se comió Pac-Man con el Power Pellet
    // actual. Sirve para el puntaje en cadena: 200, 400, 800, 1600.
    private int fantasmasComidosSeguidos = 0;
    
    public Tablero() {
        setPreferredSize(new Dimension(COLUMNAS * TILE_SIZE, FILAS * TILE_SIZE));
        setBackground(Color.BLACK);

        // Posicion inicial: DEBE ser multiplo de TILE_SIZE para que el
        // sistema de alineacion a la grilla funcione desde el arranque.
        int filaInicial = 1;
        int columnaInicial = 1;
        pacman = new PacmanJugador(columnaInicial * TILE_SIZE, filaInicial * TILE_SIZE);

        // Punto de la casa al que vuelven los fantasmas cuando los comen.
        int xCasa = CASA_COLUMNA * TILE_SIZE;
        int yCasa = CASA_FILA * TILE_SIZE;

        // Los fantasmas 2, 3 y 4 arrancan dentro de la casa (fila 16).
        // Blinky arranca afuera, listo para salir de inmediato.
        fantasma1 = new Fantasma(13 * TILE_SIZE, 11 * TILE_SIZE, xCasa, yCasa);  
        fantasma2 = new Fantasma(12 * TILE_SIZE, 16 * TILE_SIZE, xCasa, yCasa);  
        fantasma3 = new Fantasma(13 * TILE_SIZE, 16 * TILE_SIZE, xCasa, yCasa);  
        fantasma4 = new Fantasma(14 * TILE_SIZE, 16 * TILE_SIZE, xCasa, yCasa);  
        
              // NUEVO: Asignar tipo a cada fantasma y su tiempo de salida  
        fantasma1.setTipo(Fantasma.TIPO_BLINKY);  
        fantasma1.setSalirEnMillis(SALIDA_BLINKY_MS);  
        fantasma1.setEstado(Fantasma.EN_CASA);  

        fantasma2.setTipo(Fantasma.TIPO_PINKY);  
        fantasma2.setSalirEnMillis(SALIDA_PINKY_MS);  
        fantasma2.setEstado(Fantasma.EN_CASA);  

        fantasma3.setTipo(Fantasma.TIPO_INKY);  
        fantasma3.setSalirEnMillis(SALIDA_INKY_MS);  
        fantasma3.setEstado(Fantasma.EN_CASA);  

        fantasma4.setTipo(Fantasma.TIPO_CLYDE);  
        fantasma4.setSalirEnMillis(SALIDA_CLYDE_MS);  
        fantasma4.setEstado(Fantasma.EN_CASA);  

          // NUEVO: Registrar cuándo arrancó el juego  
        inicioJuegoMillis = System.currentTimeMillis();  


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
                } else if (codigo == KeyEvent.VK_F) {
                    // NUEVO: habilidad de romper paredes.
                    intentarRomperPared();
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

            // Power Pellet normal: activa el modo asustado.
            if (Puntos.comerPowerPellet(fila, columna)) {
                activarModoAsustado();
            }

            // NUEVO: Power Pellet especial (tile 5): activa el modo
            // asustado Y da una carga para romper paredes.
            if (Puntos.comerPowerPelletEspecial(fila, columna)) {
                activarModoAsustado();
                pacman.agregarCargaRomperParedes();
            }

            // Comprobamos si ya no queda ningún punto en el mapa.
            if (Puntos.todosLosPuntosComidos()) {
                
                gano = true;
                juegoTerminado = true;
                timer.stop();

                System.out.println("¡GANASTE! Pac-Man comió todos los puntos.");
            }

            // Si el jugador pidio girar y ese camino esta libre, se adopta ahora.
            if (puedeAvanzar(fila, columna, pacman.getDireccionDeseada(), null)) {
                pacman.setDireccionActual(pacman.getDireccionDeseada());
            }

            // Si la direccion actual choca con pared, Pacman se frena
            // exactamente centrado en la celda.
            if (!puedeAvanzar(fila, columna, pacman.getDireccionActual(), null)) {
                return;
            }
        }

        moverSegunDireccionActual();
        aplicarTunel();
    }

    // =========================================================
    // NUEVO: habilidad de romper paredes con la tecla F.
    // Rompe la celda que está JUSTO delante de Pac-Man, en la
    // dirección en la que se está moviendo.
    // Requisitos:
    //   - Tener al menos 1 carga disponible.
    //   - La celda destino debe ser una pared (tile 1).
    //   - La celda destino no puede ser la puerta (tile 4) ni
    //     estar fuera del mapa.
    // Cuando se rompe, la celda pasa a tile 6 (transitable).
    // =========================================================
    private void intentarRomperPared() {

        if (juegoTerminado) {
            return;
        }

        // Necesita una dirección actual para saber hacia dónde romper.
        String dir = pacman.getDireccionActual();
        if (dir == null) {
            return;
        }

        int fila = pacman.getY() / TILE_SIZE;
        int columna = pacman.getX() / TILE_SIZE;

        int filaDestino = fila;
        int columnaDestino = columna;

        switch (dir) {
            case "derecha":   columnaDestino++; break;
            case "izquierda": columnaDestino--; break;
            case "arriba":    filaDestino--;    break;
            case "abajo":     filaDestino++;    break;
        }

        // 1) No puede romper fuera del mapa (borde).
        if (filaDestino < 0 || filaDestino >= FILAS ||
            columnaDestino < 0 || columnaDestino >= COLUMNAS) {
            return;
        }

        // 2) No puede romper puertas de la casa de fantasmas.
        if (mapa.MATRIZ[filaDestino][columnaDestino] == 4) {
            return;
        }

        // 3) Solo se rompen paredes (tile 1).
        //    Ya rota (6), pasillo (0/2/3/5): no se puede romper.
        if (mapa.MATRIZ[filaDestino][columnaDestino] != 1) {
            return;
        }

        // 4) Necesita una carga disponible.
        if (!pacman.usarCargaRomperParedes()) {
            return;
        }

        // 5) Romper la pared: pasa a tile 6 (transitable, no cuenta
        //    para ganar y no rompe la lógica de la casa).
        mapa.MATRIZ[filaDestino][columnaDestino] = 6;
    }

    // Revisa si desde (fila, columna) se puede avanzar un paso en esa direccion.
    private boolean puedeAvanzar(int fila, int columna, String direccion, Fantasma fantasma) {
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

        // Tunel: en la fila habilitada, dejar "salir" del mapa por los bordes.
        if (fila == FILA_TUNEL && (columnaDestino < 0 || columnaDestino >= COLUMNAS)) {
            return true;
        }

        if (esPared(filaDestino, columnaDestino)) {
            return false;
        }

        // Solo dejamos ENTRAR a la casa de los fantasmas (desde afuera)
        // a un fantasma que está en estado "comido".
        boolean entrandoACasa = esCasaFantasmas(filaDestino, columnaDestino)
                && !esCasaFantasmas(fila, columna);

        if (entrandoACasa) {
            boolean puedeEntrar = fantasma != null && Fantasma.COMIDO.equals(fantasma.getEstado());
            if (!puedeEntrar) {
                return false;
            }
        }

        return true;
    }

    // Indica si la celda (fila, columna) forma parte de la casa de los fantasmas.
    // Ojo: el tile 6 (pared rota) NO es casa, así que Pac-Man puede pasar
    // por donde rompió una pared.
   private boolean esCasaFantasmas(int fila, int columna) {

    if (fila < 15 || fila > 17 || columna < 11 || columna > 16) {
        return false;
    }

    int tile = mapa.MATRIZ[fila][columna];

    // 4 = puerta
    // 0 = interior de la casa
    // 6 = pared rota -> NO cuenta como casa

    return tile == 0 || tile == 4;
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
private void actualizarFantasma(Fantasma fantasma) {

    boolean centrado = (fantasma.getX() % TILE_SIZE == 0)
            && (fantasma.getY() % TILE_SIZE == 0);

    if (centrado) {
        actualizarEstadoFantasma(fantasma);
    }

    moverFantasmaSegunDireccion(fantasma);

    aplicarTunelFantasma(fantasma);
}


// Decide en qué estado queda el fantasma y hacia dónde se mueve
// despues. Se llama UNICAMENTE cuando el fantasma está centrado.
private void actualizarEstadoFantasma(Fantasma fantasma) {

    // =========================================================
    // SALIDA ESCALONADA: mientras el fantasma esté EN_CASA y
    // todavía no le toque su turno, lo dejamos quieto.
    // =========================================================
    if (Fantasma.EN_CASA.equals(fantasma.getEstado())) {
        long transcurrido = System.currentTimeMillis() - inicioJuegoMillis;
        if (transcurrido < fantasma.getSalirEnMillis()) {
            fantasma.setDireccionActual(null);
            return;
        }
        fantasma.setEstado(Fantasma.NORMAL);
    }

    // Si el modo asustado ya terminó, este fantasma deja de estar asustado.
    if (Fantasma.ASUSTADO.equals(fantasma.getEstado()) && yaTerminoElAsustado()) {
        fantasma.setEstado(Fantasma.NORMAL);
    }

    int fila = fantasma.getY() / TILE_SIZE;
    int columna = fantasma.getX() / TILE_SIZE;

    // =========================================================
    // LLEGADA A LA CASA (ojos volviendo).
    // =========================================================
    if (Fantasma.COMIDO.equals(fantasma.getEstado())) {
        int filaCasa = fantasma.getYCasa() / TILE_SIZE;
        int colCasa  = fantasma.getXCasa() / TILE_SIZE;

        if (fila == filaCasa && columna == colCasa) {
            // Snap exacto para evitar desalineaciones.
            fantasma.setX(fantasma.getXCasa());
            fantasma.setY(fantasma.getYCasa());

            if (System.currentTimeMillis() >= fantasma.getRevivirEnMillis()) {
                fantasma.setEstado(Fantasma.NORMAL);
            } else {
                fantasma.setDireccionActual(null);
                return;
            }
        }
    }

    // Ajustamos la velocidad según el estado ACTUAL.
    switch (fantasma.getEstado()) {
        case Fantasma.ASUSTADO:
            fantasma.setVelocidad(1);
            break;
        case Fantasma.COMIDO:
            fantasma.setVelocidad(7);
            break;
        default:
            fantasma.setVelocidad(3);
            break;
    }

    // La IA decide la mejor dirección para ESTE fantasma.
    fantasma.setDireccionActual(elegirDireccionFantasma(fila, columna, fantasma));
}

// Indica si ya se cumplió el tiempo de efecto del último Power Pellet.
private boolean yaTerminoElAsustado() {
    return finAsustadoEnMillis != 0 && System.currentTimeMillis() >= finAsustadoEnMillis;
}

    // =========================================================
    // IA DE FANTASMAS
    // =========================================================
private String elegirDireccionFantasma(int fila, int columna, Fantasma fantasma) {

    boolean comido = Fantasma.COMIDO.equals(fantasma.getEstado());
    boolean huyendo = Fantasma.ASUSTADO.equals(fantasma.getEstado());

    // CASO 1: dentro de la casa y no es un par de ojos. Forzamos la salida.
    if (esCasaFantasmas(fila, columna) && !comido) {
        return direccionParaSalirDeCasa(fila, columna, fantasma);
    }

    // Determinamos el objetivo según estado y tipo.
    int filaObjetivo;
    int columnaObjetivo;

    if (comido) {
        filaObjetivo  = fantasma.getYCasa() / TILE_SIZE;
        columnaObjetivo = fantasma.getXCasa() / TILE_SIZE;
    } else if (huyendo) {
        filaObjetivo  = pacman.getY() / TILE_SIZE;
        columnaObjetivo = pacman.getX() / TILE_SIZE;
    } else {
        int[] objetivo = calcularObjetivoPersecucion(fantasma);
        filaObjetivo  = objetivo[0];
        columnaObjetivo = objetivo[1];
    }

    String[] direcciones = {"arriba", "abajo", "izquierda", "derecha"};
    String opuesta = direccionOpuesta(fantasma.getDireccionActual());

    String mejorDireccion = null;
    int mejorDistancia = huyendo ? Integer.MIN_VALUE : Integer.MAX_VALUE;

    for (String direccion : direcciones) {

        if (direccion.equals(opuesta)) {
            continue;
        }

        if (!puedeAvanzar(fila, columna, direccion, fantasma)) {
            continue;
        }

        int[] destino = calcularDestino(fila, columna, direccion);

        int distancia = distanciaAlCuadrado(
                destino[0], destino[1],
                filaObjetivo, columnaObjetivo);

        boolean esMejor = huyendo ? (distancia > mejorDistancia) : (distancia < mejorDistancia);

        if (esMejor) {
            mejorDistancia = distancia;
            mejorDireccion = direccion;
        }
    }

    // Fallback: si solo puede volver por donde vino, lo permitimos.
    if (mejorDireccion == null) {
        for (String direccion : direcciones) {
            if (puedeAvanzar(fila, columna, direccion, fantasma)) {
                mejorDireccion = direccion;
                break;
            }
        }
    }

    return mejorDireccion;
}

    // =========================================================
    // Objetivo de persecución según el TIPO de fantasma.
    // =========================================================
    private int[] calcularObjetivoPersecucion(Fantasma fantasma) {

        int pf = pacman.getY() / TILE_SIZE;
        int pc = pacman.getX() / TILE_SIZE;

        String dirPac = pacman.getDireccionActual();
        int df = 0, dc = 0;
        if ("arriba".equals(dirPac))         df = -1;
        else if ("abajo".equals(dirPac))     df =  1;
        else if ("izquierda".equals(dirPac)) dc = -1;
        else if ("derecha".equals(dirPac))   dc =  1;

        if (Fantasma.TIPO_PINKY.equals(fantasma.getTipo())) {
            return new int[] { pf + 4 * df, pc + 4 * dc };
        }

        if (Fantasma.TIPO_INKY.equals(fantasma.getTipo())) {
            int filaBlinky = fantasma1.getY() / TILE_SIZE;
            int colBlinky  = fantasma1.getX() / TILE_SIZE;

            int filaFrente = pf + 2 * df;
            int colFrente  = pc + 2 * dc;

            int vf = filaFrente - filaBlinky;
            int vc = colFrente  - colBlinky;

            return new int[] { filaFrente + vf, colFrente + vc };
        }

        if (Fantasma.TIPO_CLYDE.equals(fantasma.getTipo())) {
            int filaFant = fantasma.getY() / TILE_SIZE;
            int colFant  = fantasma.getX() / TILE_SIZE;

            int dist2 = distanciaAlCuadrado(filaFant, colFant, pf, pc);

            if (dist2 > 64) {
                return new int[] { pf, pc };
            }
            return new int[] { 29, 1 };
        }

        return new int[] { pf, pc };
    }

    // =========================================================
    // Ruta forzada para SALIR DE LA CASA.
    // =========================================================
    private String direccionParaSalirDeCasa(int fila, int columna, Fantasma fantasma) {

        int colPuerta = 13;
        if (Math.abs(columna - 14) < Math.abs(columna - 13)) {
            colPuerta = 14;
        }

        if (columna < colPuerta && puedeAvanzar(fila, columna, "derecha", fantasma)) {
            return "derecha";
        }
        if (columna > colPuerta && puedeAvanzar(fila, columna, "izquierda", fantasma)) {
            return "izquierda";
        }
        if (puedeAvanzar(fila, columna, "arriba", fantasma)) {
            return "arriba";
        }

        for (String d : new String[]{"arriba", "izquierda", "derecha", "abajo"}) {
            if (puedeAvanzar(fila, columna, d, fantasma)) {
                return d;
            }
        }
        return null;
    }

    // Se llama cuando Pac-Man come un Power Pellet.
    private void activarModoAsustado() {
        finAsustadoEnMillis = System.currentTimeMillis() + DURACION_ASUSTADO_MS;
        fantasmasComidosSeguidos = 0;

        ponerAsustado(fantasma1);
        ponerAsustado(fantasma2);
        ponerAsustado(fantasma3);
        ponerAsustado(fantasma4);
    }

    private void ponerAsustado(Fantasma fantasma) {
        if (Fantasma.COMIDO.equals(fantasma.getEstado())) {
            return;
        }
        fantasma.setEstado(Fantasma.ASUSTADO);
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

    String dir = fantasma.getDireccionActual();

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

    // Permite que el fantasma atraviese el túnel.
private void aplicarTunelFantasma(Fantasma fantasma) {

    if (fantasma.getY() != FILA_TUNEL * TILE_SIZE) {
        return;
    }

    int limiteDerecho = (COLUMNAS - 1) * TILE_SIZE;

    if (fantasma.getX() < 0) {
        fantasma.setX(limiteDerecho);
    } else if (fantasma.getX() > limiteDerecho) {
        fantasma.setX(0);
    }
}

    // Comprueba si Pac-Man chocó con alguno de los cuatro fantasmas.
    private void verificarColisionConFantasma() {

        if (procesarColision(fantasma1)) return;
        if (procesarColision(fantasma2)) return;
        if (procesarColision(fantasma3)) return;
        procesarColision(fantasma4);
    }

    private boolean procesarColision(Fantasma fantasma) {

        if (!hayColision(pacman, fantasma)) {
            return false;
        }

        if (Fantasma.COMIDO.equals(fantasma.getEstado())) {
            return false;
        }

        if (Fantasma.ASUSTADO.equals(fantasma.getEstado())) {
            comerFantasma(fantasma);
            return false;
        }

        perderVida();
        return true;
    }

    private boolean hayColision(PacmanJugador pacman, Fantasma fantasma) {

        return pacman.getX() < fantasma.getX() + Fantasma.TAMANO
                && pacman.getX() + PacmanJugador.TAMANO > fantasma.getX()
                && pacman.getY() < fantasma.getY() + Fantasma.TAMANO
                && pacman.getY() + PacmanJugador.TAMANO > fantasma.getY();
    }

    // Pac-Man se come a un fantasma asustado.
    private void comerFantasma(Fantasma fantasma) {
        fantasmasComidosSeguidos++;

        int puntos = 200 * (int) Math.pow(2, fantasmasComidosSeguidos - 1);
        if (puntos > 1600) {
            puntos = 1600;
        }
        Puntos.sumarPuntos(puntos);

        fantasma.setEstado(Fantasma.COMIDO);
        fantasma.setRevivirEnMillis(System.currentTimeMillis() + TIEMPO_REAPARICION_MS);
    }

    // Le hace perder una vida a Pac-Man.
    private void perderVida() {
        vidas--;

        if (vidas <= 0) {
            terminarPorGameOver();
            return;
        }

        reiniciarPosiciones();
    }

    // Vuelve a Pac-Man y a los fantasmas a sus posiciones y estado iniciales.
    // OJO: las cargas de romper paredes NO se resetean, y las paredes ya
    // rotas siguen rotas (la matriz conserva los cambios de esta partida).
    private void reiniciarPosiciones() {
        pacman.setX(1 * TILE_SIZE);
        pacman.setY(1 * TILE_SIZE);
        pacman.setDireccionActual(null);
        pacman.setDireccionDeseada(null);

        reiniciarFantasma(fantasma1, 13 * TILE_SIZE, 11 * TILE_SIZE);
        reiniciarFantasma(fantasma2, 12 * TILE_SIZE, 16 * TILE_SIZE);
        reiniciarFantasma(fantasma3, 13 * TILE_SIZE, 16 * TILE_SIZE);
        reiniciarFantasma(fantasma4, 14 * TILE_SIZE, 16 * TILE_SIZE);

        finAsustadoEnMillis = 0;
        fantasmasComidosSeguidos = 0;

        inicioJuegoMillis = System.currentTimeMillis();
    }

    private void reiniciarFantasma(Fantasma fantasma, int x, int y) {
        fantasma.setX(x);
        fantasma.setY(y);
        fantasma.setDireccionActual(null);
        fantasma.setEstado(Fantasma.EN_CASA);
        fantasma.setVelocidad(3);
        fantasma.setRevivirEnMillis(0);
    }

    // Termina el juego cuando Pac-Man se queda sin vidas.
    private void terminarPorGameOver() {

        juegoTerminado = true;
        perdio = true;

        timer.stop();

        System.out.println("Pacman fue atrapado. Game Over.");
    }

    public boolean esPared(int fila, int columna) {
        if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS) {
            return true;
        }
        // Solo el tile 1 es pared. El 6 (pared rota) NO es pared.
        return mapa.MATRIZ[fila][columna] == 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        dibujarMapa(g);
        dibujarPacman(g);
        dibujarFantasma(g);
        dibujarPuntaje(g);
        dibujarRecordatorioHabilidad(g);
        if (gano){dibujarPantallaWin(g);}
        if (perdio){dibujarPantallaGameOver(g);}
        }

    // NUEVO: recordatorio visual de la habilidad de romper paredes.
    // Se muestra en la esquina superior derecha SOLO si hay cargas.
    private void dibujarRecordatorioHabilidad(Graphics g) {

        int cargas = pacman.getCargasRomperParedes();

        if (cargas <= 0) {
            return;
        }

        int anchoPanel = 180;
        int altoPanel = 50;
        int xPanel = getWidth() - anchoPanel - 10;
        int yPanel = 10;

        // Fondo negro semitransparente para que se lea bien.
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(xPanel, yPanel, anchoPanel, altoPanel, 12, 12);

        // Borde naranja (dos veces, para darle un poco de grosor).
        g.setColor(Color.ORANGE);
        g.drawRoundRect(xPanel, yPanel, anchoPanel, altoPanel, 12, 12);
        g.drawRoundRect(xPanel + 1, yPanel + 1, anchoPanel - 2, altoPanel - 2, 12, 12);

        // Línea 1: la tecla y la acción.
        g.setFont(g.getFont().deriveFont(java.awt.Font.BOLD, 14f));
        g.setColor(Color.ORANGE);
        g.drawString("[F] Romper pared", xPanel + 14, yPanel + 22);

        // Línea 2: cuántas cargas quedan.
        g.setFont(g.getFont().deriveFont(13f));
        g.setColor(Color.WHITE);
        g.drawString("Cargas: " + cargas, xPanel + 14, yPanel + 40);
    }

    // Dibuja la pantalla que aparece cuando Pac-Man pierde todas sus vidas.
    private void dibujarPantallaGameOver(Graphics g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.RED);
        g.setFont(g.getFont().deriveFont(40f));

        String titulo = "GAME OVER";

        int anchoTitulo = g.getFontMetrics().stringWidth(titulo);
        int xTitulo = (getWidth() - anchoTitulo) / 2;
        int yTitulo = 230;

        g.drawString(titulo, xTitulo, yTitulo);

        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(22f));

        String textoPuntaje = "Puntaje: " + Puntos.getPuntaje();
        int anchoPuntaje = g.getFontMetrics().stringWidth(textoPuntaje);
        int xPuntaje = (getWidth() - anchoPuntaje) / 2;

        g.drawString(textoPuntaje, xPuntaje, 280);

        g.setFont(g.getFont().deriveFont(18f));

        String mensaje = "Te atraparon los fantasmas.";
        int anchoMensaje = g.getFontMetrics().stringWidth(mensaje);
        int xMensaje = (getWidth() - anchoMensaje) / 2;

        g.drawString(mensaje, xMensaje, 320);
    }

    // Dibuja la pantalla que aparece cuando Pac-Man gana.
    private void dibujarPantallaWin(Graphics g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.YELLOW);
        g.setFont(g.getFont().deriveFont(40f));

        String titulo = "¡GANASTE!";

        int anchoTitulo = g.getFontMetrics().stringWidth(titulo);
        int xTitulo = (getWidth() - anchoTitulo) / 2;
        int yTitulo = 230;

        g.drawString(titulo, xTitulo, yTitulo);

        g.setFont(g.getFont().deriveFont(22f));

        String textoPuntaje = "Puntaje: " + Puntos.getPuntaje();
        int anchoPuntaje = g.getFontMetrics().stringWidth(textoPuntaje);
        int xPuntaje = (getWidth() - anchoPuntaje) / 2;

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
            g.drawString("Vidas: " + vidas, 10, 40);
    }

private void dibujarMapa(Graphics g) {

    int[][] matriz = mapa.MATRIZ;

    g.setColor(Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight());

    for (int fila = 0; fila < FILAS; fila++) {

        for (int col = 0; col < COLUMNAS; col++) {

            int tile = matriz[fila][col];

            int x = col * TILE_SIZE;
            int y = fila * TILE_SIZE;

            switch (tile) {

                case 1:
                    g.setColor(new Color(0, 180, 255));
                    g.drawRoundRect(
                        x + 2,
                        y + 2,
                        TILE_SIZE - 4,
                        TILE_SIZE - 4,
                        6,
                        6
                    );
                    break;

                case 2:
                    g.setColor(Color.WHITE);
                    g.fillOval(
                        x + TILE_SIZE / 2 - 2,
                        y + TILE_SIZE / 2 - 2,
                        4,
                        4
                    );
                    break;

                case 3:
                    // Power Pellet normal (solo asusta).
                    g.setColor(Color.YELLOW);
                    g.fillOval(
                        x + TILE_SIZE / 2 - 5,
                        y + TILE_SIZE / 2 - 5,
                        10,
                        10
                    );
                    break;

                case 4:
                    g.setColor(Color.PINK);
                    g.fillRect(
                        x + 2,
                        y + TILE_SIZE / 2 - 2,
                        TILE_SIZE - 4,
                        4
                    );
                    break;

                case 5:
                    // NUEVO: Power Pellet ESPECIAL (naranja con anillo
                    // blanco). Da el poder de romper paredes con F.
                    g.setColor(Color.ORANGE);
                    g.fillOval(
                        x + TILE_SIZE / 2 - 6,
                        y + TILE_SIZE / 2 - 6,
                        12,
                        12
                    );
                    g.setColor(Color.WHITE);
                    g.drawOval(
                        x + TILE_SIZE / 2 - 8,
                        y + TILE_SIZE / 2 - 8,
                        16,
                        16
                    );
                    break;

                case 6:
                    // NUEVO: pared rota. No se dibuja nada (se ve
                    // como una celda vacía), pero es transitable.
                    break;

                case 0:
                    // Interior de la casa: no dibujamos nada.
                    break;
            }
        }
    }
}

    private void dibujarPacman(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(pacman.getX() + 1, pacman.getY() + 1, PacmanJugador.TAMANO, PacmanJugador.TAMANO);
    }

    private void dibujarFantasma(Graphics g) {
        dibujarUnFantasma(g, fantasma1, Color.RED);
        dibujarUnFantasma(g, fantasma2, Color.PINK);
        dibujarUnFantasma(g, fantasma3, Color.CYAN);
        dibujarUnFantasma(g, fantasma4, Color.ORANGE);
    }

    private void dibujarUnFantasma(Graphics g, Fantasma fantasma, Color colorNormal) {
        String estado = fantasma.getEstado();

        if (Fantasma.COMIDO.equals(estado)) {
            g.setColor(Color.WHITE);
            g.fillOval(fantasma.getX() + 3, fantasma.getY() + 5, 5, 5);
            g.fillOval(fantasma.getX() + 10, fantasma.getY() + 5, 5, 5);
            return;
        }

        if (Fantasma.ASUSTADO.equals(estado)) {
            long restante = finAsustadoEnMillis - System.currentTimeMillis();
            boolean porTerminar = restante < 2000;
            boolean parpadeoBlanco = porTerminar && (System.currentTimeMillis() / 200) % 2 == 0;

            g.setColor(parpadeoBlanco ? Color.WHITE : new Color(33, 33, 222));
        } else {
            g.setColor(colorNormal);
        }

        g.fillOval(
                fantasma.getX() + 1,
                fantasma.getY() + 1,
                Fantasma.TAMANO,
                Fantasma.TAMANO
        );
    }
}