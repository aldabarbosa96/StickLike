package com.sticklike.core.entidades.objetos.armas.jugador.comportamiento;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.jugador._02NubePedo;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Ataque Nube Pedo; genera una nube dañina de forma periódica y permite activar su versión triple.
 */

public class _02AtaqueNubePedo {
    private _02NubePedo a02NubePedo;
    private Jugador jugador;
    private float cooldownTimer = 0f;
    private boolean esTriple = false;


    public _02AtaqueNubePedo(Jugador jugador) {
        this.jugador = jugador;
        a02NubePedo = new _02NubePedo(jugador);
        jugador.getControladorProyectiles().anyadirNuevoProyectil(a02NubePedo);
    }

    public void procesarAtaque(float delta) {
        cooldownTimer += delta;
        if (cooldownTimer >= DELAY_ENTRE_PEDOS) {
            if (!a02NubePedo.isProyectilActivo()) {
                a02NubePedo = new _02NubePedo(jugador);
                jugador.getControladorProyectiles().anyadirNuevoProyectil(a02NubePedo);
                cooldownTimer = 0f;
            }
        }
    }

    public _02NubePedo getNubePedo() {
        return a02NubePedo;
    }

    public boolean isEsTriple() {
        return esTriple;
    }

    public void setEsTriple(boolean esTriple) {
        this.esTriple = esTriple;
    }
}
