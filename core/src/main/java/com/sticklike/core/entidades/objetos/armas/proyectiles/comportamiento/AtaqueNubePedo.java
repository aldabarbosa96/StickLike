package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.NubePedo;

public class AtaqueNubePedo {
    private NubePedo nubePedo;
    private Jugador jugador;

    public AtaqueNubePedo(Jugador jugador) {
        this.jugador = jugador;
        nubePedo = new NubePedo(jugador);
        jugador.getControladorProyectiles().anyadirNuevoProyectil(nubePedo);
    }

    public void procesarAtaque(float delta) {
        if (!nubePedo.isProyectilActivo()) {
            nubePedo = new NubePedo(jugador);
            jugador.getControladorProyectiles().anyadirNuevoProyectil(nubePedo);
        }
    }
}
