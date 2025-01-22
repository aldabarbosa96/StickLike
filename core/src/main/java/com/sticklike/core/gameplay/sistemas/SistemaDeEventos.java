package com.sticklike.core.gameplay.sistemas;

import com.sticklike.core.gameplay.eventos.Evento;
import com.sticklike.core.gameplay.managers.ControladorEnemigos;
import com.sticklike.core.ui.RenderHUDComponents;

import java.util.PriorityQueue;

public class SistemaDeEventos {
    private PriorityQueue<Evento> eventos; // Cola de prioridad para eventos
    private RenderHUDComponents renderHUDComponents;
    private ControladorEnemigos controladorEnemigos;

    public SistemaDeEventos(RenderHUDComponents renderHUDComponents, ControladorEnemigos controladorEnemigos) {
        this.eventos = new PriorityQueue<>();
        this.renderHUDComponents = renderHUDComponents;
        this.controladorEnemigos = controladorEnemigos;
        inicializarEventos();
    }

    private void inicializarEventos() {
        eventos.add(new Evento("Aumenta nº enemigos", 30f, () -> eventoAumentaEnemigos1()));
        eventos.add(new Evento("Aumenta nº enemigos", 75f, () -> eventoAumentaEnemigos2()));
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
        float tiempoActual = renderHUDComponents.getTiempoTranscurrido();

        // Procesar eventos que deban activarse
        while (!eventos.isEmpty() && eventos.peek().getTiempoActivacion() <= tiempoActual) {
            Evento evento = eventos.poll();
            System.out.println("Activando evento: " + evento.getNombreEvento());
            evento.applyEvento();
        }
    }
}
