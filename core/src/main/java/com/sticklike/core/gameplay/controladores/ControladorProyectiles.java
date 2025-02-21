package com.sticklike.core.gameplay.controladores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilTazo;
import com.sticklike.core.entidades.objetos.texto.FontManager;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.interfaces.Proyectiles;

import static com.sticklike.core.utilidades.GestorConstantes.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Gestiona los proyectiles disparados en el juego.
 * Se encarga de su actualización, colisiones, daño aplicado y renderizado.
 */

public class ControladorProyectiles {
    private Array<Proyectiles> proyectiles;
    private float multiplicadorDeDanyo = MULT_DANYO;
    private Map<Enemigo, Float> ultimaYTexto = new HashMap<>();

    public ControladorProyectiles() {
        proyectiles = new Array<>();
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
                boolean esNubePedo = proyectil instanceof ProyectilTazo;
                boolean colision = esNubePedo ?
                    estaEnRadioTazo(enemigo, proyectil) :
                    enemigo.esGolpeadoPorProyectil(proyectil.getX(), proyectil.getY(), proyectil.getRectanguloColision().width, proyectil.getRectanguloColision().height);

                if (enemigo.getVida() > 0 && proyectil.isProyectilActivo() &&
                    !proyectil.yaImpacto(enemigo) && colision) {

                    // 1) Calcular daño
                    float baseDamage = proyectil.getBaseDamage();
                    float damage = baseDamage * multiplicadorDeDanyo;

                    // 2) Aplicar daño y parpadeo
                    enemigo.reducirSalud(damage);
                    enemigo.activarParpadeo(DURACION_PARPADEO_ENEMIGO);
                    aplicarKnockback(enemigo, proyectil);


                    // 4) Generar texto flotante
                    float baseX = enemigo.getX() + enemigo.getSprite().getWidth() / 2f;
                    float posicionTextoY = enemigo.getY() + enemigo.getSprite().getHeight() + DESPLAZAMIENTOY_TEXTO2;

                    // Ajustar posición vertical si hay múltiples textos
                    Float ultimaY = ultimaYTexto.get(enemigo);
                    if (ultimaY != null) {
                        posicionTextoY = ultimaY + DESPLAZAMIENTOY_TEXTO2;
                    }
                    // Determinar si el golpe fue crítico
                    boolean golpeCritico = proyectil.esCritico();

                    // Crear el texto flotante
                    TextoFlotante damageText = new TextoFlotante(String.valueOf((int) damage), baseX, posicionTextoY, DURACION_TEXTO, FontManager.damageFont, golpeCritico);
                    dmgText.add(damageText);

                    ultimaYTexto.put(enemigo, posicionTextoY);

                    proyectil.registrarImpacto(enemigo);

                    // Desactivar solo proyectiles no persistentes y no nubes
                    if (!proyectil.isPersistente()) {
                        proyectil.desactivarProyectil();
                        break;
                    }
                }
            }

            // Eliminar proyectiles inactivos (las nubes se eliminan automáticamente cuando tiempoVida <= 0)
            if (!proyectil.isProyectilActivo()) {
                iterator.remove();
                if (proyectil instanceof ProyectilTazo) {
                    ((ProyectilTazo) proyectil).getAtaqueTazo().reducirTazosActivos();
                }
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
        Gdx.app.log("DamageMultiplier", "Multiplicador de daño actualizado a: " + multiplicadorDeDanyo);
    }

    private boolean estaEnRadioTazo(Enemigo enemigo, Proyectiles proyectil) {
        if (!(proyectil instanceof ProyectilTazo)) return false;

        float enemigoX = enemigo.getX() + enemigo.getSprite().getWidth() / 2;
        float enemigoY = enemigo.getY() + enemigo.getSprite().getHeight() / 2;

        Rectangle areaTazos = proyectil.getRectanguloColision();
        float centroTazoX = areaTazos.x + areaTazos.width / 2;
        float centroTazoY = areaTazos.y + areaTazos.height / 2;

        float dx = enemigoX - centroTazoX;
        float dy = enemigoY - centroTazoY;
        float distanciaSq = dx * dx + dy * dy;

        float radio = areaTazos.width / 2;
        float radioSq = radio * radio;

        return distanciaSq <= radioSq;
    }


    public ProyectilTazo obtenerUltimoProyectilTazo() {
        for (int i = proyectiles.size - 1; i >= 0; i--) {
            if (proyectiles.get(i) instanceof ProyectilTazo) {
                return (ProyectilTazo) proyectiles.get(i);
            }
        }
        return null;
    }

    public ProyectilTazo obtenerProyectilPorIndice(int indice) {
        int contador = 0;
        for (Proyectiles p : proyectiles) {
            if (p instanceof ProyectilTazo) {
                if (contador == indice) {
                    return (ProyectilTazo) p;
                }
                contador++;
            }
        }
        return null;
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

