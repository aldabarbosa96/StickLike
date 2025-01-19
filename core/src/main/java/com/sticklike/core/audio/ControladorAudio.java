package com.sticklike.core.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class ControladorAudio {
    private Music musicaFondo;
    private Sound efectoSonido;

    public ControladorAudio() {
        // Cargar los recursos de audio
        cargarRecursos();
    }

    private void cargarRecursos() {
        // Cargar música de fondo
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("audio/musica/fondo.mp3"));
    }

    /**
     * Reproducir música de fondo.
     */
    public void reproducirMusica() {
        if (musicaFondo != null) {
            musicaFondo.setLooping(true); // Hacer que la música se repita en bucle
            musicaFondo.setVolume(0.35f); // Ajustar el volumen (0 = silencio, 1 = máximo)
            musicaFondo.play();
        }
    }

    /**
     * Pausar la música de fondo.
     */
    public void pausarMusica() {
        if (musicaFondo != null) {
            musicaFondo.pause();
        }
    }

    /**
     * Detener la música de fondo.
     */
    public void detenerMusica() {
        if (musicaFondo != null) {
            musicaFondo.stop();
        }
    }

    /**
     * Reproducir un efecto de sonido.
     */
    public void reproducirEfecto() {
        if (efectoSonido != null) {
            efectoSonido.play(1.0f); // Reproducir al volumen máximo
        }
    }

    /**
     * Liberar los recursos de audio.
     */
    public void dispose() {
        if (musicaFondo != null) {
            musicaFondo.dispose();
        }
        if (efectoSonido != null) {
            efectoSonido.dispose();
        }
    }
}
