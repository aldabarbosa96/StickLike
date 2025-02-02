package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilTazo;
import com.sticklike.core.gameplay.controladores.ControladorProyectiles;
import static com.sticklike.core.utilidades.GestorConstantes.*;

public class AtaqueTazo {
    private float temporizador;
    private int nubesActivas;
    private float radio = RADIO_TAZOS_JUGADOR;
    private float globalAngle = 0f;
    private final float velocidadRotacion = VEL_ROTACION;

    public void actualizar(float delta, Jugador jugador) {
        temporizador += delta;
        // Actualizamos el ángulo global
        globalAngle += velocidadRotacion * delta;

        // Si se cumple el intervalo para generar daño y aún no se han creado todas las nubes generamos nube
        if (temporizador >= INTERVALO_DANYO_TAZOS && nubesActivas < NUM_TAZOS) {
            generarTazo(jugador);
            temporizador = 0;
        }
    }

    private void generarTazo(Jugador jugador) {
        if (nubesActivas >= NUM_TAZOS) return;

        ControladorProyectiles cp = jugador.getControladorProyectiles();

        // Calcula la separación en grados entre cada nube
        float separation = 360f / NUM_TAZOS;
        // El offset para cada nube es el número de nubes activas multiplicado por la separación
        float offsetAngle = separation * nubesActivas;

        cp.anyadirNuevoProyectil(new ProyectilTazo(jugador, this, offsetAngle, radio));
        nubesActivas++;
    }

    public void reducirNubesActivas() {
        if (nubesActivas > 0) nubesActivas--;
    }

    public float getGlobalAngle() {
        return globalAngle;
    }
}
