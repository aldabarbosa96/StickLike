package com.sticklike.core.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Gestiona la animación y renderizado de la pantalla de carga, implementando la interfaz Screen.
 */
public class VentanaLoading implements Screen {
    private BitmapFont font;
    private float timer;
    private OrthographicCamera camera;
    private Texture dotTexture;
    private SpriteBatch spriteBatch;

    public VentanaLoading() {
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(3f);
        font.setColor(Color.BLUE);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        timer = 0f;
        dotTexture = new Texture("drops/02cacaDorada.png");
    }

    @Override
    public void render(float delta) {
        // Actualizamos el timer y calcula cuántos puntos suspensivos mostramos
        timer += delta;
        int dotIndex = (int) (timer / 0.25f) % 4;

        Gdx.gl.glClearColor(0.89f, 0.89f, 0.89f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        String textoBase = "Cagando";
        GlyphLayout layout = new GlyphLayout(font, textoBase);

        float sw = camera.viewportWidth;
        float sh = camera.viewportHeight;
        float textWidth = layout.width;
        float textHeight = layout.height;

        float dotSize = 15;
        float iconSpacing = 5;
        float spaceBetweenTextIcons = 10;

        // Calculamos el ancho total del bloque de iconos
        float iconBlockWidth = dotIndex * (dotSize + iconSpacing);
        if (dotIndex > 0) {
            iconBlockWidth += spaceBetweenTextIcons;
        }

        float totalWidth = textWidth + iconBlockWidth;
        float xBlock = (sw - totalWidth) / 2f;
        float yText = (sh + textHeight) / 2f + 75f;

        spriteBatch.begin();
        font.draw(spriteBatch, layout, xBlock, yText);

        float iconStartX = xBlock + textWidth + (dotIndex > 0 ? spaceBetweenTextIcons : 0);
        float iconY = yText - textHeight;

        for (int i = 0; i < dotIndex; i++) {
            float xIcon = iconStartX + i * (dotSize + iconSpacing);
            spriteBatch.draw(dotTexture, xIcon, iconY, dotSize, dotSize);
        }
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
        dispose(); // todo --> testear si da error al llegar a niveles elevados
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void dispose() {
        font.dispose();
        dotTexture.dispose();
        if (spriteBatch != null) {
            spriteBatch.dispose();
            spriteBatch = null;
        }
    }
}
