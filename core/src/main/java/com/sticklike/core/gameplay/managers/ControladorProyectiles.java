package com.sticklike.core.gameplay.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.GestorConstantes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * La clase ProjectileManager gestiona todos los proyectiles disparados por el jugador (o potencialmente por enemigos en el futuro)
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
    private Map<Enemigo, Float> ultimaYTexto = new HashMap<>();


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
     * @param delta   tiempo transcurrido desde el último frame
     * @param enemies lista de enemigos activos
     * @param dmgText array para almacenar textos flotantes de daño
     */
    public void actualizarProyectiles(float delta, Array<Enemigo> enemies, Array<TextoFlotante> dmgText) {
        Iterator<Proyectiles> iterator = proyectiles.iterator();

        while (iterator.hasNext()) {
            Proyectiles proyectil = iterator.next();
            proyectil.actualizarProyectil(delta);

            // Para cada enemigo, verificamos colisión
            for (Enemigo enemigo : enemies) {
                if (enemigo.getVida() > 0 && proyectil.isProyectilActivo() &&
                    enemigo.esGolpeadoPorProyectil(
                        proyectil.getX(),
                        proyectil.getY(),
                        proyectil.getRectanguloColision().width,
                        proyectil.getRectanguloColision().height)) {

                    // 1) Calcular daño
                    float baseDamage = proyectil.getBaseDamage();
                    float damage = baseDamage * multiplicadorDeDanyo;

                    // 2) Aplicar daño y parpadeo
                    enemigo.reducirSalud(damage);
                    enemigo.activarParpadeo(GestorConstantes.DURACION_PARPADEO_ENEMIGO);

                    // CENTRO del enemigo
                    float enemyCenterX = enemigo.getX() + enemigo.getSprite().getWidth() / 2f;
                    float enemyCenterY = enemigo.getY() + enemigo.getSprite().getHeight() / 2f;

                    // CENTRO del proyectil (su bounding rectangle)
                    float projCenterX = proyectil.getX() + proyectil.getRectanguloColision().width / 2f;
                    float projCenterY = proyectil.getY() + proyectil.getRectanguloColision().height / 2f;

                    // Vector de dirección (enemigo - proyectil)
                    float difX = enemyCenterX - projCenterX;
                    float difY = enemyCenterY - projCenterY;

                    float dist = (float) Math.sqrt(difX * difX + difY * difY);
                    if (dist != 0) {
                        difX /= dist; // normalizamos
                        difY /= dist;
                    }

                    float fuerza = proyectil.getKnockbackForce();
                    enemigo.aplicarKnockback(fuerza, difX, difY);

                    float baseX = enemigo.getX() + enemigo.getSprite().getWidth() / 2f;
                    float baseY = enemigo.getY() + enemigo.getSprite().getHeight() + GestorConstantes.DESPLAZAMIENTOY_TEXTO2;

                    // Si ya existe un valor en ultimaYTexto para este enemigo, incrementa
                    Float ultimaY = ultimaYTexto.get(enemigo);
                    if (ultimaY != null) {
                        baseY = ultimaY + GestorConstantes.DESPLAZAMIENTOY_TEXTO2;
                    }

                    TextoFlotante damageText = new TextoFlotante(String.valueOf((int) damage), baseX, baseY, GestorConstantes.DURACION_TEXTO);
                    dmgText.add(damageText);

                    // Actualiza el valor en el map
                    ultimaYTexto.put(enemigo, baseY);


                    // 5) Desactivar proyectil
                    proyectil.desactivarProyectil();
                    break; // Salimos del loop de enemigos
                }
            }

            // Si se desactivó el proyectil, lo eliminamos
            if (!proyectil.isProyectilActivo()) {
                iterator.remove();
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
        multiplicadorDeDanyo *= multiplier;
        System.out.println("Multiplicador de daño actualizado a: " + multiplicadorDeDanyo);
    }

    public void reset() { // Reseteamos de proyectiles para evitar interferencias y poder gestionar el nuevo estado de estos
        // todo --> se deberá gestionar desde aquí en vez de desde dispose (así podrán mantenerse algunas mejoras si existe la posibilidad de vida extra)
        proyectiles.clear();
        //multiplicadorDeDanyo = 1.0f;
    }

    public void dispose() {
        for (Proyectiles proyectil : proyectiles) {
            proyectil.dispose();
        }
    }

}

