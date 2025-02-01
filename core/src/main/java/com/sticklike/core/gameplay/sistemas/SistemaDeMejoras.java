package com.sticklike.core.gameplay.sistemas;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaqueCalcetin;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaquePedo;
import com.sticklike.core.gameplay.progreso.Mejora;
import com.sticklike.core.MainGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase que gestiona la inicialización, creación y aplicación de las mejoras disponibles para el {@link Jugador}
 * Permite:
 * Definir todas las mejoras posibles (con su nombre, descripción y efecto)
 * Seleccionar un conjunto de upgrades aleatorias para mostrarlas al jugador
 * Aplicar la mejora elegida, ejecutando su efecto sobre el jugador
 */
public class SistemaDeMejoras {
    private final Jugador jugador;
    private final List<Mejora> todasLasMejoras;
    private final List<Mejora> mejorasMostradas;
    private final MainGame game;

    public SistemaDeMejoras(Jugador jugador, MainGame game) {
        this.jugador = jugador;
        this.game = game;
        this.todasLasMejoras = new ArrayList<>();
        this.mejorasMostradas = new ArrayList<>();
        inicializarMejoras();
    }

    private void inicializarMejoras() {
        todasLasMejoras.add(new Mejora("PIES VELOCES", "Aumenta la velocidad de movimiento un 13%", () -> jugador.aumentarVelocidad(0.13f), 5));
        todasLasMejoras.add(new Mejora("BRAZOS LARGOS", "Aumenta el rango de ataque un 20%", () -> jugador.aumentarRangoAtaque(0.20f), 5));
        todasLasMejoras.add(new Mejora("MANOS RÁPIDAS", "Aumenta la velocidad de ataque un 14%", () -> jugador.reducirIntervaloDisparo(0.125f), 5));
        todasLasMejoras.add(new Mejora("PUÑO DURO", "Aumenta el daño del Ataque Básico un 11%", () -> jugador.aumentarDanyo(1.11f), 5));
        todasLasMejoras.add(new Mejora("MULTI PROYECTIL", "Aumenta el número de Proyectiles en 1", () -> jugador.aumentarProyectilesPorDisparo(1), 5));
        todasLasMejoras.add(new Mejora("CALCETÍN ACARTONADO", "Desbloquea ataque calcetines lefados", () -> jugador.setCalcetinazo(new AtaqueCalcetin(jugador.getIntervaloDisparo() + 1.15f)), 1));
        todasLasMejoras.add(new Mejora("PEDOS GIRATORIOS", "Desbloquea ataque nubes pedorras", () -> jugador.setPedo(new AtaquePedo()), 1));
        todasLasMejoras.add(new Mejora("CORAZÓN GORDO", "Aumenta la salud máxima en 15 puntos", () -> {
            jugador.setVidaMax(jugador.getMaxVidaJugador()+15);jugador.setVidaJugador(jugador.getVidaJugador()+15);},10));
        // todo --> implementar nuevas mejoras de habilidades y stats restantes
    }

    public List<Mejora> generarOpcionesDeMejoraAleatorias(int numMejoras) {
        mejorasMostradas.clear();

        // filtramos las mejoras disponibles
        List<Mejora> mejorasDisponibles = new ArrayList<>();
        for (Mejora mejora : todasLasMejoras) {
            if (mejora.estaDisponible()) {
                mejorasDisponibles.add(mejora);
            }
        }

        // Mezclamos y cogemos las primeras disponibles
        Collections.shuffle(mejorasDisponibles);
        for (int i = 0; i < Math.min(numMejoras, mejorasDisponibles.size()); i++) {
            mejorasMostradas.add(mejorasDisponibles.get(i));
        }

        return new ArrayList<>(mejorasMostradas); // devolvemos una nueva copia cada vez
    }


    public void anyadirMejorasAlPopUp() {
        List<Mejora> options = generarOpcionesDeMejoraAleatorias(3);
        game.ventanaJuego.mostrarPopUpDeMejoras(options);
    }

    public void aplicarMejora(Mejora mejoraSeleccionada) {
        if (!mejorasMostradas.contains(mejoraSeleccionada)) { // No debería llegar nunca a este punto
            throw new IllegalArgumentException("La mejora seleccionada no es válida.");
        }
        if (!mejoraSeleccionada.estaDisponible()) {
            throw new IllegalStateException("La mejora seleccionada ya no está disponible.");
        }

        System.out.println("Mejora aplicada: " + mejoraSeleccionada.getNombreMejora());

        mejoraSeleccionada.apply();
        mejorasMostradas.clear();
    }

    public List<Mejora> getMejorasMostradas() {
        return mejorasMostradas;
    }
}
