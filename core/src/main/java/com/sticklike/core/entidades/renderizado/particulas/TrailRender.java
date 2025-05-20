package com.sticklike.core.entidades.renderizado.particulas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.interfaces.Trail;

public class TrailRender {

    private static final TrailRender INSTANCE = new TrailRender();

    public static TrailRender get() {
        return INSTANCE;
    }

    private final ShapeRenderer sr = new ShapeRenderer();
    private final Array<Trail> pending = new Array<>();

    private TrailRender() {
    }


    public void submit(Trail t) {
        pending.add(t);
    }

    public void flush(Matrix4 projection) {
        if (pending.size == 0) return;

        sr.setProjectionMatrix(projection);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < pending.size; i++) {
            pending.get(i).draw(sr);
        }
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
        pending.clear();
    }
}
