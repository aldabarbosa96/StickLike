package com.sticklike.core.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

import static com.sticklike.core.utilidades.GestorConstantes.*;


public class ControladorAudio {
    private Music musicaFondo;
    private Map<String, Sound> efectosSonido;

    public ControladorAudio() {
        efectosSonido = new HashMap<>();
        cargarRecursos();
    }

    private void cargarRecursos() {
        // Cargar música de fondo
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("audio/musica/fondo2.mp3"));

        efectosSonido.put("recibeDanyo", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/recibeDaño.wav")));
        efectosSonido.put("lanzarPiedra", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoLanzarPiedra.wav")));
        efectosSonido.put("lanzarCalcetin", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoCalcetines.wav")));
        efectosSonido.put("muerteJugador", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoMuerteJugador.wav")));
        efectosSonido.put("recogerXP", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoRecogerObjetoXPCaca.wav")));
        efectosSonido.put("recogerVida", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoRecogerCorazon.wav")));
        efectosSonido.put("upgrade", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoUpgrade.wav")));
    }

    public void reproducirMusica() {
        if (musicaFondo != null) {
            musicaFondo.setLooping(true);
            musicaFondo.setVolume(MUSICA_VOLUMEN);
            musicaFondo.play();
        }
    }

    public void pausarMusica() {
        if (musicaFondo != null) {
            musicaFondo.setVolume(MUSICA_VOLUMEN_PAUSA);
        }
    }

    public void detenerMusica() {
        if (musicaFondo != null) {
            musicaFondo.stop();
        }
    }

    public void reproducirEfecto(String nombreEfecto, float volumen) { // todo --> usar try/catch en un futuro
        Sound efecto = efectosSonido.get(nombreEfecto);
        if (efecto != null) {
            efecto.play(volumen);
        }
    }
    public void dispose() {
        if (musicaFondo != null) {
            musicaFondo.dispose();
        }
        for (Sound efecto : efectosSonido.values()) {
            efecto.dispose();
        }
        efectosSonido.clear();
    }
}
