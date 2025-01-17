package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.sticklike.core.entities.Jugador;
import com.sticklike.core.systems.SistemaDeNiveles;
import com.sticklike.core.utils.GestorDeAssets;
import com.sticklike.core.utils.GestorConstantes;

/**
 * Clase responsable de dibujar la información de interfaz del jugador en pantalla (vida, experiencia, nivel, etc.)
 * <p>
 * Utiliza un {@link ShapeRenderer} para dibujar formas (barras de vida y XP) y un {@link SpriteBatch} para renderizar
 * iconos y texto. Maneja también una cámara y un viewport independientes para la interfaz (HUD)
 */
public class HUD {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch spriteBatch;
    private final Jugador jugador;
    private final SistemaDeNiveles sistemaDeNiveles;
    private final Texture texturaCorazonVida, texturaLapizXP;
    private final OrthographicCamera hudCamara;
    private final FillViewport hudViewport;
    private final BitmapFont fuente;
    private final GlyphLayout layout; // Sirve para calcular el tamaño del texto
    private static final float VIRTUAL_WIDTH = GestorConstantes.VIRTUAL_WIDTH; // Usamos VIRTUAL_WIDTH para algunas referencias de dibujo (alineaciones, etc.)

    /**
     * @param jugador          referencia al jugador, para consultar su vida
     * @param sistemaDeNiveles referencia al sistema que maneja la XP y el nivel del jugador
     * @param shapeRenderer    usado para dibujar rectángulos y líneas (barras, grids)
     * @param spriteBatch      usado para renderizar texturas e iconos
     */
    public HUD(Jugador jugador, SistemaDeNiveles sistemaDeNiveles, ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
        this.jugador = jugador;
        this.shapeRenderer = shapeRenderer;
        this.sistemaDeNiveles = sistemaDeNiveles;
        this.spriteBatch = spriteBatch;
        this.texturaCorazonVida = GestorDeAssets.corazonVida;
        this.texturaLapizXP = GestorDeAssets.iconoXP;

        this.fuente = new BitmapFont();
        this.layout = new GlyphLayout();

        // Configuramos la cámara y el viewport
        this.hudCamara = new OrthographicCamera();
        this.hudViewport = new FillViewport(GestorConstantes.VIRTUAL_WIDTH, GestorConstantes.VIRTUAL_HEIGHT, hudCamara);
        this.hudCamara.update();
    }

    /**
     * Dibuja el HUD estático en la parte inferior de la pantalla:
     * fondo, barras de salud y XP, iconos, texto de nivel, etc.
     * todo -- > falta implementar elementos en el HUD (stats player, mejoras obtenidas...)
     */
    public void renderizarHUD() {
        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudCamara.combined);
        shapeRenderer.setProjectionMatrix(hudCamara.combined);

        float hudHeight = 200f;
        renderizarFondoHUD();
        renderizarLineaDivisoria();
        renderizarLineasHorizontalesCuadricula(hudHeight);
        renderizarBarraDeSalud();
        renderizarBarraXP();
        renderizarTextoSalud(hudHeight);
        renderizarIconoVidaJugador();
        renderizarTextoNivelPlayer();
    }

    /**
     * Dibuja un fondo claro para la zona del HUD
     */
    private void renderizarFondoHUD() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.99f, 0.9735f, 0.863f, 1);
        shapeRenderer.rect(0, 0, GestorConstantes.VIRTUAL_WIDTH, GestorConstantes.HUD_HEIGHT);
        shapeRenderer.end();
    }

    /**
     * Dibuja una línea divisoria entre el HUD y el área de juego para crear efecto de sombra leve
     * o de separador (es un folio superpuesto en el cuaderno, se supone)
     */
    private void renderizarLineaDivisoria() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);
        shapeRenderer.rect(0, GestorConstantes.HUD_HEIGHT, GestorConstantes.VIRTUAL_WIDTH, 1);
        shapeRenderer.end();
    }

    /**
     * Dibuja una cuadrícula (grid) en la zona del HUD, utilizando shapeRenderer en modo línea (horizontal)
     *
     * @param alturaHUD altura del área del HUD
     */
    private void renderizarLineasHorizontalesCuadricula(float alturaHUD) { // Solo hacemos horizontales porque es otro estilo de folio de cuaderno
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.64f, 0.80f, 0.86f, 1);

        float screenWidth = VIRTUAL_WIDTH;
        float cellSize = GestorConstantes.GRID_CELL_SIZE;

        for (float y = 0; y <= alturaHUD; y += cellSize) {
            shapeRenderer.line(0, y, screenWidth, y);
        }

        shapeRenderer.end();
    }

    /**
     * Dibuja el texto del nivel (LVL) y el número del nivel del jugador de forma centrada en el HUD
     */
    private void renderizarTextoNivelPlayer() {
        String levelText = "LVL :  ";
        String levelNumber = String.valueOf(sistemaDeNiveles.getNivelActual());

        layout.setText(fuente, levelText + levelNumber);

        float textX = (VIRTUAL_WIDTH - layout.width) / 2;
        float textY = GestorConstantes.HUD_HEIGHT - 22f;

        spriteBatch.begin();

        // Texto Lvl
        fuente.getData().setScale(0.95f);
        fuente.setColor(0, 0, 0, 1); // Blanco con sombreado negro
        float offset = 1f;
        fuente.draw(spriteBatch, levelText, textX - offset, textY + offset);
        fuente.draw(spriteBatch, levelText, textX + offset, textY + offset);
        fuente.draw(spriteBatch, levelText, textX - offset, textY - offset);
        fuente.draw(spriteBatch, levelText, textX + offset, textY - offset);

        fuente.setColor(1, 1, 1, 1);
        fuente.draw(spriteBatch, levelText, textX, textY);

        // Coordenadas para el número
        float levelTextWidth = new GlyphLayout(fuente, levelText).width;
        float levelNumberX = textX + levelTextWidth;
        float textYNumber = GestorConstantes.HUD_HEIGHT - 17.5f;

        // Texto número
        fuente.getData().setScale(1.4f);
        fuente.setColor(0, 0, 1, 1);
        fuente.draw(spriteBatch, levelNumber, levelNumberX - offset, textYNumber + offset);
        fuente.draw(spriteBatch, levelNumber, levelNumberX + offset, textYNumber + offset);
        fuente.draw(spriteBatch, levelNumber, levelNumberX - offset, textYNumber - offset);
        fuente.draw(spriteBatch, levelNumber, levelNumberX + offset, textYNumber - offset);

        fuente.setColor(1, 1, 1, 1); // Blanco/Azul
        fuente.draw(spriteBatch, levelNumber, levelNumberX, textYNumber);

        fuente.getData().setScale(1.4f);

        spriteBatch.end();
    }

    /**
     * Dibuja la barra de salud del jugador. Incluye borde, fondo y la parte que indica la cantidad de vida restante
     */
    private void renderizarBarraDeSalud() {
        float healthPercentage = jugador.obtenerPorcetajeVida();
        float barWidth = GestorConstantes.HUD_BAR_WIDTH;
        float barHeight = GestorConstantes.HUD_BAR_HEIGHT;
        float barX = GestorConstantes.HUD_BAR_X;
        float barY = GestorConstantes.HUD_HEIGHT - barHeight - GestorConstantes.HUD_BAR_Y_OFFSET;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Borde negro
        shapeRenderer.setColor(0, 0, 0, 1f);
        shapeRenderer.rect(barX - 2f, barY - 2, barWidth + 4f, barHeight + 4f);

        // Fondo rojo
        shapeRenderer.setColor(0.8f, 0.1f, 0.1f, 0.5f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Barra de salud actual (verde según el porcentaje de vida)
        shapeRenderer.setColor(0.1f, 0.8f, 0.1f, 0.8f);
        shapeRenderer.rect(barX, barY, barWidth * healthPercentage, barHeight);

        shapeRenderer.end();
    }

    /**
     * Muestra el texto numérico de la vida del jugador (ej. "45/50") encima de la barra de salud
     *
     * @param hudHeight altura del HUD para posicionar el texto
     */
    private void renderizarTextoSalud(float hudHeight) {
        float barX = 75f;
        float barWidth = 180f;
        float barY = hudHeight - 24;

        String healthText = (int) jugador.getVidaJugador() + " / " + (int) jugador.getMaxVidaJugador();

        layout.setText(fuente, healthText);
        float textWidth = layout.width;
        float textX = barX + (barWidth - textWidth) / 2;

        spriteBatch.begin();

        fuente.setColor(0, 0, 0, 1); // color negro (sombra)
        fuente.getData().setScale(1.2f, 1.1f);
        float offset = 1f;

        // Dibujamos el texto desplazado en las cuatro direcciones para efecto "negrita"
        fuente.draw(spriteBatch, healthText, textX - offset, barY + offset);
        fuente.draw(spriteBatch, healthText, textX + offset, barY + offset);
        fuente.draw(spriteBatch, healthText, textX - offset, barY - offset);
        fuente.draw(spriteBatch, healthText, textX + offset, barY - offset);

        fuente.setColor(1, 1, 1, 1);
        fuente.draw(spriteBatch, healthText, textX, barY);
        fuente.getData().setScale(1.0f);

        spriteBatch.end();
    }

    /**
     * Dibuja el icono de corazón que representa la vida del jugador
     */
    private void renderizarIconoVidaJugador() {
        float heartSize = GestorConstantes.HEART_SIZE;
        float heartX = GestorConstantes.HEART_X;
        float heartY = GestorConstantes.HUD_HEIGHT - heartSize - GestorConstantes.HEART_Y_OFFSET;

        spriteBatch.begin();
        spriteBatch.draw(texturaCorazonVida, heartX, heartY, heartSize, heartSize);
        spriteBatch.end();
    }

    /**
     * Dibuja la barra de experiencia (XP) del jugador. Incluye fondo, la parte azul según la fracción actual de XP y un icono
     */
    private void renderizarBarraXP() {
        float barWidth = GestorConstantes.HUD_BAR_WIDTH;
        float barHeight = GestorConstantes.HUD_BAR_HEIGHT;
        float barX = GestorConstantes.HUD_BAR_X;
        float barY = GestorConstantes.HUD_HEIGHT - barHeight - GestorConstantes.HUD_BAR_Y_OFFSET - 38;
        float experiencePercentage = sistemaDeNiveles.getXpActual() / sistemaDeNiveles.getXpHastaSiguienteNivel();

        renderizarFondoBarraXP(barX, barY, barWidth, barHeight, experiencePercentage);
        renderizarTextoBarraXP(barX, barY - 2, barWidth, barHeight);
        renderizarIconoBarraXP(barX, barY, barHeight);
    }

    /**
     * Dibuja el fondo y la fracción azul de la barra de experiencia
     *
     * @param barX,barY            X,Y de la barra
     * @param barWidth,barHeight             ancho, alto de la barra
     * @param experiencePercentage fracción de XP actual / XP necesaria
     */
    private void renderizarFondoBarraXP(float barX, float barY, float barWidth, float barHeight, float experiencePercentage) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Borde negro
        shapeRenderer.setColor(0, 0, 0, 1f);
        shapeRenderer.rect(barX - 2f, barY - 2f, barWidth + 4f, barHeight + 4f);

        // Fondo gris claro
        shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 0.5f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Barra azul con la fracción de XP
        shapeRenderer.setColor(0.1f, 0.6f, 0.9f, 0.8f);
        shapeRenderer.rect(barX, barY, barWidth * experiencePercentage, barHeight);

        shapeRenderer.end();
    }

    /**
     * Dibuja el texto numérico encima de la barra de XP
     *
     * @param barX,barY      X, Y de la barra
     * @param barWidth,barHeight  ancho, alto de la barra
     */
    private void renderizarTextoBarraXP(float barX, float barY, float barWidth, float barHeight) {
        String experienceText = (int) sistemaDeNiveles.getXpActual() + "/" + (int) sistemaDeNiveles.getXpHastaSiguienteNivel();
        layout.setText(fuente, experienceText);
        float textWidth = layout.width;
        float textX = barX + (barWidth - textWidth) / 2;
        float textY = barY + (barHeight + layout.height + 1) / 2;

        spriteBatch.begin();
        fuente.setColor(0, 0, 0, 1);
        fuente.getData().setScale(1.2f, 1.1f);

        float offset = 1f;
        // Dibujamos 4 veces para la sombra
        fuente.draw(spriteBatch, experienceText, textX - offset, textY + offset);
        fuente.draw(spriteBatch, experienceText, textX + offset, textY + offset);
        fuente.draw(spriteBatch, experienceText, textX - offset, textY - offset);
        fuente.draw(spriteBatch, experienceText, textX + offset, textY - offset);

        fuente.setColor(1, 1, 1, 1);
        fuente.draw(spriteBatch, experienceText, textX, textY);

        fuente.getData().setScale(1.0f);
        spriteBatch.end();
    }

    /**
     * Dibuja un icono al lado de la barra de XP como indicativo
     */
    private void renderizarIconoBarraXP(float barX, float barY, float barHeight) {
        float iconSize = GestorConstantes.HEART_SIZE;
        float iconX = barX - iconSize - 5f;
        float iconY = barY - 5f;

        spriteBatch.begin();
        spriteBatch.draw(texturaLapizXP, iconX, iconY, iconSize - 10, iconSize);
        spriteBatch.end();
    }

    /**
     * Ajusta el viewport del HUD al redimensionar la ventana
     *
     * @param width,height  nuevo ancho, alto
     */
    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
    }

    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        texturaCorazonVida.dispose();
        texturaLapizXP.dispose();
    }
}
