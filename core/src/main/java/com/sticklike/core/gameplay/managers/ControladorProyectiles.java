package com.sticklike.core.gameplay.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.interfaces.Proyectiles;
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
    private ArrayList<Proyectiles> proyectiles;
    private float multiplicadorDeDanyo = 1.0f;

    /**
     * Inicializamos una lista de proyectiles vacía en el constructor
     */
    public ControladorProyectiles() {
        proyectiles = new ArrayList<>();
    }

    public void anyadirNuevoProyectil(Proyectiles proyectil) {
        proyectiles.add(proyectil);
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
        Iterator<Proyectiles> iterator = proyectiles.iterator();

        while (iterator.hasNext()) {
            Proyectiles proyectiles = iterator.next();
            proyectiles.actualizarProyectil(delta);

            for (Enemigo enemigo : enemies) {
                // Verificamos impacto solo si el proyectil sigue activo y el enemigo no está muerto
                if (!enemigo.estaMuerto() && proyectiles.isProyectilActivo() &&
                    enemigo.esGolpeadoPorProyectil(proyectiles.getX(), proyectiles.getY(), proyectiles.getRectanguloColision().width, proyectiles.getRectanguloColision().height)) {

                    // Cálculo del daño con el multiplicador aplicado
                    float baseDamage = 22 + (float) Math.random() * 8; // Daño base aleatorio entre 25 y 34
                    float damage = baseDamage * multiplicadorDeDanyo;

                    enemigo.reducirSalud(damage);

                    // Creamos un texto flotante con la cantidad de daño infligido
                    dmgText.add(new TextoFlotante(String.valueOf((int) damage), enemigo.getX() + enemigo.getSprite().getWidth() / 2, enemigo.getY() + enemigo.getSprite().getHeight() + 20, 0.5f
                    ));

                    // El proyectil impacta y se desactiva
                    proyectiles.desactivarProyectil();
                    break; // No verificamos más enemigos para este proyectil
                }
            }

            if (!proyectiles.isProyectilActivo()) {
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
        for (Proyectiles proyectil : proyectiles) {
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
        for (Proyectiles proyectil : proyectiles) {
            proyectil.dispose();
        }
    }
}

