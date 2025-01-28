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

    /**
     * Renderiza un temporizador en la pantalla del HUD, mostrando el tiempo transcurrido desde el inicio del juego
     *
     * @param delta Tiempo transcurrido entre frames
     */
    public void renderizarTemporizador(float delta) {
        if (!pausadoTemporizador) {
            // Actualizamos el tiempo transcurrido si no está en pausa
            tiempoTranscurrido += delta;
        }

        // Formateamos el tiempo en "MM:SS"
        tiempoFormateado = formatearTiempo(tiempoTranscurrido);

        // Configuramos la posición del texto
        float textX = (VIRTUAL_WIDTH / 2) - (jugador.getSprite().getWidth() / 2f + 10f);
        float textY = VIRTUAL_HEIGHT / 2 + TIMER_Y_POS;

        spriteBatch.begin();

        fuente.getData().setScale(TIMER_SCALE);
        float offset = 1f; // Tamaño del reborde
        Color colorReborde = Color.BLACK;
        Color colorTexto = Color.WHITE;
        dibujarTextoConReborde(spriteBatch, tiempoFormateado, textX, textY, offset, colorReborde, colorTexto);
        spriteBatch.end();
    }

    /**
     * Convierte el tiempo en segundos a un formato "MM:SS"
     *
     * @param tiempoSegundos Tiempo en segundos.
     * @return Tiempo formateado como "MM:SS"
     */
    private String formatearTiempo(float tiempoSegundos) {
        int minutos = (int) (tiempoSegundos / TIMER_SECONDS);
        int segundos = (int) (tiempoSegundos % TIMER_SECONDS);
        return String.format("%02d : %02d", minutos, segundos);
    }

    /**
     * Dibuja un fondo claro para la zona del HUD
     */
    public void renderizarFondoHUD() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1.0f, 0.95f, 0.75f, 1);
        shapeRenderer.rect(0, 0, VIRTUAL_WIDTH, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);
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
        shapeRenderer.rect(-grosorSombra, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD - grosorTotalSombra, VIRTUAL_WIDTH + 2 * grosorSombra, grosorTotalSombra);
        shapeRenderer.rect(-grosorSombra, -grosorSombra, grosorTotalSombra, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD + grosorSombra);
        shapeRenderer.rect(VIRTUAL_WIDTH - grosorMarcoNegro, -grosorSombra, grosorTotalSombra, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD + grosorSombra);

        // Dibujar SOMBRA
        shapeRenderer.setColor(0.45f, 0.45f, 0.45f, 1);
        shapeRenderer.rect(0, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD - grosorMarcoNegro, VIRTUAL_WIDTH, grosorMarcoNegro);
        shapeRenderer.rect(0, 0, grosorMarcoNegro, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);
        shapeRenderer.rect(VIRTUAL_WIDTH - grosorMarcoNegro, 0, grosorMarcoNegro, HUD_HEIGHT + DESPLAZAMIENTO_VERTICAL_HUD);

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
        float cellSize = GRID_CELL_SIZE - GRID_CELL_SIZE_CORRECTION;

        for (float y = 0; y <= alturaHUD; y += cellSize) {
            shapeRenderer.line(0, y, screenWidth, y);
        }

        shapeRenderer.end();
    }

    /**
     * Dibuja el texto del nivel (LVL) y el número del nivel del jugador de forma centrada en el HUD
     */
    public void renderizarTextoNivelPlayer() {
        String levelText = "LVL:  ";
        String levelNumber = String.valueOf(sistemaDeNiveles.getNivelActual());

        layout.setText(fuente, levelText + levelNumber);

        float textX = (VIRTUAL_WIDTH - layout.width) / 2 - 10f;
        float textY = HUD_HEIGHT - 13f + DESPLAZAMIENTO_VERTICAL_HUD;

        spriteBatch.begin();

        // Texto "LVL"
        fuente.getData().setScale(0.95f);
        float offset = 1f;
        Color colorReborde = Color.BLACK; // Negro para el reborde
        Color colorTexto = Color.WHITE; // Blanco para el texto principal
        dibujarTextoConReborde(spriteBatch, levelText, textX, textY, offset, colorReborde, colorTexto);

        // Coordenadas número
        float levelTextWidth = new GlyphLayout(fuente, levelText).width;
        float levelNumberX = textX + levelTextWidth;
        float textYNumber = HUD_HEIGHT - 8.5f + DESPLAZAMIENTO_VERTICAL_HUD;

        // Texto número del nivel
        fuente.getData().setScale(1.4f);
        colorReborde = Color.BLUE; // Azul para el reborde del número
        colorTexto = Color.WHITE; // Blanco para el número principal
        dibujarTextoConReborde(spriteBatch, levelNumber, levelNumberX, textYNumber, offset, colorReborde, colorTexto);

        spriteBatch.end();
    }



   /* public void renderizarIconoVidaJugador() { todo --> se usará próximamente
        float heartSize = HEART_SIZE;
        float heartX = HEART_X;
        float heartY = HUD_HEIGHT - heartSize - HEART_Y_OFFSET + DESPLAZAMIENTO_VERTICAL_HUD;

        spriteBatch.begin();
        spriteBatch.draw(texturaCorazonVida, heartX, heartY, heartSize, heartSize);
        spriteBatch.end();
    }*/

    /**
     * Dibuja la barra de experiencia (XP) del jugador. Incluye fondo, la parte azul según la fracción actual de XP y un icono
     */
    public void renderizarBarraXP() {
        float barWidth = HUD_BAR_WIDTH;
        float barHeight = HUD_BAR_HEIGHT;
        float barX = HUD_BAR_X;
        float barY = HUD_HEIGHT - barHeight - HUD_BAR_Y_OFFSET - 25f + DESPLAZAMIENTO_VERTICAL_HUD;
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
        shapeRenderer.rect(barX - 1.5f, barY - 1.5f, barWidth + 3f, barHeight + 3f);

        // Fondo gris claro
        shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 0.5f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Barra azul con la fracción de XP
        shapeRenderer.setColor(0f, 0.5f, 1f, 1f);
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
        float textY = barY + (barHeight + textHeight) / 2 + 1.75f;

        spriteBatch.begin();

        float offset = 0.8f;
        Color colorReborde = Color.BLACK;
        Color colorTexto = Color.WHITE;
        dibujarTextoConReborde(spriteBatch, experienceText, textX, textY, offset, colorReborde, colorTexto);


        spriteBatch.end();
    }


    /**
     * Dibuja un icono al lado de la barra de XP como indicativo
     */
    private void renderizarIconoBarraXP(float barX, float barY, float barHeight) {
        float iconSize = HEART_SIZE;
        float iconX = HEART_X;
        float iconY = barY - HEART_Y_OFFSET;

        spriteBatch.begin();
        spriteBatch.draw(texturaLapizXP, iconX, iconY, iconSize, iconSize);
        spriteBatch.end();
    }

    public void renderizarStatsJugador() {
        DecimalFormat df = new DecimalFormat("#.##");

        // Definimos los textos de las estadísticas
        String velocidadJugador = "Vel. Movimiento";
        String rangoJugador = "Rango Disparo";
        String velocidadAtaque = "Vel. Ataque";
        String fuerzaAtaque = "Fuerza";
        String numProyectiles = "Núm. Proyectiles";

        // Valores de las estadísticas
        String valorVelocidad = df.format(jugador.getVelocidadJugador());
        String valorRango = df.format(jugador.getRangoAtaqueJugador());
        String valorVelAtaque = df.format(jugador.getVelocidadAtaque());
        String valorFuerza = df.format(jugador.getDanyoAtaqueJugador());
        String valorProyectiles = "+" + df.format(jugador.getProyectilesPorDisparo());

        // Posiciones
        float margenDerecho = 930f;
        float statsX = VIRTUAL_WIDTH - margenDerecho;
        float statsY = HUD_HEIGHT - 45;
        float espaciado = 18f; // Espacio entre líneas
        float anchoDescripcion = 110; // Ancho fijo para la descripción

        // Arreglo de descripciones y valores
        String[] descripciones = {velocidadJugador, rangoJugador, velocidadAtaque, fuerzaAtaque, numProyectiles};
        String[] valores = {valorVelocidad, valorRango, valorVelAtaque, valorFuerza, valorProyectiles};

        // Renderizar el bloque de estadísticas utilizando el método reutilizable
        renderizarBloqueStats(descripciones, valores, statsX, statsY, espaciado, anchoDescripcion, Color.BLACK, Color.BLUE);
    }

    /**
     * Renderiza un bloque adicional de estadísticas del jugador, mostrando información como vida máxima, regeneración de vida, etc.
     */
    public void renderizarMasStatsJugador() {
        DecimalFormat df = new DecimalFormat("#.##");

        // Definimos los textos de las estadísticas adicionales
        String vidaMaxima = "Vida Máxima";
        String regeneracionVida = "Reg. Vida";
        String poderAtaque = "Poder Ataque";
        String resistencia = "Resistencia";
        String critico = "Crítico";

        // Valores de las estadísticas adicionales
        String valorVidaMaxima = df.format(jugador.getVidaJugador()) + " / " + jugador.getMaxVidaJugador();
        String valorRegeneracionVida = df.format(0);
        String valorPoderAtaque = df.format(0);
        String valorResistencia = df.format(0);
        String valorCritico = df.format(0 ) + "%";

        // Posiciones
        float margenDerecho = 770f;
        float statsX = VIRTUAL_WIDTH - margenDerecho;
        float statsY = HUD_HEIGHT - 45;
        float espaciado = 18f;
        float anchoDescripcion = 110;

        // Arreglo de descripciones y valores
        String[] descripciones = {vidaMaxima, regeneracionVida, poderAtaque, resistencia, critico};
        String[] valores = {valorVidaMaxima, valorRegeneracionVida, valorPoderAtaque, valorResistencia, valorCritico};

        // Renderizar el bloque adicional de estadísticas utilizando el método reutilizable
        renderizarBloqueStats(descripciones, valores, statsX, statsY, espaciado, anchoDescripcion, Color.BLACK, Color.BLUE);
    }

    /**
     * Método reutilizable para renderizar un bloque de estadísticas.
     *
     * @param descripciones    Arreglo de descripciones de las estadísticas.
     * @param valores          Arreglo de valores correspondientes a las descripciones.
     * @param statsX           Posición X inicial del bloque de estadísticas.
     * @param statsY           Posición Y inicial del bloque de estadísticas.
     * @param espaciado        Espacio entre cada línea de estadísticas.
     * @param anchoDescripcion Ancho fijo para las descripciones.
     * @param colorDescripcion Color de las descripciones.
     * @param colorValor        Color de los valores.
     */
    private void renderizarBloqueStats(String[] descripciones, String[] valores, float statsX, float statsY, float espaciado, float anchoDescripcion, Color colorDescripcion, Color colorValor) {
        spriteBatch.begin();

        fuente.getData().setScale(0.7f);

        // Renderizamos las descripciones y los valores
        for (int i = 0; i < descripciones.length; i++) {
            // Calculamos la posición Y de cada línea
            float posicionY = statsY - i * espaciado;

            // descripción en el color especificado
            fuente.setColor(colorDescripcion);
            fuente.draw(spriteBatch, descripciones[i], statsX - anchoDescripcion, posicionY);

            // valor en el color especificado
            fuente.setColor(colorValor);
            fuente.draw(spriteBatch, valores[i], statsX, posicionY);
        }

        spriteBatch.end();
    }



    private void dibujarTextoConReborde(SpriteBatch batch, String texto, float x, float y, float offset, Color colorReborde, Color colorTexto) {
        // Establecer el color del reborde
        fuente.setColor(colorReborde);

        // Dibujar en las 8 direcciones cardinales
        fuente.draw(batch, texto, x - offset, y); // (O)
        fuente.draw(batch, texto, x + offset, y); // (E)
        fuente.draw(batch, texto, x, y - offset); // (S)
        fuente.draw(batch, texto, x, y + offset); // (N)
        fuente.draw(batch, texto, x - offset, y + offset); // (NO)
        fuente.draw(batch, texto, x + offset, y + offset); // (NE)
        fuente.draw(batch, texto, x - offset, y - offset); // (SO)
        fuente.draw(batch, texto, x + offset, y - offset); // (SE)

        // Establecer el color del texto principal
        fuente.setColor(colorTexto);

        // Dibujar el texto principal
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
