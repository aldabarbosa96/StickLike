package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.gameplay.sistemas.SistemaDeNiveles;
import com.sticklike.core.utilidades.GestorDeAssets;
import com.sticklike.core.utilidades.GestorConstantes;

/**
 * Clase responsable de dibujar la información de interfaz del jugador en pantalla (vida, experiencia, nivel, etc.)
 * <p>
 * Utiliza un {@link ShapeRenderer} para dibujar formas (barras de vida y XP) y un {@link SpriteBatch} para renderizar
 * iconos y texto. Maneja también una cámara y un viewport independientes para la interfaz (HUD)
 * todo --> mover toda la lógica de renderizado de componentes del HUD a otra clase dedicada
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
    private float tiempoTranscurrido;
    private String tiempoFormateado;
    private final GlyphLayout layout; // Sirve para calcular el tamaño del texto
    private static final float VIRTUAL_WIDTH = GestorConstantes.VIRTUAL_WIDTH;
    private static final float VIRTUAL_HEIGHT = GestorConstantes.VIRTUAL_HEIGHT;
    private static final float DESPLAZAMIENTO_VERTICAL_HUD = GestorConstantes.DESPLAZAMIENTO_VERTICAL_HUD;

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
        this.tiempoTranscurrido = 0;
        this.tiempoFormateado = formatearTiempo(tiempoTranscurrido);

        // Configuramos la cámara y el viewport
        this.hudCamara = new OrthographicCamera();
        this.hudViewport = new FillViewport(GestorConstantes.VIRTUAL_WIDTH, GestorConstantes.VIRTUAL_HEIGHT, hudCamara);
        this.hudCamara.update();
    }


    /**
     * Renderiza un temporizador en la pantalla del HUD, mostrando el tiempo transcurrido desde el inicio del juego
     *
     * @param delta Tiempo transcurrido entre frames
     */
    private void renderizarTemporizador(float delta) {
        // Actualizamos el tiempo transcurrido
        tiempoTranscurrido += delta;

        // Formateamos el tiempo en "MM:SS"
        tiempoFormateado = formatearTiempo(tiempoTranscurrido);

        // Configuramos la posición del texto
        float textX = (VIRTUAL_WIDTH / 2) - (jugador.getSprite().getWidth() / 2f + 10f);
        float textY = VIRTUAL_HEIGHT / 2 + 285f;

        spriteBatch.begin();

        fuente.getData().setScale(0.8f);
        // Dibujamos el texto principal
        fuente.setColor(0, 0, 0, 1);
        fuente.draw(spriteBatch, tiempoFormateado, textX, textY);

        spriteBatch.end();
    }

    /**
     * Convierte el tiempo en segundos a un formato "MM:SS"
     *
     * @param tiempoSegundos Tiempo en segundos.
     * @return Tiempo formateado como "MM:SS"
     */
    private String formatearTiempo(float tiempoSegundos) {
        int minutos = (int) (tiempoSegundos / 60);
        int segundos = (int) (tiempoSegundos % 60);
        return String.format("%02d : %02d", minutos, segundos);
    }


    /**
     * Dibuja el HUD estático en la parte inferior de la pantalla:
     * fondo, barras de salud y XP, iconos, texto de nivel, etc.
     * todo -- > falta implementar elementos en el HUD (stats player, mejoras obtenidas...)
     */
    public void renderizarHUD(float delta) {
        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudCamara.combined);
        shapeRenderer.setProjectionMatrix(hudCamara.combined);

        float hudHeight = 200f + DESPLAZAMIENTO_VERTICAL_HUD;
        renderizarFondoHUD();
        renderizarLineasHorizontalesCuadricula(hudHeight);
        renderizarMarcoHUD();
        renderizarBarraDeSalud();
        renderizarBarraXP();
        renderizarTextoSalud(hudHeight);
        renderizarIconoVidaJugador();
        renderizarTextoNivelPlayer();
        renderizarTemporizador(delta);
    }

    /**
     * Dibuja un fondo claro para la zona del HUD
     */
    private void renderizarFondoHUD() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1.0f, 0.96f, 0.78f, 1);
        shapeRenderer.rect(0, 0, GestorConstantes.VIRTUAL_WIDTH, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);
        shapeRenderer.end();
    }

    /**
     * Dibuja un marco alrededor del HUD con un borde negro y una sombra más clara que sobresale
     */
    private void renderizarMarcoHUD() {
        float grosorMarcoNegro = 0.9f;
        float grosorSombra = 0.7f;
        float grosorTotalSombra = grosorMarcoNegro + grosorSombra;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dibujar REBORDE NEGRO
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);

        // Parte superior reborde
        shapeRenderer.rect(-grosorSombra, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD - grosorTotalSombra, GestorConstantes.VIRTUAL_WIDTH + 2 * grosorSombra, grosorTotalSombra);
        // Parte izquierda reborde
        shapeRenderer.rect(-grosorSombra, -grosorSombra, grosorTotalSombra, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD + grosorSombra);
        // Parte derecha reborde
        shapeRenderer.rect(GestorConstantes.VIRTUAL_WIDTH - grosorMarcoNegro, -grosorSombra, grosorTotalSombra, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD + grosorSombra);

        // Dibujar SOMBRA
        shapeRenderer.setColor(0.45f, 0.45f, 0.45f, 1);

        // Parte superior sombra
        shapeRenderer.rect(0, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD - grosorMarcoNegro, GestorConstantes.VIRTUAL_WIDTH, grosorMarcoNegro);
        // Parte izquierda sombra
        shapeRenderer.rect(0, 0, grosorMarcoNegro, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);
        // Parte derecha sombra
        shapeRenderer.rect(GestorConstantes.VIRTUAL_WIDTH - grosorMarcoNegro, 0, grosorMarcoNegro, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);

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
        float cellSize = GestorConstantes.GRID_CELL_SIZE - 20f;

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
        float textY = GestorConstantes.HUD_HEIGHT - 13f + DESPLAZAMIENTO_VERTICAL_HUD;

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
        float textYNumber = GestorConstantes.HUD_HEIGHT - 8.5f + DESPLAZAMIENTO_VERTICAL_HUD;

        // Texto número
        fuente.getData().setScale(1.4f);
        fuente.setColor(0, 0, 1, 1);
        fuente.draw(spriteBatch, levelNumber, levelNumberX - offset, textYNumber + offset);
        fuente.draw(spriteBatch, levelNumber, levelNumberX + offset, textYNumber + offset);
        fuente.draw(spriteBatch, levelNumber, levelNumberX - offset, textYNumber - offset);
        fuente.draw(spriteBatch, levelNumber, levelNumberX + offset, textYNumber - offset);

        fuente.setColor(1, 1, 1, 1); // Blanco/Azul
        fuente.draw(spriteBatch, levelNumber, levelNumberX, textYNumber);

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
        float barY = GestorConstantes.HUD_HEIGHT - barHeight - GestorConstantes.HUD_BAR_Y_OFFSET + DESPLAZAMIENTO_VERTICAL_HUD + 5f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Borde negro
        shapeRenderer.setColor(0, 0, 0, 1f);
        shapeRenderer.rect(barX - 1.8f, barY - 1.8f, barWidth + 3.6f, barHeight + 3.6f);

        // Fondo rojo
        shapeRenderer.setColor(0.8f, 0.1f, 0.1f, 0.5f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Barra de salud actual (verde según el porcentaje de vida)
        shapeRenderer.setColor(0.1f, 0.8f, 0.1f, 0.8f);
        shapeRenderer.rect(barX, barY, barWidth * healthPercentage, barHeight);

        shapeRenderer.end();
    }

    /**
     * Muestra el texto numérico de la vida del jugador (ej. "45/50") dentro de la barra de salud.
     *
     * @param hudHeight altura del HUD para posicionar el texto
     */
    private void renderizarTextoSalud(float hudHeight) {
        float barX = GestorConstantes.HUD_BAR_X;
        float barWidth = GestorConstantes.HUD_BAR_WIDTH;
        float barHeight = GestorConstantes.HUD_BAR_HEIGHT;
        float barY = GestorConstantes.HUD_HEIGHT - barHeight - GestorConstantes.HUD_BAR_Y_OFFSET + DESPLAZAMIENTO_VERTICAL_HUD + 5f;

        String healthText = (int) jugador.getVidaJugador() + " / " + (int) jugador.getMaxVidaJugador();

        layout.setText(fuente, healthText);
        float textWidth = layout.width;
        float textHeight = layout.height + 1.5f;

        // Centramos el texto dentro de la barra
        float textX = barX + (barWidth - textWidth) / 2;
        float textY = barY + (barHeight + textHeight) / 2;

        spriteBatch.begin();

        fuente.setColor(0, 0, 0, 1); // Sombra negra
        float offset = 1f;
        fuente.draw(spriteBatch, healthText, textX - offset, textY + offset);
        fuente.draw(spriteBatch, healthText, textX + offset, textY + offset);
        fuente.draw(spriteBatch, healthText, textX - offset, textY - offset);
        fuente.draw(spriteBatch, healthText, textX + offset, textY - offset);

        fuente.setColor(1, 1, 1, 1); // Texto blanco
        fuente.draw(spriteBatch, healthText, textX, textY);

        spriteBatch.end();
    }

    /**
     * Dibuja el icono de corazón que representa la vida del jugador
     */
    private void renderizarIconoVidaJugador() {
        float heartSize = GestorConstantes.HEART_SIZE;
        float heartX = GestorConstantes.HEART_X;
        float heartY = GestorConstantes.HUD_HEIGHT - heartSize - GestorConstantes.HEART_Y_OFFSET + DESPLAZAMIENTO_VERTICAL_HUD;

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
        float barY = GestorConstantes.HUD_HEIGHT - barHeight - GestorConstantes.HUD_BAR_Y_OFFSET - 25f + DESPLAZAMIENTO_VERTICAL_HUD;
        float experiencePercentage = sistemaDeNiveles.getXpActual() / sistemaDeNiveles.getXpHastaSiguienteNivel();

        renderizarFondoBarraXP(barX, barY, barWidth, barHeight, experiencePercentage);
        renderizarTextoBarraXP(barX, barY - 1, barWidth, barHeight);
        renderizarIconoBarraXP(barX, barY, barHeight);
    }

    /**
     * Dibuja el fondo y la fracción azul de la barra de experiencia
     *
     * @param barX,barY            X,Y de la barra
     * @param barWidth,barHeight   ancho, alto de la barra
     * @param experiencePercentage fracción de XP actual / XP necesaria
     */
    private void renderizarFondoBarraXP(float barX, float barY, float barWidth, float barHeight, float experiencePercentage) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Borde negro
        shapeRenderer.setColor(0, 0, 0, 1f);
        shapeRenderer.rect(barX - 1.8f, barY - 1.8f, barWidth + 3.6f, barHeight + 3.6f);

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
     * @param barX,barY          X, Y de la barra
     * @param barWidth,barHeight ancho, alto de la barra
     */
    /**
     * Dibuja el texto numérico encima de la barra de XP centrado dentro de la barra.
     *
     * @param barX,barY          X, Y de la barra
     * @param barWidth,barHeight ancho, alto de la barra
     */
    private void renderizarTextoBarraXP(float barX, float barY, float barWidth, float barHeight) {
        // Calculamos el porcentaje de experiencia
        float xpActual = sistemaDeNiveles.getXpActual();
        float xpHastaSiguienteNivel = sistemaDeNiveles.getXpHastaSiguienteNivel();
        int porcentajeXP = (int) ((xpActual / xpHastaSiguienteNivel) * 100);

        // Formateamos el texto para mostrar el porcentaje
        String experienceText = porcentajeXP + "%";
        layout.setText(fuente, experienceText);

        float textWidth = layout.width;
        float textHeight = layout.height;

        // Centramos el texto dentro de la barra
        float textX = barX + (barWidth - textWidth) / 2;
        float textY = barY + (barHeight + textHeight) / 2 + 1.5f;

        spriteBatch.begin();

        fuente.setColor(0, 0, 0, 1); // Sombra negra
        float offset = 1f;
        fuente.draw(spriteBatch, experienceText, textX - offset, textY + offset);
        fuente.draw(spriteBatch, experienceText, textX + offset, textY + offset);
        fuente.draw(spriteBatch, experienceText, textX - offset, textY - offset);
        fuente.draw(spriteBatch, experienceText, textX + offset, textY - offset);

        fuente.setColor(1, 1, 1, 1); // Texto blanco
        fuente.draw(spriteBatch, experienceText, textX, textY);

        spriteBatch.end();
    }


    /**
     * Dibuja un icono al lado de la barra de XP como indicativo
     */
    private void renderizarIconoBarraXP(float barX, float barY, float barHeight) {
        float iconSize = GestorConstantes.HEART_SIZE;
        float iconX = GestorConstantes.HEART_X + (GestorConstantes.HEART_SIZE / 4);
        float iconY = barY - 5f;

        spriteBatch.begin();
        spriteBatch.draw(texturaLapizXP, iconX, iconY, iconSize - 10, iconSize);
        spriteBatch.end();
    }

    /**
     * Ajusta el viewport del HUD al redimensionar la ventana
     *
     * @param width,height nuevo ancho, alto
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
