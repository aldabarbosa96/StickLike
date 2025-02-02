package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.NubePedo;

import static com.sticklike.core.utilidades.GestorConstantes.*;

public class AtaqueNubePedo {
    private NubePedo nubePedo;
    private Jugador jugador;
    private float cooldownTimer = 0f;


    public AtaqueNubePedo(Jugador jugador) {
        this.jugador = jugador;
        cooldownTimer = 0f;
        nubePedo = new NubePedo(jugador);
        jugador.getControladorProyectiles().anyadirNuevoProyectil(nubePedo);
    }

    public void procesarAtaque(float delta) {
        cooldownTimer += delta;
        if (cooldownTimer >= DELAY_ENTRE_PEDOS) {
            if (!nubePedo.isProyectilActivo()) {
                nubePedo = new NubePedo(jugador);
                jugador.getControladorProyectiles().anyadirNuevoProyectil(nubePedo);
                cooldownTimer = 0f;
            }
        }
    }
}
