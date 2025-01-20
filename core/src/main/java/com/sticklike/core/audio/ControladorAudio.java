package com.sticklike.core.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class ControladorAudio {
    private Music musicaFondo;
    private Sound efectoSonido, efectoSonido2, efectoSonido3;

    public ControladorAudio() {
        cargarRecursos();
    }

    private void cargarRecursos() {
        // Cargar música de fondo
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("audio/musica/fondo.mp3"));
        efectoSonido = Gdx.audio.newSound(Gdx.files.internal("audio/efectos/recibeDaño.wav"));
        efectoSonido2 = Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoLanzarPiedra.wav"));
        efectoSonido3 = Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoCalcetines.wav"));
    }

    public void reproducirMusica() {
        if (musicaFondo != null) {
            musicaFondo.setLooping(true); // Hacer que la música se repita en bucle
            musicaFondo.setVolume(0.12f); // Ajustar el volumen (0 = silencio, 1 = máximo)
            musicaFondo.play();
        }
    }

    public void pausarMusica() {
        if (musicaFondo != null) {
            musicaFondo.pause();
        }
    }

    public void detenerMusica() {
        if (musicaFondo != null) {
            musicaFondo.stop();
        }
    }

    public void reproducirEfecto() {
        if (efectoSonido != null) {
            efectoSonido.play(0.85f); // Reproducir al volumen máximo (1.0f)
        }
    }
    public void reproducirEfecto2() {
        if (efectoSonido2 != null) {
            efectoSonido2.play(0.70f);
        }
    }

    public void dispose() {
        if (musicaFondo != null) {
            musicaFondo.dispose();
        }
        if (efectoSonido != null) {
            efectoSonido.dispose();
        }
    }

    public void reproducirEfecto3() {
        if (efectoSonido3 != null) {
            efectoSonido3.play(0.50f);
        }
    }
}
