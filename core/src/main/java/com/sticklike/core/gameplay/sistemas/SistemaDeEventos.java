package com.sticklike.core.gameplay.sistemas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.sticklike.core.entidades.enemigos.bosses.BossPolla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.gameplay.progreso.Evento;
import com.sticklike.core.gameplay.controladores.ControladorEnemigos;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.ui.RenderHUDComponents;
import com.sticklike.core.utilidades.GestorDeAudio;

import static com.sticklike.core.utilidades.GestorConstantes.*;

import java.util.PriorityQueue;

/**
 * Gestiona los eventos del juego basados en el progreso del jugador.
 * Activa cambios en la aparición de enemigos, ajustes de dificultad y jefes según el nivel alcanzado.
 */

public class SistemaDeEventos {
    private PriorityQueue<Evento> eventos; // Cola de prioridad para eventos
    private RenderHUDComponents renderHUDComponents; // Para futuros eventos basados en timer
    private ControladorEnemigos controladorEnemigos;
    private SistemaDeNiveles sistemaDeNiveles;
    private boolean efectoPollaEjecutado = false;


    public SistemaDeEventos(RenderHUDComponents renderHUDComponents, ControladorEnemigos controladorEnemigos, SistemaDeNiveles sistemaDeNiveles) {
        this.eventos = new PriorityQueue<>();
        this.renderHUDComponents = renderHUDComponents;
        this.controladorEnemigos = controladorEnemigos;
        this.sistemaDeNiveles = sistemaDeNiveles;
        inicializarEventos();
    }

    private void inicializarEventos() {
        eventos.add(new Evento("Aumenta nº enemigos", this::eventoAumentaEnemigos1, LVL_EVENTO1));
        eventos.add(new Evento("Aumenta nº enemigos 2", this::eventoAumentaEnemigos2, LVL_EVENTO2));
        eventos.add(new Evento("Aparecen las pollas", this::entraEnemigoPolla, LVL_EVENTO3));
        eventos.add(new Evento("BOSSPOLLA Aparece", this::spawnPrimerBoss, LVL_EVENTO4));
        eventos.add(new Evento("Examen Aparece", this::spawnExamen, LVL_EVENTO4));
    }

    private void eventoAumentaEnemigos1() {
        controladorEnemigos.setIntervaloDeAparicion(EVENTO1_SPAWN_RATE);
        controladorEnemigos.setTiposDeEnemigos(TIPOS_ENEMIGOS2);
        incrementarVelocidadCulo(EVENTO1_SPEED_MULT);
        Gdx.app.log("Aparición", "Aparición cada: " + controladorEnemigos.getIntervaloDeAparicion());

    }

    private void eventoAumentaEnemigos2() {
        controladorEnemigos.setIntervaloDeAparicion(EVENTO2_SPAWN_RATE);
        incrementarVelocidadCulo(EVENTO2_SPEED_MULT);
        Gdx.app.log("Aparición", "Aparición cada: " + controladorEnemigos.getIntervaloDeAparicion());

    }

    private void incrementarVelocidadCulo(float factorMultiplicador) {
        for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
            if (enemigo instanceof EnemigoCulo culo) {
                float velocidadActual = culo.getVelocidad();
                culo.setVelocidad(velocidadActual * factorMultiplicador);
            }
        }
    }

    private void entraEnemigoPolla() {
        // Configura el efecto de spawn masivo
        controladorEnemigos.setTiposDeEnemigos(LISTA_POLLAS);
        controladorEnemigos.setIntervaloDeAparicion(EVENTO_POLLAS_SPAWN_RATE);
        Gdx.app.log("Polla", "Modo pollas activado: spawn muy rápido");

        Timer.schedule(new Timer.Task(){
            @Override
            public void run() {
                restaurarSpawnNormal();
            }
        }, 5); // 5 segundos de duración del efecto
    }

    private void restaurarSpawnNormal() {
        controladorEnemigos.setTiposDeEnemigos(TIPOS_ENEMIGOS3);
        controladorEnemigos.setIntervaloDeAparicion(EVENTO2_SPAWN_RATE);
        Gdx.app.log("Polla", "Spawn restaurado a configuración normal");
        efectoPollaEjecutado = true;
    }

    private void spawnPrimerBoss() {
        controladorEnemigos.spawnBossPollaAleatorio();
        GestorDeAudio.getInstance().cambiarMusica("fondo3");
        Gdx.app.log("BossPolla", "¡Ha aparecido el PollaBOSS en el nivel 9!");
    }


    private void spawnExamen() {
        GestorDeAudio.getInstance().cambiarMusica("fondo4");
        controladorEnemigos.setTiposDeEnemigos(LISTA_EXAMEN);
        controladorEnemigos.setIntervaloDeAparicion(EVENTO3_SPAWN_RATE);
        incrementarVelocidadCulo(EVENTO3_SPEED_MULT);
        Gdx.app.log("Examen", "¡Exámenes comienzan a aparecer!");
    }
    public void actualizar() {
        int nivelActual = sistemaDeNiveles.getNivelActual();

        if (!eventos.isEmpty()) {
            Evento siguienteEvento = eventos.peek();
            if (nivelActual >= siguienteEvento.getNivelRequerido()) {
                if (!comprobarBossPollaMuerto(siguienteEvento)) {
                    return;
                }
                Evento evento = eventos.poll();
                Gdx.app.log("Act. Eventos.", "Activando evento: " + evento.getNombreEvento() + " [nivel requerido: " + evento.getNivelRequerido() + "]");
                evento.applyEvento();
                return;
            }
        }

        if (nivelActual >= LVL_EVENTO3 && !efectoPollaEjecutado) {
            entraEnemigoPolla();
        }
    }


    public void dispose() {
        eventos.clear();
    }

    private boolean comprobarBossPollaMuerto(Evento siguienteEvento) {
        if (siguienteEvento.getNombreEvento().equals("Examen Aparece")) {
            for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
                if (enemigo instanceof BossPolla && !enemigo.estaMuerto()) {
                    return false;
                }
            }
        }
        return true;
    }
}
