package com.sticklike.core.gameplay.controladores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.objetos.armas.proyectiles.proyectil.LatigoDildo;
import com.sticklike.core.entidades.objetos.armas.proyectiles.proyectil.LluviaMocos;
import com.sticklike.core.entidades.objetos.armas.proyectiles.proyectil.ProyectilPapelCulo;
import com.sticklike.core.entidades.objetos.armas.proyectiles.proyectil.ProyectilTazo;
import com.sticklike.core.entidades.objetos.texto.FontManager;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.interfaces.Enemigo;
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
        float multDanyo = multiplicadorDeDanyo;

        while (iterator.hasNext()) {
            Proyectiles proyectil = iterator.next();
            proyectil.actualizarProyectil(delta);

            float projX = proyectil.getX();
            float projY = proyectil.getY();
            Rectangle projRect = proyectil.getRectanguloColision();

            for (Enemigo enemigo : enemies) {

                boolean colision = false;

                /* ======================= 1) Colisión por tipo ======================= */
                switch (proyectil) {
                    case ProyectilTazo proyectilTazo -> colision = estaEnRadioTazo(enemigo, proyectil);
                    case ProyectilPapelCulo proyectilPapelCulo -> {

                        if (proyectilPapelCulo.isImpactoAnimacionActiva()) {
                            Circle explosionArea = proyectilPapelCulo.getCirculoColision();
                            float enemyCX = enemigo.getX() + enemigo.getSprite().getWidth() * 0.5f;
                            float enemyCY = enemigo.getY() + enemigo.getSprite().getHeight() * 0.5f;
                            colision = explosionArea.contains(enemyCX, enemyCY);
                        } else {
                            colision = enemigo.esGolpeadoPorProyectil(projX, projY, projRect.width, projRect.height);
                        }
                    }
                    case LatigoDildo dildo when dildo.isHaloActivo() -> {
                        /* Recorremos cada “segmento” del halo */
                        for (Vector2 base : dildo.getPuntosHalo()) {
                            float hx = base.x + dildo.getLado() * dildo.getHaloTravel();
                            float hy = base.y;

                            if (enemigo.esGolpeadoPorProyectil(hx, hy, dildo.getHaloW(), dildo.getHaloH())) {
                                colision = true;
                                break; // ya tenemos colisión con este enemigo
                            }
                        }

                        /* --------------- Cualquier otro proyectil ------------------- */
                    }
                    default -> colision = enemigo.esGolpeadoPorProyectil(projRect.x, projRect.y, projRect.width, projRect.height);
                }

                /* ======================= 2) Si hay colisión ========================= */
                if (enemigo.getVida() > 0 && proyectil.isProyectilActivo() && colision && !proyectil.yaImpacto(enemigo)) {

                    /* 2.1) DAÑO */
                    float baseDamage = proyectil.getBaseDamage();
                    float damage = baseDamage * multDanyo;
                    enemigo.reducirSalud(damage);
                    enemigo.activarParpadeo(DURACION_PARPADEO_ENEMIGO);

                    /* 2.2) KNOCKBACK */
                    if (proyectil instanceof LatigoDildo dildo && dildo.isHaloActivo()) {
                        // Empuje sólo horizontal en la dirección del halo
                        enemigo.aplicarKnockback(proyectil.getKnockbackForce(), dildo.getLado(), 0f);
                    } else if (!(proyectil instanceof ProyectilPapelCulo)) {
                        aplicarKnockback(enemigo, proyectil);
                    }

                    /* 2.3) TEXTO DE DAÑO */
                    float baseX = enemigo.getX() + enemigo.getSprite().getWidth() * 0.5f;
                    float posY = enemigo.getY() + enemigo.getSprite().getHeight() + DESPLAZAMIENTOY_TEXTO2;

                    Float ultimaY = ultimaYTexto.get(enemigo);
                    if (ultimaY != null) posY = ultimaY + DESPLAZAMIENTOY_TEXTO2;

                    TextoFlotante texto = new TextoFlotante(String.valueOf((int) damage), baseX, posY, DURACION_TEXTO, FontManager.getDamageFont(), proyectil.esCritico());
                    dmgText.add(texto);
                    ultimaYTexto.put(enemigo, posY);

                    /* 2.4) Registrar impacto y posible desactivación */
                    proyectil.registrarImpacto(enemigo);

                    if (!proyectil.isPersistente()) {
                        proyectil.desactivarProyectil();
                        break; // No seguimos comprobando con otros enemigos
                    }
                }
            }

            /* ======================= 3) Limpiar inactivos ========================= */
            if (!proyectil.isProyectilActivo()) {
                iterator.remove();
                if (proyectil instanceof ProyectilTazo tazo) {
                    tazo.getAtaqueTazo().reducirTazosActivos();
                }
            }
        }
    }


    private void aplicarKnockback(Enemigo enemigo, Proyectiles proyectil) {
        float enemyCenterX = enemigo.getX() + enemigo.getSprite().getWidth() / 2f;
        float enemyCenterY = enemigo.getY() + enemigo.getSprite().getHeight() / 2f;
        Rectangle rect = proyectil.getRectanguloColision();
        float projCenterX = proyectil.getX() + rect.width / 2f;
        float projCenterY = proyectil.getY() + rect.height / 2f;

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

    public void renderizarProyectiles(SpriteBatch batch) { // Render sobre enemigos
        for (Proyectiles p : proyectiles) {
            if (!(p instanceof LluviaMocos && p.isPersistente())) {
                p.renderizarProyectil(batch);
            }
        }
    }

    public void renderizarProyectilesFondo(SpriteBatch batch) { // Render por debajo de los enemigos
        for (Proyectiles p : proyectiles) {
            if (p instanceof LluviaMocos && p.isPersistente()) {
                p.renderizarProyectil(batch);
            }
        }
    }

    public void aumentarDanyoProyectil(float multiplier) {
        multiplicadorDeDanyo *= multiplier;
        Gdx.app.log("DamageMultiplier", "Multiplicador de daño actualizado a: " + multiplicadorDeDanyo);
    }

    private boolean estaEnRadioTazo(Enemigo enemigo, Proyectiles proyectil) {
        if (!(proyectil instanceof ProyectilTazo)) return false;

        Rectangle areaTazos = proyectil.getRectanguloColision();
        float centroTazoX = areaTazos.x + areaTazos.width / 2;
        float centroTazoY = areaTazos.y + areaTazos.height / 2;
        float radio = (areaTazos.width / 2) * 0.5f;
        Circle tazoCircle = new Circle(centroTazoX, centroTazoY, radio);

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

    public void reset() {
        proyectiles.clear();
        multiplicadorDeDanyo = 1.0f;
    }

    public void dispose() {
        for (Proyectiles proyectil : proyectiles) {
            if (proyectil != null) {
                proyectil.dispose();
            }

        }
    }

    public float getMultiplicadorDeDanyo() {
        return multiplicadorDeDanyo;
    }

    public void setMultiplicadorDeDanyo(float multiplicadorDeDanyo) {
        this.multiplicadorDeDanyo = multiplicadorDeDanyo;
    }

    public Array<Proyectiles> getProyectiles() {
        return proyectiles;
    }
}
