package com.sticklike.core.gameplay.sistemas;

import com.badlogic.gdx.utils.Timer;
import com.sticklike.core.entidades.enemigos.bosses.BossPolla;
import com.sticklike.core.gameplay.controladores.ControladorEnemigos;
import com.sticklike.core.gameplay.progreso.Evento;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.ui.MensajesData;
import com.sticklike.core.ui.RenderHUDComponents;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Gestiona los eventos de progresión del jugador:
 * – Cambios en tipos/ratio de spawn
 * – Aumento de velocidad
 * – Aparición de bosses
 */
public class SistemaDeEventos {

    private final RenderHUDComponents hud;
    private final ControladorEnemigos ctrlEnemigos;
    private final SistemaDeNiveles niveles;
    private final List<Evento> eventos;
    private int nextEventIndex = 0;
    private BossPolla bossRef;
    private boolean efectoPollasActivo;
    private Timer.Task restauraSpawnTask;

    public SistemaDeEventos(RenderHUDComponents hud, ControladorEnemigos ctrlEnemigos, SistemaDeNiveles niveles) {
        this.hud = hud;
        this.ctrlEnemigos = ctrlEnemigos;
        this.niveles = niveles;

        // Inicializamos lista de eventos y la ordenamos por nivel requerido
        this.eventos = new ArrayList<>();
        inicializarEventos();
        this.eventos.sort(Comparator.comparingInt(Evento::getNivelRequerido));

        MensajesData.getInstance().activarMensajesCulos(hud);

    }

    private void inicializarEventos() {
        eventos.add(new Evento("Polla y condón", this::eventoAumentaEnemigos1, LVL_EVENTO1, 1));
        eventos.add(new Evento("+Vel +spawn", this::eventoAumentaEnemigos2, LVL_EVENTO2, 2));
        eventos.add(new Evento("Tetas", this::entraEnemigoTeta, LVL_EVENTO3, 3));
        eventos.add(new Evento("BossPolla", this::spawnPrimerBoss, LVL_EVENTO4, 4));
        eventos.add(new Evento("Alarma y regla", this::spawnAlarma, LVL_EVENTO4, 5));
        eventos.add(new Evento("Examen", this::spawnExamen, LVL_EVENTO5, 6));
        eventos.add(new Evento("Regla", this::spawnReglas, LVL_EVENTO6, 7));
        eventos.add(new Evento("Calculadora y libro", this::spawnCalculadoras, LVL_EVENTO7, 8));
        eventos.add(new Evento("Grapadoras", this::spawnGrapadoras, LVL_EVENTO8, 9));
        eventos.add(new Evento("Perforadoras", this::spawnPerforadoras, 1, 10));
    }

    public void actualizar() {
        int nivel = niveles.getNivelActual();

        // Procesamos todos los eventos cuyo nivel requerido ya esté alcanzado
        while (nextEventIndex < eventos.size()) {
            Evento e = eventos.get(nextEventIndex);
            if (nivel < e.getNivelRequerido()) break;

            // Si es el evento de alarma y el boss no ha muerto, lo aplazamos
            if (e.getId() == 5 && (bossRef == null || !bossRef.estaMuerto())) {
                break;
            }

            // Aplicamos el evento y avanzamos el índice
            e.applyEvento();
            nextEventIndex++;
        }
    }

    private void eventoAumentaEnemigos1() {
        ctrlEnemigos.setIntervaloDeAparicion(EVENTO1_SPAWN_RATE);
        ctrlEnemigos.setTiposDeEnemigos(TIPOS_ENEMIGOS2);
        float nuevaVel = ctrlEnemigos.getSpeedMult() * EVENTO1_SPEED_MULT;
        ctrlEnemigos.setSpeedMult(nuevaVel);
        MensajesData.getInstance().activarMensajesPollas(hud);
    }

    private void eventoAumentaEnemigos2() {
        ctrlEnemigos.setIntervaloDeAparicion(EVENTO2_SPAWN_RATE);
        float nuevaVel = ctrlEnemigos.getSpeedMult() * EVENTO2_SPEED_MULT;
        ctrlEnemigos.setSpeedMult(nuevaVel);
    }

    private void entraEnemigoTeta() {
        if (efectoPollasActivo) return;

        efectoPollasActivo = true;

        ctrlEnemigos.setTiposDeEnemigos(LISTA_TETAS);
        ctrlEnemigos.setIntervaloDeAparicion(EVENTO_TETAS_SPAWN_RATE);

        // Programamos restaurar spawn normal
        if (restauraSpawnTask != null) restauraSpawnTask.cancel();
        restauraSpawnTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                restaurarSpawnNormal();
            }
        }, 3.5f);
    }

    private void restaurarSpawnNormal() {
        ctrlEnemigos.setTiposDeEnemigos(TIPOS_ENEMIGOS3);
        ctrlEnemigos.setIntervaloDeAparicion(EVENTO2_SPAWN_RATE);
    }

    private void spawnPrimerBoss() {
        ctrlEnemigos.spawnBossPollaAleatorio();
        for (Enemigo e : ctrlEnemigos.getEnemigos()) {
            if (e instanceof BossPolla bp) {
                bossRef = bp;
                break;
            }
        }
        GestorDeAudio.getInstance().cambiarMusica("fondo3");
    }

    private void spawnAlarma() {
        GestorDeAudio.getInstance().cambiarMusica("fondo4");
        ctrlEnemigos.setTiposDeEnemigos(LISTA_ALARMA);
        ctrlEnemigos.setIntervaloDeAparicion(EVENTO3_SPAWN_RATE);
        float nuevaVel = ctrlEnemigos.getSpeedMult() * EVENTO3_SPEED_MULT;
        ctrlEnemigos.setSpeedMult(nuevaVel);
        // Activamos mensajes de alarmas con scheduling
        MensajesData.getInstance().activarMensajesAlarmas(hud);
    }

    private void spawnExamen() {
        ctrlEnemigos.setTiposDeEnemigos(LISTA_EXAMEN);
        ctrlEnemigos.setIntervaloDeAparicion(EVENTO3_SPAWN_RATE);
    }

    private void spawnReglas() {
        ctrlEnemigos.setTiposDeEnemigos(LISTA_REGLA);
        ctrlEnemigos.setIntervaloDeAparicion(EVENTO3_SPAWN_RATE);
        ctrlEnemigos.setSpeedMult(ctrlEnemigos.getSpeedMult() * 0.35f);
    }

    private void spawnCalculadoras() {
        ctrlEnemigos.setTiposDeEnemigos(TIPOS_ENEMIGOS4);
        ctrlEnemigos.setIntervaloDeAparicion(EVENTO4_SPAWN_RATE);
    }

    private void spawnGrapadoras() {
        ctrlEnemigos.setTiposDeEnemigos(TIPOS_ENEMIGOS5);
        ctrlEnemigos.setIntervaloDeAparicion(EVENTO5_SPAWN_RATE);
    }

    private void spawnPerforadoras() {
        ctrlEnemigos.setTiposDeEnemigos(TIPOS_ENEMIGOS6);
        ctrlEnemigos.setIntervaloDeAparicion(EVENTO6_SPAWN_RATE);
    }

    public void dispose() {
        if (restauraSpawnTask != null) restauraSpawnTask.cancel();
        eventos.clear();
    }
}
