package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.utilidades.GestorDeAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilTazo;
import com.sticklike.core.gameplay.controladores.ControladorProyectiles;

import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Gestiona ataque Tazo; genera tazos orbitando alrededor del jugador, dañando enemigos y aumentando en número, velocidad y duración con mejoras.
 */

public class AtaqueTazo {
    private float temporizador;
    private int tazosActivos;
    private float radio = RADIO_TAZOS_JUGADOR;
    private float globalAngle = 0f;
    private float velocidadRotacion = VEL_ROTACION;
    private float incrementoNumTazos = 0;
    private float incrementoPendiente = 0; // Variable para el incremento en espera
    private float duracionActivaTazo = 8.5f;


    public void actualizar(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        temporizador += delta;
        globalAngle += velocidadRotacion * delta;

        // Aplicar el incremento de tazos solo cuando el temporizador se reinicia
        if (temporizador >= INTERVALO_DANYO_TAZOS) {
            if (incrementoPendiente > 0) {
                incrementoNumTazos += incrementoPendiente;
                incrementoPendiente = 0; // Reseteamos la variable
            }

            // Generamos un tazo solo si hay espacio
            if (tazosActivos < NUM_TAZOS + incrementoNumTazos) {
                generarTazo(jugador, gestorDeAudio);
            }

            temporizador = 0;
        }
    }

    private void generarTazo(Jugador jugador, GestorDeAudio gestorDeAudio) {
        if (tazosActivos >= NUM_TAZOS + incrementoNumTazos) return;

        ControladorProyectiles cp = jugador.getControladorProyectiles();
        float separation = 360f / (NUM_TAZOS + incrementoNumTazos);

        // Obtener el último tazo activo para copiar su fase y temporizador
        ProyectilTazo referenciaTazo = cp.obtenerUltimoProyectilTazo();
        ProyectilTazo.Phase faseInicial = referenciaTazo != null ? referenciaTazo.getPhase() : ProyectilTazo.Phase.GROWING;
        float phaseTimerInicial = referenciaTazo != null ? referenciaTazo.getPhaseTimer() : 0;

        // Reajustar los ángulos de todos los tazos activos antes de generar el nuevo
        for (int i = 0; i < tazosActivos; i++) {
            ProyectilTazo tazo = cp.obtenerProyectilPorIndice(i);
            if (tazo != null) {
                tazo.setOffsetAngle(separation * i);
            }
        }

        float offsetAngle = separation * tazosActivos;

        // Crear el nuevo tazo y asignarle la fase y tiempo del tazo de referencia
        ProyectilTazo nuevoTazo = new ProyectilTazo(jugador, this, offsetAngle, radio, gestorDeAudio);
        nuevoTazo.setPhase(faseInicial, phaseTimerInicial);
        nuevoTazo.setActiveDuration(duracionActivaTazo);
        cp.anyadirNuevoProyectil(nuevoTazo);

        tazosActivos++;
    }

    public void aumentarVelocidadTazos(float incremento) {
        velocidadRotacion *= incremento;
    }

    public void reducirTazosActivos() {
        if (tazosActivos > 0) tazosActivos--;
    }

    public void aumentarNumTazos(float incremento) {
        incrementoPendiente += incremento;
    }

    public void aumentarDuracionActivaTazos(float incremento) {
        this.duracionActivaTazo += incremento;
    }


    public float getGlobalAngle() {
        return globalAngle;
    }

    public float getDuracionActivaTazo() {
        return duracionActivaTazo;
    }
}
