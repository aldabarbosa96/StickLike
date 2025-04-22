package com.sticklike.core.gameplay.sistemas;

import com.badlogic.gdx.graphics.Texture;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.*;
import com.sticklike.core.gameplay.progreso.Mejora;
import com.sticklike.core.MainGame;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Gestiona el sistema de mejoras en el juego.
 * Permite seleccionar, aplicar y administrar mejoras globales y específicas de habilidades.
 */

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
        // STATS (no relacionada con una habilidad, idHabilidad = null)
        todasLasMejoras.add(new Mejora("¡PIES VELOCES!", "Aumenta velocidad de movimiento un 13%", () -> jugador.aumentarVelocidad(0.13f), 3, manager.get(ICONO_VEL_MOV, Texture.class), null));
        todasLasMejoras.add(new Mejora("¡BRAZOS LARGOS!", "Aumenta rango de ataque un 100%", () -> jugador.aumentarRangoAtaque(1f), 1, manager.get(ICONO_RANGO, Texture.class), null));
        todasLasMejoras.add(new Mejora("¡MANOS RÁPIDAS!", "Aumenta velocidad de ataque un 28%", () -> jugador.reducirIntervaloDisparo(0.22f), 5, manager.get(ICONO_VEL_ATAQUE, Texture.class), null));
        todasLasMejoras.add(new Mejora("¡PUÑO DURO!", "Aumenta daño del ataque básico un 39%", () -> jugador.aumentarDanyo(1.39f), 5, manager.get(ICONO_FUERZA, Texture.class), null));
        todasLasMejoras.add(new Mejora("¡MULTI PROYECTIL!", "Aumenta número de proyectiles en 2", () -> jugador.aumentarProyectilesPorDisparo(2), 5, manager.get(ICONO_PROYECTILES, Texture.class), null));
        todasLasMejoras.add(new Mejora("¡CHUTE VITAL!", "Aumenta regeneración de vida un 5%", () -> jugador.aumentarRegVida(0.005f), 5, manager.get(ICONO_REGENERACION, Texture.class), null));
        todasLasMejoras.add(new Mejora("¡CRITICÓN!", "Aumenta probabilidad de crítico un 15%", () -> jugador.aumentarCritico(0.15f), 3, manager.get(ICONO_CRITICO, Texture.class), null));
        todasLasMejoras.add(new Mejora("¡PECHO FIRME!", "Aumenta porcentaje de resistencia un 20%", () -> jugador.aumentarResistencia(0.2f), 3, manager.get(ICONO_RESISTENCIA, Texture.class), null));
        todasLasMejoras.add(new Mejora("¡PODER PODEROSO!", "Aumenta porcentaje de poder un 50%", () -> jugador.aumentarPoderJugador(1.5f), 5, manager.get(ICONO_PODER, Texture.class), null));
        todasLasMejoras.add(new Mejora("¡CORAZÓN GORDO!", "Aumenta la salud máxima en 15 puntos", () -> {
            jugador.setVidaMax(Jugador.getMaxVidaJugador() + 15);
            jugador.setVidaJugador(Jugador.getVidaJugador() + 15);
        }, 5, manager.get(ICONO_VIDA, Texture.class), null));

        // HABILIDADES (se especifica el id de la habilidad; no contienen "_" en el id)
        todasLasMejoras.add(new Mejora("¡CALCETÍN ACARTONADO!", "Lanza calcetines lefados en todas direcciones", () -> jugador.setCalcetinazo(new AtaqueCalcetin()), 1, manager.get(ARMA_CALCETIN, Texture.class), "CALCETIN"));
        todasLasMejoras.add(new Mejora("¡GIROTAZOS!", "Invoca un tazo giratorio rotativo", () -> jugador.setTazo(new AtaqueTazo()), 1, manager.get(ARMA_TAZOS, Texture.class), "TAZO"));
        todasLasMejoras.add(new Mejora("¡PEDO TÓXICO!", "Emana pedo tóxico repelente", () -> jugador.setAtaqueNubePedo(new AtaqueNubePedo(jugador)), 1, manager.get(ARMA_NUBE_PEDO_HUD, Texture.class), "PEDO"));
        todasLasMejoras.add(new Mejora("¡PAPEL DEL CULO!", "Lanza rollos de papel de váter como granadas", () -> jugador.setPapelCulo(new AtaquePapelCulo()), 1, manager.get(ARMA_PAPELCULO, Texture.class), "PAPEL"));
        todasLasMejoras.add(new Mejora("LLUVIA DE MOCOS", "Llueven mocardones con impacto mucoso", () -> jugador.setAtaqueMocos(new AtaqueMocos()), 1, manager.get(ARMA_MOCO, Texture.class), "MOCO"));
        todasLasMejoras.add(new Mejora("BOLI BICAZO", "Lanzas bolis Bic como navajas en tu dirección", () -> jugador.setAtaqueBoliBic(new AtaqueBoliBic()), 1, manager.get(ARMA_BOLIBIC, Texture.class), "BOLI"));
        todasLasMejoras.add(new Mejora("SABLE DILDO", "Bates un dildo rosa a derecha e izquierda", () -> jugador.setAtaqueDildo(new AtaqueDildo()), 1, manager.get(ARMA_DILDO, Texture.class), "DILDO"));

        // Upgrades específicos para las habilidades
        // Calcetín
        todasLasMejoras.add(new Mejora("¡CALCETÍN: ACARTONADO MÁX!", "Aumenta el daño de los calcetines un 20%", () -> {
            jugador.getAtaqueCalcetin().aumentarDamage(0.2f);
        }, 5, manager.get(ARMA_CALCETIN, Texture.class), "CALCETIN_damage"));
        todasLasMejoras.add(new Mejora("¡CALCETÍN: MULTICALCETIN!", "Aumenta el número de calcetines +1", () -> {
            jugador.getAtaqueCalcetin().incrementarNumeroProyectiles(1);
        }, 4, manager.get(ARMA_CALCETIN, Texture.class), "CALCETIN_multi"));
        todasLasMejoras.add(new Mejora("¡CALCETÍN: PRECOZ!", "Aumenta velocidad de lanzamiento un 25%", () -> {
            jugador.getAtaqueCalcetin().aumentarVelocidadDisparo(0.25f);
        }, 3, manager.get(ARMA_CALCETIN, Texture.class), "CALCETIN_speed"));

        // Tazo
        todasLasMejoras.add(new Mejora("¡TAZO: ROTATAZO!", "Aumenta la velocidad de rotación del tazo", () -> {
            jugador.getAtaqueTazo().aumentarVelocidadTazos(1.5f);
        }, 3, manager.get(ARMA_TAZOS, Texture.class), "TAZO_rotacion"));
        todasLasMejoras.add(new Mejora("¡TAZO: MULTITETAZO!", "Aumenta el número de tazos +1", () -> {
            jugador.getAtaqueTazo().aumentarNumTazos(1);
        }, 3, manager.get(ARMA_TAZOS, Texture.class), "TAZO_proyectil"));
        todasLasMejoras.add(new Mejora("¡TAZO: TEMPOTAZO!", "Aumenta el tiempo que dura la habilidad un 50%", () -> {
            jugador.getAtaqueTazo().aumentarDuracionActivaTazos(4.25f);
        }, 3, manager.get(ARMA_TAZOS, Texture.class), "TAZO_duracion"));

        // NubePedo
        todasLasMejoras.add(new Mejora("¡PEDO: ÁREA PESTOSA!", "Aumenta el area de efecto del pedo un 100%", () -> {
            jugador.getAtaqueNubePedo().getNubePedo().setEscalaMax(2f);
        }, 2, manager.get(ARMA_NUBE_PEDO_HUD, Texture.class), "PEDO_maxArea"));
        todasLasMejoras.add(new Mejora("¡PEDO: EMPUJE PEDORRO!", "Aumenta el empuje del pedo un 30%", () -> {
            jugador.getAtaqueNubePedo().getNubePedo().setMaxKnockBack(1.3f);
        }, 2, manager.get(ARMA_NUBE_PEDO_HUD, Texture.class), "PEDO_maxEmpuje"));
        todasLasMejoras.add(new Mejora("¡PEDO: TRIIIPPPLEEE!", "Añade una vibración pedorra extra", () -> {
            jugador.getAtaqueNubePedo().setEsTriple(true);
        }, 1, manager.get(ARMA_NUBE_PEDO_HUD, Texture.class), "PEDO_triple"));

        // PapelCulo
        todasLasMejoras.add(new Mejora("¡PAPEL: DOBLE CARA!", "Lanzas 2 rollos de papel simultáneos", () -> {
            jugador.getAtaquePapelCulo().setMejoraAmbosLados(true);
        }, 1, manager.get(ARMA_PAPELCULO, Texture.class), "PAPEL_doble"));
        todasLasMejoras.add(new Mejora("¡PAPEL: RÁPIDO!", "Aumenta la velocidad de lanzamiento del rollo", () -> {
            jugador.getAtaquePapelCulo().aumentarVelocidadDisparo(0.33f);
        }, 3, manager.get(ARMA_PAPELCULO, Texture.class), "PAPEL_rápido"));
        todasLasMejoras.add(new Mejora("¡PAPEL: FRAGMENTACIÓN!", "Se fragmenta en rollos más pequeños al impactar", () -> {
            jugador.getAtaquePapelCulo().setFragmentado(true);
        }, 1, manager.get(ARMA_PAPELCULO, Texture.class), "PAPEL_frag"));

        // LluviaMocos
        todasLasMejoras.add(new Mejora("¡MOCOS: TORMENTA!", "Aumenta la cadencia de la lluvia", () -> {
            jugador.getAtaqueMocos().tormentaMucosa(0.5f);
        }, 3, manager.get(ARMA_MOCO, Texture.class), "MOCO_tormenta"));
        todasLasMejoras.add(new Mejora("¡MOCOS: REBOTE!", "Los mocos rebotan al impactar en el suelo", () -> {
            jugador.getAtaqueMocos().activarReboteMucoso();
        }, 1, manager.get(ARMA_MOCO, Texture.class), "MOCO_rebote")); // todo --> permite gestionar internamente más rebotes
        todasLasMejoras.add(new Mejora("¡MOCOS: SANGRIENTOS!", "Mocos de sangre que aplican un 250% de daño", () -> {
            jugador.getAtaqueMocos().mocosConSangre();
        }, 1, manager.get(ARMA_MOCO, Texture.class), "MOCO_sangre"));

        // BoliBic
        todasLasMejoras.add(new Mejora("¡BOLI: RICOCHET!", "Los bolis rebotan una vez al impactar", () ->
            jugador.getAtaqueBoliBic().activarRicochet(),
            1, manager.get(ARMA_BOLIBIC, Texture.class), "BOLI_ricochet"));
        todasLasMejoras.add(new Mejora("¡BOLI: DOBLE TAP!", "Dispara 2 bolis seguidos", () ->
            jugador.getAtaqueBoliBic().mejorarDoubleTap(),
            3, manager.get(ARMA_BOLIBIC, Texture.class), "BOLI_doble"));
        todasLasMejoras.add(new Mejora("¡BOLI: ABANICO!", "Añade +1 boli y dispara en abanico", () ->
            jugador.getAtaqueBoliBic().activarSplitShot(),
            1, manager.get(ARMA_BOLIBIC, Texture.class), "BOLI_split"));

        // LátigoDildo
        todasLasMejoras.add(new Mejora("DILDO: HALO", "Proyecta una onda de energía perforante", () ->
            jugador.getAtaqueDildo().activarHaloEnergia(),
            1, manager.get(ARMA_DILDO, Texture.class), "DILDO_halo"));
        todasLasMejoras.add(new Mejora("DILDO: DOBLE", "Repite el golpe una vez más", () ->
            jugador.getAtaqueDildo().activarGolpeDoble(),
            2, manager.get(ARMA_DILDO, Texture.class), "DILDO_doble"));
        todasLasMejoras.add(new Mejora("DILDO: VELOZ", "Multiplica x2 la velocidad de golpeo", () ->
            jugador.getAtaqueDildo().mejorarVelocidadSwing(),
            1, manager.get(ARMA_DILDO, Texture.class), "DILDO_veloz"));
    }

    public List<Mejora> generarOpcionesDeMejoraAleatorias(int numMejoras) {
        mejorasMostradas.clear();
        List<Mejora> mejorasDisponibles = new ArrayList<>();

        for (Mejora mejora : todasLasMejoras) {
            if (!mejora.estaDisponible()) continue;

            String idHabilidad = mejora.getIdHabilidad();
            if (idHabilidad != null) {
                if (idHabilidad.contains("_")) {
                    // Upgrade específico: se muestra solo si la habilidad base (antes del "_") está activa
                    String baseId = idHabilidad.split("_")[0];
                    if (habilidadesActivasIds.contains(baseId)) {
                        mejorasDisponibles.add(mejora);
                    }
                } else {
                    // Es una habilidad base: se añade sin restricciones
                    mejorasDisponibles.add(mejora);
                }
            } else {
                // Es una mejora global
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

        // marcamos las mejoras de stats para aplicar el cambio de color al escogerlas
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
        // Sólo se añaden al HUD las mejoras de tipo HABILIDAD ("HAB")
        // Después: sólo metemos la habilidad base (la que NO tiene “_” en el Id)
        if (mejoraSeleccionada.getIcono() != null
            && mejoraSeleccionada.getTipoMejora().equals("HAB")
            && mejoraSeleccionada.getIdHabilidad() != null
            && !mejoraSeleccionada.getIdHabilidad().contains("_")) {

            habilidadesActivasIds.add(mejoraSeleccionada.getIdHabilidad());
            habilidadesActivas.add(mejoraSeleccionada);
        }

    }

    public List<Mejora> getMejorasMostradas() {
        return mejorasMostradas;
    }

    public List<Mejora> getHabilidadesActivas() {
        return habilidadesActivas;
    }
}
