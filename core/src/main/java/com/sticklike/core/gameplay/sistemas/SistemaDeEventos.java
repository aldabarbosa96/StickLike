package com.sticklike.core.gameplay.sistemas;

import com.sticklike.core.entidades.enemigos.culo.EnemigoCulo;
import com.sticklike.core.gameplay.eventos.Evento;
import com.sticklike.core.gameplay.managers.ControladorEnemigos;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.ui.RenderHUDComponents;
import static com.sticklike.core.utilidades.GestorConstantes.*;

import java.util.PriorityQueue;

import static com.sticklike.core.utilidades.GestorConstantes.LVL_EVENTO2;

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
            () -> eventoAumentaEnemigos1(), LVL_EVENTO1));

        eventos.add(new Evento("Aumenta nº enemigos2", sistemaDeNiveles,
            () -> eventoAumentaEnemigos2(),LVL_EVENTO2));

        // todo --> gestionar más eventos próximamente
    }

    private void eventoAumentaEnemigos1() {
        controladorEnemigos.setIntervaloDeAparicion(0.5f);
        incrementarVelocidadCulo(1.5f);
        System.out.println("¡Se ha reducido el intervalo de aparición de enemigos!");
        System.out.println("Aparición cada: " + controladorEnemigos.getIntervaloDeAparicion());
        System.out.println("Velocidad enemigo aumentada un 50%");
    }

    private void eventoAumentaEnemigos2() {
        controladorEnemigos.setIntervaloDeAparicion(0.35f);
        incrementarVelocidadCulo(2f);
        System.out.println("¡Se ha reducido el intervalo de aparición de enemigos!");
        System.out.println("Aparición cada: " + controladorEnemigos.getIntervaloDeAparicion());
        System.out.println("Velocidad enemigo aumentada un 100%");

    }

    private void incrementarVelocidadCulo(float factorMultiplicador) {
        for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
            if (enemigo instanceof EnemigoCulo) {
                EnemigoCulo culo = (EnemigoCulo) enemigo;
                float velocidadActual = culo.getVelocidad();
                culo.setVelocidad(velocidadActual * factorMultiplicador);
            }
        }
    }

    public void actualizar() {
        if (!eventos.isEmpty()) {
            // No hacemos poll(), hacemos "peek" (ver el primer evento sin sacarlo)
            Evento siguiente = eventos.peek();
            if (sistemaDeNiveles.getNivelActual() >= siguiente.getNivelRequerido()) {
                Evento evento = eventos.poll();
                System.out.println("Activando evento: " + evento.getNombreEvento() + " [nivel requerido: " + evento.getNivelRequerido() + "]");
                evento.applyEvento();
            }
        }
    }

}
