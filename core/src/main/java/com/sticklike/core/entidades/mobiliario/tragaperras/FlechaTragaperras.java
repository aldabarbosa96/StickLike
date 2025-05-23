package com.sticklike.core.entidades.mobiliario.tragaperras;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.sticklike.core.entidades.objetos.texto.FontManager;
import com.sticklike.core.ui.RenderHUDComponents;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class FlechaTragaperras {

    private static final float MARGIN = 22.5f;
    private static final float HUD_MARGIN = 185f;
    private static final float SIDE_Y_OFFSET = 100f;
    private final Sprite arrow;
    private final OrthographicCamera hudCam;
    private final ExtendViewport vp;
    private final Array<Tragaperras> slots;
    private final Vector3 tmp = new Vector3();
    private final Vector2 dir = new Vector2();
    private final float hudHeight;
    private final float W2, H2;
    private final BitmapFont font = FontManager.getHudFont();
    private float invScale = 1f / FontManager.getScale();
    private final GlyphLayout lay = new GlyphLayout();

    public FlechaTragaperras(OrthographicCamera hudCam, ExtendViewport vp, Array<Tragaperras> slots, float hudHeight) {
        this.hudCam = hudCam;
        this.vp = vp;
        this.slots = slots;
        this.hudHeight = hudHeight;
        font.setUseIntegerPositions(false);

        arrow = new Sprite(manager.get(FLECHA, Texture.class));
        arrow.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        arrow.setSize(30, 14);
        arrow.setOriginCenter();
        W2 = arrow.getWidth() * .5f;
        H2 = arrow.getHeight() * .5f;
    }

    public void render(SpriteBatch batch, RenderHUDComponents renderHUDComponents) {
        float tiempo = renderHUDComponents.getTiempoTranscurrido();

        int flechasAMostrar;
        if (tiempo >= 120f) {
            flechasAMostrar = Math.min(slots.size, 3);
        } else if (tiempo >= 30f) {
            flechasAMostrar = Math.min(slots.size, 2);
        } else if (tiempo >= 0f) {
            flechasAMostrar = Math.min(slots.size, 1);
        } else {
            flechasAMostrar = 0;
        }
        if (flechasAMostrar == 0) return;

        vp.apply();
        hudCam.update();
        batch.setProjectionMatrix(hudCam.combined);

        float halfW = hudCam.viewportWidth * hudCam.zoom * .5f;
        float halfH = hudCam.viewportHeight * hudCam.zoom * .5f;
        float left = hudCam.position.x - halfW;
        float right = hudCam.position.x + halfW;
        float top = hudCam.position.y + halfH;
        float bottom = hudCam.position.y - halfH;

        font.setColor(0, 0, 1, 1);
        font.getData().setScale(0.9f * invScale);

        batch.begin();
        // Solo iteramos los primeros N slots
        for (int i = 0; i < flechasAMostrar; i++) {
            Tragaperras slot = slots.get(i);

            // culling
            tmp.set(slot.getX() + ANCHO_TRAGAPERRAS * .5f, slot.getY() + ALTO_TRAGAPERRAS * .5f, 0);
            if (hudCam.frustum.pointInFrustum(tmp)) continue;

            dir.set(tmp.x - hudCam.position.x, tmp.y - hudCam.position.y).nor();
            arrow.setRotation(dir.angleDeg());

            float cx = 0, cy = 0;
            switch (slot.getDir()) {
                case NORTE -> {
                    cx = hudCam.position.x + SIDE_Y_OFFSET;
                    cy = top - MARGIN - H2;
                }
                case SUR -> {
                    cx = hudCam.position.x - SIDE_Y_OFFSET;
                    cy = bottom + HUD_MARGIN + H2;
                }
                case ESTE -> {
                    cx = right - MARGIN - H2;
                    cy = hudCam.position.y + SIDE_Y_OFFSET;
                }
                case OESTE -> {
                    cx = left + MARGIN + H2;
                    cy = hudCam.position.y + SIDE_Y_OFFSET;
                }
            }

            arrow.setCenter(cx, cy);
            arrow.draw(batch);

            // Etiqueta con la distancia
            float dx = tmp.x - hudCam.position.x;
            float dy = tmp.y - hudCam.position.y;
            int metros = Math.round(Vector2.len(dx, dy));
            String txt = metros + "m";
            lay.setText(font, txt);

            float tx = cx, ty = cy;
            switch (slot.getDir()) {
                case NORTE -> {
                    tx -= lay.width * .5f;
                    ty = cy - H2 - 15f;
                }
                case SUR -> {
                    tx -= lay.width * .5f;
                    ty = cy + H2 + lay.height + 15f;
                }
                case ESTE -> {
                    tx = cx - W2 - lay.width - 7.5f;
                    ty += lay.height * .5f;
                }
                case OESTE -> {
                    tx = cx + W2 + 5f;
                    ty += lay.height * .5f;
                }
            }
            font.draw(batch, txt, tx, ty);
        }
        batch.end();
    }
}
