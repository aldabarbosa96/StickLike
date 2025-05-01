package com.sticklike.core.entidades.renderizado;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.enemigos.bosses.BossPolla;
import com.sticklike.core.entidades.enemigos.destructibles.Destructibles;
import com.sticklike.core.entidades.enemigos.destructibles.Destructibles2;
import com.sticklike.core.entidades.enemigos.mobs.escuela.EnemigoExamen;
import com.sticklike.core.entidades.enemigos.mobs.escuela.EnemigoRegla;
import com.sticklike.core.entidades.enemigos.mobs.sexo.EnemigoCondon;
import com.sticklike.core.entidades.enemigos.mobs.sexo.EnemigoCulo;
import com.sticklike.core.entidades.enemigos.mobs.sexo.EnemigoPolla;
import com.sticklike.core.interfaces.Enemigo;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class RenderBaseEnemigos {
    private static final Color tmpColor = new Color();

    public void dibujarEnemigos(SpriteBatch batch, Enemigo enemigo) {
        float vida = enemigo.getVida();
        boolean show = vida > 0 || enemigo.getAnimaciones().estaEnFade() || enemigo.getAnimaciones().estaEnParpadeo();
        if (!show) return;

        float alpha = enemigo.getAnimaciones().estaEnFade() ? enemigo.getAnimaciones().getAlphaActual() : 1f;
        Color orig = tmpColor.set(enemigo.getSprite().getColor());

        enemigo.getSprite().setColor(orig.r, orig.g, orig.b, alpha);
        enemigo.getSprite().draw(batch);
        enemigo.getAnimaciones().restaurarColor(enemigo.getSprite(), orig);
    }

    public void dibujarSombrasEnemigos(ShapeRenderer r, Array<Enemigo> enemigos, OrthographicCamera cam) {
        r.setProjectionMatrix(cam.combined);

        // Cálculo de frustum
        float halfW = cam.viewportWidth * cam.zoom * 0.5f;
        float halfH = cam.viewportHeight * cam.zoom * 0.5f;
        float left = cam.position.x - halfW;
        float right = cam.position.x + halfW;
        float bottom = cam.position.y - halfH;
        float top = cam.position.y + halfH;

        for (Enemigo e : enemigos) {
            // Bounds de sprite
            float ex = e.getX();
            float ey = e.getY();
            float ew = e.getSprite().getWidth();
            float eh = e.getSprite().getHeight();

            // Frustum culling
            if (ex + ew < left || ex > right || ey + eh < bottom || ey > top) {
                continue;
            }

            // Saltar muertos sin daño visible
            if (!(e instanceof BossPolla) && e.getVida() <= 0 && !e.isMostrandoDamageSprite()) {
                continue;
            }

            // Switch por tipo
            switch (e) {
                case BossPolla boss -> drawBoss(boss, r, ex, ey, ew, eh);
                case Destructibles d1 -> drawDestructible(d1, r, ex, ey, ew, eh);
                case Destructibles2 d2 -> drawDestructible2(d2, r, ex, ey, ew, eh);
                case EnemigoCulo culo -> drawSimple(culo, r, ex, ey, ew, eh, ew * SHADOW_WIDTH_CULO, eh * SHADOW_HEIGHT_CULO, SHADOW_OFFSET);
                case EnemigoPolla polla -> drawPolla(polla, r, ex, ey, ew, eh);
                case EnemigoExamen exam -> drawExamen(exam, r, ex, ey, ew, eh);
                case EnemigoRegla regla -> drawSimple(regla, r, ex, ey, ew, eh, ew * 0.75f, eh * 0.3f, 3.5f);
                case EnemigoCondon condon -> drawSimple(condon, r, ex, ey, ew, eh, ew , eh * 0.225f, 10f);
                default -> drawVater(e, r, ex, ey, ew, eh);
            }
        }
    }

    private void drawBoss(BossPolla b, ShapeRenderer r, float ex, float ey, float ew, float eh) {
        float cx = ex + ew / 2f;
        float w = ew;
        float h = eh * 0.25f;
        float x = cx - w / 2f;
        float y = ey - 8f;

        dibujarParpadeoSombra(b, r, Color.BLACK);
        r.ellipse(x, y, w, h);
    }

    private void drawDestructible(Destructibles d, ShapeRenderer r, float ex, float ey, float ew, float eh) {
        float cx = ex + ew / 2f;
        float w = ew * d.getShadowWidthMultiplier();
        float h = eh * d.getShadowHeightMultiplier();
        float x = cx - w / 2f;
        float y = ey + d.getShadowYOffset();

        dibujarParpadeoSombra(d, r, Color.WHITE);
        r.ellipse(x, y, w, h);
    }

    private void drawDestructible2(Destructibles2 d, ShapeRenderer r, float ex, float ey, float ew, float eh) {
        float cx = ex + ew / 2f;
        float w = ew;
        float h = eh * 0.25f;
        float x = cx - w / 2f;
        float y = ey - 5f;

        dibujarParpadeoSombra(d, r, Color.WHITE);
        r.ellipse(x, y, w, h);
    }

    private void drawSimple(Enemigo e, ShapeRenderer r, float ex, float ey, float ew, float eh, float w, float h, float offsetY) {
        float cx = ex + ew / 2f;
        float x = cx - w / 2f;
        float y = ey - offsetY;

        dibujarParpadeoSombra(e, r, Color.WHITE);
        r.ellipse(x, y, w, h);
    }

    private void drawPolla(EnemigoPolla p, ShapeRenderer r, float ex, float ey, float ew, float eh) {
        float offset = p.getMovimientoPolla().getCurrentOffset();
        float maxZig = p.getMovimientoPolla().getAmplitudZigzag();
        float norm = Math.min(1f, Math.max(-1f, offset / maxZig));
        float t = (norm + 1f) / 2f;
        float factor = 0.6f + (0.2f - 0.6f) * t;

        float baseW = ew * 1.6f;
        float baseH = eh * 0.5f;
        float baseY = ey - 1f;
        float w = baseW * factor;
        float h = baseH * factor;
        float x = ex + ew / SHADOW_OFFSET_POLLA - w / 2f;

        dibujarParpadeoSombra(p, r, Color.WHITE);
        r.ellipse(x, baseY, w, h);
    }

    private void drawExamen(EnemigoExamen ex, ShapeRenderer r, float ex0, float ey0, float ew, float eh) {
        float size = (ew + eh) / 2f * 0.55f;
        float cx = ex0 + ew / 2f + 1.5f;
        float x = cx - size / 2f;
        float y = ey0;

        dibujarParpadeoSombra(ex, r, Color.WHITE);
        r.ellipse(x, y, size + 1f, size - 10f);
    }

    private void drawVater(Enemigo e, ShapeRenderer r, float ex, float ey, float ew, float eh) {
        float w = ew * 0.65f;
        float h = eh * 0.15f;
        float baseX = ex + ew / 2f - w / 2f;
        float x = e.getAnimaciones().isEstaFlipped() ? baseX - 2.5f : baseX + 2.5f;
        float y = ey - 7.5f;

        dibujarParpadeoSombra(e, r, Color.WHITE);
        r.ellipse(x, y, w, h);
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
}
