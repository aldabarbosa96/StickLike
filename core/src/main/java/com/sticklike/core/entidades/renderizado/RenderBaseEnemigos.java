package com.sticklike.core.entidades.renderizado;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.enemigos.bosses.BossPolla;
import com.sticklike.core.entidades.enemigos.destructibles.Destructibles2;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoExamen;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoPolla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoRegla;
import com.sticklike.core.entidades.enemigos.destructibles.Destructibles;
import com.sticklike.core.interfaces.Enemigo;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class RenderBaseEnemigos {
    private static final Color tmpColor = new Color();

    /**
     * Dibuja el sprite del enemigo, aplicando efectos de fade o parpadeo.
     */
    public void dibujarEnemigos(SpriteBatch batch, Enemigo enemigo) {
        float vidaEnemigo = enemigo.getVida();
        boolean mostrarSprite = (vidaEnemigo > 0) || enemigo.getAnimaciones().estaEnFade() || enemigo.getAnimaciones().estaEnParpadeo();
        if (mostrarSprite) {

            Color originalColor = tmpColor.set(enemigo.getSprite().getColor());

            if (enemigo.getAnimaciones().estaEnFade()) {
                float alphaFade = enemigo.getAnimaciones().getAlphaActual();
                enemigo.getSprite().setColor(originalColor.r, originalColor.g, originalColor.b, alphaFade);
            } else if (enemigo.getAnimaciones().estaEnParpadeo()) {
                enemigo.getSprite().setColor(originalColor.r, originalColor.g, originalColor.b, 1);
            } else {
                enemigo.getSprite().setColor(originalColor.r, originalColor.g, originalColor.b, 1);
            }

            enemigo.getSprite().draw(batch);
            enemigo.getAnimaciones().restaurarColor(enemigo.getSprite(), originalColor);
        }
    }

    /**
     * Dibuja las sombras de todos los enemigos. La proyección se establece con la cámara.
     */
    public void dibujarSombrasEnemigos(ShapeRenderer shapeRenderer, Array<Enemigo> enemigos, OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        for (Enemigo enemigo : enemigos) {
            // Se omite a enemigos no visibles (por ejemplo, sin vida y sin mostrar daño)
            if (!(enemigo instanceof BossPolla) && enemigo.getVida() <= 0 && !(enemigo.isMostrandoDamageSprite())) {
                continue;
            }
            switch (enemigo) {
                case BossPolla bossPolla -> dibujarSombraBossPolla(bossPolla, shapeRenderer);
                case EnemigoCulo culo -> dibujarSombraCulo(culo, shapeRenderer);
                case EnemigoPolla enemigoPolla -> dibujarSombraPolla(enemigoPolla, shapeRenderer);
                case EnemigoExamen enemigoExamen -> dibujarSombraExamen(enemigoExamen, shapeRenderer);
                case EnemigoRegla enemigoRegla -> dibujarSombraRegla(enemigoRegla, shapeRenderer);
                case Destructibles destructibles -> dibujarSombraDestructible(destructibles, shapeRenderer);
                case Destructibles2 destructibles2 -> dibujarSombraDestructible2(destructibles2, shapeRenderer);
                default -> dibujarSombraVater(enemigo, shapeRenderer);
            }
        }
    }

    private float[] obtenerDatosEnemigo(Enemigo enemigo) {
        float ex = enemigo.getX();
        float ey = enemigo.getY();
        float ew = enemigo.getSprite().getWidth();
        float eh = enemigo.getSprite().getHeight();
        return new float[]{ex, ey, ew, eh};
    }

    private void dibujarParpadeoSombra(Enemigo enemigo, ShapeRenderer shapeRenderer, Color color) {
        if (enemigo.getAnimaciones().estaEnParpadeo()) {
            shapeRenderer.setColor(color);
        } else if (enemigo.getAnimaciones().estaEnFade()) {
            shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 1);
        } else {
            shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.5f);
        }
    }

    private void dibujarSombraBossPolla(BossPolla bossPolla, ShapeRenderer shapeRenderer) {
        float[] datos = obtenerDatosEnemigo(bossPolla);
        float ex = datos[0], ey = datos[1], ew = datos[2], eh = datos[3];
        float centerX = ex + ew / 2f;
        float shadowWidth = ew;
        float shadowHeight = eh * 0.25f;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = ey - 8f;

        dibujarParpadeoSombra(bossPolla, shapeRenderer, Color.BLACK);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }

    private void dibujarSombraDestructible(Destructibles destructible, ShapeRenderer shapeRenderer) {
        float ex = destructible.getX();
        float ey = destructible.getY();
        float ew = destructible.getSprite().getWidth();
        float eh = destructible.getSprite().getHeight();
        float centerX = ex + ew / 2f;

        float shadowWidth = ew * destructible.getShadowWidthMultiplier();
        float shadowHeight = eh * destructible.getShadowHeightMultiplier();
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = ey + destructible.getShadowYOffset();

        dibujarParpadeoSombra(destructible, shapeRenderer, Color.WHITE);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }

    private void dibujarSombraDestructible2(Destructibles2 enemigo, ShapeRenderer shapeRenderer) {
        float[] datos = obtenerDatosEnemigo(enemigo);
        float ex = datos[0], ey = datos[1], ew = datos[2], eh = datos[3];
        float centerX = ex + ew / 2f;

        float shadowWidth = ew;
        float shadowHeight = eh * 0.25f;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = ey - 5f;

        dibujarParpadeoSombra(enemigo, shapeRenderer, Color.WHITE);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }

    private void dibujarSombraCulo(EnemigoCulo culo, ShapeRenderer shapeRenderer) {
        float[] datos = obtenerDatosEnemigo(culo);
        float ex = datos[0], ey = datos[1], ew = datos[2], eh = datos[3];
        float centerX = ex + ew / 2f;
        float shadowWidth = ew * SHADOW_WIDTH_CULO;
        float shadowHeight = eh * SHADOW_HEIGHT_CULO;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = ey - SHADOW_OFFSET;

        dibujarParpadeoSombra(culo, shapeRenderer, Color.WHITE);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }

    private void dibujarSombraPolla(EnemigoPolla polla, ShapeRenderer shapeRenderer) {
        float offset = polla.getMovimientoPolla().getCurrentOffset();
        float[] datos = obtenerDatosEnemigo(polla);
        float ex = datos[0], ey = datos[1], ew = datos[2], eh = datos[3];
        float centerX = ex + ew / SHADOW_OFFSET_POLLA;  // Nota: Se usa SHADOW_OFFSET_POLLA para calcular centerX.

        float baseShadowWidth = ew * 1.6f;
        float baseShadowHeight = eh * 0.5f;
        float baseShadowY = ey - 1f;

        float maxZigzag = polla.getMovimientoPolla().getAmplitudZigzag();
        float normalizado = offset / maxZigzag;
        normalizado = Math.max(-1f, Math.min(normalizado, 1f)); // clamp a [-1, 1]

        float topScale = 0.2f;
        float bottomScale = 0.6f;
        float factor = bottomScale + (topScale - bottomScale) * ((normalizado + 1f) / 2f);

        float finalShadowWidth = baseShadowWidth * factor;
        float finalShadowHeight = baseShadowHeight * factor;
        float finalShadowX = centerX - finalShadowWidth / 2f;

        dibujarParpadeoSombra(polla, shapeRenderer, Color.WHITE);
        shapeRenderer.ellipse(finalShadowX, baseShadowY, finalShadowWidth, finalShadowHeight);
    }

    private void dibujarSombraExamen(EnemigoExamen examen, ShapeRenderer shapeRenderer) {
        float[] datos = obtenerDatosEnemigo(examen);
        float ex = datos[0], ey = datos[1], ew = datos[2], eh = datos[3];
        float shadowSize = ((ew + eh) / 2f) * 0.55f;
        float centerX = ex + ew / 2f + 1.5f;
        float shadowX = centerX - shadowSize / 2f;
        float shadowY = ey;

        dibujarParpadeoSombra(examen, shapeRenderer, Color.WHITE);
        shapeRenderer.ellipse(shadowX, shadowY, shadowSize + 1f, shadowSize - 10f);
    }

    private void dibujarSombraRegla(EnemigoRegla regla, ShapeRenderer shapeRenderer) {
        float[] datos = obtenerDatosEnemigo(regla);
        float ex = datos[0], ey = datos[1], ew = datos[2], eh = datos[3];
        float centerX = ex + ew / 2f;
        float shadowWidth = ew * 0.75f;
        float shadowHeight = eh * 0.3f;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = ey - 3.5f;

        dibujarParpadeoSombra(regla, shapeRenderer, Color.WHITE);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }

    private void dibujarSombraVater(Enemigo enemigo, ShapeRenderer shapeRenderer) {
        float[] datos = obtenerDatosEnemigo(enemigo);
        float ex = datos[0], ey = datos[1], ew = datos[2], eh = datos[3];
        float centerX = ex + ew / 2f;
        float shadowWidth = ew * 0.65f;
        float shadowHeight = eh * 0.15f;
        float shadowX = centerX - (shadowWidth / 2f) - 2.5f;
        float shadowXOffset = centerX - (shadowWidth / 2f) + 2.5f;
        // Se usa la propiedad de flip del sprite para decidir la posición
        float finalShadowX = enemigo.getAnimaciones().isEstaFlipped() ? shadowX : shadowXOffset;
        float shadowY = ey - 7.5f;

        dibujarParpadeoSombra(enemigo, shapeRenderer, Color.WHITE);
        shapeRenderer.ellipse(finalShadowX, shadowY, shadowWidth, shadowHeight);
    }
}
