package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilPedo;
import com.sticklike.core.gameplay.controladores.ControladorProyectiles;
import static com.sticklike.core.utilidades.GestorConstantes.*;

public class AtaquePedo {
    private float temporizador;
    private int nubesActivas;
    private float radio = 40f;
    private float globalAngle = 0f;
    private final float velocidadRotacion = 100f;

    public void actualizar(float delta, Jugador jugador) {
        temporizador += delta;
        // Actualizamos el ángulo global
        globalAngle += velocidadRotacion * delta;

        // Si se cumple el intervalo para generar daño y aun no se han creado todas las nubes generamos nube
        if (temporizador >= INTERVALO_DANYO_NUBE && nubesActivas < MAX_NUBES_PEDO) {
            generarNube(jugador);
            temporizador = 0;
        }
    }

    private void generarNube(Jugador jugador) {
        if (nubesActivas >= MAX_NUBES_PEDO) return;

        ControladorProyectiles cp = jugador.getControladorProyectiles();

        // Calcula la separación en grados entre cada nube
        float separation = 360f / MAX_NUBES_PEDO;
        // El offset para cada nube es el número de nubes activas multiplicado por la separación
        float offsetAngle = separation * nubesActivas;

        cp.anyadirNuevoProyectil(new ProyectilPedo(jugador, this, offsetAngle, radio));
        nubesActivas++;
    }

    public void reducirNubesActivas() {
        if (nubesActivas > 0) nubesActivas--;
    }

    public float getGlobalAngle() {
        return globalAngle;
    }
}
