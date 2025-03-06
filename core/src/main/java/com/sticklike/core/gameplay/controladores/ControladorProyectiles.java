package com.sticklike.core.gameplay.controladores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilPapelCulo;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilTazo;
import com.sticklike.core.entidades.objetos.texto.FontManager;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.interfaces.Proyectiles;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Gestiona los proyectiles disparados en el juego. Se encarga de su actualización, colisiones, daño aplicado y renderizado.
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
                boolean colision = false;

                if (proyectil instanceof ProyectilTazo) {
                    colision = estaEnRadioTazo(enemigo, proyectil);
                }
                // Si es ProyectilPapelCulo, comprobamos si está en explosión
                else if (proyectil instanceof ProyectilPapelCulo) {
                    ProyectilPapelCulo proyectilPapelCulo = (ProyectilPapelCulo) proyectil;
                    if (proyectilPapelCulo.isImpactoAnimacionActiva()) {
                        // Usar el área circular de la explosión
                        Circle explosionArea = proyectilPapelCulo.getCirculoColision();
                        float enemyCenterX = enemigo.getX() + enemigo.getSprite().getWidth() / 2f;
                        float enemyCenterY = enemigo.getY() + enemigo.getSprite().getHeight() / 2f;
                        colision = explosionArea.contains(enemyCenterX, enemyCenterY);
                    } else {
                        // Si no está en explosión, usar el rectángulo normal
                        Rectangle rect = proyectil.getRectanguloColision();
                        colision = enemigo.esGolpeadoPorProyectil(proyectil.getX(), proyectil.getY(), rect.width, rect.height);
                    }
                }
                // Para los demás proyectiles, usamos la colisión rectangular
                else {
                    Rectangle rect = proyectil.getRectanguloColision();
                    colision = enemigo.esGolpeadoPorProyectil(proyectil.getX(), proyectil.getY(), rect.width, rect.height);
                }

                if (enemigo.getVida() > 0 && proyectil.isProyectilActivo() &&
                    !proyectil.yaImpacto(enemigo) && colision) {

                    // 1) Calcular daño
                    float baseDamage = proyectil.getBaseDamage();
                    float damage = baseDamage * multiplicadorDeDanyo;

                    // 2) Aplicar daño y parpadeo
                    enemigo.reducirSalud(damage);
                    enemigo.activarParpadeo(DURACION_PARPADEO_ENEMIGO);

                    // Aplicar knockback solo si no es ProyectilPapelCulo (ya se aplicará internamente en la explosión)
                    if (!(proyectil instanceof ProyectilPapelCulo)) {
                        aplicarKnockback(enemigo, proyectil);
                    }

                    // 3) Generar texto flotante
                    float baseX = enemigo.getX() + enemigo.getSprite().getWidth() / 2f;
                    float posicionTextoY = enemigo.getY() + enemigo.getSprite().getHeight() + DESPLAZAMIENTOY_TEXTO2;
                    Float ultimaY = ultimaYTexto.get(enemigo);
                    if (ultimaY != null) {
                        posicionTextoY = ultimaY + DESPLAZAMIENTOY_TEXTO2;
                    }
                    boolean golpeCritico = proyectil.esCritico();
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

        // Obtenemos el rectángulo de colisión del tazo
        Rectangle areaTazos = proyectil.getRectanguloColision();
        float centroTazoX = areaTazos.x + areaTazos.width / 2;
        float centroTazoY = areaTazos.y + areaTazos.height / 2;
        float radio = (areaTazos.width / 2) * 0.5f; // radio reducido para efectuar impacto real acorde con el visual
        Circle tazoCircle = new Circle(centroTazoX, centroTazoY, radio);

        // Usamos el bounding rectangle del enemigo
        Rectangle enemyRect = enemigo.getSprite().getBoundingRectangle();

        return Intersector.overlaps(tazoCircle, enemyRect);
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

