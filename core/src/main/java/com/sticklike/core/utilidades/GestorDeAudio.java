package com.sticklike.core.utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Timer;

import java.util.HashMap;
import java.util.Map;

import static com.sticklike.core.utilidades.GestorConstantes.*;

public class GestorDeAudio {
    private Music musicaFondo;
    private Map<String, Sound> efectosSonido;
    // Usaremos un mapa para contar las instancias activas de cada sonido.
    private Map<String, Integer> contadorInstancias;
    // Duración estimada de cada efecto (en segundos)
    private Map<String, Float> duracionSonidos;

    public GestorDeAudio() {
        efectosSonido = new HashMap<>();
        contadorInstancias = new HashMap<>();
        duracionSonidos = new HashMap<>();
        cargarRecursos();
    }

    private void cargarRecursos() {
        // Cargar música de fondo
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("audio/musica/fondo2.mp3"));

        // Cargar efectos de sonido
        efectosSonido.put("recibeDanyo", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/recibeDaño.wav")));
        efectosSonido.put("lanzarPiedra", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoLanzarPiedra.wav")));
        efectosSonido.put("lanzarCalcetin", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoCalcetines.wav")));
        efectosSonido.put("tazo", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoTazo.wav")));
        efectosSonido.put("pedo", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoPedo.wav")));
        efectosSonido.put("muerteJugador", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoMuerteJugador.wav")));
        efectosSonido.put("recogerXP", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoRecogerObjetoXPCaca.wav")));
        efectosSonido.put("recogerVida", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoRecogerCorazon.wav")));
        efectosSonido.put("recogerOro", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoOro.wav")));
        efectosSonido.put("recogerPowerUP", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoPowerUp.wav")));
        efectosSonido.put("upgrade", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoUpgrade.wav")));
        efectosSonido.put("pausa", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoPausa.wav")));

        // Manejar tiempos de duración de los efectos
        duracionSonidos.put("tazo", 0.5f);
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
    public void reproducirEfecto(String nombre, float volumen) {
        int instancias = contadorInstancias.getOrDefault(nombre, 0);
        if (instancias >= MAX_INSTANCIAS_SONIDO) {
            return;
        }

        Sound sonido = efectosSonido.get(nombre);
        if (sonido == null) {
            System.out.println("Sonido no encontrado: " + nombre);
            return;
        }

        sonido.play(volumen);
        contadorInstancias.put(nombre, instancias + 1);

        // Programar la disminución del contador después de la duración estimada
        float duracion = duracionSonidos.getOrDefault(nombre, 0.5f);
        Timer.schedule(new Timer.Task(){
            @Override
            public void run() {
                int actual = contadorInstancias.getOrDefault(nombre, 0);
                if (actual > 0) {
                    contadorInstancias.put(nombre, actual - 1);
                }
            }
        }, duracion);
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

    // todo --> podrá servir en un futuro para resetear los contadores, por ejemplo para una nueva pantalla
    public void resetearInstancias() {
        contadorInstancias.clear();
    }

    // Si usas un singleton, podrías implementar:
    private static GestorDeAudio instance;
    public static GestorDeAudio getInstance() {
        if (instance == null) {
            instance = new GestorDeAudio();
        }
        return instance;
    }
}
