package com.sticklike.core.pantallas.menus.renders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.sticklike.core.entidades.jugador.Jugador;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class RenderBaseMenuPersonaje extends RenderBaseMenus {

    private Table rootTable;

    public RenderBaseMenuPersonaje() {
        super();
        crearElementosUI();
    }

    private void crearElementosUI() {
        rootTable = new Table();
        rootTable.setFillParent(true);

        // Configuración del ScrollPane
        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        Pixmap blankPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        blankPixmap.setColor(1, 1, 1, 0);
        blankPixmap.fill();
        TextureRegionDrawable blankDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(blankPixmap)));
        blankPixmap.dispose();
        scrollStyle.background = blankDrawable;
        uiSkin.add("default", scrollStyle, ScrollPane.ScrollPaneStyle.class);

        // Fondo post-it general (no animado)
        TextureRegionDrawable postItDrawable = crearPostItDrawable();

        // Creación de los paneles originales
        Table panelEstadisticas = crearPanelEstadisticas(postItDrawable);
        Table panelEquipo = crearPanelEquipo();

        // Creamos los contenedores de indicadores
        Table contenedorOro = crearContenedorOro();
        Table contenedorPowerup = crearContenedorPowerup();

        Table rightContainer = new Table();
        rightContainer.setBackground(postItDrawable);
        Table verticalEquipo = new Table();
        verticalEquipo.add(panelEquipo).center().padBottom(10);
        verticalEquipo.row();
        verticalEquipo.add(contenedorPowerup).center();

        verticalEquipo.getColor().a = 0;
        verticalEquipo.addAction(Actions.sequence(Actions.delay(0.25f), Actions.fadeIn(0.25f)));
        rightContainer.add(verticalEquipo);

        Table leftContainer = new Table();
        Table verticalStats = new Table();
        verticalStats.add(panelEstadisticas).center().padBottom(10);
        verticalStats.row();
        verticalStats.add(contenedorOro).center();
        verticalStats.getColor().a = 0;
        verticalStats.addAction(Actions.sequence(Actions.delay(0.25f), Actions.fadeIn(0.25f)));
        leftContainer.add(verticalStats);

        rootTable.add(rightContainer).expand().fill().width(VIRTUAL_WIDTH / 2);
        rootTable.add(leftContainer).expand().fill().width(VIRTUAL_WIDTH / 2);
        stage.addActor(rootTable);

        panelEstadisticas.getColor().a = 0;
        panelEstadisticas.addAction(Actions.sequence(Actions.delay(0.25f), Actions.fadeIn(0.25f)));
        panelEquipo.getColor().a = 0;
        panelEquipo.addAction(Actions.sequence(Actions.delay(0.25f), Actions.fadeIn(0.25f)));

        // Selector de ítems en el centro
        Table selectorContainer = crearSelectorItems();
        selectorContainer.getColor().a = 0;
        selectorContainer.addAction(Actions.sequence(Actions.delay(0.25f), Actions.fadeIn(0.25f)));
        stage.addActor(selectorContainer);

        // Título principal
        Actor titleActor = tituloConReborde("PERSONAJE", 2.25f);
        titleActor.getColor().a = 0;
        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top();
        titleTable.add(titleActor).padTop(75).padBottom(50).center();
        stage.addActor(titleTable);
        titleActor.addAction(Actions.sequence(Actions.delay(0.25f), Actions.fadeIn(0.25f)));
    }

    // Métodos auxiliares para crear los indicadores de oro y powerup
    private Table crearContenedorOro() {
        Table container = new Table();
        Texture goldTexture = manager.get(RECOLECTABLE_CACA_DORADA, Texture.class);
        Image goldIcon = new Image(goldTexture);
        BitmapFont font = new BitmapFont();
        font.getData().markupEnabled = true;
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label goldLabel = new Label("[BLACK]x[]" + "[BLUE]" + Jugador.getOroGanado() + "[]", labelStyle);
        goldLabel.setFontScale(1.2f);
        container.add(goldIcon).width(40).height(40).padRight(5).padLeft(15);
        container.add(goldLabel).left();
        container.padTop(25);
        return container;
    }

    private Table crearContenedorPowerup() {
        Table container = new Table();
        Texture powerupTexture = manager.get(RECOLECTABLE_POWER_UP, Texture.class);
        Image powerupIcon = new Image(powerupTexture);
        powerupIcon.setScaleX(0.6f);
        powerupIcon.setScaleY(0.85f);
        BitmapFont font = new BitmapFont();
        font.getData().markupEnabled = true;
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label powerupLabel = new Label("[BLACK]x[]" + "[BLUE]" + Jugador.getTrazosGanados() + "[]", labelStyle);
        powerupLabel.setFontScale(1.2f);
        container.add(powerupIcon).padRight(5).padLeft(5);
        container.add(powerupLabel).left();
        container.padTop(20);
        return container;
    }

    private NinePatchDrawable crearSombraConBordeRectangulo(Color shadowColor) {
        int totalWidth = 400 + 2 * (8 + 3);
        int totalHeight = 500 + 2 * (8 + 3);
        Pixmap pixmap = new Pixmap(totalWidth, totalHeight, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.SourceOver);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();

        // 1) Sombra en anillos concéntricos
        for (int i = 0; i < 8; i++) {
            float alpha = 0.25f * ((float) i / (8 - 1));
            pixmap.setColor(shadowColor.r, shadowColor.g, shadowColor.b, alpha);
            int w = totalWidth - 2 * i;
            int h = totalHeight - 2 * i;
            pixmap.drawRectangle(i, i, w, h);
        }

        // 2) Zona del borde
        pixmap.setColor(Color.BLUE);
        int borderStart = 8;
        int borderW = totalWidth - 2 * 8;
        int borderH = totalHeight - 2 * 8;
        pixmap.fillRectangle(borderStart, borderStart, borderW, borderH);

        // 3) Interior vacío (transparente)
        int insideOffset = 8 + 3;
        int insideW = totalWidth - 2 * insideOffset;
        int insideH = totalHeight - 2 * insideOffset;
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fillRectangle(insideOffset, insideOffset, insideW, insideH);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        int split = 8 + 3;
        NinePatch ninePatch = new NinePatch(new TextureRegion(texture), split, split, split, split);
        return new NinePatchDrawable(ninePatch);
    }

    private TextureRegionDrawable crearPostItDrawable() {
        Color postItColor = new Color(0.97f, 0.88f, 0.6f, 1f);
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(postItColor);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    // Panel derecho: "ESTADÍSTICAS"
    private Table crearPanelEstadisticas(TextureRegionDrawable postItDrawable) {
        // Contenedor interno con fondo "post-it"
        Table centerContainerLeft = new Table();
        centerContainerLeft.setBackground(postItDrawable);

        // Tabla para mostrar las stats en tres columnas: icono, nombre y valor
        Table statsTable = new Table();

        // Fuente y estilo para las etiquetas
        BitmapFont font = new BitmapFont();
        font.getData().markupEnabled = true;
        Label.LabelStyle statsLabelStyle = new Label.LabelStyle(font, Color.WHITE);

        // Creación de etiquetas para cada estadística
        Label statVidaName = new Label("[BLACK]Vida total:[]", statsLabelStyle);
        Label statVidaValue = new Label("[BLUE]" + Jugador.getMaxVidaJugador() + "[]", statsLabelStyle);
        Label statRegName = new Label("[BLACK]Regeneración de vida:[]", statsLabelStyle);
        Label statRegValue = new Label("[BLUE]" + Jugador.getRegVidaJugador() + "[]", statsLabelStyle);
        Label statDanyoName = new Label("[BLACK]Fuerza física:[]", statsLabelStyle);
        Label statDanyoValue = new Label("[BLUE]" + Jugador.getDanyoAtaqueJugador() + "[]", statsLabelStyle);
        Label statRangoName = new Label("[BLACK]Rango de alcance:[]", statsLabelStyle);
        Label statRangoValue = new Label("[BLUE]" + Jugador.getRangoAtaqueJugador() + "[]", statsLabelStyle);
        Label statVelAtaqueName = new Label("[BLACK]Velocidad de ataque:[]", statsLabelStyle);
        Label statVelAtaqueValue = new Label("[BLUE]" + Jugador.getVelocidadAtaque() + "[]", statsLabelStyle);
        Label statMunicionName = new Label("[BLACK]Munición por disparo:[]", statsLabelStyle);
        Label statMunicionValue = new Label("[BLUE]" + Jugador.getProyectilesPorDisparo() + "[]", statsLabelStyle);
        Label statCriticoName = new Label("[BLACK]Probabilidad de crítico:[]", statsLabelStyle);
        Label statCriticoValue = new Label("[BLUE]" + Jugador.getCritico() + "[]", statsLabelStyle);
        Label statPoderName = new Label("[BLACK]Poder mágico:[]", statsLabelStyle);
        Label statPoderValue = new Label("[BLUE]" + Jugador.getPoderJugador() + "[]", statsLabelStyle);
        Label statVelMovimientoName = new Label("[BLACK]Velocidad de movimiento:[]", statsLabelStyle);
        Label statVelMovimientoValue = new Label("[BLUE]" + Jugador.getVelocidadJugador() + "[]", statsLabelStyle);
        Label statResistenciaName = new Label("[BLACK]Resistencia:[]", statsLabelStyle);
        Label statResistenciaValue = new Label("[BLUE]" + Jugador.getResistenciaJugador() + "[]", statsLabelStyle);

        // Ajustamos la escala de fuente para cada etiqueta
        statVidaName.setFontScale(1);
        statVidaValue.setFontScale(1);
        statRegName.setFontScale(1);
        statRegValue.setFontScale(1);
        statDanyoName.setFontScale(1);
        statDanyoValue.setFontScale(1);
        statRangoName.setFontScale(1);
        statRangoValue.setFontScale(1);
        statVelAtaqueName.setFontScale(1);
        statVelAtaqueValue.setFontScale(1);
        statMunicionName.setFontScale(1);
        statMunicionValue.setFontScale(1);
        statCriticoName.setFontScale(1);
        statCriticoValue.setFontScale(1);
        statPoderName.setFontScale(1);
        statPoderValue.setFontScale(1);
        statVelMovimientoName.setFontScale(1);
        statVelMovimientoValue.setFontScale(1);
        statResistenciaName.setFontScale(1);
        statResistenciaValue.setFontScale(1);

        agregarFilaEstadisticaConUpgrade(statsTable, ICONO_VIDA, statVidaName, statVidaValue, 3);
        agregarFilaEstadisticaConUpgrade(statsTable, ICONO_REGENERACION, statRegName, statRegValue, 3);
        agregarFilaEstadisticaConUpgrade(statsTable, ICONO_FUERZA, statDanyoName, statDanyoValue, 4);
        agregarFilaEstadisticaConUpgrade(statsTable, ICONO_RANGO, statRangoName, statRangoValue, 2);
        agregarFilaEstadisticaConUpgrade(statsTable, ICONO_VEL_ATAQUE, statVelAtaqueName, statVelAtaqueValue, 4);
        agregarFilaEstadisticaConUpgrade(statsTable, ICONO_PROYECTILES, statMunicionName, statMunicionValue, 5);
        agregarFilaEstadisticaConUpgrade(statsTable, ICONO_CRITICO, statCriticoName, statCriticoValue, 5);
        agregarFilaEstadisticaConUpgrade(statsTable, ICONO_PODER, statPoderName, statPoderValue, 5);
        agregarFilaEstadisticaConUpgrade(statsTable, ICONO_VEL_MOV, statVelMovimientoName, statVelMovimientoValue, 3);
        agregarFilaEstadisticaConUpgrade(statsTable, ICONO_RESISTENCIA, statResistenciaName, statResistenciaValue, 3);

        centerContainerLeft.add(statsTable).expandX().fillX().pad(10);

        // Creamos contenedor con sombra y borde para las estadísticas
        NinePatchDrawable sombraRebordeLeft = crearSombraConBordeRectangulo(new Color(0, 0, 0, 1f));
        Table leftWrapper = new Table();
        leftWrapper.setBackground(sombraRebordeLeft);
        leftWrapper.add(centerContainerLeft).size(400, 500).center();

        // Título "ESTADÍSTICAS"
        Label estadisticasLabel = new Label("ESTADÍSTICAS", uiSkin);
        estadisticasLabel.setAlignment(Align.center);
        estadisticasLabel.setFontScale(1.25f);

        Table estadisticasContainer = new Table();
        estadisticasContainer.add(estadisticasLabel).padBottom(10).row();
        estadisticasContainer.add(leftWrapper).size(422, 522);

        return estadisticasContainer;
    }


    private void agregarFilaEstadisticaConUpgrade(Table table, String iconoPath, Label nombre, Label valor, int upgradeCost) {
        // 1) Icono de la estadística
        Image icono = new Image(manager.get(iconoPath, Texture.class));
        table.add(icono).width(20).height(20).pad(10).padLeft(10);

        // 2) Nombre y valor
        table.add(nombre).left().pad(10).padRight(10);
        table.add(valor).left().pad(10).padRight(25);

        // 3) Contenedor para el botón "+" y el icono de oro + label con coste
        Table upgradeContainer = new Table();

        // 3a) Creamos el botón "+"
        BitmapFont font = new BitmapFont();

        TextButton.TextButtonStyle plusStyle = new TextButton.TextButtonStyle();
        plusStyle.font = font;
        plusStyle.fontColor = new Color(0, 0.75f, 0, 1);

        TextButton plusButton = new TextButton("+", plusStyle);
        Label.LabelStyle shadowStyle = new Label.LabelStyle(font, Color.BLACK);
        Label plusShadow = new Label("+", shadowStyle);
        plusShadow.setFontScale(1.33f);

        plusShadow.setPosition(2, -2);

        Stack plusStack = new Stack();
        plusStack.add(plusShadow);
        plusStack.add(plusButton);

        upgradeContainer.add(plusStack).padRight(5);
        table.add(upgradeContainer).pad(5).padLeft(25);

        table.row();

        // 4) Listener en el botón
        plusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // todo --> implementar lógica para manejar las mejoras de las stats
            }
        });
    }

    // Panel izquierdo: "EQUIPO" + stickman
    private Table crearPanelEquipo() {
        // 1) Fondo de cuadrícula
        int cellSize = 50;
        int textureSize = cellSize + 1;
        Color baseColor = new Color(0.89f, 0.89f, 0.89f, 1f);
        Color gridColor = new Color(0.64f, 0.80f, 0.9f, 1f);
        Pixmap gridPixmap = new Pixmap(textureSize, textureSize, Pixmap.Format.RGBA8888);
        gridPixmap.setColor(baseColor);
        gridPixmap.fill();
        gridPixmap.setColor(gridColor);
        gridPixmap.drawLine(textureSize - 1, 0, textureSize - 1, textureSize);
        gridPixmap.drawLine(0, textureSize - 1, textureSize, textureSize - 1);
        Texture gridTexture = new Texture(gridPixmap);
        gridPixmap.dispose();
        TextureRegion gridRegion = new TextureRegion(gridTexture);
        TiledDrawable gridDrawable = new TiledDrawable(gridRegion);

        // 2) Creamos el Stack que contendrá el fondo y el contenido
        Stack stack = new Stack();
        Table centerContainerRight = new Table();
        centerContainerRight.setBackground(gridDrawable);
        stack.add(centerContainerRight);

        // Creamos un Group que contendrá la imagen del stickman y los slots
        Group equipoGroup = new Group();
        equipoGroup.setSize(350, 450);
        equipoGroup.setPosition(25, 25);

        // Imagen del stickman
        Image stickmanImage = new Image(manager.get(STICKMAN_MENU, Texture.class));
        stickmanImage.setScaling(Scaling.fit);
        stickmanImage.setSize(350, 450);
        stickmanImage.setPosition(0, 0);
        equipoGroup.addActor(stickmanImage);

        Actor slotCabeza = crearSlotEquipo("Cabeza");
        Actor slotPecho = crearSlotEquipo("Pecho");
        Actor slotManos = crearSlotEquipo("Manos");
        Actor slotPiernas = crearSlotEquipo("Piernas");
        Actor slotPies = crearSlotEquipo("Pies");

        // Posicionamiento x,y relativo al equipoGroup (que tiene 350x450)
        slotCabeza.setPosition(350 / 5.75f - slotCabeza.getWidth() / 2, 450 - slotCabeza.getHeight());
        slotPecho.setPosition(350 / 1.25f - slotPecho.getWidth() / 2, 450 / 1.75f);
        slotManos.setPosition((float) 350 / 4 - slotManos.getWidth(), (float) 450 / 2 - slotManos.getHeight() / 2);
        slotPiernas.setPosition(350 / 1.25f - slotPiernas.getWidth() / 2, 450 / 4f);
        slotPies.setPosition((float) 350 / 4 - slotPies.getWidth(), 15);

        equipoGroup.addActor(slotCabeza);
        equipoGroup.addActor(slotPecho);
        equipoGroup.addActor(slotManos);
        equipoGroup.addActor(slotPiernas);
        equipoGroup.addActor(slotPies);

        // Colocamos el equipoGroup en un Table para mantener el layout
        Table imageContainer = new Table();
        imageContainer.addActor(equipoGroup);
        imageContainer.setSize(400, 500);

        stack.add(imageContainer);

        // 3) Wrapper con sombra y borde para el conjunto de equipo
        NinePatchDrawable sombraRebordeRight = crearSombraConBordeRectangulo(new Color(0, 0, 0, 1f));
        Table rightWrapper = new Table();
        rightWrapper.setBackground(sombraRebordeRight);
        rightWrapper.add(stack).size(400, 500).center();

        // 4) Título "EQUIPO"
        Label equipoLabel = new Label("EQUIPO", uiSkin);
        equipoLabel.setAlignment(Align.center);
        equipoLabel.setFontScale(1.25f);

        Table equipoContainer = new Table();
        equipoContainer.add(equipoLabel).padBottom(10).row();
        equipoContainer.add(rightWrapper).size(422, 522);

        return equipoContainer;
    }


    private Actor crearSlotEquipo(String tipo) {
        int slotSize = 60;
        // Creamos un pixmap para dibujar el recuadro
        Pixmap pixmap = new Pixmap(slotSize, slotSize, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);

        // Dibujamos el borde del slot
        pixmap.fillRectangle(0, 0, slotSize, 2);
        pixmap.fillRectangle(0, slotSize - 2, slotSize, 2);
        pixmap.fillRectangle(0, 0, 2, slotSize);
        pixmap.fillRectangle(slotSize - 2, 0, 2, slotSize);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        Image slotImage = new Image(new TextureRegionDrawable(new TextureRegion(texture)));

        // Usamos un Table para incluir el slot y una etiqueta debajo
        Table slotContainer = new Table();
        slotContainer.add(slotImage).size(slotSize, slotSize);
        BitmapFont font = new BitmapFont();
        font.getData().markupEnabled = true;
        Label.LabelStyle style = new Label.LabelStyle(font, Color.BLACK);
        Label slotLabel = new Label(tipo, style);
        slotLabel.setFontScale(0.8f);
        slotContainer.row();
        slotContainer.add(slotLabel).padTop(2);

        // Ajustamos el tamaño total del contenedor del slot
        slotContainer.setSize(slotSize, slotSize + 20);
        return slotContainer;
    }

    private Table crearSelectorItems() {
        Table itemsTable = new Table();
        String[] items = {"Gorra", "Visera", "Casco", "Sombrero", "Gorro", "Camiseta", "Pechera", "Chaleco", "Sudadera", "Anorak", "Guantes", "Muñequeras", "Peluco", "Puño\nAmericano", "Esposas", "Bermudas", "Chándal", "Bañador", "Falda", "Strapon", "Bambas", "Chanclas", "Zapatillas", "Tacones", "Botas"};
        int[] cantidades = {5, 3, 2, 1, 4, 1, 3, 4, 2, 5, 2, 1, 4, 5, 3, 1, 4, 3, 2, 5, 5, 1, 2, 3, 4};

        Texture powerupTexture = manager.get(RECOLECTABLE_POWER_UP, Texture.class);

        BitmapFont font = new BitmapFont();
        font.getData().markupEnabled = true;
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        for (int i = 0; i < items.length; i++) {
            Table row = new Table();
            row.defaults().space(5).pad(5).align(Align.left);

            Image iconClone = new Image(powerupTexture);
            iconClone.setScaleX(0.5f);
            iconClone.setScaleY(0.75f);

            Label itemLabel = new Label("[BLACK]" + items[i] + "[]", labelStyle);
            itemLabel.setFontScale(1);
            Label cantidadLabel = new Label("[BLACK]x[]" + "[BLUE]" + cantidades[i] + "[]", labelStyle);
            cantidadLabel.setFontScale(1);

            row.add(itemLabel).minWidth(75).padRight(10);
            row.add(iconClone).width(15).height(30).padLeft(5).padRight(5);
            row.add(cantidadLabel).width(40).align(Align.center);

            itemsTable.add(row).expandX().fillX();
            itemsTable.row();
        }

        ScrollPane scrollItems = new ScrollPane(itemsTable, uiSkin);

        Table selectorContainer = new Table();
        selectorContainer.setFillParent(true);
        selectorContainer.center();
        selectorContainer.add(scrollItems).expandX().fillX().height(400);

        return selectorContainer;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void animarSalida(Runnable callback) {
    }
}
