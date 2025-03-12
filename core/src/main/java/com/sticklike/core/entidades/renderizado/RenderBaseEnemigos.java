package com.sticklike.core.entidades.renderizado;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.enemigos.bosses.BossPolla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoExamen;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoPolla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoRegla;
import com.sticklike.core.entidades.enemigos.mobs.Destructibles;
import com.sticklike.core.entidades.objetos.recolectables.Boost;
import com.sticklike.core.interfaces.Enemigo;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Clase encargada de renderizar a los enemigos y sus sombras; ajustamos la forma y tamaño de la sombra según el tipo.
 */

public class RenderBaseEnemigos {
    private float x;
    private float y;
    private float w;
    private float h;

    public void dibujarEnemigos(SpriteBatch batch, Enemigo enemigo) {
        float vidaEnemigo = enemigo.getVida();
        boolean mostrarSprite = (vidaEnemigo > 0) || enemigo.getAnimaciones().estaEnFade();
        if (mostrarSprite) {
            Color originalColor = enemigo.getSprite().getColor().cpy();

            // Si el fade está activo, aplicamos el alfa del fade
            if (enemigo.getAnimaciones().estaEnFade()) {
                float alphaFade = enemigo.getAnimaciones().getAlphaActual();
                enemigo.getSprite().setColor(originalColor.r, originalColor.g, originalColor.b, alphaFade);
            } else if (enemigo.getAnimaciones().estaEnParpadeo()) {
                // Si está en parpadeo, cambiamos la textura, pero el alfa lo dejamos en 1
                enemigo.getSprite().setColor(originalColor.r, originalColor.g, originalColor.b, 1);
            } else {
                enemigo.getSprite().setColor(originalColor.r, originalColor.g, originalColor.b, 1);
            }

            enemigo.getSprite().draw(batch);
            enemigo.getAnimaciones().restaurarColor(enemigo.getSprite(), originalColor);
        }
    }

    // todo --> mover a la interfaz de enemigos en un futuro para que cada uno gestione el dibujado de su sombra individualmente (evitamos uso excesivo de instanceOf)
    public void dibujarSombrasEnemigos(ShapeRenderer shapeRenderer, Array<Enemigo> enemigos, OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        for (Enemigo enemigo : enemigos) {
            if (enemigo instanceof BossPolla) {
                dibujarSombraBossPolla((BossPolla) enemigo, shapeRenderer);
            } else if (enemigo instanceof EnemigoCulo) {
                dibujarSombraCulo((EnemigoCulo) enemigo, shapeRenderer);
            } else if (enemigo instanceof EnemigoPolla) {
                dibujarSombraPolla((EnemigoPolla) enemigo, shapeRenderer);
            } else if (enemigo instanceof EnemigoExamen) {
                dibujarSombraExamen((EnemigoExamen) enemigo, shapeRenderer);
            } else if (enemigo instanceof EnemigoRegla) {
                dibujarSombraRegla((EnemigoRegla) enemigo, shapeRenderer);
            } else if (enemigo instanceof Destructibles) {
                dibujarSombraDestructible((Destructibles) enemigo, shapeRenderer);

            } else {
                dibujarSombraVater(enemigo, shapeRenderer);
            }
        }
    }

    private void dibujarParpadeoSombra(Enemigo enemigo, ShapeRenderer shapeRenderer) {
        if (enemigo.getAnimaciones().estaEnParpadeo()) {
            shapeRenderer.setColor(Color.WHITE);
        } else if (enemigo.getAnimaciones().estaEnFade()) {
            shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 1);
        } else shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.5f);
    }

    private void dibujarSombraBossPolla(BossPolla enemigo, ShapeRenderer shapeRenderer) {

        getEnemyCenter(enemigo, x, y, w, h);

        float centerX = x + w / 2f;

        float shadowWidth = w;
        float shadowHeight = h * 0.25f;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = y - 8f;

        dibujarParpadeoSombra(enemigo, shapeRenderer);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);

    }
    private void dibujarSombraDestructible(Destructibles destructible, ShapeRenderer shapeRenderer) {
        float x = destructible.getX();
        float y = destructible.getY();
        float w = destructible.getSprite().getWidth();
        float h = destructible.getSprite().getHeight();
        float centerX = x + w / 2f;

        float shadowWidth = w * destructible.getShadowWidthMultiplier();
        float shadowHeight = h * destructible.getShadowHeightMultiplier();
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = y + destructible.getShadowYOffset();

        dibujarParpadeoSombra(destructible, shapeRenderer);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }


    private void dibujarSombraCulo(EnemigoCulo culo, ShapeRenderer shapeRenderer) {
        getEnemyCenter(culo, x, y, w, h);

        float centerX = x + w / 2f;
        float shadowWidth = w * SHADOW_WIDTH_CULO;
        float shadowHeight = h * SHADOW_HEIGHT_CULO;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = y - SHADOW_OFFSET;

        dibujarParpadeoSombra(culo, shapeRenderer);

        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }

    private void dibujarSombraPolla(EnemigoPolla polla, ShapeRenderer shapeRenderer) {
        float offset = polla.getMovimientoPolla().getCurrentOffset();

        getEnemyCenter(polla, x, y, w, h);

        float centerX = x + w / SHADOW_OFFSET_POLLA;

        float baseShadowWidth = w * 1.6f;
        float baseShadowHeight = h * 0.5f;
        float baseShadowY = y - 1f;

        float maxZigzag = polla.getMovimientoPolla().getAmplitudZigzag();

        // Normalizamos offset en [-1, 1].
        float normalizado = offset / maxZigzag;
        if (normalizado > 1) normalizado = 1;
        if (normalizado < -1) normalizado = -1;

        float topScale = 0.2f;
        float bottomScale = 0.6f;
        float factor = bottomScale + (topScale - bottomScale) * ((normalizado + 1f) / 2f);

        float finalShadowWidth = baseShadowWidth * factor;
        float finalShadowHeight = baseShadowHeight * factor;
        float finalShadowX = centerX - finalShadowWidth / 2f;

        dibujarParpadeoSombra(polla, shapeRenderer);
        shapeRenderer.ellipse(finalShadowX, baseShadowY, finalShadowWidth, finalShadowHeight);
    }

    private void dibujarSombraExamen(EnemigoExamen examen, ShapeRenderer shapeRenderer) {
        getEnemyCenter(examen, x, y, w, h);

        float shadowSize = ((w + h) / 2f) * 0.55f;
        float centerX = x + w / 2f + 1.5f;
        float shadowX = centerX - shadowSize / 2f;
        float shadowY = y;

        dibujarParpadeoSombra(examen, shapeRenderer);

        shapeRenderer.ellipse(shadowX, shadowY, shadowSize + 1f, shadowSize - 10f);
    }

    private void dibujarSombraRegla(EnemigoRegla regla, ShapeRenderer shapeRenderer) {
        getEnemyCenter(regla, x, y, w, h);

        float centerX = x + w / 2f;
        float shadowWidth = w * 0.75f;
        float shadowHeight = h * 0.3f;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = y - 3.5f;

        dibujarParpadeoSombra(regla, shapeRenderer);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }

    private void dibujarSombraVater(Enemigo enemigo, ShapeRenderer shapeRenderer) {
        getEnemyCenter(enemigo, x, y, w, h);
        float centerX = x + w / 2f;
        float shadowWidth = w * 0.65f;
        float shadowHeight = h * 0.15f;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = y - 12f;

        dibujarParpadeoSombra(enemigo, shapeRenderer);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }

    private void getEnemyCenter(Enemigo enemigo, float x, float y, float w, float h) {
        this.x = enemigo.getX();
        this.y = enemigo.getY();
        this.w = enemigo.getSprite().getWidth();
        this.h = enemigo.getSprite().getHeight();

    }
}
