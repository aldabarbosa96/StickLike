package com.sticklike.core.pantallas.gameOver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static com.sticklike.core.utilidades.GestorConstantes.*;


public class RenderVentanaGameOver {
    public void renderizarVentanaGameOver(FitViewport viewport, OrthographicCamera camera, SpriteBatch spriteBatch, GlyphLayout layout, BitmapFont font) {
        // Limpiamos la pantalla con un color rojizo oscuro
        Gdx.gl.glClearColor(0.2f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        dibujarTextoVentanaGameOver(layout, font, spriteBatch);
        spriteBatch.end();
    }

    private void dibujarTextoVentanaGameOver(GlyphLayout layout, BitmapFont font, SpriteBatch spriteBatch) {
        //font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear); todo --> valorar en un futuro
        font.getData().setScale(4f);
        String gameOverText = GAMEOVER;
        layout.setText(font, gameOverText);
        float textWidth = layout.width;
        float textHeight = layout.height;

        float textX = (VIRTUAL_WIDTH - textWidth) / 2f;
        float textY = (VIRTUAL_HEIGHT + textHeight) / 2f + 300;
        font.draw(spriteBatch, gameOverText, textX, textY);

        font.getData().setScale(1f);
        String optionsText = GAMEOVER_TEXT;
        layout.setText(font, optionsText);

        textWidth = layout.width;

        textX = (VIRTUAL_WIDTH - textWidth) / 2f;
        textY -= GAMEOVER_TEXT_Y;
        font.draw(spriteBatch, optionsText, textX, textY);
    }
}
