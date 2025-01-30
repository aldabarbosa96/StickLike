package com.sticklike.core.gameplay.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.interfaces.Proyectiles;

import static com.sticklike.core.utilidades.GestorConstantes.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * La clase ProjectileManager gestiona todos los proyectiles disparados por el jugador (o potencialmente por enemigos en el futuro)
 * Se encarga de:
 * Añadir proyectiles nuevos con dirección y objetivo definidos
 * Actualizar su posición y verificar colisiones con enemigos
 * Aplicar el daño y generar textos flotantes (daño) al impactar
 * Renderizarlos y liberar recursos al morir
 * todo --> valorar refactorización en un futuro
 */
public class ControladorProyectiles {
    private ArrayList<Proyectiles> proyectiles;
    private float multiplicadorDeDanyo = MULT_DANYO;
    private Map<Enemigo, Float> ultimaYTexto = new HashMap<>();

    public ControladorProyectiles() {
        proyectiles = new ArrayList<>();
    }

    public void anyadirNuevoProyectil(Proyectiles proyectil) {
        proyectiles.add(proyectil);
    }

    public void actualizarProyectiles(float delta, Array<Enemigo> enemies, Array<TextoFlotante> dmgText) {
        ultimaYTexto.clear();

        Iterator<Proyectiles> iterator = proyectiles.iterator();

        while (iterator.hasNext()) {
            Proyectiles proyectil = iterator.next();
            proyectil.actualizarProyectil(delta);

            for (Enemigo enemigo : enemies) {
                // Verificamos si el proyectil es activo, el enemigo tiene vida y no ha sido impactado antes
                if (enemigo.getVida() > 0 && proyectil.isProyectilActivo() &&
                    !proyectil.yaImpacto(enemigo) &&
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
                    enemigo.activarParpadeo(DURACION_PARPADEO_ENEMIGO);

                    // 3) Aplicar knockback
                    aplicarKnockback(enemigo, proyectil);

                    // 4) Generar texto flotante
                    float baseX = enemigo.getX() + enemigo.getSprite().getWidth() / 2f;
                    float posicionTextoY = enemigo.getY() + enemigo.getSprite().getHeight() + DESPLAZAMIENTOY_TEXTO2;

                    // Ajustar la posición si ya existe un texto flotante para el enemigo
                    Float ultimaY = ultimaYTexto.get(enemigo);
                    if (ultimaY != null) {
                        posicionTextoY = ultimaY + DESPLAZAMIENTOY_TEXTO2;
                    }

                    TextoFlotante damageText = new TextoFlotante(String.valueOf((int) damage), baseX, posicionTextoY, DURACION_TEXTO);
                    dmgText.add(damageText);

                    ultimaYTexto.put(enemigo, posicionTextoY);

                    // Registrar el impacto para evitar daño repetido
                    proyectil.registrarImpacto(enemigo);

                    // Si el proyectil no es persistente, desactivarlo
                    if (!proyectil.isPersistente()) {
                        proyectil.desactivarProyectil();
                        break; // Salimos del loop de enemigos
                    }
                }
            }

            // Eliminar proyectiles inactivos
            if (!proyectil.isProyectilActivo()) {
                iterator.remove();
            }
        }
    }

    private void aplicarKnockback(Enemigo enemigo, Proyectiles proyectil) {
        float enemyCenterX = enemigo.getX() + enemigo.getSprite().getWidth() / 2f;
        float enemyCenterY = enemigo.getY() + enemigo.getSprite().getHeight() / 2f;

        float projCenterX = proyectil.getX() + proyectil.getRectanguloColision().width / 2f;
        float projCenterY = proyectil.getY() + proyectil.getRectanguloColision().height / 2f;

        float difX = enemyCenterX - projCenterX;
        float difY = enemyCenterY - projCenterY;

        float dist = (float) Math.sqrt(difX * difX + difY * difY);
        if (dist != 0) {
            difX /= dist;
            difY /= dist;
        }

        float fuerza = proyectil.getKnockbackForce();
        enemigo.aplicarKnockback(fuerza, difX, difY);
    }

    public void renderizarProyectiles(SpriteBatch batch) {
        for (Proyectiles proyectil : proyectiles) {
            proyectil.renderizarProyectil(batch);
        }
    }

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

