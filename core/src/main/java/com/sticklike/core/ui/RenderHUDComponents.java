package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.gameplay.managers.ControladorProyectiles;
import com.sticklike.core.gameplay.sistemas.SistemaDeNiveles;
import com.sticklike.core.utilidades.GestorConstantes;
import com.sticklike.core.utilidades.GestorDeAssets;

import java.text.DecimalFormat;

import static com.sticklike.core.utilidades.GestorConstantes.*;


public class RenderHUDComponents {
    private ShapeRenderer shapeRenderer;
    private GlyphLayout layout;
    private BitmapFont fuente;
    private SpriteBatch spriteBatch;
    private Jugador jugador;
    private SistemaDeNiveles sistemaDeNiveles;
    private ControladorProyectiles controladorProyectiles;
    private final Texture texturaCorazonVida, texturaLapizXP;
    private float tiempoTranscurrido = 0;
    private String tiempoFormateado;

    public RenderHUDComponents(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, Jugador jugador, SistemaDeNiveles sistemaDeNiveles) {
        this.sistemaDeNiveles = sistemaDeNiveles;
        this.shapeRenderer = shapeRenderer;
        this.layout = new GlyphLayout();
        this.fuente = new BitmapFont();
        this.spriteBatch = spriteBatch;
        this.jugador = jugador;
        this.texturaCorazonVida = GestorDeAssets.corazonVida;
        this.texturaLapizXP = GestorDeAssets.iconoXP;
        this.controladorProyectiles = jugador.getControladorProyectiles();
    }

    /**
     * Renderiza un temporizador en la pantalla del HUD, mostrando el tiempo transcurrido desde el inicio del juego
     *
     * @param delta Tiempo transcurrido entre frames
     */
    public void renderizarTemporizador(float delta) {
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
     * Dibuja un fondo claro para la zona del HUD
     */
    public void renderizarFondoHUD() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1.0f, 0.96f, 0.78f, 1);
        shapeRenderer.rect(0, 0, VIRTUAL_WIDTH, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);
        shapeRenderer.end();
    }

    /**
     * Dibuja un marco alrededor del HUD con un borde negro y una sombra más clara que sobresale
     */
    public void renderizarMarcoHUD() {
        float grosorMarcoNegro = 0.9f;
        float grosorSombra = 0.7f;
        float grosorTotalSombra = grosorMarcoNegro + grosorSombra;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dibujar REBORDE NEGRO
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);

        // Parte superior reborde
        shapeRenderer.rect(-grosorSombra, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD - grosorTotalSombra, VIRTUAL_WIDTH + 2 * grosorSombra, grosorTotalSombra);
        // Parte izquierda reborde
        shapeRenderer.rect(-grosorSombra, -grosorSombra, grosorTotalSombra, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD + grosorSombra);
        // Parte derecha reborde
        shapeRenderer.rect(VIRTUAL_WIDTH - grosorMarcoNegro, -grosorSombra, grosorTotalSombra, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD + grosorSombra);

        // Dibujar SOMBRA
        shapeRenderer.setColor(0.45f, 0.45f, 0.45f, 1);

        // Parte superior sombra
        shapeRenderer.rect(0, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD - grosorMarcoNegro, VIRTUAL_WIDTH, grosorMarcoNegro);
        // Parte izquierda sombra
        shapeRenderer.rect(0, 0, grosorMarcoNegro, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);
        // Parte derecha sombra
        shapeRenderer.rect(VIRTUAL_WIDTH - grosorMarcoNegro, 0, grosorMarcoNegro, GestorConstantes.HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);

        shapeRenderer.end();
    }


    /**
     * Dibuja una cuadrícula (grid) en la zona del HUD, utilizando shapeRenderer en modo línea (horizontal)
     *
     * @param alturaHUD altura del área del HUD
     */
    public void renderizarLineasHorizontalesCuadricula(float alturaHUD) { // Solo hacemos horizontales porque es otro estilo de folio de cuaderno
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
    public void renderizarTextoNivelPlayer() {
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
    public void renderizarBarraDeSalud() {
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
    public void renderizarTextoSalud(float hudHeight) {
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
    public void renderizarIconoVidaJugador() {
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
    public void renderizarBarraXP() {
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

    public void renderizarStatsJugador() {
        DecimalFormat df = new DecimalFormat("#.##");

        // Definimos los textos de las estadísticas
        String velocidadJugador = "Vel. Movimiento   " + df.format(jugador.getVelocidadJugador());
        String rangoJugador = "Rango Disparo   " + df.format(jugador.getRangoAtaqueJugador());
        String velocidadAtaque = "Vel. Ataque   " + df.format(jugador.getVelocidadAtaque());
        String fuerzaAtaque = "Fuerza   " + df.format(jugador.getDanyoAtaqueJugador());
        String numProyectiles = "Núm. Proyectiles   " + df.format(jugador.getProyectilesPorDisparo());

        // Definimos las posiciones de renderizado (parte derecha del HUD)
        float margenDerecho = 75f;
        float statsX = VIRTUAL_WIDTH - margenDerecho;
        float statsY = GestorConstantes.HUD_HEIGHT - 42.5f;
        float espaciado = 18f; // Espacio entre líneas

        // Calculamos el ancho del texto más largo para alinear todas las líneas
        float maxWidth = calcularAnchoMaximoTexto(velocidadJugador, rangoJugador, velocidadAtaque, fuerzaAtaque);

        spriteBatch.begin();

        fuente.getData().setScale(0.8f);
        fuente.setColor(Color.BLACK);

        // Renderizamos cada línea de estadísticas alineadas a la derecha
        fuente.draw(spriteBatch, fuerzaAtaque, statsX - maxWidth, statsY);
        fuente.draw(spriteBatch, velocidadAtaque, statsX - maxWidth, statsY - espaciado);
        fuente.draw(spriteBatch, velocidadJugador, statsX - maxWidth, statsY - 2 * espaciado);
        fuente.draw(spriteBatch, rangoJugador, statsX - maxWidth, statsY - 3 * espaciado);
        fuente.draw(spriteBatch, numProyectiles, statsX - maxWidth, statsY - 4 * espaciado);

        spriteBatch.end();
    }

    /**
     * Calcula el ancho máximo entre varias líneas de texto.
     *
     * @param textos Varias líneas de texto.
     * @return El ancho máximo entre las líneas.
     */
    private float calcularAnchoMaximoTexto(String... textos) {
        float maxWidth = 0f;
        for (String texto : textos) {
            layout.setText(fuente, texto);
            if (layout.width > maxWidth) {
                maxWidth = layout.width;
            }
        }
        return maxWidth;
    }


    public void dispose() {
        if (texturaCorazonVida != null) texturaCorazonVida.dispose();
        if (texturaLapizXP != null) texturaLapizXP.dispose();
        if (fuente != null) fuente.dispose();
    }
}
