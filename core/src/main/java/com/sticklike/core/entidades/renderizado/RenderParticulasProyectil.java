package com.sticklike.core.entidades.renderizado;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.interfaces.Trail;

/**
 * Genera la estela de un proyectil y la dibuja a través del renderer
 * global {@link TrailRender}.  Ya no contiene su propio ShapeRenderer.
 */
public class RenderParticulasProyectil implements Trail {

    /* ---------- configuración ---------- */
    private final int maxLength;        // nº máximo de muestras
    private final float width;            // grosor de la estela
    private Color color;
    private float alphaMult = 1f;

    /* ---------- buffers ---------- */
    private final Vector2[] buffer;
    private final Vector2[] normals;
    private int head = 0;                 // posición de escritura
    private int size = 0;                 // nº muestras válidas

    /* ---------- temporales ---------- */
    private final Vector2 tmpTangent = new Vector2();
    private final Vector2 tmpNormal = new Vector2();
    private final Vector2 tmpV1 = new Vector2();
    private final Vector2 tmpV2 = new Vector2();
    private final Vector2 tmpV3 = new Vector2();
    private final Vector2 tmpV4 = new Vector2();

    /* ===================================================================== */

    public RenderParticulasProyectil(int baseMaxLength, float width, Color baseColor) {
        float scale = Gdx.graphics.getWidth() / 2560f;           // ajusta a la resolución
        this.maxLength = Math.max(4, (int) (baseMaxLength * scale));
        this.width = width;
        this.color = baseColor.cpy();

        buffer = new Vector2[maxLength];
        normals = new Vector2[maxLength];
        for (int i = 0; i < maxLength; i++) {
            buffer[i] = new Vector2();
            normals[i] = new Vector2();
        }
    }

    /* --------------------------------------------------------------------- */
    /*  API pública                                                          */
    /* --------------------------------------------------------------------- */

    public void reset() {
        head = 0;
        size = 0;
    }
    /**
     * Guarda la posición de la punta del proyectil (llámalo en update).
     */
    public void update(Vector2 position) {
        buffer[head].set(position);
        head = (head + 1) % maxLength;
        if (size < maxLength) size++;
    }

    /**
     * TrailRender nos invocará una vez por frame.
     */
    @Override
    public void draw(ShapeRenderer sr) {
        if (size < 2) return;

        float halfW = width * 0.5f;
        int base = (head - size + maxLength) % maxLength;

        /* 1) calcular normales suavizadas */
        for (int i = 0; i < size; i++) {
            int idxPrev = (base + Math.max(0, i - 1)) % maxLength;
            int idxNext = (base + Math.min(size - 1, i + 1)) % maxLength;

            tmpTangent.set(buffer[idxNext]).sub(buffer[idxPrev]).nor();
            tmpNormal.set(-tmpTangent.y, tmpTangent.x);           // (-y, x)

            normals[i].set(tmpNormal);
        }

        /* 2) dibujar cada segmento como dos triángulos */
        for (int i = 0; i < size - 1; i++) {
            Vector2 p1 = buffer[(base + i) % maxLength];
            Vector2 p2 = buffer[(base + i + 1) % maxLength];
            Vector2 n1 = normals[i];
            Vector2 n2 = normals[i + 1];

            tmpV1.set(p1.x + n1.x * halfW, p1.y + n1.y * halfW);
            tmpV2.set(p1.x - n1.x * halfW, p1.y - n1.y * halfW);
            tmpV3.set(p2.x - n2.x * halfW, p2.y - n2.y * halfW);
            tmpV4.set(p2.x + n2.x * halfW, p2.y + n2.y * halfW);

            float alpha = MathUtils.clamp((float) i / (size - 1) * alphaMult, 0f, 1f);
            sr.setColor(color.r, color.g, color.b, alpha);

            sr.triangle(tmpV1.x, tmpV1.y, tmpV2.x, tmpV2.y, tmpV3.x, tmpV3.y);
            sr.triangle(tmpV1.x, tmpV1.y, tmpV3.x, tmpV3.y, tmpV4.x, tmpV4.y);
        }
    }

    /* ---------- setters auxiliares ---------- */

    public void setAlphaMult(float m) {
        alphaMult = m;
    }

    public void setColor(Color c) {
        color = c.cpy();
    }

    /* ---------- sin recursos propios que liberar ---------- */
    public void dispose() {/* nada */}
}
