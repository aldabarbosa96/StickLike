package com.sticklike.core.entidades.renderizado;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class RenderParticulasProyectil {

    private Array<Vector2> positions;
    private int maxLength;
    private float width;
    private Color color;
    private ShapeRenderer shapeRenderer;
    private float alphaMult = 1f;

    /**
     * @param baseMaxLength Usamos el valor escalado en base a la resolución 2560x1440p
     */
    public RenderParticulasProyectil(int baseMaxLength, float width, Color color) {
        // Calculamos un factor de escala basado en el ancho actual en relación a 2560.
        float scaleFactor = Gdx.graphics.getWidth() / 2560f;
        this.maxLength = (int)(baseMaxLength * scaleFactor);
        this.width = width;
        this.color = color;
        this.positions = new Array<>();
        this.shapeRenderer = new ShapeRenderer();
    }

    public void update(Vector2 position) {
        positions.add(position.cpy());
        if (positions.size > maxLength) {
            positions.removeIndex(0);
        }
    }

    public void render(SpriteBatch batch) {
        // Si hay pocos puntos, no se dibuja la estela
        if (positions.size < 2) return;

        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Array auxiliar para guardar las normales suavizadas
        Array<Vector2> normals = new Array<>();
        int count = positions.size;
        float halfWidth = width / 2f;

        // Calcular la normal en cada punto de la traza
        for (int i = 0; i < count; i++) {
            Vector2 current = positions.get(i);
            Vector2 tangent = new Vector2();

            if (i == 0) {
                // Para el primer punto, usar la dirección al siguiente
                tangent.set(positions.get(i + 1)).sub(current);
            } else if (i == count - 1) {
                // Para el último, usar la dirección del punto anterior
                tangent.set(current).sub(positions.get(i - 1));
            } else {
                // Para puntos intermedios, promediar la dirección anterior y la siguiente
                tangent.set(positions.get(i + 1)).sub(positions.get(i - 1));
            }
            tangent.nor();
            // La normal se calcula como (–tangent.y, tangent.x)
            normals.add(new Vector2(-tangent.y, tangent.x));
        }

        // Dibujar la estela como una malla continua
        for (int i = 0; i < count - 1; i++) {
            Vector2 p1 = positions.get(i);
            Vector2 p2 = positions.get(i + 1);
            Vector2 n1 = normals.get(i);
            Vector2 n2 = normals.get(i + 1);

            // Calcular los dos vértices en cada punto desplazando según la normal
            Vector2 v1 = new Vector2(p1.x + n1.x * halfWidth, p1.y + n1.y * halfWidth);
            Vector2 v2 = new Vector2(p1.x - n1.x * halfWidth, p1.y - n1.y * halfWidth);
            Vector2 v3 = new Vector2(p2.x - n2.x * halfWidth, p2.y - n2.y * halfWidth);
            Vector2 v4 = new Vector2(p2.x + n2.x * halfWidth, p2.y + n2.y * halfWidth);

            // Calcular alfa para el segmento (puede ser un promedio, o según la posición en la traza)
            float alpha = (((float) i / (count - 1)) * alphaMult);
            alpha = MathUtils.clamp(alpha, 0f, 1f);
            shapeRenderer.setColor(color.r, color.g, color.b, alpha);

            // Dibujar el cuadrilátero del segmento como dos triángulos
            shapeRenderer.triangle(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y);
            shapeRenderer.triangle(v1.x, v1.y, v3.x, v3.y, v4.x, v4.y);
        }

        shapeRenderer.end();
        batch.begin();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        if (shapeRenderer != null){
            shapeRenderer.dispose();
            shapeRenderer = null;
        }

    }

    public void setAlphaMult(float alphaMult) {
        this.alphaMult = alphaMult;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
