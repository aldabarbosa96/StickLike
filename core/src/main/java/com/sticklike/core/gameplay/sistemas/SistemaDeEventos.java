package com.sticklike.core.gameplay.sistemas;

import com.badlogic.gdx.Gdx;
import com.sticklike.core.entidades.enemigos.bosses.BossPolla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.gameplay.progreso.Evento;
import com.sticklike.core.gameplay.controladores.ControladorEnemigos;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.ui.RenderHUDComponents;
import com.sticklike.core.utilidades.GestorDeAudio;

import static com.sticklike.core.utilidades.GestorConstantes.*;

import java.util.PriorityQueue;

public class SistemaDeEventos {
    private PriorityQueue<Evento> eventos; // Cola de prioridad para eventos
    private RenderHUDComponents renderHUDComponents; // Para futuros eventos basados en timer
    private ControladorEnemigos controladorEnemigos;
    private SistemaDeNiveles sistemaDeNiveles;

    public SistemaDeEventos(RenderHUDComponents renderHUDComponents, ControladorEnemigos controladorEnemigos, SistemaDeNiveles sistemaDeNiveles) {
        this.eventos = new PriorityQueue<>();
        this.renderHUDComponents = renderHUDComponents;
        this.controladorEnemigos = controladorEnemigos;
        this.sistemaDeNiveles = sistemaDeNiveles;
        inicializarEventos();
    }

    private void inicializarEventos() {
        eventos.add(new Evento("Aumenta nº enemigos", sistemaDeNiveles, this::eventoAumentaEnemigos1, LVL_EVENTO1));
        eventos.add(new Evento("Aumenta nº enemigos 2", sistemaDeNiveles, this::eventoAumentaEnemigos2, LVL_EVENTO2));
        eventos.add(new Evento("Aparecen las pollas", sistemaDeNiveles, this::entraEnemigoPolla, LVL_EVENTO3));
        eventos.add(new Evento("BOSSPOLLA Aparece", sistemaDeNiveles, this::spawnPrimerBoss, LVL_EVENTO4));
        eventos.add(new Evento("Examen Aparece", sistemaDeNiveles, this::spawnExamen, LVL_EVENTO4));
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
        controladorEnemigos.setTiposDeEnemigos(LISTA_POLLAS);
        controladorEnemigos.setIntervaloDeAparicion(EVENTO_POLLAS_SPAWN_RATE);
        Gdx.app.log("Polla", "Enemigo polla aparece");
    }

    private void spawnPrimerBoss() {
        controladorEnemigos.spawnBossPollaAleatorio();
        GestorDeAudio.getInstance().cambiarMusica("fondo3");
        Gdx.app.log("BossPolla", "¡Ha aparecido el PollaBOSS en el nivel 10!");
    }


    private void spawnExamen() {
        GestorDeAudio.getInstance().cambiarMusica("fondo4");
        controladorEnemigos.setTiposDeEnemigos(LISTA_EXAMEN);
        controladorEnemigos.setIntervaloDeAparicion(EVENTO3_SPAWN_RATE);
        incrementarVelocidadCulo(EVENTO3_SPEED_MULT);
        Gdx.app.log("Examen", "¡Exámenes comienzan a aparecer!");
    }

    public void actualizar() {
        if (!eventos.isEmpty()) {
            Evento siguienteEvento = eventos.peek();
            if (sistemaDeNiveles.getNivelActual() >= siguienteEvento.getNivelRequerido()) {
                if (!comprobarBossPollaMuerto(siguienteEvento)) {
                    return;
                }
                Evento evento = eventos.poll();
                assert evento != null;
                Gdx.app.log("Act. Eventos.", "Activando evento: " + evento.getNombreEvento() + " [nivel requerido: " + evento.getNivelRequerido() + "]");
                evento.applyEvento();
                return;
            }
        }
    }


    public void dispose() {
        eventos.clear();
        eventos = null;
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
