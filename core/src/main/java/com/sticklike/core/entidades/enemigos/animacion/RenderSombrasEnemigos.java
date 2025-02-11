package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.enemigos.bosses.BossPolla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoPolla;
import com.sticklike.core.interfaces.Enemigo;
import static com.sticklike.core.utilidades.GestorConstantes.*;

public class RenderSombrasEnemigos {

    public void dibujarSombrasEnemigos(ShapeRenderer shapeRenderer, Array<Enemigo> enemigos, OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        for (Enemigo enemigo : enemigos) {
            if (enemigo instanceof BossPolla) {
                dibujarSombraBossPolla(enemigo, shapeRenderer);
            }
            else if (enemigo instanceof EnemigoCulo) {
                dibujarSombraCulo(enemigo, shapeRenderer);
            }
            else if (enemigo instanceof EnemigoPolla) {
                dibujarSombraPolla((EnemigoPolla) enemigo, shapeRenderer);
            }
            else {
                dibujarSombraDefault(enemigo, shapeRenderer);
            }
        }
    }

    private void dibujarSombraBossPolla(Enemigo enemigo, ShapeRenderer shapeRenderer) {
        float x = enemigo.getX();
        float y = enemigo.getY();
        float w = enemigo.getSprite().getWidth();
        float h = enemigo.getSprite().getHeight();

        float centerX = x + w / 2f;

        float shadowWidth = w;
        float shadowHeight = h * 0.2f;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = y - 8f;

        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.5f);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);

    }

    private void dibujarSombraCulo(Enemigo enemigo, ShapeRenderer shapeRenderer) {
        float x = enemigo.getX();
        float y = enemigo.getY();
        float w = enemigo.getSprite().getWidth();
        float h = enemigo.getSprite().getHeight();

        float centerX = x + w / 2f;
        float shadowWidth = w * SHADOW_WIDTH_CULO;
        float shadowHeight = h * SHADOW_HEIGHT_CULO;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = y - SHADOW_OFFSET;

        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.5f);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }

    private void dibujarSombraPolla(EnemigoPolla polla, ShapeRenderer shapeRenderer) {
        float offset = polla.getMovimientoPolla().getCurrentOffset();

        float x = polla.getX();
        float y = polla.getY();
        float w = polla.getSprite().getWidth();
        float h = polla.getSprite().getHeight();

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

        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.5f);
        shapeRenderer.ellipse(finalShadowX, baseShadowY, finalShadowWidth, finalShadowHeight);
    }

    private void dibujarSombraDefault(Enemigo enemigo, ShapeRenderer shapeRenderer) {
        float x = enemigo.getX();
        float y = enemigo.getY();
        float w = enemigo.getSprite().getWidth();
        float h = enemigo.getSprite().getHeight();

        float centerX = x + w / 2f;
        float shadowWidth = w * 0.75f;
        float shadowHeight = h * 0.3f;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = y - 2.5f;

        shapeRenderer.setColor(0.35f, 0.35f, 0.35f, 0.5f);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }
}
