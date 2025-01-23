package com.sticklike.core.gameplay.sistemas;

import com.sticklike.core.gameplay.eventos.Evento;
import com.sticklike.core.gameplay.managers.ControladorEnemigos;
import com.sticklike.core.ui.RenderHUDComponents;

import java.util.PriorityQueue;

public class SistemaDeEventos {
    private PriorityQueue<Evento> eventos; // Cola de prioridad para eventos
    private RenderHUDComponents renderHUDComponents; // Lo necesitaré en un futuro para gestionar algún evento basándome en el tiempo
    private ControladorEnemigos controladorEnemigos;
    private SistemaDeNiveles sistemaDeNiveles;

    public SistemaDeEventos(RenderHUDComponents renderHUDComponents, ControladorEnemigos controladorEnemigos, SistemaDeNiveles sistemaDeNiveles) {
        this.eventos = new PriorityQueue<>();
        this.renderHUDComponents = renderHUDComponents;
        this.controladorEnemigos = controladorEnemigos;
        this.sistemaDeNiveles = sistemaDeNiveles;
        inicializarEventos();
    }

    private void inicializarEventos() {
        eventos.add(new Evento("Aumenta nº enemigos", sistemaDeNiveles,
            () -> eventoAumentaEnemigos1(), 3));

        eventos.add(new Evento("Aumenta nº enemigos2", sistemaDeNiveles,
            () -> eventoAumentaEnemigos2(), 5));

        // todo --> gestionar más eventos próximamente
    }

    private void eventoAumentaEnemigos1() {
        controladorEnemigos.setIntervaloDeAparicion(0.6f);
        System.out.println("¡Se ha reducido el intervalo de aparición de enemigos!");
        System.out.println("Aparición cada: " + controladorEnemigos.getIntervaloDeAparicion());
    }

    private void eventoAumentaEnemigos2() {
        controladorEnemigos.setIntervaloDeAparicion(0.4f);
        System.out.println("¡Se ha reducido el intervalo de aparición de enemigos!");
        System.out.println("Aparición cada: " + controladorEnemigos.getIntervaloDeAparicion());
    }

    public void actualizar() {
        if (!eventos.isEmpty()) {
            // No hacemos poll(), hacemos "peek" (ver el primer evento sin sacarlo)
            Evento siguiente = eventos.peek();
            if (sistemaDeNiveles.getNivelActual() >= siguiente.getNivelRequerido()) {
                Evento evento = eventos.poll();
                System.out.println("Activando evento: " + evento.getNombreEvento()
                    + " [nivel requerido: " + evento.getNivelRequerido() + "]");
                evento.applyEvento();
            }
        }
    }

}
