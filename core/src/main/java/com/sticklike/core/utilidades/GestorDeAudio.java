package com.sticklike.core.utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Timer;

import java.util.HashMap;
import java.util.Map;

import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Clase encargada de cargar y liberar los recursos de audio usados en el juego (música y efectos de sonido).
 */
public class GestorDeAudio {
    // Mantenemos una pista de música actualmente en reproducción.
    private Music musicaActual;
    // Usamos un mapa para todas las pistas de música de fondo.
    private Map<String, Music> musicasFondo;
    private Map<String, Sound> efectosSonido;
    private Map<String, Integer> contadorInstancias;
    private Map<String, Float> duracionSonidos;

    public GestorDeAudio() {
        efectosSonido = new HashMap<>();
        contadorInstancias = new HashMap<>();
        duracionSonidos = new HashMap<>();
        cargarRecursos();
    }

    private void cargarRecursos() {
        // Cargar música de fondo
        musicasFondo = new HashMap<>();
        musicasFondo.put("fondo", Gdx.audio.newMusic(Gdx.files.internal("audio/musica/fondo.mp3")));
        musicasFondo.put("fondo2", Gdx.audio.newMusic(Gdx.files.internal("audio/musica/fondo2.mp3")));
        musicasFondo.put("fondo3", Gdx.audio.newMusic(Gdx.files.internal("audio/musica/fondo3.mp3")));
        musicasFondo.put("fondo4", Gdx.audio.newMusic(Gdx.files.internal("audio/musica/fondo4.mp3")));

        // fondo2 por defecto (música inicial)
        musicaActual = musicasFondo.get("fondo2");

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
        efectosSonido.put("impactoBase", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoImpactoBase2.wav")));
        efectosSonido.put("sonidoBossPolla", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoBossPolla.wav")));
        efectosSonido.put("sonidoBossPolla2", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoBossPolla2.wav")));
        efectosSonido.put("sonidoBossPolla3", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoBossPolla3.wav")));
        efectosSonido.put("sonidoBossPolla4", Gdx.audio.newSound(Gdx.files.internal("audio/efectos/sonidoBossPolla4.wav")));

        // Manejar tiempos de duración de los efectos
        duracionSonidos.put("tazo", 0.5f);
    }

    public void reproducirMusica() {
        if (musicaActual != null) {
            musicaActual.setLooping(true);
            musicaActual.setVolume(MUSICA_VOLUMEN);
            musicaActual.play();
        }
    }

    public void pausarMusica() {
        if (musicaActual != null) {
            musicaActual.setVolume(MUSICA_VOLUMEN_PAUSA);
        }
    }

    public void detenerMusica() {
        if (musicaActual != null) {
            musicaActual.stop();
        }
    }

    public void cambiarMusica(String nombre) {
        // Busca la nueva música en el mapa
        Music nuevaMusica = musicasFondo.get(nombre);
        if (nuevaMusica == null) {
            Gdx.app.log("GestorDeAudio", "Música no encontrada: " + nombre);
            return;
        }
        // Si hay una pista actual, detenerla y opcionalmente liberarla o hacer una transición
        if (musicaActual != null && musicaActual.isPlaying()) {
            musicaActual.stop();
        }
        // Asignamos la nueva música
        musicaActual = nuevaMusica;
        // Configuramos la nueva música
        musicaActual.setLooping(true);
        musicaActual.setVolume(MUSICA_VOLUMEN);
        musicaActual.play();
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
        if (musicaActual != null) {
            musicaActual.dispose();
        }
        for (Music musica : musicasFondo.values()) {
            musica.dispose();
        }
        for (Sound efecto : efectosSonido.values()) {
            efecto.dispose();
        }
        efectosSonido.clear();
    }

    public void resetearInstancias() {
        contadorInstancias.clear();
    }

    // Implementación del patrón singleton
    private static GestorDeAudio instance;
    public static GestorDeAudio getInstance() {
        if (instance == null) {
            instance = new GestorDeAudio();
        }
        return instance;
    }
}
