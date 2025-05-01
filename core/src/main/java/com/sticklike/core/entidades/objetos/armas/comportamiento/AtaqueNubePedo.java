package com.sticklike.core.entidades.objetos.armas.comportamiento;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.NubePedo;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Ataque Nube Pedo; genera una nube dañina de forma periódica y permite activar su versión triple.
 */

public class AtaqueNubePedo {
    private NubePedo nubePedo;
    private Jugador jugador;
    private float cooldownTimer = 0f;
    private boolean esTriple = false;


    public AtaqueNubePedo(Jugador jugador) {
        this.jugador = jugador;
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

    public NubePedo getNubePedo() {
        return nubePedo;
    }

    public boolean isEsTriple() {
        return esTriple;
    }

    public void setEsTriple(boolean esTriple) {
        this.esTriple = esTriple;
    }
}
