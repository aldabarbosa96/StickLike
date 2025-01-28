package com.sticklike.core.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * ControladorAudio gestiona la reproducción de música de fondo y efectos de sonido en el juego.
 * Utiliza un mapa para almacenar efectos de sonido, permitiendo su reproducción bajo demanda.
 */
public class ControladorAudio {
    private Music musicaFondo;
    private Map<String, Sound> efectosSonido; // Mapa para almacenar efectos de sonido por nombre

    /**
     * Constructor de ControladorAudio. Inicializa el mapa de efectos de sonido y carga los recursos de audio.
     */
    public ControladorAudio() {
        efectosSonido = new HashMap<>();
        cargarRecursos();
    }

    /**
     * Carga los recursos de audio, incluyendo la música de fondo y los efectos de sonido.
     * Los efectos de sonido se almacenan en un mapa para su fácil acceso mediante nombres.
     */
    private void cargarRecursos() {
        // Cargar música de fondo
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("audio/musica/fondo2.mp3"));

        // Cargar efectos de sonido y almacenarlos en el mapa
        efectosSonido.put("recibeDanyo", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/recibeDaño.wav")));
        efectosSonido.put("lanzarPiedra", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoLanzarPiedra.wav")));
        efectosSonido.put("lanzarCalcetin", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoCalcetines.wav")));
        efectosSonido.put("muerteJugador", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoMuerteJugador.wav")));
        efectosSonido.put("recogerXP", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoRecogerObjetoXPCaca.wav")));
        efectosSonido.put("recogerVida", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoRecogerCorazon.wav")));
        efectosSonido.put("upgrade", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoUpgrade.wav")));
        efectosSonido.put("pausa", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoPausa.wav")));
    }

    /**
     * Reproduce la música de fondo en bucle con el volumen especificado.
     */
    public void reproducirMusica() {
        if (musicaFondo != null) {
            musicaFondo.setLooping(true);
            musicaFondo.setVolume(MUSICA_VOLUMEN);
            musicaFondo.play();
        }
    }

    /**
     * Pausa la música de fondo ajustando su volumen al nivel de pausa.
     * Esto permite reducir el volumen sin detener completamente la música.
     */
    public void pausarMusica() {
        if (musicaFondo != null) {
            musicaFondo.setVolume(MUSICA_VOLUMEN_PAUSA); // Reduce el volumen durante la pausa (efecto opacado)
        }
    }

    /**
     * Detiene la reproducción de la música de fondo.
     */
    public void detenerMusica() {
        if (musicaFondo != null) {
            musicaFondo.stop();
        }
    }

    /**
     * Reproduce un efecto de sonido específico con el volumen indicado.
     *
     * @param nombreEfecto Nombre del efecto de sonido a reproducir (debe coincidir con una clave del mapa).
     * @param volumen Volumen al que se reproducirá el efecto (0.0 a 1.0).
     */
    public void reproducirEfecto(String nombreEfecto, float volumen) {
        Sound efecto = efectosSonido.get(nombreEfecto);
        if (efecto != null) {
            efecto.play(volumen);
        }
        // TODO: Implementar try/catch para manejar posibles errores (por ejemplo, si el efecto no existe)
    }

    /**
     * Libera los recursos de audio utilizados por la música de fondo y los efectos de sonido.
     * Este método debe llamarse cuando el audio ya no sea necesario para evitar fugas de memoria.
     */
    public void dispose() {
        if (musicaFondo != null) {
            musicaFondo.dispose();
        }
        for (Sound efecto : efectosSonido.values()) {
            efecto.dispose();
        }
        efectosSonido.clear(); // Limpia el mapa para liberar memoria
    }


}
