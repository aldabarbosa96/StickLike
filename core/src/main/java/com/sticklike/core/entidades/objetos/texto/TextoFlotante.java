package com.sticklike.core.entidades.objetos.texto;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * La clase InGameText se encarga de gestionar el renderizado, actualizado y destrucción
 * del texto flotante del daño aplicado a enemigos
 */
public class TextoFlotante {
    private String texto;
    private float x, y;
    private float duracion;
    private BitmapFont fuente;

    /**
     * @param texto    valor del texto a mnostrar
     * @param x,y      coordenadas donde se mostrará
     * @param duracion tiempo que permanecerá visible
     */
    public TextoFlotante(String texto, float x, float y, float duracion) {
        this.texto = texto;
        this.x = x;
        this.y = y;
        this.duracion = duracion;

        fuente = new BitmapFont();
        fuente.getData().setScale(TEXTO_WIDTH, TEXTO_HEIGHT);
    }

    /**
     * Comprueba si el texto ha desaparecido
     *
     * @return devuelve true si la duración es igual o menor a 0
     */
    public boolean haDesaparecido() {
        return duracion <= 0;
    }

    /**
     * Renderiza el texto en pantalla
     *
     * @param batch SpriteBatch para dibujar el texto
     */
    public void renderizarTextoFlotante(SpriteBatch batch) {
        dibujarReborde(batch);
        fuente.setColor(1f, 1f, 1f, 1f);
        fuente.draw(batch, texto, x, y);
    }

    /**
     * Calcula el tiempo que el texto permanece en pantalla
     * mientras lo desplaza en el eje Y
     *
     * @param delta frecuencia de act. de pantalla
     */
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

