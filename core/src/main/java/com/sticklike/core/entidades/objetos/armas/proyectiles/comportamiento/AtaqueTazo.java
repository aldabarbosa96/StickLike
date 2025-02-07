package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.utilidades.GestorDeAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilTazo;
import com.sticklike.core.gameplay.controladores.ControladorProyectiles;
import static com.sticklike.core.utilidades.GestorConstantes.*;

public class AtaqueTazo {
    private float temporizador;
    private int tazosActivos;
    private float radio = RADIO_TAZOS_JUGADOR;
    private float globalAngle = 0f;
    private float velocidadRotacion = VEL_ROTACION;

    public void actualizar(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        temporizador += delta;
        // Actualizamos el ángulo global
        globalAngle += velocidadRotacion * delta;

        // Si se cumple el intervalo para generar daño y aún no se han creado todos los tazos generamos tazo
        if (temporizador >= INTERVALO_DANYO_TAZOS && tazosActivos < NUM_TAZOS) {
            generarTazo(jugador, gestorDeAudio);
            temporizador = 0;
        }
    }

    private void generarTazo(Jugador jugador, GestorDeAudio gestorDeAudio) {
        if (tazosActivos >= NUM_TAZOS) return;

        ControladorProyectiles cp = jugador.getControladorProyectiles();

        // Calcula la separación en grados entre cada nube
        float separation = 360f / NUM_TAZOS;
        // El offset para cada nube es el número de nubes activas multiplicado por la separación
        float offsetAngle = separation * tazosActivos;

        cp.anyadirNuevoProyectil(new ProyectilTazo(jugador, this, offsetAngle, radio, gestorDeAudio));
        tazosActivos++;
    }

    public void aumentarVelocidadTazos(float incremento){
        velocidadRotacion *= incremento;
    }

    public void reducirTazosActivos() {
        if (tazosActivos > 0) tazosActivos--;
    }

    public float getGlobalAngle() {
        return globalAngle;
    }
}
