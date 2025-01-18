package com.sticklike.core.gameplay.managers;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.gameplay.mejoras.Mejora;
import com.sticklike.core.MainGame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase que gestiona la inicialización, creación y aplicación de las mejoras
 * disponibles para el {@link Jugador}. Permite:
 *
 * Definir todas las mejoras posibles (con su nombre, descripción y efecto)
 * Seleccionar un conjunto de upgrades aleatorias para mostrarlas al jugador
 * Aplicar la mejora elegida, ejecutando su efecto sobre el jugador
 *
 */
public class ControladorMejoras {
    private final Jugador jugador;
    private final List<Mejora> todasLasMejoras;
    private final List<Mejora> mejorasMostradas;
    private final MainGame game;

    /**
     * Inicializa las listas de mejoras y define sus efectos
     *
     * @param jugador referencia al {@link Jugador}, necesario para aplicar los efectos
     * @param game    referencia a la clase principal {@link MainGame},
     *                que gestiona la comunicación con la pantalla de juego
     */
    public ControladorMejoras(Jugador jugador, MainGame game) {
        this.jugador = jugador;
        this.game = game;
        this.todasLasMejoras = new ArrayList<>();
        this.mejorasMostradas = new ArrayList<>();
        inicializarMejoras();
    }

    /**
     * Define todas las mejoras del juego y sus efectos sobre el jugador
     * Se añaden a la lista {@code todasLasMejoras}
     */
    private void inicializarMejoras() {
        todasLasMejoras.add(new Mejora(
            "PIES VELOCES", "Aumenta la velocidad de movimiento un 15%", () -> jugador.aumentarVelocidad(0.15f)));
        todasLasMejoras.add(new Mejora(
            "BRAZOS LARGOS",
            "Aumenta el rango de ataque un 10%",
            () -> jugador.aumentarRangoAtaque(0.10f)
        ));
        todasLasMejoras.add(new Mejora(
            "MANOS RÁPIDAS",
            "Reduce el intervalo de disparo un 9%",
            () -> jugador.reducirIntervaloDisparo(0.15f)
        ));
        todasLasMejoras.add(new Mejora(
            "PUÑO DURO",
            "Aumenta el daño del Ataque Básico un 4%",
            () -> jugador.aumentarDanyo(1.03f)
        ));
        todasLasMejoras.add(new Mejora(
            "MULTI PROYECTIL",
            "Aumenta el número de Proyectiles en 1",
            () -> jugador.aumentarProyectilesPorDisparo(1)
        ));
    }

    /**
     * Selecciona un conjunto aleatorio de upgrades de la lista total
     *
     * @param numMejoras número de mejoras que se generarán
     * @return la lista de upgrades seleccionadas
     */
    public List<Mejora> generarOpcionesDeMejora(int numMejoras) {
        mejorasMostradas.clear();
        // Mezclamos la lista completa
        Collections.shuffle(todasLasMejoras);
        // Tomamos las primeras {@code numMejoras} mejoras
        for (int i = 0; i < Math.min(numMejoras, todasLasMejoras.size()); i++) {
            mejorasMostradas.add(todasLasMejoras.get(i));
        }
        return new ArrayList<>(mejorasMostradas); // Devolvemos una copia para no exponer la lista interna
    }

    /**
     * Llamado cuando el Jugador sube de nivel
     * Genera 3 mejoras aleatorias y muestra el pop-up para que el Jugador escoja
     */
    public void anyadirMejorasAlPopUp() {
        List<Mejora> options = generarOpcionesDeMejora(3);
        game.ventanaJuego.mostrarPopUpDeMejoras(options);
    }

    /**
     * Aplica la mejora elegida por el jugador, ejecutando su efecto
     * Si la mejora no está en {@code mejorasMostradas}, lanzamos excepción
     *
     * @param mejoraSeleccionada la mejora seleccionada
     */
    public void aplicarMejora(Mejora mejoraSeleccionada) {
        if (!mejorasMostradas.contains(mejoraSeleccionada)) { // No debería llegar nunca a este punto
            throw new IllegalArgumentException("La mejora seleccionada no es válida.");
        }

        mejoraSeleccionada.apply();

        System.out.println("Mejora aplicada: " + mejoraSeleccionada.getNombreMejora());
        mejorasMostradas.clear();
    }
}
