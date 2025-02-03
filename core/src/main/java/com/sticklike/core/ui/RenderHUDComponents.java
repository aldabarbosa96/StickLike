package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.gameplay.controladores.ControladorProyectiles;
import com.sticklike.core.gameplay.sistemas.SistemaDeNiveles;

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

import java.text.DecimalFormat;

/**
 * Clase encargada de dibujar los elementos del HUD en pantalla, como los stats, experiencia, nivel, temporizador, etc.
 * Ahora utiliza una cámara y viewport propios para el HUD, de forma similar al MenuPause,
 * lo que garantiza que se mantenga la relación de aspecto definida (por ejemplo, 1600x900) sin importar el tamaño real.
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

    // --- NUEVA CAMARA Y VIEWPORT PARA EL HUD ---
    private OrthographicCamera hudCamera;
    private Viewport hudViewport;

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

        // Inicializamos la cámara y viewport para el HUD usando la resolución virtual (por ejemplo, 1600x900)
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        hudViewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, hudCamera);
        hudViewport.apply();
    }

    public void renderizarTemporizador(float delta) {
        // Usar la proyección del HUD
        spriteBatch.setProjectionMatrix(hudCamera.combined);

        if (!pausadoTemporizador) {
            tiempoTranscurrido += delta;
        }
        tiempoFormateado = formatearTiempo(tiempoTranscurrido);

        // Se añade un offset vertical adicional para subir el timer
        float timerVerticalOffset = 90f; // Ajusta según prefieras
        float textX = (VIRTUAL_WIDTH / 2) - (jugador.getSprite().getWidth() / 2f + 15f);
        float textY = (VIRTUAL_HEIGHT / 2) + TIMER_Y_POS + timerVerticalOffset;

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
        // Usar la proyección del HUD en la shapeRenderer
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1.0f, 0.95f, 0.75f, 1);
        shapeRenderer.rect(0, 0, VIRTUAL_WIDTH, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);
        shapeRenderer.end();
    }

    public void renderizarMarcoHUD() {
        float grosorMarcoNegro = GROSOR_MARCO;
        float grosorSombra = GROSOR_SOMBRA;
        float grosorTotalSombra = grosorMarcoNegro + grosorSombra;

        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dibujar reborde negro
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);
        shapeRenderer.rect(-grosorSombra, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD - grosorTotalSombra, VIRTUAL_WIDTH + BORDER_CORRECTION * grosorSombra, grosorTotalSombra);
        shapeRenderer.rect(-grosorSombra, -grosorSombra, grosorTotalSombra, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD + grosorSombra);
        shapeRenderer.rect(VIRTUAL_WIDTH - grosorMarcoNegro, -grosorSombra, grosorTotalSombra, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD + grosorSombra);

        // Dibujar sombra
        shapeRenderer.setColor(0.45f, 0.45f, 0.45f, 1);
        shapeRenderer.rect(0, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD - grosorMarcoNegro, VIRTUAL_WIDTH, grosorMarcoNegro);
        shapeRenderer.rect(0, 0, grosorMarcoNegro, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);
        shapeRenderer.rect(VIRTUAL_WIDTH - grosorMarcoNegro, 0, grosorMarcoNegro, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);

        shapeRenderer.end();
    }

    public void renderizarLineasHorizontalesCuadricula(float alturaHUD) {
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.64f, 0.80f, 0.86f, 1);

        float cellSize = GRID_CELL_SIZE - GRID_CELL_SIZE_CORRECTION;
        for (float y = 0; y <= alturaHUD; y += cellSize) {
            shapeRenderer.line(0, y, VIRTUAL_WIDTH, y);
        }
        shapeRenderer.end();
    }

    public void renderizarTextoNivelPlayer() {
        layout.setText(fuente, TEXTO_LVL + sistemaDeNiveles.getNivelActual());

        float textX = (VIRTUAL_WIDTH - layout.width) / 2 - TEXT_X_CORRECTION + 2f;
        float textY = HUD_HEIGHT - TEXT_Y_CORRECTION + DESPLAZAMIENTO_VERTICAL_HUD;

        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        fuente.getData().setScale(0.95f);
        Color colorReborde = Color.BLACK;
        Color colorTexto = Color.WHITE;
        dibujarTextoConReborde(spriteBatch, TEXTO_LVL, textX, textY, BASIC_OFFSET, colorReborde, colorTexto);

        // Dibujar el número del nivel
        float levelTextWidth = new GlyphLayout(fuente, TEXTO_LVL).width;
        float levelNumberX = textX + levelTextWidth + 2f;
        float textYNumber = HUD_HEIGHT - NUMBER_Y_CORRECTION + DESPLAZAMIENTO_VERTICAL_HUD;

        fuente.getData().setScale(1.4f);
        colorReborde = Color.BLUE;
        dibujarTextoConReborde(spriteBatch, String.valueOf(sistemaDeNiveles.getNivelActual()), levelNumberX, textYNumber, BASIC_OFFSET, colorReborde, colorTexto);
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
    }

    private void renderizarFondoBarraXP(float barX, float barY, float barWidth, float barHeight, float experiencePercentage) {
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
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
        float xpActual = sistemaDeNiveles.getXpActual();
        float xpHastaSiguienteNivel = sistemaDeNiveles.getXpHastaSiguienteNivel();
        int porcentajeXP = (int) ((xpActual / xpHastaSiguienteNivel) * 100);
        String experienceText = porcentajeXP + " %";
        layout.setText(fuente, experienceText);
        float textWidth = layout.width;
        float textHeight = layout.height;
        float textX = barX + (barWidth - textWidth) / 2;
        float textY = barY + (barHeight + textHeight) / 2 + XPTEXT_Y_CORRECTION;

        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        fuente.getData().setScale(0.8f);
        dibujarTextoConReborde(spriteBatch, experienceText, textX, textY, UNDER_OFFSET, Color.DARK_GRAY, Color.WHITE);
        spriteBatch.end();
    }

    public void renderizarCacaDorada(float oroAcumulado) {
        float iconSize = 15f;
        float posX = HUD_BAR_X + 10f;
        float posY = HUD_HEIGHT - HUD_BAR_Y_OFFSET - XPBAR_Y_CORRECTION - 72f;
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        // Dibujar icono de caca
        spriteBatch.draw(recolectableCacaDorada, posX, posY, iconSize, iconSize);
        // Dibujar contador numérico al lado
        DecimalFormat df = new DecimalFormat("#");
        String cantidad = df.format(oroAcumulado);
        fuente.getData().setScale(0.8f);
        float textX = posX + iconSize + 4f;
        float textY = posY + iconSize / 2 + 4;
        dibujarTextoConReborde(spriteBatch, cantidad, textX, textY, 1f, Color.DARK_GRAY, Color.GOLD);
        spriteBatch.end();
    }

    public void renderizarStatsJugador() {
        DecimalFormat df = new DecimalFormat("#.##");
        String valorVelocidad = df.format(jugador.getVelocidadJugador());
        String valorRango = df.format(jugador.getRangoAtaqueJugador());
        String valorVelAtaque = df.format(jugador.getVelocidadAtaque()) + " %";
        String valorFuerza = df.format(jugador.getDanyoAtaqueJugador());
        String valorProyectiles = "+" + df.format(jugador.getProyectilesPorDisparo());
        String[] descripciones = {VEL_MOV, RANGO, VEL_ATAQUE, FUERZA, NUM_PROYECTILES};
        String[] valores = {valorVelocidad, valorRango, valorVelAtaque, valorFuerza, valorProyectiles};
        Texture[] iconos = {iconoVelMov, iconoRango, iconoVelAt, iconoFuerza, iconoProyectiles};
        float statsX = VIRTUAL_WIDTH - STATS_X_CORRECTION + 10;
        float statsY = HUD_HEIGHT - STATS_Y_CORRECTION - 10f;
        renderizarBloqueStatsConIconos(descripciones, iconos, valores, statsX, statsY, ANCHO_DESC1);
    }

    public void renderizarMasStatsJugador() {
        DecimalFormat df = new DecimalFormat("#.#");
        String valorVidaMaxima = df.format(jugador.getVidaJugador()) + " / " + df.format(jugador.getMaxVidaJugador());
        String valorRegeneracionVida = df.format(jugador.getRegVidaJugador() * 100) + " %";
        String valorPoderAtaque = df.format(0);
        String valorResistencia = df.format(jugador.getResistenciaJugador() * 100) + " %";
        String valorCritico = df.format(jugador.getCritico() * 100) + " %";
        String[] descripciones = {VIDA_MAX, REG_VIDA, PODER, RESIST, CRITIC};
        String[] valores = {valorVidaMaxima, valorRegeneracionVida, valorPoderAtaque, valorResistencia, valorCritico};
        Texture[] iconos = {iconoVida, iconoRegeneracion, iconoPoder, iconoResistencia, iconoCritico};
        float statsX = VIRTUAL_WIDTH - STATS_X_CORRECTION2 + 10f;
        float statsY = HUD_HEIGHT - STATS_Y_CORRECTION - 10f;
        renderizarBloqueStatsConIconos(descripciones, iconos, valores, statsX, statsY, ANCHO_DESC2);
    }

    private void renderizarBloqueStatsConIconos(String[] descripciones, Texture[] iconos, String[] valores, float statsX, float statsY, float anchoDescripcion) {
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        fuente.getData().setScale(0.8f);
        for (int i = 0; i < descripciones.length; i++) {
            float posicionY = statsY - i * ESPACIADO;
            // Descripción a la izquierda
            fuente.setColor(Color.BLACK);
            fuente.draw(spriteBatch, descripciones[i], statsX - anchoDescripcion, posicionY);
            // Icono en medio
            if (iconos != null && i < iconos.length && iconos[i] != null) {
                Texture icono = iconos[i];
                float iconSize = STATS_ICON_SIZE;
                float iconX = statsX - (iconSize * 0.6f);
                float iconY = posicionY - iconSize / 2f - ICON_Y_CORRECTION;
                spriteBatch.draw(icono, iconX, iconY, iconSize, iconSize);
            }
            dibujarTextoConReborde(spriteBatch, valores[i], statsX + ESPACIADO_LATERAL, posicionY, BASIC_OFFSET, Color.GRAY, Color.WHITE);
        }
        spriteBatch.end();
    }

    private void dibujarTextoConReborde(SpriteBatch batch, String texto, float x, float y, float offset, Color colorReborde, Color colorTexto) {
        fuente.setColor(colorReborde);
        // Dibujar sombra en las 8 direcciones cardinales
        fuente.draw(batch, texto, x - offset, y); // O
        fuente.draw(batch, texto, x + offset, y); // E
        fuente.draw(batch, texto, x, y - offset); // S
        fuente.draw(batch, texto, x, y + offset); // N
        fuente.draw(batch, texto, x - offset, y + offset); // NO
        fuente.draw(batch, texto, x + offset, y + offset); // NE
        fuente.draw(batch, texto, x - offset, y - offset); // SO
        fuente.draw(batch, texto, x + offset, y - offset); // SE
        fuente.setColor(colorTexto);
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
