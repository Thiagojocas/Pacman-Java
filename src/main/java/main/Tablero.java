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

        // CORREGIDO: los fantasmas 2, 3 y 4 arrancaban DENTRO de paredes
        // (fila 13, columnas 12/14/15 son tile 1). Ahora arrancan dentro
        // de la casa (fila 16), que es tile 0 (interior transitable).
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

            // Si en esta celda había un Power Pellet, activamos el modo
            // asustado: los fantasmas se ponen azules y huyen.
            if (Puntos.comerPowerPellet(fila, columna)) {
                activarModoAsustado();
            }

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
            if (puedeAvanzar(fila, columna, pacman.getDireccionDeseada(), null)) {
                pacman.setDireccionActual(pacman.getDireccionDeseada());
            }

            // Si la direccion actual choca con pared, Pacman se frena
            // exactamente centrado en la celda (nunca queda a mitad de camino).
            if (!puedeAvanzar(fila, columna, pacman.getDireccionActual(), null)) {
                return;
            }
        }

        moverSegunDireccionActual();
        aplicarTunel();
    }

        
    // Revisa si desde (fila, columna) se puede avanzar un paso en esa direccion.
    // El parametro "fantasma" indica QUIEN se quiere mover: si es Pac-Man,
    // se pasa null. Se usa para saber si puede entrar a la casa de los
    // fantasmas (solo los ojos de un fantasma "comido" pueden hacerlo).
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

        // Tunel: en la fila habilitada, dejar "salir" del mapa por los bordes
        // en vez de bloquear como si fuera pared.
        if (fila == FILA_TUNEL && (columnaDestino < 0 || columnaDestino >= COLUMNAS)) {
            return true;
        }

        if (esPared(filaDestino, columnaDestino)) {
            return false;
        }

        // Solo dejamos ENTRAR a la casa de los fantasmas (desde afuera)
        // a un fantasma que está en estado "comido" (volviendo como ojos).
        // Si ya está adentro, puede moverse libremente para salir.
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

    // Indica si la celda (fila, columna) forma parte de la casa de los
    // fantasmas (su interior o la puerta de entrada).
   private boolean esCasaFantasmas(int fila, int columna) {

    if (fila < 15 || fila > 17 || columna < 11 || columna > 16) {
        return false;
    }

    int tile = mapa.MATRIZ[fila][columna];

    // 4 = puerta
    // 0 = interior de la casa

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
// Recibimos como parámetro cuál de los fantasmas queremos mover.
private void actualizarFantasma(Fantasma fantasma) {

    // Comprobamos si el fantasma está exactamente centrado en una celda.
    boolean centrado = (fantasma.getX() % TILE_SIZE == 0)
            && (fantasma.getY() % TILE_SIZE == 0);

    // Los cambios de estado (y sobre todo de VELOCIDAD) de un fantasma
    // solo se procesan cuando está centrado. Si se cambiara la velocidad
    // a mitad de camino entre dos celdas, la posición del fantasma
    // dejaría de coincidir con los múltiplos de TILE_SIZE para siempre
    // (según la matemática de módulo), "centrado" nunca volvería a dar
    // true, y el fantasma quedaría moviéndose en línea recta sin que
    // nadie vuelva a revisarle paredes: se iba derecho para afuera del
    // mapa. Por eso TODO lo que decide velocidad vive acá adentro.
    if (centrado) {
        actualizarEstadoFantasma(fantasma);
    }

    // Movemos el fantasma en la dirección que decidió la IA.
    moverFantasmaSegunDireccion(fantasma);

    // Aplicamos el túnel a ESTE fantasma.
    aplicarTunelFantasma(fantasma);
}


// Decide en qué estado queda el fantasma y hacia dónde se mueve
// despues. Se llama UNICAMENTE cuando el fantasma está centrado.
private void actualizarEstadoFantasma(Fantasma fantasma) {

    // Si el modo asustado ya terminó, este fantasma deja de estar asustado.
    if (Fantasma.ASUSTADO.equals(fantasma.getEstado()) && yaTerminoElAsustado()) {
        fantasma.setEstado(Fantasma.NORMAL);
    }

    int fila = fantasma.getY() / TILE_SIZE;
    int columna = fantasma.getX() / TILE_SIZE;

    // Si el fantasma son solo ojos volviendo a la casa y ya llegó...
    if (Fantasma.COMIDO.equals(fantasma.getEstado())
            && fantasma.getX() == fantasma.getXCasa()
            && fantasma.getY() == fantasma.getYCasa()) {

        if (System.currentTimeMillis() >= fantasma.getRevivirEnMillis()) {
            // Ya pasó el tiempo de reaparición: revive.
            fantasma.setEstado(Fantasma.NORMAL);
        } else {
            // Todavía tiene que esperar adentro de la casa.
            fantasma.setDireccionActual(null);
            return;
        }
    }

    // Ajustamos la velocidad según el estado ACTUAL. Esto pasa siempre
    // acá, en un punto alineado a la grilla (ver el comentario de arriba).
    switch (fantasma.getEstado()) {
        case Fantasma.ASUSTADO:
            fantasma.setVelocidad(1); // mas lento mientras esta asustado
            break;
        case Fantasma.COMIDO:
            fantasma.setVelocidad(7); // los ojos vuelven rapido a la casa
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

    // Si está "comido" (son solo ojos), el objetivo ya no es Pac-Man
    // sino el punto de la casa. Si está "asustado", en vez de acercarse
    // al objetivo va a alejarse de él (huir).
    boolean comido = Fantasma.COMIDO.equals(fantasma.getEstado());
    boolean huyendo = Fantasma.ASUSTADO.equals(fantasma.getEstado());

    int filaObjetivo;
    int columnaObjetivo;

    if (comido) {
        filaObjetivo = fantasma.getYCasa() / TILE_SIZE;
        columnaObjetivo = fantasma.getXCasa() / TILE_SIZE;
    } else {
        // Perseguir (normal) o huir (asustado) toman como referencia
        // la posición actual de Pac-Man.
        filaObjetivo = pacman.getY() / TILE_SIZE;
        columnaObjetivo = pacman.getX() / TILE_SIZE;
    }

    String mejorDireccion = null;
    int mejorDistancia = huyendo ? Integer.MIN_VALUE : Integer.MAX_VALUE;

    // Probamos las cuatro direcciones posibles.
    for (String direccion : direcciones) {

        // Evitamos volver por donde venía el fantasma,
        // salvo que sea la única opción.
        if (direccion.equals(opuesta)) {
            continue;
        }

        // Si hay una pared (o es una entrada a la casa que este
        // fantasma no puede usar), descartamos esa dirección.
        if (!puedeAvanzar(fila, columna, direccion, fantasma)) {
            continue;
        }

        // Calculamos la celda a la que llegaría.
        int[] destino = calcularDestino(
                fila,
                columna,
                direccion
        );

        // Calculamos qué tan cerca (o lejos) queda del objetivo.
        int distancia = distanciaAlCuadrado(
                destino[0],
                destino[1],
                filaObjetivo,
                columnaObjetivo
        );

        // Si está huyendo, nos interesa la dirección que lo aleja más;
        // si no, la que lo acerca más.
        boolean esMejor = huyendo ? (distancia > mejorDistancia) : (distancia < mejorDistancia);

        if (esMejor) {
            mejorDistancia = distancia;
            mejorDireccion = direccion;
        }
    }

    // Si quedó encerrado y la única posibilidad es volver atrás,
    // permitimos la dirección contraria.
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

    // Se llama cuando Pac-Man come un Power Pellet: todos los fantasmas
    // que no estén "comidos" (ojos volviendo a casa) se ponen azules y
    // huyen, y arrancamos (o reiniciamos) la cuenta regresiva.
    private void activarModoAsustado() {
        finAsustadoEnMillis = System.currentTimeMillis() + DURACION_ASUSTADO_MS;
        fantasmasComidosSeguidos = 0;

        ponerAsustado(fantasma1);
        ponerAsustado(fantasma2);
        ponerAsustado(fantasma3);
        ponerAsustado(fantasma4);
    }

    private void ponerAsustado(Fantasma fantasma) {
        // Un fantasma que ya está volviendo como ojos no se ve afectado
        // por un nuevo Power Pellet.
        if (Fantasma.COMIDO.equals(fantasma.getEstado())) {
            return;
        }

        // Ojo: acá SOLO cambiamos el estado (para que se vea azul y la
        // IA empiece a huir apenas se pueda). La velocidad NO se toca
        // acá: se ajusta sola la próxima vez que el fantasma esté
        // centrado, dentro de actualizarEstadoFantasma().
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
    // Comprueba si Pac-Man chocó con alguno de los cuatro fantasmas y
    // reacciona segun el estado de cada uno.
    private void verificarColisionConFantasma() {

        if (procesarColision(fantasma1)) return;
        if (procesarColision(fantasma2)) return;
        if (procesarColision(fantasma3)) return;
        procesarColision(fantasma4);
    }

    // Comprueba la colision de Pac-Man con UN fantasma y actua segun su
    // estado. Devuelve true si Pac-Man perdió una vida (para no seguir
    // revisando a los demás fantasmas en el mismo instante).
    private boolean procesarColision(Fantasma fantasma) {

        if (!hayColision(pacman, fantasma)) {
            return false;
        }

        // Los ojos que vuelven a la casa no le hacen nada a Pac-Man.
        if (Fantasma.COMIDO.equals(fantasma.getEstado())) {
            return false;
        }

        // Si el fantasma estaba asustado, Pac-Man se lo come.
        if (Fantasma.ASUSTADO.equals(fantasma.getEstado())) {
            comerFantasma(fantasma);
            return false;
        }

        // Si el fantasma estaba en su estado normal, Pac-Man pierde una vida.
        perderVida();
        return true;
    }

    // Comprueba si Pac-Man y un fantasma están chocando.
    private boolean hayColision(PacmanJugador pacman, Fantasma fantasma) {

        // Comparamos los rectángulos que ocupan Pac-Man y el fantasma.
        return pacman.getX() < fantasma.getX() + Fantasma.TAMANO
                && pacman.getX() + PacmanJugador.TAMANO > fantasma.getX()
                && pacman.getY() < fantasma.getY() + Fantasma.TAMANO
                && pacman.getY() + PacmanJugador.TAMANO > fantasma.getY();
    }

    // Pac-Man se come a un fantasma asustado: suma puntos (que se van
    // duplicando si come varios seguidos, como en el juego original) y
    // lo convierte en un par de ojos que vuelven a la casa. Una vez ahí,
    // espera TIEMPO_REAPARICION_MS antes de poder volver a salir.
    private void comerFantasma(Fantasma fantasma) {
        fantasmasComidosSeguidos++;

        int puntos = 200 * (int) Math.pow(2, fantasmasComidosSeguidos - 1);
        if (puntos > 1600) {
            puntos = 1600;
        }
        Puntos.sumarPuntos(puntos);

        fantasma.setEstado(Fantasma.COMIDO);
        fantasma.setRevivirEnMillis(System.currentTimeMillis() + TIEMPO_REAPARICION_MS);
        // La velocidad NO se cambia acá: se ajusta sola la próxima vez
        // que el fantasma esté centrado, dentro de actualizarEstadoFantasma().
    }

    // Le hace perder una vida a Pac-Man. Si ya no le quedan vidas,
    // termina el juego; si le quedan, reinicia las posiciones para
    // seguir jugando (sin tocar el puntaje ni los puntos ya comidos).
    private void perderVida() {
        vidas--;

        if (vidas <= 0) {
            terminarPorGameOver();
            return;
        }

        reiniciarPosiciones();
    }

    // Vuelve a Pac-Man y a los fantasmas a sus posiciones y estado
    // iniciales despues de perder una vida.
    private void reiniciarPosiciones() {
        pacman.setX(1 * TILE_SIZE);
        pacman.setY(1 * TILE_SIZE);
        pacman.setDireccionActual(null);
        pacman.setDireccionDeseada(null);

        // CORREGIDO: mismas posiciones que en el constructor. Los
        // fantasmas 2, 3 y 4 van dentro de la casa (fila 16), no dentro
        // de paredes (fila 13).
        reiniciarFantasma(fantasma1, 13 * TILE_SIZE, 11 * TILE_SIZE);
        reiniciarFantasma(fantasma2, 12 * TILE_SIZE, 16 * TILE_SIZE);
        reiniciarFantasma(fantasma3, 13 * TILE_SIZE, 16 * TILE_SIZE);
        reiniciarFantasma(fantasma4, 14 * TILE_SIZE, 16 * TILE_SIZE);

        finAsustadoEnMillis = 0;
        fantasmasComidosSeguidos = 0;
    }

    private void reiniciarFantasma(Fantasma fantasma, int x, int y) {
        fantasma.setX(x);
        fantasma.setY(y);
        fantasma.setDireccionActual(null);
        fantasma.setEstado(Fantasma.NORMAL);
        fantasma.setVelocidad(3);
        fantasma.setRevivirEnMillis(0);
    }

    // Termina el juego cuando Pac-Man se queda sin vidas.
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
        if (perdio){dibujarPantallaGameOver(g);}
        }

    // Dibuja la pantalla que aparece cuando Pac-Man pierde todas sus vidas.
    // Antes no existía: el juego solo detenía el Timer (por eso, al perder
    // la última vida, la pantalla se quedaba "congelada" sin ningún aviso).
    private void dibujarPantallaGameOver(Graphics g) {

        // Pintamos todo el tablero de negro.
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Título en rojo.
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
            g.drawString("Vidas: " + vidas, 10, 40);
    }

private void dibujarMapa(Graphics g) {

    int[][] matriz = mapa.MATRIZ;

    // Fondo negro
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight());

    for (int fila = 0; fila < FILAS; fila++) {

        for (int col = 0; col < COLUMNAS; col++) {

            int tile = matriz[fila][col];

            int x = col * TILE_SIZE;
            int y = fila * TILE_SIZE;

            switch (tile) {

                case 1:
                    // PARED
                    g.setColor(new Color(0, 180, 255));

                    // Dibujamos solamente el contorno,
                    // dando un aspecto más parecido al mapa de Pac-Man.
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
                    // PUNTO NORMAL
                    g.setColor(Color.WHITE);

                    g.fillOval(
                        x + TILE_SIZE / 2 - 2,
                        y + TILE_SIZE / 2 - 2,
                        4,
                        4
                    );
                    break;

                case 3:
                    // POWER PELLET
                    g.setColor(Color.YELLOW);

                    g.fillOval(
                        x + TILE_SIZE / 2 - 5,
                        y + TILE_SIZE / 2 - 5,
                        10,
                        10
                    );
                    break;

                case 4:
                    // PUERTA DE LA CASA
                    g.setColor(Color.PINK);

                    g.fillRect(
                        x + 2,
                        y + TILE_SIZE / 2 - 2,
                        TILE_SIZE - 4,
                        4
                    );
                    break;

                case 0:
                    // Interior de la casa.
                    // No dibujamos pared.
                    break;
            }
        }
    }
}

    private void dibujarPacman(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(pacman.getX() + 1, pacman.getY() + 1, PacmanJugador.TAMANO, PacmanJugador.TAMANO);
    }

    // Dibuja los cuatro fantasmas en el tablero, cada uno con su color
    // propio (salvo que esté asustado o comido).
    private void dibujarFantasma(Graphics g) {
        dibujarUnFantasma(g, fantasma1, Color.RED);
        dibujarUnFantasma(g, fantasma2, Color.PINK);
        dibujarUnFantasma(g, fantasma3, Color.CYAN);
        dibujarUnFantasma(g, fantasma4, Color.ORANGE);
    }

    // Dibuja UN fantasma segun su estado actual:
    // - normal: con su color propio.
    // - asustado: azul (y parpadea en blanco justo antes de volver a la normalidad).
    // - comido: solo se ven los "ojos" volviendo a la casa.
    private void dibujarUnFantasma(Graphics g, Fantasma fantasma, Color colorNormal) {
        String estado = fantasma.getEstado();

        if (Fantasma.COMIDO.equals(estado)) {
            // Son solo un par de ojitos volviendo a la casa.
            g.setColor(Color.WHITE);
            g.fillOval(fantasma.getX() + 3, fantasma.getY() + 5, 5, 5);
            g.fillOval(fantasma.getX() + 10, fantasma.getY() + 5, 5, 5);
            return;
        }

        if (Fantasma.ASUSTADO.equals(estado)) {
            // Cuando falta poco para que se termine el efecto, parpadea
            // entre azul y blanco para avisarle al jugador.
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