package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.texto.FontManager;
import com.sticklike.core.gameplay.controladores.ControladorEnemigos;
import com.sticklike.core.gameplay.controladores.ControladorProyectiles;
import com.sticklike.core.gameplay.progreso.Mejora;
import com.sticklike.core.gameplay.sistemas.SistemaDeNiveles;
import com.sticklike.core.pantallas.juego.VentanaJuego1;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Gestiona y renderiza los componentes del HUD en el juego.
 * Se encarga de la visualización de información del jugador (salud, experiencia, nivel, estadísticas, etc.)
 * y de la interacción con los elementos del HUD.
 */

public class RenderHUDComponents {
    private static final float EXTRA_HUD_HEIGHT = 20f;
    private ShapeRenderer shapeRenderer;
    private GlyphLayout layout;
    private GlyphLayout layoutNum;
    private BitmapFont fuente;
    private SpriteBatch spriteBatch;
    private Jugador jugador;
    private SistemaDeNiveles sistemaDeNiveles;
    private ControladorProyectiles controladorProyectiles;
    private ControladorEnemigos controladorEnemigos;
    private final Texture texturaCorazonVida, texturaLapizXP;
    private float tiempoTranscurrido = 0;
    private String tiempoFormateado;
    private boolean pausadoTemporizador = false;
    private Stage hudStage;
    private Stage chatlogStage;
    private Set<String> upgradeStats = new HashSet<>();
    private OrthographicCamera hudCamera;
    private Viewport hudViewport;
    private List<Rectangle> slotsList = new ArrayList<>();
    private Set<String> statBoosteada = new HashSet<>();
    private static final Color COLOR_SHAPEREDNDERER = new Color(0.75f, 0.75f, 0.75f, 1);
    private final DecimalFormat dfStats1 = new DecimalFormat("#.##");
    private final DecimalFormat dfStats2 = new DecimalFormat("#.#");

    public RenderHUDComponents(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, Jugador jugador, SistemaDeNiveles sistemaDeNiveles) {
        this.sistemaDeNiveles = sistemaDeNiveles;
        this.shapeRenderer = shapeRenderer;
        this.layout = new GlyphLayout();
        this.layoutNum = new GlyphLayout();
        this.fuente = FontManager.getHudFont();
        this.spriteBatch = spriteBatch;
        this.jugador = jugador;
        this.texturaCorazonVida = manager.get(RECOLECTABLE_VIDA, Texture.class);
        this.texturaLapizXP = manager.get(RECOLECTABLE_POWER_UP, Texture.class);
        this.controladorProyectiles = jugador.getControladorProyectiles();
        this.controladorEnemigos = jugador.getControladorEnemigos();

        // Configurar la cámara y el viewport del HUD
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        hudViewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, hudCamera);
        hudViewport.apply();
        hudStage = new Stage(hudViewport, spriteBatch);
        chatlogStage = new Stage(hudViewport, spriteBatch);
        Mensajes.init(chatlogStage, this, VentanaJuego1.getCamara(), spriteBatch);
    }

    public void renderizarTemporizador(float delta) {
        if (!pausadoTemporizador) {
            tiempoTranscurrido += delta;
        }
        tiempoFormateado = formatearTiempo(tiempoTranscurrido);
        float timerVerticalOffset = 35f;
        float textX = (VIRTUAL_WIDTH / 2) - (jugador.getSprite().getWidth() / 2f + 12.5f);
        float textY = (VIRTUAL_HEIGHT / 2) + TIMER_Y_POS + timerVerticalOffset;
        fuente.getData().setScale(TIMER_SCALE);
        Color colorReborde = Color.BLACK;
        Color colorTexto = Color.WHITE;
        dibujarTextoConReborde(spriteBatch, tiempoFormateado, textX, textY, BASIC_OFFSET, colorReborde, colorTexto);
    }

    public String formatearTiempo(float tiempoSegundos) {
        int minutos = (int) (tiempoSegundos / TIMER_SECONDS);
        int segundos = (int) (tiempoSegundos % TIMER_SECONDS);
        return String.format("%02d : %02d", minutos, segundos);
    }

    public void renderizarFondoHUD() {
        float effectiveHUDHeight = HUD_HEIGHT + EXTRA_HUD_HEIGHT;
        shapeRenderer.setColor(0.987f, 0.897f, 0.617f, 1f);
        shapeRenderer.rect(0, 0, VIRTUAL_WIDTH, effectiveHUDHeight + DESPLAZAMIENTO_VERTICAL_HUD);
    }

    public void renderizarMarcoHUD() {
        float effectiveHUDHeight = HUD_HEIGHT + EXTRA_HUD_HEIGHT;
        float grosorMarcoNegro = GROSOR_MARCO;
        float grosorSombra = GROSOR_SOMBRA;
        float grosorTotalSombra = grosorMarcoNegro + grosorSombra;
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);
        shapeRenderer.rect(-grosorSombra, effectiveHUDHeight + DESPLAZAMIENTO_VERTICAL_HUD - grosorTotalSombra, VIRTUAL_WIDTH + BORDER_CORRECTION * grosorSombra, grosorTotalSombra);
        shapeRenderer.rect(-grosorSombra, -grosorSombra, grosorTotalSombra, effectiveHUDHeight + DESPLAZAMIENTO_VERTICAL_HUD + grosorSombra);
        shapeRenderer.rect(VIRTUAL_WIDTH - grosorMarcoNegro, -grosorSombra, grosorTotalSombra, effectiveHUDHeight + DESPLAZAMIENTO_VERTICAL_HUD + grosorSombra);

        shapeRenderer.setColor(0.45f, 0.45f, 0.45f, 1);
        shapeRenderer.rect(0, effectiveHUDHeight + DESPLAZAMIENTO_VERTICAL_HUD - grosorMarcoNegro, VIRTUAL_WIDTH, grosorMarcoNegro);
        shapeRenderer.rect(0, 0, grosorMarcoNegro, effectiveHUDHeight + DESPLAZAMIENTO_VERTICAL_HUD);
        shapeRenderer.rect(VIRTUAL_WIDTH - grosorMarcoNegro, 0, grosorMarcoNegro, effectiveHUDHeight + DESPLAZAMIENTO_VERTICAL_HUD);
    }

    public void renderizarLineasHorizontalesCuadricula(float alturaHUD) {
        shapeRenderer.setColor(0.72f, 0.82f, 0.875f, 1);
        float cellSize = GRID_CELL_SIZE_HUD - GRID_CELL_SIZE_CORRECTION;
        for (float y = 0; y <= alturaHUD; y += cellSize) {
            shapeRenderer.rectLine(0, y, VIRTUAL_WIDTH, y, 2);
        }
    }

    public void renderizarLineaVerticalCuadricula(float alturaHUD) {
        shapeRenderer.setColor(Color.RED);
        float margenIzquierdo = 40f;
        float grosorLinea = 1.5f;
        float espacioEntreLineas = 2.5f;

        // Líneas en el lado izquierdo
        shapeRenderer.rect(margenIzquierdo, 0, grosorLinea, alturaHUD + EXTRA_HUD_HEIGHT - 2);
        shapeRenderer.rect(margenIzquierdo + grosorLinea + espacioEntreLineas, 0, grosorLinea, alturaHUD + EXTRA_HUD_HEIGHT - 2f);

        // Líneas en el lado derecho
        float margenDerecho = 40f;
        float xLinea1 = VIRTUAL_WIDTH - margenDerecho - grosorLinea;
        float xLinea2 = xLinea1 - espacioEntreLineas - grosorLinea;
        shapeRenderer.rect(xLinea1, 0, grosorLinea, alturaHUD + EXTRA_HUD_HEIGHT - 2);
        shapeRenderer.rect(xLinea2, 0, grosorLinea, alturaHUD + EXTRA_HUD_HEIGHT - 2f);

        float circleRadius = 6f;
        float circleYBottom = 35f + EXTRA_HUD_HEIGHT;
        float circleYTop = 108f + EXTRA_HUD_HEIGHT;

        // Círculos lado izquierdo
        float circleXLeft = margenIzquierdo / 2f;
        shapeRenderer.setColor(COLOR_SHAPEREDNDERER);
        shapeRenderer.circle(circleXLeft, circleYBottom, circleRadius);
        shapeRenderer.circle(circleXLeft, circleYTop, circleRadius);
    }

    public void renderizarTextoNivelPlayer() {
        float effectiveHUDHeight = HUD_HEIGHT + EXTRA_HUD_HEIGHT;

        // Título "LVL"
        fuente.getData().setScale(0.95f);
        layout.setText(fuente, TEXTO_LVL);
        float widthLVL = layout.width;

        // Número de nivel
        String nivelStr = String.valueOf(sistemaDeNiveles.getNivelActual());
        fuente.getData().setScale(1.4f);
        layout.setText(fuente, nivelStr);
        float widthNumero = layout.width;

        // Calculamos posición centrada
        float spacing = 2f;
        float totalWidth = widthLVL + spacing + widthNumero;
        float groupX = (VIRTUAL_WIDTH - totalWidth) / 2;

        float textY = effectiveHUDHeight - TEXT_Y_CORRECTION + DESPLAZAMIENTO_VERTICAL_HUD;
        float textYNumber = effectiveHUDHeight - NUMBER_Y_CORRECTION + DESPLAZAMIENTO_VERTICAL_HUD;

        // Dibujamos
        fuente.getData().setScale(0.95f);
        dibujarTextoConReborde(spriteBatch, TEXTO_LVL, groupX, textY, BASIC_OFFSET, Color.BLACK, Color.WHITE);

        fuente.getData().setScale(1.4f);
        dibujarTextoConReborde(spriteBatch, nivelStr, groupX + widthLVL + spacing, textYNumber, BASIC_OFFSET, Color.BLUE, Color.WHITE);
    }


    public void renderizarBarraXPFondo() {
        float effectiveHUDHeight = HUD_HEIGHT + EXTRA_HUD_HEIGHT;
        float barWidth = HUD_BAR_WIDTH;
        float barHeight = HUD_BAR_HEIGHT;
        float barX = (VentanaJuego1.worldWidth - HUD_BAR_WIDTH) / 2;
        float barY = effectiveHUDHeight - barHeight - HUD_BAR_Y_OFFSET - XPBAR_Y_CORRECTION + DESPLAZAMIENTO_VERTICAL_HUD;
        float experiencePercentage = sistemaDeNiveles.getXpActual() / sistemaDeNiveles.getXpHastaSiguienteNivel();

        // Borde negro
        shapeRenderer.setColor(0, 0, 0, 1f);
        shapeRenderer.rect(barX - BORDER_NEGATIVE, barY - BORDER_NEGATIVE, barWidth + BORDER_POSITIVE, barHeight + BORDER_POSITIVE);
        // Fondo gris claro
        shapeRenderer.setColor(0.89f, 0.89f, 0.89f, 1);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        // Barra azul con la fracción de XP
        shapeRenderer.setColor(0f, 0.5f, 1f, 1f);
        shapeRenderer.rect(barX, barY, barWidth * experiencePercentage, barHeight);
    }

    public void renderizarBarraXPInfo() {
        float effectiveHUDHeight = HUD_HEIGHT + EXTRA_HUD_HEIGHT;
        float barWidth = HUD_BAR_WIDTH;
        float barHeight = HUD_BAR_HEIGHT;
        float barX = HUD_BAR_X;
        float barY = effectiveHUDHeight - barHeight - HUD_BAR_Y_OFFSET - XPBAR_Y_CORRECTION + DESPLAZAMIENTO_VERTICAL_HUD;
        renderizarTextoBarraXP(barX, barY - BASIC_OFFSET2, barWidth, barHeight);

        float centerX = barX + barWidth * 0.5f;
        float posY = barY - 35f;
        float offset = 60f;
        renderizarIconoConTexto(manager.get(RECOLECTABLE_CACA_DORADA, Texture.class), 22f, 22f, centerX - offset, posY, String.valueOf(Jugador.getOroGanado()), 0.8f, Color.GOLD, Color.DARK_GRAY);
        renderizarIconoConTexto(manager.get(ICONO_CALEAVELA_KILLS, Texture.class), 23f, 23f, centerX, posY, String.valueOf(controladorEnemigos.getKillCounter()), 0.8f, Color.WHITE, Color.DARK_GRAY);
        renderizarIconoConTexto(manager.get(RECOLECTABLE_POWER_UP, Texture.class), 9f, 22f, centerX + offset, posY, String.valueOf(Jugador.getTrazosGanados()), 0.8f, Color.WHITE, Color.DARK_GRAY);
    }

    private void renderizarTextoBarraXP(float barX, float barY, float barWidth, float barHeight) {
        float xpActual = sistemaDeNiveles.getXpActual();
        float xpHastaSiguienteNivel = sistemaDeNiveles.getXpHastaSiguienteNivel();
        int porcentajeXP = (int) ((xpActual / xpHastaSiguienteNivel) * 100);
        String experienceText = porcentajeXP + " %";
        layout.setText(fuente, experienceText);
        float textHeight = layout.height;
        float textX = barX + (barWidth) / 2;
        float textY = barY + (barHeight + textHeight) / 2 + XPTEXT_Y_CORRECTION;
        fuente.getData().setScale(0.8f);
        fuente.setColor(Color.BLACK);
        fuente.draw(spriteBatch, experienceText, textX, textY);
    }

    public void renderizarIconoConTexto(Texture iconTexture, float iconWidth, float iconHeight, float posX, float posY, String texto, float scaleFuente, Color colorTexto, Color colorReborde) {
        spriteBatch.draw(iconTexture, posX - iconWidth * 0.5f, posY - iconHeight * 0.5f, iconWidth, iconHeight);
        fuente.getData().setScale(scaleFuente);
        float textX = posX + (iconWidth * 0.5f) + 5f;
        float textY = posY + 5f;
        dibujarTextoConReborde(spriteBatch, texto, textX, textY, 1f, colorReborde, colorTexto);
    }

    public void renderizarStatsJugadorBloque1() {
        float effectiveHUDHeight = HUD_HEIGHT + EXTRA_HUD_HEIGHT;

        String valorVelocidad = dfStats1.format(jugador.getVelocidadJugador());
        String valorRango = dfStats1.format(jugador.getRangoAtaqueJugador());
        String valorVelAtaque = dfStats1.format(jugador.getVelocidadAtaque()) + " %";
        String valorFuerza = dfStats1.format(jugador.getDanyoAtaqueJugador());
        String valorProyectiles = "+" + dfStats1.format(jugador.getProyectilesPorDisparo());

        String[] descripciones = {VEL_MOV, RANGO, VEL_ATAQUE, FUERZA, NUM_PROYECTILES};
        String[] valores = {valorVelocidad, valorRango, valorVelAtaque, valorFuerza, valorProyectiles};
        Texture[] iconos = {manager.get(ICONO_VEL_MOV, Texture.class), manager.get(ICONO_RANGO, Texture.class), manager.get(ICONO_VEL_ATAQUE, Texture.class), manager.get(ICONO_FUERZA, Texture.class), manager.get(ICONO_PROYECTILES, Texture.class)};
        float statsX = VIRTUAL_WIDTH - STATS_X_CORRECTION + 10;
        float statsY = effectiveHUDHeight - STATS_Y_CORRECTION - 20f;
        renderizarBloqueStatsConIconos(descripciones, iconos, valores, statsX, statsY, ANCHO_DESC1);
    }

    public void renderizarStatsJugadorBloque2() {
        float effectiveHUDHeight = HUD_HEIGHT + EXTRA_HUD_HEIGHT;

        String valorVidaMaxima = dfStats2.format(jugador.getVidaJugador()) + " / " + dfStats2.format(jugador.getMaxVidaJugador());
        String valorRegeneracionVida = dfStats2.format(jugador.getRegVidaJugador() * 100) + " %";
        String valorPoderAtaque = dfStats2.format(jugador.getPoderJugador()) + " %";
        String valorResistencia = dfStats2.format(jugador.getResistenciaJugador() * 100) + " %";
        String valorCritico = dfStats2.format(jugador.getCritico() * 100) + " %";

        String[] descripciones = {VIDA_MAX, REG_VIDA, PODER, RESIST, CRITIC};
        String[] valores = {valorVidaMaxima, valorRegeneracionVida, valorPoderAtaque, valorResistencia, valorCritico};
        Texture[] iconos = {manager.get(ICONO_VIDA, Texture.class), manager.get(ICONO_REGENERACION, Texture.class), manager.get(ICONO_PODER, Texture.class), manager.get(ICONO_RESISTENCIA, Texture.class), manager.get(ICONO_CRITICO, Texture.class)};
        float statsX = VIRTUAL_WIDTH - STATS_X_CORRECTION2 + 10f;
        float statsY = effectiveHUDHeight - STATS_Y_CORRECTION - 20f;
        renderizarBloqueStatsConIconos(descripciones, iconos, valores, statsX, statsY, ANCHO_DESC2);
    }


    private void renderizarBloqueStatsConIconos(String[] descripciones, Texture[] iconos, String[] valores, float statsX, float statsY, float anchoDescripcion) {
        // Ajustamos el tamaño de la fuente
        fuente.getData().setScale(0.8f);

        for (int i = 0; i < descripciones.length; i++) {
            float posicionY = statsY - i * ESPACIADO;

            // 1) Dibujamos la descripción de la stat
            fuente.setColor(Color.BLACK);
            fuente.draw(spriteBatch, descripciones[i], statsX - anchoDescripcion, posicionY);

            // 2) Dibujamos el icono
            if (iconos != null && i < iconos.length && iconos[i] != null) {
                Texture icono = iconos[i];
                float iconSize = STATS_ICON_SIZE;
                float iconX = statsX - iconSize;
                float iconY = posicionY - iconSize / 2f - ICON_Y_CORRECTION;
                spriteBatch.draw(icono, iconX, iconY, iconSize, iconSize);
            }

            // 3) Preparamos el valor a mostrar
            String statValue = valores[i];
            Color textColor = Color.WHITE;
            Color borderColor = Color.BLUE;

            // Si la stat fue mejorada, cambiamos los colores
            if (upgradeStats.contains(descripciones[i])) {
                textColor = Color.GREEN;
                borderColor = Color.BLACK;
            }

            // Si la stat está en modo "boosted", mostramos "???" en rojo
            if (isStatBoosted(descripciones[i])) {
                statValue = "???";
                textColor = Color.RED;
            }

            // 4) Dibujamos el valor CON reborde
            dibujarTextoConReborde(spriteBatch, statValue, statsX + ESPACIADO_LATERAL, posicionY, BASIC_OFFSET, borderColor, textColor);
        }
    }

    // Se actualiza la posición vertical (baseY) del slot y marco del arma
    public void dibujarAtaqueBasico(Texture texturaArma) {
        float slotSize = 42.5f;
        float attackSlotSize = slotSize / 1.8f;
        float offsetX = 500;
        float baseX = VIRTUAL_WIDTH - offsetX;
        float baseY = 65f + EXTRA_HUD_HEIGHT;

        // Dibujamos marco, arma y texto
        spriteBatch.draw(manager.get(TEXTURA_MARCO, Texture.class), baseX, baseY, attackSlotSize, attackSlotSize);
        if (texturaArma != null) {
            float iconSize = attackSlotSize * 0.725f;
            float iconX = baseX + (attackSlotSize - iconSize) / 2;
            float iconY = baseY + (attackSlotSize - iconSize) / 2 + 0.5f;
            spriteBatch.draw(texturaArma, iconX, iconY, iconSize, iconSize);
        }
        String textoAtaque = "ARMA";
        layout.setText(fuente, textoAtaque);
        float textX = baseX + (attackSlotSize - layout.width) / 2 + 2.5f;
        float textY = baseY - 7.5f;
        fuente.getData().setScale(0.6f);
        fuente.setColor(Color.BLACK);
        fuente.draw(spriteBatch, textoAtaque, textX, textY);
    }

    public void crearSlots() {
        slotsList.clear();
        float baseX = VIRTUAL_WIDTH - 435f;
        float baseY = 75f + EXTRA_HUD_HEIGHT;
        float colGap = 80f;
        float rowGap = 55f;
        int columns = 5;
        int totalSlots = 10;
        float slotSize = 45f;
        for (int i = 0; i < totalSlots; i++) {
            int rowIndex = i / columns;
            int colIndex = i % columns;
            float slotX = baseX + colIndex * colGap;
            float slotY = baseY - rowIndex * rowGap;
            slotsList.add(new Rectangle(slotX, slotY, slotSize, slotSize));
        }
    }

    public void setHabilidadesActivas(List<Mejora> habilidadesActivas) {
        hudStage.clear();

        for (int i = 0; i < habilidadesActivas.size() && i < slotsList.size(); i++) {
            Mejora mejora = habilidadesActivas.get(i);
            Texture icono = mejora.getIcono();
            if (icono == null) continue;

            Rectangle slot = slotsList.get(i);
            TextureRegionDrawable drawableIcono = new TextureRegionDrawable(new TextureRegion(icono));

            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
            style.imageUp = drawableIcono;
            ImageButton boton = new ImageButton(style);

            // Ajustamos tamaño y posición idéntico para todas las habilidades, excepto “DILDO” que necesita un pequeño tweak
            float margin = slot.width * 0.175f;
            float margin2 = slot.width * 0.0375f;
            if ("DILDO".equals(mejora.getIdHabilidad())) {
                boton.setSize(slot.width - 2, slot.height - 7.5f * margin2);
                boton.setPosition(slot.x + margin2, slot.y + margin * 1.35f);
            } else {
                boton.setSize(slot.width - 2 * margin, slot.height - 2 * margin);
                boton.setPosition(slot.x + margin, slot.y + margin * 1.5f);
            }

            /*boton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) { // todo --> implementar info de las mejoras en un futuro
                    Gdx.app.log("MEJORA","Hiciste clic en " + mejora.getNombreMejora());
                }
            });*/

            hudStage.addActor(boton);
        }
    }

    private String toRoman(int num) {
        switch (num) {
            case 1:
                return "I";
            case 2:
                return "I I";
            case 3:
                return "I I I";
            case 4:
                return "I V";
            case 5:
                return "V";
            case 6:
                return "V I";
            case 7:
                return "V I I";
            case 8:
                return "V I I I";
            case 9:
                return "I X";
            case 10:
                return "X";
            default:
                return "";
        }
    }

    public void renderizarMarcosMejoras() {
        fuente.getData().setScale(0.65f);

        List<Mejora> habilidades = sistemaDeNiveles.getSistemaDeMejoras().getHabilidadesActivas();

        for (int i = 0; i < slotsList.size(); i++) {
            Rectangle rect = slotsList.get(i);
            spriteBatch.draw(manager.get(TEXTURA_MARCO, Texture.class), rect.x, rect.y, rect.width, rect.height);

            if (i < habilidades.size()) {
                int upgrades = sistemaDeNiveles.getSistemaDeMejoras().getUpgradeCount(habilidades.get(i).getIdHabilidad());

                if (upgrades > 0) {
                    String romano = toRoman(upgrades);

                    float prevScale = fuente.getData().scaleX;
                    fuente.getData().setScale(0.8f);

                    layout.setText(fuente, romano);
                    float padding = 4f;
                    float textX = rect.x + rect.width + padding;
                    float textY = rect.y + layout.height + padding;

                    dibujarTextoConReborde(spriteBatch, romano, textX, textY, BASIC_OFFSET, Color.BLACK, Color.GREEN);

                    fuente.getData().setScale(prevScale);
                }

            } else {
                String textoNumero = String.valueOf(i + 1);

                layoutNum.setText(fuente, textoNumero);
                float textWidth = layoutNum.width;
                float textHeight = layoutNum.height;
                float textX = rect.x + (rect.width - textWidth) / 2;
                float textY = rect.y + (rect.height + textHeight) / 2;

                fuente.setColor(Color.BLUE);
                fuente.draw(spriteBatch, textoNumero, textX, textY);
            }
        }
        fuente.setColor(Color.WHITE);
    }

    private void dibujarTextoConReborde(SpriteBatch batch, String texto, float x, float y, float offset, Color colorReborde, Color colorTexto) {
        fuente.setColor(colorReborde);
        fuente.draw(batch, texto, x - offset, y);
        fuente.draw(batch, texto, x + offset, y);
        fuente.draw(batch, texto, x, y - offset);
        fuente.draw(batch, texto, x, y + offset);
        fuente.draw(batch, texto, x - offset, y - offset);
        fuente.draw(batch, texto, x + offset, y - offset);
        fuente.draw(batch, texto, x - offset, y + offset);
        fuente.draw(batch, texto, x + offset, y + offset);
        fuente.setColor(colorTexto);
        fuente.draw(batch, texto, x, y);
    }

    public void marcarStatComoMejorado(String statKey) {
        upgradeStats.add(statKey);
    }

    public void setStatBoosted(String statKey, boolean boosted) {
        if (boosted) {
            statBoosteada.add(statKey);
        } else {
            statBoosteada.remove(statKey);
        }
    }

    public boolean isStatBoosted(String statKey) {
        return statBoosteada.contains(statKey);
    }

    public void dispose() {
        if (fuente != null) fuente.dispose();
    }

    public void pausarTemporizador() {
        pausadoTemporizador = true;
    }

    public void reanudarTemporizador() {
        pausadoTemporizador = false;
    }

    public Stage getHudStage() {
        return hudStage;
    }

    public float getTiempoTranscurrido() {
        return tiempoTranscurrido;
    }

    public boolean isPausadoTemporizador() {
        return pausadoTemporizador;
    }

    public ControladorEnemigos getControladorEnemigos() {
        return controladorEnemigos;
    }

}
