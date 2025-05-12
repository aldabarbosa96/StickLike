package com.sticklike.core.pantallas.menus.renders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.function.IntSupplier;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

// Se utiliza un FontManager para compartir la fuente común de menús
import com.sticklike.core.entidades.objetos.texto.FontManager;

public abstract class RenderBaseMenus {
    protected Stage stage;
    protected Skin uiSkin;
    protected ShapeRenderer shapeRenderer;
    private static TextureRegionDrawable cachedBotonDrawable;
    private static TextureRegionDrawable cachedHoverDrawable;
    private static NinePatchDrawable cachedSelectedDrawable;
    private static final Color PAPEL_FONDO = new Color(0.82f, 0.90f, 1f, 1f);
    private static final Color DEFAULT_COLOR = new Color(0.97f, 0.88f, 0.6f, 1f);
    private static final Color HOVER_COLOR = new Color(0.1f, 0.2f, 0.6f, 0.2f);
    private static final Color GLOW_COLOR = new Color(0.4f, 0.6f, 1f, 0.6f);

    public RenderBaseMenus() {
        stage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        uiSkin = crearSkinBasico();
        shapeRenderer = new ShapeRenderer();
        agregarVersionLabel();
    }

    protected Skin crearSkinBasico() {
        Skin skin = new Skin();
        float inv = 1f / FontManager.getScale();

        BitmapFont menuFont = FontManager.getMenuFont();
        menuFont.getData().setScale(inv);
        BitmapFont titleFont = FontManager.getMenuTitleFont();
        titleFont.getData().setScale(inv);

        skin.add("default-font", menuFont);
        skin.add("default", crearLabelStyle(menuFont, Color.BLACK), LabelStyle.class);
        skin.add("default-button", crearDefaultButtonStyle(menuFont), TextButtonStyle.class);
        skin.add("hover-button",   crearHoverButtonStyle(menuFont),   TextButtonStyle.class);
        skin.add("selected-button", crearSelectedButtonStyle(menuFont), TextButtonStyle.class);
        skin.add("title", new Label.LabelStyle(titleFont, Color.WHITE), LabelStyle.class);

        return skin;
    }


    private LabelStyle crearLabelStyle(BitmapFont font, Color color) {
        return new LabelStyle(font, color);
    }

    private TextButtonStyle crearDefaultButtonStyle(BitmapFont font) {
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.up = crearBotonDrawable();
        return style;
    }

    // Creamos y cacheamos un Drawable a partir de un Pixmap para el botón (fondo)
    private TextureRegionDrawable crearBotonDrawable() {
        if (cachedBotonDrawable == null) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(DEFAULT_COLOR);
            pm.fill();
            Texture tex = new Texture(pm);
            pm.dispose();
            cachedBotonDrawable = new TextureRegionDrawable(new TextureRegion(tex));
        }
        return cachedBotonDrawable;
    }

    private TextButtonStyle crearHoverButtonStyle(BitmapFont font) {
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        if (cachedHoverDrawable == null) {
            Pixmap hoverPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            hoverPixmap.setColor(HOVER_COLOR);
            hoverPixmap.fill();
            Texture hoverTexture = new Texture(hoverPixmap);
            hoverPixmap.dispose();
            cachedHoverDrawable = new TextureRegionDrawable(new TextureRegion(hoverTexture));
        }
        style.up = cachedHoverDrawable;
        return style;
    }

    // Creamos y cacheamos el estilo para el botón seleccionado (con efecto glow)
    private TextButtonStyle crearSelectedButtonStyle(BitmapFont font) {
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        if (cachedSelectedDrawable == null) {
            Pixmap glowPixmap = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
            glowPixmap.setColor(GLOW_COLOR);
            glowPixmap.fill();
            Texture glowTexture = new Texture(glowPixmap);
            glowPixmap.dispose();
            NinePatch glowNinePatch = new NinePatch(glowTexture, 5, 5, 5, 5);
            cachedSelectedDrawable = new NinePatchDrawable(glowNinePatch);
        }
        style.up = cachedSelectedDrawable;
        style.fontColor = Color.WHITE;
        return style;
    }

    public void render(float delta) {
        // Limpieza de pantalla
        Gdx.gl.glClearColor(0.92f, 0.92f, 0.92f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujo de cuadrícula
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(1.5f);
        shapeRenderer.setColor(0.64f, 0.80f, 0.9f, 0.9f);
        float cellSize = GRID_CELL_SIZE;
        float startX = stage.getCamera().position.x - stage.getViewport().getWorldWidth() / 2;
        float endX = stage.getCamera().position.x + stage.getViewport().getWorldWidth() / 2;
        float startY = stage.getCamera().position.y - stage.getViewport().getWorldHeight() / 2;
        float endY = stage.getCamera().position.y + stage.getViewport().getWorldHeight() / 2;
        for (float x = startX - (startX % cellSize); x <= endX; x += cellSize) {
            shapeRenderer.line(x, startY, x, endY);
        }
        for (float y = startY - (startY % cellSize); y <= endY; y += cellSize) {
            shapeRenderer.line(startX, y, endX, y);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glLineWidth(1f);

        float lineThickness = 1f;
        float marginX = startX + 64;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(marginX - lineThickness / 2, startY, lineThickness, endY - startY);
        shapeRenderer.end();

        // Actualización y dibujo del Stage
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        uiSkin.dispose();
        shapeRenderer.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    protected Drawable papelFondo() {
        Pixmap pixmap = new Pixmap(200, 40, Pixmap.Format.RGBA8888);
        pixmap.setColor(PAPEL_FONDO);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TiledDrawable(new TextureRegion(texture));
    }

    private void agregarVersionLabel() {
        float inv = 1f / FontManager.getScale();
        BitmapFont menuFont = FontManager.getMenuFont();
        menuFont.getData().setScale(inv);
        Label.LabelStyle versionStyle = new Label.LabelStyle(menuFont, Color.BLACK);
        Label versionLabel = new Label("v 1.11.6-dev", versionStyle);

        // lo coloco en su tabla
        Table versionTable = new Table();
        versionTable.setFillParent(true);
        versionTable.bottom().right().padRight(35).padBottom(30);
        versionTable.add(versionLabel);
        stage.addActor(versionTable);
    }


    protected NinePatchDrawable crearSombraConBorde(Color shadowColor, int shadowSize, Color borderColor, int borderThickness) {
        int totalSize = 16 + (shadowSize + borderThickness) * 2;

        Pixmap pixmap = new Pixmap(totalSize, totalSize, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.SourceOver);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();

        // 1) Dibujamos la sombra como "anillos" concéntricos
        for (int i = 0; i < shadowSize; i++) {
            float alpha = 0.25f * ((float) i / (shadowSize - 1));
            pixmap.setColor(shadowColor.r, shadowColor.g, shadowColor.b, alpha);

            int size = totalSize - i * 2;
            pixmap.drawRectangle(i, i, size, size);
        }

        // 2) Rellenamos la zona del borde
        pixmap.setColor(borderColor);
        pixmap.fillRectangle(shadowSize, shadowSize, totalSize - 2 * shadowSize, totalSize - 2 * shadowSize);

        // 3) Vaciamos el interior (zona transparente) para que se vea el contenido o fondo del contenedor
        int insideOffset = shadowSize + borderThickness;
        int insideSize = totalSize - 2 * insideOffset;
        if (insideSize > 0) {
            pixmap.setColor(0, 0, 0, 0);
            pixmap.fillRectangle(insideOffset, insideOffset, insideSize, insideSize);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        int split = shadowSize + borderThickness;
        NinePatch ninePatch = new NinePatch(new TextureRegion(texture), split, split, split, split);
        return new NinePatchDrawable(ninePatch);
    }

    protected TextButton crearBotonesNumerados(int number, String text, String styleName) {
        TextButton button = new TextButton("", uiSkin, styleName);

        Table contentTable = new Table();
        contentTable.defaults().center().pad(0);

        Label numberLabel = new Label(String.format("%2d.", number), uiSkin);
        numberLabel.setAlignment(Align.left);
        contentTable.add(numberLabel).width(30);

        Label textLabel = new Label(text, uiSkin);
        textLabel.setAlignment(Align.center);
        contentTable.add(textLabel).expandX().fillX();

        contentTable.add(new Label("", uiSkin)).width(30); // dummy

        button.clearChildren();
        button.add(contentTable).expand().fill();

        // Se utiliza una clase auxiliar para agrupar los labels
        button.setUserObject(new ButtonLabels(numberLabel, textLabel));

        return button;
    }

    protected void efectoHover(final java.util.List<TextButton> buttons, final IntSupplier selectedIndexSupplier) {
        for (final TextButton btn : buttons) {
            btn.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    int idx = buttons.indexOf(btn);
                    if (idx != selectedIndexSupplier.getAsInt()) {
                        btn.setStyle(uiSkin.get("hover-button", TextButtonStyle.class));
                    }
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    int idx = buttons.indexOf(btn);
                    if (idx != selectedIndexSupplier.getAsInt()) {
                        btn.setStyle(uiSkin.get("default-button", TextButtonStyle.class));
                    }
                }
            });
        }
    }

    protected void actualizarBotonResaltado(java.util.List<TextButton> buttons, int selectedIndex) {
        TextButtonStyle defaultStyle = uiSkin.get("default-button", TextButtonStyle.class);
        TextButtonStyle selectedStyle = uiSkin.get("selected-button", TextButtonStyle.class);

        for (int i = 0; i < buttons.size(); i++) {
            TextButton button = buttons.get(i);
            ButtonLabels labels = (ButtonLabels) button.getUserObject();

            if (i == selectedIndex) {
                button.setStyle(selectedStyle);
                labels.text.setStyle(new Label.LabelStyle(labels.text.getStyle().font, selectedStyle.fontColor));
                labels.number.setStyle(new Label.LabelStyle(labels.number.getStyle().font, selectedStyle.fontColor));
            } else {
                button.setStyle(defaultStyle);
                labels.text.setStyle(new Label.LabelStyle(labels.text.getStyle().font, defaultStyle.fontColor));
                labels.number.setStyle(new Label.LabelStyle(labels.number.getStyle().font, defaultStyle.fontColor));
            }
        }
    }

    protected void animarEntrada(Actor container, float heightOffset) {
        container.setPosition((VIRTUAL_WIDTH - container.getWidth()) / 2, -container.getHeight());
        container.addAction(Actions.sequence(Actions.delay(0.75f), Actions.parallel(Actions.moveTo((VIRTUAL_WIDTH - container.getWidth()) / 2, (VIRTUAL_HEIGHT - container.getHeight()) / heightOffset, 0.25f), Actions.fadeIn(0.5f))));
        stage.addActor(container);
    }

    public void animarSalida(Actor container, Runnable callback) {
        float finalX = container.getX();
        float finalY = -container.getHeight();
        container.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(finalX, finalY, 0.25f), Actions.fadeOut(0.25f)), Actions.run(callback)));
    }

    public abstract void animarSalida(Runnable callback);

    // Clase auxiliar para almacenar los labels del botón
    protected static class ButtonLabels {
        public Label number;
        public Label text;

        public ButtonLabels(Label number, Label text) {
            this.number = number;
            this.text = text;
        }
    }
}
