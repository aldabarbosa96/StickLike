package com.sticklike.core.gameplay.sistemas;

import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.gameplay.progreso.Evento;
import com.sticklike.core.gameplay.controladores.ControladorEnemigos;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.ui.RenderHUDComponents;

import static com.sticklike.core.utilidades.GestorConstantes.*;

import java.util.PriorityQueue;

/**
 * Clase que permite crear y gestionar los eventos de la partida
 */
public class SistemaDeEventos {
    private PriorityQueue<Evento> eventos; // Cola de prioridad para eventos
    private RenderHUDComponents renderHUDComponents; // todo --> lo necesitaré en un futuro para gestionar algún evento basándome en el timer
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
        eventos.add(new Evento("Aumenta nº enemigos", sistemaDeNiveles, this::eventoAumentaEnemigos1, LVL_EVENTO1));
        eventos.add(new Evento("Aumenta nº enemigos 2", sistemaDeNiveles, this::eventoAumentaEnemigos2, LVL_EVENTO2));
        eventos.add(new Evento("Aparecen las pollas", sistemaDeNiveles, this::entraEnemigoPolla, LVL_EVENTO3));
        eventos.add(new Evento("BOSSPOLLA Aparece", sistemaDeNiveles, this::spawnPrimerBoss, LVL_EVENTO4));

        // todo --> gestionar más eventos próximamente (nuevos enemigos, bosses y algún otro evento diferente)
    }

    private void eventoAumentaEnemigos1() {
        controladorEnemigos.setIntervaloDeAparicion(EVENTO1_SPAWN_RATE);
        incrementarVelocidadCulo(EVENTO1_SPEED_MULT);
        System.out.println("¡Se ha reducido el intervalo de aparición de enemigos!");
        System.out.println("Aparición cada: " + controladorEnemigos.getIntervaloDeAparicion());
        System.out.println("Velocidad enemigo aumentada un 15%");
    }

    private void eventoAumentaEnemigos2() {
        controladorEnemigos.setIntervaloDeAparicion(EVENTO2_SPAWN_RATE);
        controladorEnemigos.setTiposDeEnemigos(TIPOS_ENEMIGOS2);
        incrementarVelocidadCulo(EVENTO2_SPEED_MULT);
        System.out.println("¡Se ha reducido el intervalo de aparición de enemigos!");
        System.out.println("Aparición cada: " + controladorEnemigos.getIntervaloDeAparicion());
        System.out.println("Velocidad enemigo aumentada un 30%%");

    }

    private void incrementarVelocidadCulo(float factorMultiplicador) {
        float nuevaVelocidadBase = EnemigoCulo.getVelocidadBase() * factorMultiplicador;
        EnemigoCulo.setVelocidadBase(nuevaVelocidadBase);

        for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
            if (enemigo instanceof EnemigoCulo culo) {
                float velocidadActual = culo.getVelocidad();
                culo.setVelocidad(velocidadActual * factorMultiplicador);
            }
        }
    }

    private void entraEnemigoPolla() {
        controladorEnemigos.setTiposDeEnemigos(LISTA_POLLAS);
        controladorEnemigos.setIntervaloDeAparicion(EVENTO_POLLAS_SPAWN_RATE);
        System.out.println("Enemigo polla aparece");
    }

    private void spawnPrimerBoss() {
       controladorEnemigos.setTiposDeEnemigos(LISTA_BOSSPOLLA);
        System.out.println("¡Ha aparecido el PollaBOSS en el nivel 10!");
    }

    public void actualizar() {
        if (!eventos.isEmpty()) {
            // No hacemos poll(), hacemos "peek" (ver el primer evento sin sacarlo)
            Evento siguiente = eventos.peek();
            if (sistemaDeNiveles.getNivelActual() >= siguiente.getNivelRequerido()) {
                Evento evento = eventos.poll();
                assert evento != null;
                System.out.println("Activando evento: " + evento.getNombreEvento() + " [nivel requerido: " + evento.getNivelRequerido() + "]");
                evento.applyEvento();
            }
        }
    }



        /* todo --> se usará en un futuro
    private void pollasLocas(float factorMultiplicador) { // por ahora solamente hace que el jugador muera al aumentar mucho la velocidad de todas las pollas
        // Modificamos la velocidad base de todos los enemigos Polla
        float nuevaVelocidadBase = EnemigoPolla.getVelocidadBase() * factorMultiplicador;
        EnemigoPolla.setVelocidadBase(nuevaVelocidadBase);

        // Ajustamos la velocidad de cada instancia existente de EnemigoPolla
        for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
            if (enemigo instanceof EnemigoPolla polla) {
                float velocidadActual = EnemigoPolla.getVelocidadBase();
                polla.setVelocidad(velocidadActual * factorMultiplicador);
            }
        }
    }*/
}
