package com.sticklike.core.gameplay.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.personajes.Enemigo;
import com.sticklike.core.entidades.objetos.TextoFlotante;
import com.sticklike.core.entidades.objetos.Proyectil;

import java.util.Iterator;

/**
 * La clase ProjectileManager gestiona todos los proyectiles disparados
 * por el jugador (o potencialmente por enemigos en el futuro)
 * Se encarga de:
 * <p>
 * Añadir proyectiles nuevos con dirección y objetivo definidos
 * Actualizar su posición y verificar colisiones con enemigos
 * Aplicar el daño y generar textos flotantes (daño) al impactar
 * Renderizarlos y liberar recursos al morir
 */
public class ControladorProyectiles {
    private ArrayList<Proyectil> proyectiles;
    private float multiplicadorDeDanyo = 1.0f;

    /**
     * Inicializamos una lista de proyectiles vacía en el constructor
     */
    public ControladorProyectiles() {
        proyectiles = new ArrayList<>();
    }

    /**
     * Crea y añade un nuevo proyectil a la lista, con posición inicial, dirección y enemigo objetivo
     * Además, se le asigna un multiplicador de velocidad aleatorio entre 0.7 y 1.1 para variar la trayectoria
     *
     * @param startX,startY posición X,Y de inicio del proyectil
     * @param dx,dt         componente X,Y de la dirección normalizada
     * @param target        enemigo objetivo (para calcular colisiones o guiarse)
     */
    public void anyadirNuevoProyectil(float startX, float startY, float dx, float dy, Enemigo target) {
        float randomSpeedMultiplier = 0.7f + (float) Math.random() * 0.4f;
        proyectiles.add(new Proyectil(startX, startY, dx, dy, target, randomSpeedMultiplier));
    }

    /**
     * Actualiza la posición de cada proyectil y verifica si impacta en un enemigo
     * Cuando un proyectil impacta, se calcula el daño y se genera un texto flotante
     * Los proyectiles inactivos se eliminan de la lista
     *
     * @param delta    tiempo transcurrido desde el último frame
     * @param enemies  lista de enemigos activos
     * @param dmgText  array para almacenar textos flotantes de daño
     */
    public void actualizarProyectiles(float delta, Array<Enemigo> enemies, Array<TextoFlotante> dmgText) {
        Iterator<Proyectil> iterator = proyectiles.iterator();

        while (iterator.hasNext()) {
            Proyectil proyectil = iterator.next();
            proyectil.actualizarProyectil(delta);

            for (Enemigo enemigo : enemies) {
                // Verificamos impacto solo si el proyectil sigue activo y el enemigo no está muerto
                if (!enemigo.estaMuerto() && proyectil.isProyectilActivo() &&
                    enemigo.esGolpeadoPorProyectil(proyectil.getX(), proyectil.getY(), proyectil.getBoundingRectangle().width, proyectil.getBoundingRectangle().height)) {

                    // Cálculo del daño con el multiplicador aplicado
                    float baseDamage = 22 + (float) Math.random() * 8; // Daño base aleatorio entre 25 y 34
                    float damage = baseDamage * multiplicadorDeDanyo;

                    enemigo.reduceHealth(damage);

                    // Creamos un texto flotante con la cantidad de daño infligido
                    dmgText.add(new TextoFlotante(String.valueOf((int) damage), enemigo.getX() + enemigo.getSprite().getWidth() / 2, enemigo.getY() + enemigo.getSprite().getHeight() + 20, 0.5f
                    ));

                    // El proyectil impacta y se desactiva
                    proyectil.desactivarProyectil();
                    break; // No verificamos más enemigos para este proyectil
                }
            }

            if (!proyectil.isProyectilActivo()) {
                iterator.remove(); // Eliminar proyectiles inactivos
            }
        }
    }

    /**
     * Dibuja todos los proyectiles activos en pantalla
     *
     * @param batch SpriteBatch para dibujar
     */
    public void renderizarProyectiles(SpriteBatch batch) {
        for (Proyectil proyectil : proyectiles) {
            proyectil.renderizarProyectil(batch);
        }
    }

    /**
     * Aumenta el factor de daño (multiplicadorDeDanyo) en el valor indicado, por ejemplo al obtener una mejora de daño
     *
     * @param multiplier valor a sumar al multiplicador de daño
     */
    public void aumentarDanyoProyectil(float multiplier) {
        multiplicadorDeDanyo += multiplier;
        System.out.println("Multiplicador de daño actualizado a: " + multiplicadorDeDanyo);
    }

    public void reset() { // Reseteamos de proyectiles para evitar interferencias y poder gestionar el nuevo estado de estos
        // todo --> se deberá gestionar desde aquí en vez de desde dispose (así podrán mantenerse algunas mejoras si existe la posibilidad de vida extra)
        proyectiles.clear();
        multiplicadorDeDanyo = 1.0f;
    }

    public void dispose() {
        for (Proyectil proyectil : proyectiles) {
            proyectil.dispose();
        }
    }
}

