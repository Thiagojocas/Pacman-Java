package main;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sonido {

    // Rutas de los sonidos (dentro de src/main/resources).
    public static final String GAME_OVER =
            "/main/sonidos/266163__plasterbrain__pacman-is-dead.wav";
    public static final String AMBIENTE = "/main/sonidos/ambiente.wav";

    // Cada sonido se carga una sola vez y se reutiliza.
    private static final Map<String, Clip> clips = new HashMap<>();

    // Poné false para silenciar todo el juego.
    private static boolean activado = true;

    private static Clip cargar(String ruta) {
        if (clips.containsKey(ruta)) {
            return clips.get(ruta);
        }

        Clip clip = null;

        try (InputStream in = Sonido.class.getResourceAsStream(ruta)) {
            if (in == null) {
                System.out.println("No se encontró el sonido: " + ruta);
            } else {
                AudioInputStream audio =
                        AudioSystem.getAudioInputStream(new BufferedInputStream(in));
                clip = AudioSystem.getClip();
                clip.open(audio);
            }
        } catch (Exception e) {
            System.out.println("Error cargando sonido " + ruta + ": " + e.getMessage());
        }

        clips.put(ruta, clip);
        return clip;
    }

    // Reproduce el sonido una vez, desde el principio.
    public static void reproducir(String ruta) {
        if (!activado) {
            return;
        }

        Clip clip = cargar(ruta);
        if (clip == null) {
            return;
        }

        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    // Reproduce el sonido en loop hasta que se lo detenga.
    public static void reproducirLoop(String ruta) {
        if (!activado) {
            return;
        }

        Clip clip = cargar(ruta);
        if (clip == null) {
            return;
        }

        clip.stop();
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    // Detiene un sonido en particular.
    public static void detener(String ruta) {
        Clip clip = clips.get(ruta);
        if (clip != null) {
            clip.stop();
        }
    }

    // Corta todos los sonidos que estén sonando.
    public static void detenerTodos() {
        for (Clip clip : clips.values()) {
            if (clip != null) {
                clip.stop();
            }
        }
    }
}