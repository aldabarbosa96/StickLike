package com.sticklike.core.entidades.objetos.texto;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * La clase InGameText se encarga de gestionar el renderizado, actualizado y destrucción del texto flotante del daño aplicado a enemigos
 */
public class TextoFlotante {
    private String texto;
    private float x, y;
    private float duracion;
    private BitmapFont fuente;

    public TextoFlotante(String texto, float x, float y, float duracion) {
        this.texto = texto;
        this.x = x;
        this.y = y;
        this.duracion = duracion;

        fuente = new BitmapFont();
        fuente.getData().setScale(TEXTO_WIDTH, TEXTO_HEIGHT);
    }

    public boolean haDesaparecido() {
        return duracion <= 0;
    }

    public void renderizarTextoFlotante(SpriteBatch batch) {
        dibujarReborde(batch);
        fuente.setColor(1f, 1f, 1f, 1f);
        fuente.draw(batch, texto, x, y);
    }

    public void actualizarTextoFlotante(float delta) {
        duracion -= delta;
        y += delta * DESPLAZAMIENTOY_TEXTO;
    }

    private void dibujarReborde(SpriteBatch batch) {
        float offset = 1; // Desplazamiento para el reborde
        fuente.setColor(0.1f,0.1f,0.1f,1f);
        fuente.draw(batch, texto, x - offset, y); // Izquierda
        fuente.draw(batch, texto, x + offset, y); // Derecha
        fuente.draw(batch, texto, x, y - offset); // Abajo
        fuente.draw(batch, texto, x, y + offset); // Arriba
        fuente.draw(batch, texto, x - offset, y - offset); // Esquina inferior izquierda
        fuente.draw(batch, texto, x + offset, y - offset); // Esquina inferior derecha
        fuente.draw(batch, texto, x - offset, y + offset); // Esquina superior izquierda
        fuente.draw(batch, texto, x + offset, y + offset); // Esquina superior derecha
    }

    public void dispose() {
        fuente.dispose();
    }

}

