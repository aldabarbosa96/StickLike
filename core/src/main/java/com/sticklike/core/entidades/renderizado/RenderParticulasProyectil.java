package com.sticklike.core.entidades.renderizado;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class RenderParticulasProyectil {
    private final int maxLength;
    private final float width;
    private Color color;
    private final ShapeRenderer shapeRenderer;
    private float alphaMult = 1f;

    private final Vector2[] buffer;
    private int head = 0;
    private int size = 0;

    private final Vector2[] normals;
    private final Vector2 tmpTangent = new Vector2();
    private final Vector2 tmpNormal = new Vector2();
    private final Vector2 tmpV1 = new Vector2();
    private final Vector2 tmpV2 = new Vector2();
    private final Vector2 tmpV3 = new Vector2();
    private final Vector2 tmpV4 = new Vector2();

    public RenderParticulasProyectil(int baseMaxLength, float width, Color color) {
        float scaleFactor = Gdx.graphics.getWidth() / 2560f;
        this.maxLength = (int) (baseMaxLength * scaleFactor);
        this.width = width;
        this.color = color.cpy();
        this.shapeRenderer = new ShapeRenderer();

        // inicializar buffer y normales
        this.buffer = new Vector2[maxLength];
        this.normals = new Vector2[maxLength];
        for (int i = 0; i < maxLength; i++) {
            buffer[i] = new Vector2();
            normals[i] = new Vector2();
        }
    }

    public void update(Vector2 position) {
        // escribimos en la ranura head
        buffer[head].set(position.x, position.y);
        head = (head + 1) % maxLength;
        if (size < maxLength) {
            size++;
        }
    }

    public void render(SpriteBatch batch) {
        if (size < 2) {
            return;
        }

        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float halfW = width * 0.5f;
        // base del buffer: el primer punto válido
        int base = (head - size + maxLength) % maxLength;

        // 1) calcular normales suavizadas
        for (int i = 0; i < size; i++) {
            int idxPrev = (base + Math.max(0, i - 1)) % maxLength;
            int idxNext = (base + Math.min(size - 1, i + 1)) % maxLength;

            Vector2 pPrev = buffer[idxPrev];
            Vector2 pNext = buffer[idxNext];

            // tangent = pNext - pPrev
            tmpTangent.set(pNext).sub(pPrev).nor();
            // normal = (-tangent.y, tangent.x)
            tmpNormal.set(-tmpTangent.y, tmpTangent.x);

            normals[i].set(tmpNormal);
        }

        // 2) dibujar segmentos uno a uno
        for (int i = 0; i < size - 1; i++) {
            Vector2 p1 = buffer[(base + i) % maxLength];
            Vector2 p2 = buffer[(base + i + 1) % maxLength];
            Vector2 n1 = normals[i];
            Vector2 n2 = normals[i + 1];

            // vértices del quad
            tmpV1.set(p1.x + n1.x * halfW, p1.y + n1.y * halfW);
            tmpV2.set(p1.x - n1.x * halfW, p1.y - n1.y * halfW);
            tmpV3.set(p2.x - n2.x * halfW, p2.y - n2.y * halfW);
            tmpV4.set(p2.x + n2.x * halfW, p2.y + n2.y * halfW);

            float alpha = MathUtils.clamp((float) i / (size - 1) * alphaMult, 0f, 1f);
            shapeRenderer.setColor(color.r, color.g, color.b, alpha);

            shapeRenderer.triangle(tmpV1.x, tmpV1.y, tmpV2.x, tmpV2.y, tmpV3.x, tmpV3.y);
            shapeRenderer.triangle(tmpV1.x, tmpV1.y, tmpV3.x, tmpV3.y, tmpV4.x, tmpV4.y);
        }

        shapeRenderer.end();
        batch.begin();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }

    public void setAlphaMult(float alphaMult) {
        this.alphaMult = alphaMult;
    }

    public void setColor(Color color) {
        this.color = color.cpy();
    }
}
