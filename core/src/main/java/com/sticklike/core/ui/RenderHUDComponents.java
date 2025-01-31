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

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

import java.text.DecimalFormat;

/**
 * Clase encargada de dibujar los elementos del hud en pantalla, como los stats, experiencia, nivel, temporizador, etc.
 */
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
    private boolean pausadoTemporizador = false;


    public RenderHUDComponents(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, Jugador jugador, SistemaDeNiveles sistemaDeNiveles) {
        this.sistemaDeNiveles = sistemaDeNiveles;
        this.shapeRenderer = shapeRenderer;
        this.layout = new GlyphLayout();
        this.fuente = new BitmapFont();
        this.spriteBatch = spriteBatch;
        this.jugador = jugador;
        this.texturaCorazonVida = corazonVida;
        this.texturaLapizXP = iconoXP;
        this.controladorProyectiles = jugador.getControladorProyectiles();
    }

    public void renderizarTemporizador(float delta) {
        if (!pausadoTemporizador) {
            tiempoTranscurrido += delta;
        }

        // Formateamos el tiempo en "MM:SS"
        tiempoFormateado = formatearTiempo(tiempoTranscurrido);

        // posición del texto
        float textX = (VIRTUAL_WIDTH / 2) - (jugador.getSprite().getWidth() / 2f + TIMER_Y_CORRECTION);
        float textY = VIRTUAL_HEIGHT / 2 + TIMER_Y_POS;

        spriteBatch.begin();

        fuente.getData().setScale(TIMER_SCALE);
        Color colorReborde = Color.BLACK;
        Color colorTexto = Color.WHITE;
        dibujarTextoConReborde(spriteBatch, tiempoFormateado, textX, textY, BASIC_OFFSET, colorReborde, colorTexto);
        spriteBatch.end();
    }

    private String formatearTiempo(float tiempoSegundos) {
        int minutos = (int) (tiempoSegundos / TIMER_SECONDS);
        int segundos = (int) (tiempoSegundos % TIMER_SECONDS);
        return String.format("%02d : %02d", minutos, segundos);
    }


    public void renderizarFondoHUD() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1.0f, 0.95f, 0.75f, 1);
        shapeRenderer.rect(0, 0, VIRTUAL_WIDTH, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);
        shapeRenderer.end();
    }

    public void renderizarMarcoHUD() {
        float grosorMarcoNegro = GROSOR_MARCO;
        float grosorSombra = GROSOR_SOMBRA;
        float grosorTotalSombra = grosorMarcoNegro + grosorSombra;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dibujar REBORDE NEGRO
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);
        shapeRenderer.rect(-grosorSombra, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD - grosorTotalSombra, VIRTUAL_WIDTH + BORDER_CORRECTION * grosorSombra, grosorTotalSombra);
        shapeRenderer.rect(-grosorSombra, -grosorSombra, grosorTotalSombra, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD + grosorSombra);
        shapeRenderer.rect(VIRTUAL_WIDTH - grosorMarcoNegro, -grosorSombra, grosorTotalSombra, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD + grosorSombra);

        // Dibujar SOMBRA
        shapeRenderer.setColor(0.45f, 0.45f, 0.45f, 1);
        shapeRenderer.rect(0, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD - grosorMarcoNegro, VIRTUAL_WIDTH, grosorMarcoNegro);
        shapeRenderer.rect(0, 0, grosorMarcoNegro, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);
        shapeRenderer.rect(VIRTUAL_WIDTH - grosorMarcoNegro, 0, grosorMarcoNegro, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);

        shapeRenderer.end();
    }


    public void renderizarLineasHorizontalesCuadricula(float alturaHUD) { // Solo hacemos horizontales porque es otro estilo de folio de cuaderno
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.64f, 0.80f, 0.86f, 1);

        float cellSize = GRID_CELL_SIZE - GRID_CELL_SIZE_CORRECTION;
        for (float y = 0; y <= alturaHUD; y += cellSize) {
            shapeRenderer.line(0, y, VIRTUAL_WIDTH, y);
        }

        shapeRenderer.end();
    }

    public void renderizarTextoNivelPlayer() {
        String levelText = TEXTO_LVL;
        String levelNumber = String.valueOf(sistemaDeNiveles.getNivelActual());

        layout.setText(fuente, levelText + levelNumber);

        float textX = (VIRTUAL_WIDTH - layout.width) / 2 - TEXT_X_CORRECTION;
        float textY = HUD_HEIGHT - TEXT_Y_CORRECTION + DESPLAZAMIENTO_VERTICAL_HUD;

        spriteBatch.begin();

        // Texto "LVL"
        fuente.getData().setScale(0.95f);
        float offset = BASIC_OFFSET;
        Color colorReborde = Color.BLACK; // reborde negro
        Color colorTexto = Color.WHITE; // texto blanco
        dibujarTextoConReborde(spriteBatch, levelText, textX, textY, offset, colorReborde, colorTexto);

        // Coordenadas número
        float levelTextWidth = new GlyphLayout(fuente, levelText).width;
        float levelNumberX = textX + levelTextWidth;
        float textYNumber = HUD_HEIGHT - NUMBER_Y_CORRECTION + DESPLAZAMIENTO_VERTICAL_HUD;

        // Texto número del nivel
        fuente.getData().setScale(1.4f);
        colorReborde = Color.BLUE; // reborde azul
        dibujarTextoConReborde(spriteBatch, levelNumber, levelNumberX, textYNumber, offset, colorReborde, colorTexto);

        spriteBatch.end();
    }

    public void renderizarBarraXP() {
        float barWidth = HUD_BAR_WIDTH;
        float barHeight = HUD_BAR_HEIGHT;
        float barX = HUD_BAR_X;
        float barY = HUD_HEIGHT - barHeight - HUD_BAR_Y_OFFSET - XPBAR_Y_CORRECTION + DESPLAZAMIENTO_VERTICAL_HUD;
        float experiencePercentage = sistemaDeNiveles.getXpActual() / sistemaDeNiveles.getXpHastaSiguienteNivel();

        renderizarFondoBarraXP(barX, barY, barWidth, barHeight, experiencePercentage);
        renderizarTextoBarraXP(barX, barY - BASIC_OFFSET, barWidth, barHeight);
        renderizarCacaDorada(jugador.getOroGanado());
        //renderizarIconoBarraXP(barX, barY, barHeight);
    }

    private void renderizarFondoBarraXP(float barX, float barY, float barWidth, float barHeight, float experiencePercentage) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Borde negro
        shapeRenderer.setColor(0, 0, 0, 1f);
        shapeRenderer.rect(barX - BORDER_NEGATIVE, barY - BORDER_NEGATIVE, barWidth + BORDER_POSITIVE, barHeight + BORDER_POSITIVE);

        // Fondo gris claro
        shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 0.5f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Barra azul con la fracción de XP
        shapeRenderer.setColor(0f, 0.5f, 1f, 1f);
        shapeRenderer.rect(barX, barY, barWidth * experiencePercentage, barHeight);

        shapeRenderer.end();
    }


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
        float textY = barY + (barHeight + textHeight) / 2 + XPTEXT_Y_CORRECTION;

        spriteBatch.begin();

        Color colorReborde = Color.BLACK;
        Color colorTexto = Color.WHITE;
        dibujarTextoConReborde(spriteBatch, experienceText, textX, textY, UNDER_OFFSET, colorReborde, colorTexto);


        spriteBatch.end();
    }

    public void renderizarCacaDorada(float oroAcumulado) {
        float iconSize = 15f;
        float posX = HUD_BAR_X + 10f; // Margen izquierdo de la barra de XP
        float posY = HUD_HEIGHT - HUD_BAR_Y_OFFSET - XPBAR_Y_CORRECTION - 73.5f; // Debajo de la barra

        spriteBatch.begin();

        // 1. Dibujar icono de caca
        spriteBatch.draw(recolectableCacaDorada, posX, posY, iconSize, iconSize);

        // 2. Dibujar contador numérico al lado
        DecimalFormat df = new DecimalFormat("#");
        String cantidad = df.format(oroAcumulado);

        fuente.getData().setScale(0.8f);
        float textX = posX + iconSize + 4f; // Padding entre icono y texto
        float textY = posY + iconSize/2 + 5f;

        dibujarTextoConReborde(spriteBatch, cantidad, textX, textY, 1f, Color.DARK_GRAY,Color.GOLD);

        spriteBatch.end();
    }
    public void renderizarStatsJugador() {
        DecimalFormat df = new DecimalFormat("#.##");

        // Valores
        String valorVelocidad = df.format(jugador.getVelocidadJugador());
        String valorRango = df.format(jugador.getRangoAtaqueJugador());
        String valorVelAtaque = df.format(jugador.getVelocidadAtaque()) + " %";
        String valorFuerza = df.format(jugador.getDanyoAtaqueJugador());
        String valorProyectiles = "+" + df.format(jugador.getProyectilesPorDisparo());

        // Arrays de descripciones, valores e iconos
        String[] descripciones = {VEL_MOV, RANGO, VEL_ATAQUE, FUERZA, NUM_PROYECTILES};
        String[] valores = {valorVelocidad, valorRango, valorVelAtaque, valorFuerza, valorProyectiles};
        Texture[] iconos = {iconoVelMov, iconoRango, iconoVelAt, iconoFuerza, iconoProyectiles,};

        float statsX = VIRTUAL_WIDTH - STATS_X_CORRECTION;
        float statsY = HUD_HEIGHT - STATS_Y_CORRECTION;

        renderizarBloqueStatsConIconos(descripciones, iconos, valores, statsX, statsY, ANCHO_DESC1);
    }

    public void renderizarMasStatsJugador() {
        DecimalFormat df = new DecimalFormat("#.##");

        // Valores
        String valorVidaMaxima = df.format(jugador.getVidaJugador()) + " / " + jugador.getMaxVidaJugador();
        String valorRegeneracionVida = df.format(0) + " %";
        String valorPoderAtaque = df.format(0);
        String valorResistencia = df.format(jugador.getResistenciaJugador() * 100) + " %";
        String valorCritico = df.format(jugador.getCriticoJugador() * 100) + " %";

        String[] descripciones = {VIDA_MAX, REG_VIDA, PODER, RESIST, CRITIC};
        String[] valores = {valorVidaMaxima, valorRegeneracionVida, valorPoderAtaque, valorResistencia, valorCritico};
        Texture[] iconos = {iconoVida, iconoRegeneracion, iconoPoder, iconoResistencia, iconoCritico};

        float statsX = VIRTUAL_WIDTH - STATS_X_CORRECTION2;
        float statsY = HUD_HEIGHT - STATS_Y_CORRECTION;

        renderizarBloqueStatsConIconos(descripciones, iconos, valores, statsX, statsY, ANCHO_DESC2);
    }

    private void renderizarBloqueStatsConIconos(String[] descripciones, Texture[] iconos, String[] valores, float statsX, float statsY, float anchoDescripcion) {

        spriteBatch.begin();
        fuente.getData().setScale(0.7f);

        for (int i = 0; i < descripciones.length; i++) {
            float posicionY = statsY - i * ESPACIADO;

            // Descripción a la izquierda
            fuente.setColor(Color.BLACK);
            fuente.draw(spriteBatch, descripciones[i], statsX - anchoDescripcion, posicionY);

            // Icono en medio
            if (iconos != null && i < iconos.length && iconos[i] != null) {
                Texture icono = iconos[i];
                float iconSize = STATS_ICON_SIZE;
                float iconX = statsX - (iconSize * 0.5f);
                float iconY = posicionY - iconSize / 2f - ICON_Y_CORRECTION;

                spriteBatch.draw(icono, iconX, iconY, iconSize, iconSize);
            }

            dibujarTextoConReborde(spriteBatch, valores[i], statsX + ESPACIADO_LATERAL, posicionY, BASIC_OFFSET, Color.GRAY, Color.WHITE);
        }

        spriteBatch.end();
    }

    private void dibujarTextoConReborde(SpriteBatch batch, String texto, float x, float y, float offset, Color colorReborde, Color colorTexto) {
        // color reborde
        fuente.setColor(colorReborde);

        // sombreado en las 8 direcciones cardinales
        fuente.draw(batch, texto, x - offset, y); // (O)
        fuente.draw(batch, texto, x + offset, y); // (E)
        fuente.draw(batch, texto, x, y - offset); // (S)
        fuente.draw(batch, texto, x, y + offset); // (N)
        fuente.draw(batch, texto, x - offset, y + offset); // (NO)
        fuente.draw(batch, texto, x + offset, y + offset); // (NE)
        fuente.draw(batch, texto, x - offset, y - offset); // (SO)
        fuente.draw(batch, texto, x + offset, y - offset); // (SE)

        // color del texto principal
        fuente.setColor(colorTexto);

        // Dibujamos el texto principal
        fuente.draw(batch, texto, x, y);
    }

    public void dispose() {
        if (texturaCorazonVida != null) texturaCorazonVida.dispose();
        if (texturaLapizXP != null) texturaLapizXP.dispose();
        if (fuente != null) fuente.dispose();
    }

    public void pausarTemporizador() {
        pausadoTemporizador = true;
    }

    public void reanudarTemporizador() {
        pausadoTemporizador = false;
    }
}
