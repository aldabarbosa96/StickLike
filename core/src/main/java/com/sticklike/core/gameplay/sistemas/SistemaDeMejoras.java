package com.sticklike.core.gameplay.sistemas;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaqueCalcetin;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaqueNubePedo;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaqueTazo;
import com.sticklike.core.gameplay.progreso.Mejora;
import com.sticklike.core.MainGame;

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class SistemaDeMejoras {
    private final Jugador jugador;
    private final List<Mejora> todasLasMejoras;
    private final List<Mejora> mejorasMostradas;
    private final List<Mejora> habilidadesActivas = new ArrayList<>();
    private final Set<String> habilidadesActivasIds = new HashSet<>();
    private final MainGame game;

    public SistemaDeMejoras(Jugador jugador, MainGame game) {
        this.jugador = jugador;
        this.game = game;
        this.todasLasMejoras = new ArrayList<>();
        this.mejorasMostradas = new ArrayList<>();
        inicializarMejoras();
    }

    private void inicializarMejoras() {
        // Mejora global (no relacionada con una habilidad, idHabilidad = null)
        todasLasMejoras.add(new Mejora("¡PIES VELOCES!", "Aumenta velocidad de movimiento un 13%",() -> jugador.aumentarVelocidad(0.13f), 5, null, null));
        todasLasMejoras.add(new Mejora("¡BRAZOS LARGOS!", "Aumenta rango de ataque un 35%",() -> jugador.aumentarRangoAtaque(0.35f), 5, null, null));
        todasLasMejoras.add(new Mejora("¡MANOS RÁPIDAS!", "Aumenta velocidad de ataque un 16%",() -> jugador.reducirIntervaloDisparo(0.16f), 5, null, null));
        todasLasMejoras.add(new Mejora("¡PUÑO DURO!", "Aumenta daño del ataque básico un 19%",() -> jugador.aumentarDanyo(1.19f), 5, null, null));
        todasLasMejoras.add(new Mejora("¡MULTI PROYECTIL!", "Aumenta número de proyectiles en 1",() -> jugador.aumentarProyectilesPorDisparo(1), 5, null, null));
        todasLasMejoras.add(new Mejora("¡CHUTE VITAL!", "Aumenta regeneración de vida un 5%",() -> jugador.aumentarRegVida(0.005f), 5, null, null));
        todasLasMejoras.add(new Mejora("¡CRITICÓN!", "Aumenta probabilidad de crítico un 15%",() -> jugador.aumentarCritico(0.15f), 5, null, null));
        todasLasMejoras.add(new Mejora("¡PECHO FIRME!", "Aumenta porcentaje de resistencia un 20%",() -> jugador.aumentarResistencia(0.2f), 5, null, null));
        todasLasMejoras.add(new Mejora("¡PODER PODEROSO!", "Aumenta porcentaje de poder un 50%",() -> jugador.aumentarPoderJugador(1.5f), 5, null, null));
        todasLasMejoras.add(new Mejora("¡CORAZÓN GORDO!", "Aumenta la salud máxima en 15 puntos", () -> {jugador.setVidaMax(jugador.getMaxVidaJugador() + 15);jugador.setVidaJugador(jugador.getVidaJugador() + 15);}, 10, null, null));

        // Mejoras base para habilidades (se especifica el id de la habilidad; no contienen "_" en el id)
        todasLasMejoras.add(new Mejora("¡CALCETÍN ACARTONADO!",
            "Lanza calcetines lefados en todas direcciones", () -> jugador.setCalcetinazo(new AtaqueCalcetin()), 1, armaCalcetin, "CALCETIN"));
        todasLasMejoras.add(new Mejora("¡GIROTAZOS!",
            "Invoca un tazo giratorio rotativo", () -> jugador.setTazo(new AtaqueTazo()), 1, armaTazos, "TAZO"));
        todasLasMejoras.add(new Mejora("¡PEDO TÓXICO!",
            "Emana pedo tóxico repelente", () -> jugador.setAtaqueNubePedo(new AtaqueNubePedo(jugador)), 1, armaNubePedo, "PEDO"));

        // Upgrades específicos para las habilidades
        // Calcetín
        todasLasMejoras.add(new Mejora("¡CALCETÍN: DAÑO MAX!",
            "Aumenta el daño de los calcetines un 20%", () -> {jugador.getAtaqueCalcetin().aumentarDamage(0.2f);}, 5, null, "CALCETIN_damage"));
        todasLasMejoras.add(new Mejora("¡CALCETÍN: MULTIPROYECTIL!",
            "Aumenta el número de calcetines +1", () -> {jugador.getAtaqueCalcetin().incrementarNumeroProyectiles(1);}, 4, null, "CALCETIN_multi"));
        todasLasMejoras.add(new Mejora("¡CALCETÍN: VELOZ!",
            "Aumenta velocidad de lanzamiento un 25%", () -> {jugador.getAtaqueCalcetin().aumentarVelocidadDisparo(0.25f);}, 3, null, "CALCETIN_speed"));
        // Calcetín-ULTIMATE todo --> balancear un poco la ulti del calcetín y buscar forma de optimizarla...
        /*todasLasMejoras.add(new Mejora("CALCETÍN: ULTIMATE",
            "?????", ()-> {jugador.getAtaqueCalcetin().ultimateCALCETIN(1.25f);},1,null,"CALCETIN_vel"));*/

        // Tazo
        todasLasMejoras.add(new Mejora("¡TAZO: + ROTATIVO!",
            "Aumenta la velocidad de rotación del tazo", () -> {jugador.getAtaqueTazo().aumentarVelocidadTazos(1.5f);}, 3, null, "TAZO_rotacion"));
        todasLasMejoras.add(new Mejora("¡TAZO: MULTIPROYECTIL!",
            "Aumenta el número de tazos +1", ()-> {jugador.getAtaqueTazo().aumentarNumTazos(1);},3,null,"TAZO_proyectil"));
        todasLasMejoras.add(new Mejora("¡TAZO: + DURACIÓN!",
            "Aumenta el tiempo que dura la habilidad un 50%", ()-> {jugador.getAtaqueTazo().aumentarDuracionActivaTazos(4.25f);},3,null,"TAZO_duracion"));

        // NubePedo
        todasLasMejoras.add(new Mejora("¡PEDO: ÁREA PESTOSA!",
            "Aumenta el area de efecto del pedo un 100%", () -> {jugador.getAtaqueNubePedo().getNubePedo().setEscalaMax(2f);}, 2, null, "PEDO_maxArea"));
        todasLasMejoras.add(new Mejora("¡PEDO: ÁREA PEDORRA!",
            "Aumenta el empuje del pedo un 30%", () -> {jugador.getAtaqueNubePedo().getNubePedo().setMaxKnockBack(1.3f);}, 2, null, "PEDO_maxEmpuje"));
        todasLasMejoras.add(new Mejora("¡PEDO: TRIIIPLEEE!",
            "Añade una vibración pedorra extra", () -> {jugador.getAtaqueNubePedo().setEsTriple(true);}, 1, null, "PEDO_triple"));


    }

    public List<Mejora> generarOpcionesDeMejoraAleatorias(int numMejoras) {
        mejorasMostradas.clear();
        List<Mejora> mejorasDisponibles = new ArrayList<>();

        for (Mejora mejora : todasLasMejoras) {
            if (!mejora.estaDisponible()) continue;

            String idHabilidad = mejora.getIdHabilidad();
            if (idHabilidad != null) {
                if (idHabilidad.contains("_")) {
                    // Upgrade específico: se muestra solo si la habilidad base (antes del "_") está activa.
                    String baseId = idHabilidad.split("_")[0];
                    if (habilidadesActivasIds.contains(baseId)) {
                        mejorasDisponibles.add(mejora);
                    }
                } else {
                    // Es una habilidad base: se añade sin restricciones.
                    mejorasDisponibles.add(mejora);
                }
            } else {
                // Es una mejora global.
                mejorasDisponibles.add(mejora);
            }
        }

        Collections.shuffle(mejorasDisponibles);
        for (int i = 0; i < Math.min(numMejoras, mejorasDisponibles.size()); i++) {
            mejorasMostradas.add(mejorasDisponibles.get(i));
        }

        return new ArrayList<>(mejorasMostradas);
    }

    public void anyadirMejorasAlPopUp() {
        List<Mejora> options = generarOpcionesDeMejoraAleatorias(3);
        game.ventanaJuego1.mostrarPopUpDeMejoras(options);
    }

    public void aplicarMejora(Mejora mejoraSeleccionada) {
        if (!mejorasMostradas.contains(mejoraSeleccionada)) {
            throw new IllegalArgumentException("La mejora seleccionada no es válida.");
        }
        if (!mejoraSeleccionada.estaDisponible()) {
            throw new IllegalStateException("La mejora seleccionada ya no está disponible.");
        }

        System.out.println("Mejora aplicada: " + mejoraSeleccionada.getNombreMejora());

        // Se aplica la mejora (se decrementa el contador de usos)
        mejoraSeleccionada.apply();

        // marcamos las mejoras para aplicar el cambio de color al escogerlas
        if (mejoraSeleccionada.getNombreMejora().equals("¡PIES VELOCES!"))
            game.ventanaJuego1.getRenderHUDComponents().marcarStatComoMejorado(VEL_MOV);
        if (mejoraSeleccionada.getNombreMejora().equals("¡BRAZOS LARGOS!"))
            game.ventanaJuego1.getRenderHUDComponents().marcarStatComoMejorado(RANGO);
        if (mejoraSeleccionada.getNombreMejora().equals("¡MANOS RÁPIDAS!"))
            game.ventanaJuego1.getRenderHUDComponents().marcarStatComoMejorado(VEL_ATAQUE);
        if (mejoraSeleccionada.getNombreMejora().equals("¡PUÑO DURO!"))
            game.ventanaJuego1.getRenderHUDComponents().marcarStatComoMejorado(FUERZA);
        if (mejoraSeleccionada.getNombreMejora().equals("¡MULTI PROYECTIL!"))
            game.ventanaJuego1.getRenderHUDComponents().marcarStatComoMejorado(NUM_PROYECTILES);
        if (mejoraSeleccionada.getNombreMejora().equals("¡CHUTE VITAL!"))
            game.ventanaJuego1.getRenderHUDComponents().marcarStatComoMejorado(REG_VIDA);
        if (mejoraSeleccionada.getNombreMejora().equals("¡PECHO FIRME!"))
            game.ventanaJuego1.getRenderHUDComponents().marcarStatComoMejorado(RESIST);
        if (mejoraSeleccionada.getNombreMejora().equals("¡CRITICÓN!"))
            game.ventanaJuego1.getRenderHUDComponents().marcarStatComoMejorado(CRITIC);
        if (mejoraSeleccionada.getNombreMejora().equals("¡PODER PODEROSO!"))
            game.ventanaJuego1.getRenderHUDComponents().marcarStatComoMejorado(PODER);
        if (mejoraSeleccionada.getNombreMejora().equals("¡CRITICÓN!"))
            game.ventanaJuego1.getRenderHUDComponents().marcarStatComoMejorado(CRITIC);
        if (mejoraSeleccionada.getNombreMejora().equals("¡CORAZÓN GORDO!"))
            game.ventanaJuego1.getRenderHUDComponents().marcarStatComoMejorado(VIDA_MAX);

        // Si la mejora aplicada tiene ícono, se considera que es una habilidad o upgrade visual.
        // Si se trata de una habilidad base (su id no contiene "_"), se registra su id para habilitar futuros upgrades.
        if (mejoraSeleccionada.getIcono() != null) {
            if (mejoraSeleccionada.getIdHabilidad() != null && !mejoraSeleccionada.getIdHabilidad().contains("_")) {
                habilidadesActivasIds.add(mejoraSeleccionada.getIdHabilidad());
            }
            habilidadesActivas.add(mejoraSeleccionada);
        }

        mejorasMostradas.clear();
    }

    public List<Mejora> getMejorasMostradas() {
        return mejorasMostradas;
    }

    public List<Mejora> getHabilidadesActivas() {
        return habilidadesActivas;
    }
}
