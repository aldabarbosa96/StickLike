package com.sticklike.core.pantallas.menus.renders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public abstract class RenderMenus {
    protected Stage stage;
    protected Skin uiSkin;
    protected ShapeRenderer shapeRenderer;

    public RenderMenus() {
        stage = new Stage(new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        uiSkin = crearSkinBasico();
        shapeRenderer = new ShapeRenderer();
        agregarVersionLabel();
    }

    protected Skin crearSkinBasico() {
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();

        skin.add("default-font", font);
        skin.add("default", crearLabelStyle(font, Color.GRAY), LabelStyle.class);
        skin.add("default-button", crearDefaultButtonStyle(font), TextButtonStyle.class);
        skin.add("hover-button", crearHoverButtonStyle(font), TextButtonStyle.class);
        skin.add("selected-button", crearSelectedButtonStyle(font), TextButtonStyle.class);
        //skin.add("shadow-container", crearSombraDrawable(Color.BLACK, 30), NinePatchDrawable.class);


        return skin;
    }

    private LabelStyle crearLabelStyle(BitmapFont font, Color color) {
        return new LabelStyle(font, color);
    }

    private TextButtonStyle crearDefaultButtonStyle(BitmapFont font) {
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.up = crearBotonDrawable();
        style.fontColor = new Color(0.3f, 0.3f, 0.3f, 1);
        return style;
    }

    // Crea un Drawable a partir de un Pixmap para el botón (fondo)
    private TextureRegionDrawable crearBotonDrawable() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(new Color(0.97f, 0.88f, 0.6f, 1f));
        pm.fill();
        Texture tex = new Texture(pm);
        pm.dispose();
        return new TextureRegionDrawable(new TextureRegion(tex));
    }

    // Crea el estilo para el botón en estado hover
    private TextButtonStyle crearHoverButtonStyle(BitmapFont font) {
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        Pixmap hoverPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        hoverPixmap.setColor(new Color(1, 1, 1, 0.3f));
        hoverPixmap.fill();
        Texture hoverTexture = new Texture(hoverPixmap);
        hoverPixmap.dispose();
        style.up = new TextureRegionDrawable(new TextureRegion(hoverTexture));
        style.fontColor = Color.DARK_GRAY;
        return style;
    }

    // Crea el estilo para el botón seleccionado (con efecto glow)
    private TextButtonStyle crearSelectedButtonStyle(BitmapFont font) {
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        Pixmap glowPixmap = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        glowPixmap.setColor(new Color(1f, 1f, 1f, 0.8f));
        glowPixmap.fill();
        Texture glowTexture = new Texture(glowPixmap);
        glowPixmap.dispose();
        NinePatch glowNinePatch = new NinePatch(glowTexture, 5, 5, 5, 5);
        style.up = new NinePatchDrawable(glowNinePatch);
        style.fontColor = Color.BLUE;
        return style;
    }


    public void render(float delta) {
        // Limpieza de pantalla
        Gdx.gl.glClearColor(0.89f, 0.89f, 0.89f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujo de cuadrícula
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.64f, 0.80f, 0.9f, 1f);
        float cellSize = 75;
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


    protected Actor tituloConReborde(String text, float fontScale) {
        Stack stack = new Stack();
        Label.LabelStyle mainStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.WHITE);
        Label mainLabel = new Label(text, mainStyle);
        mainLabel.setFontScale(fontScale);

        Label.LabelStyle outlineStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.BLUE);
        outlineStyle.font.getData().setScale(fontScale);

        Group outlineGroup = new Group();
        float offset = 2f;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Label outlineLabel = new Label(text, outlineStyle);
                outlineLabel.setFontScale(fontScale);
                outlineLabel.setPosition(dx * offset, dy * offset);
                outlineGroup.addActor(outlineLabel);
            }
        }
        stack.add(outlineGroup);
        stack.add(mainLabel);
        return stack;
    }

    protected Drawable papelFondo() {
        Pixmap pixmap = new Pixmap(200, 40, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.985f, 0.91f, 0.7f, 1.0f));
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TiledDrawable(new TextureRegion(texture));
    }

    protected Drawable bordeAzul() {
        Pixmap borderPixmap = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        borderPixmap.setColor(0, 0, 0, 0);
        borderPixmap.fill();
        borderPixmap.setColor(Color.BLUE);
        borderPixmap.drawRectangle(0, 0, 12, 12);
        Texture borderTex = new Texture(borderPixmap);
        borderPixmap.dispose();
        NinePatch borderPatch = new NinePatch(borderTex, 1, 1, 1, 1);
        return new NinePatchDrawable(borderPatch);
    }

    private void agregarVersionLabel() {
        Label versionLabel = new Label("v1.10.9-dev", uiSkin);
        versionLabel.setFontScale(0.95f);
        versionLabel.setColor(Color.DARK_GRAY);

        Table versionTable = new Table();
        versionTable.setFillParent(true);
        versionTable.bottom().right().padRight(17.5f).padBottom(30);
        versionTable.add(versionLabel);

        stage.addActor(versionTable);
    }

    protected NinePatchDrawable crearSombraDrawable(Color color, int width, int height, int offsetX, int offsetY) {
        Pixmap pixmap = new Pixmap(width + offsetX, height + offsetY, Pixmap.Format.RGBA8888);

        // Capa externa con menor intensidad
        for (int i = 0; i < offsetX; i++) {
            // Usamos un alpha menor: de 0 a 0.05 en lugar de 0 a 0.10
            float alpha = 0.05f * ((float) i / offsetX);
            pixmap.setColor(new Color(color.r, color.g, color.b, alpha));
            pixmap.fillRectangle(i, i, width, height);
        }

        // Capa interna también con intensidad reducida
        for (int i = 0; i < offsetX / 2; i++) {
            // Ajuste proporcional para que el efecto sea sutil
            float alpha = 0.05f - (0.075f * ((float) i / ((float) offsetX / 2)));
            pixmap.setColor(new Color(color.r, color.g, color.b, alpha));
            // Ajusta estos valores según el tamaño deseado; aquí se usa un offset y reducción fijos
            pixmap.fillRectangle(i + 5, i + 5, width - 200, height - 200);
        }

        Texture shadowTexture = new Texture(pixmap);
        pixmap.dispose();

        NinePatch shadowPatch = new NinePatch(new TextureRegion(shadowTexture), offsetX / 3, offsetX / 2, offsetY / 3, offsetY / 2);
        return new NinePatchDrawable(shadowPatch);
    }




    protected void animarEntrada(Actor container) {
        container.setPosition((VIRTUAL_WIDTH - container.getWidth()) / 2, -container.getHeight());
        container.addAction(Actions.sequence(Actions.delay(0.75f), Actions.parallel(Actions.moveTo((VIRTUAL_WIDTH - container.getWidth()) / 2, (VIRTUAL_HEIGHT - container.getHeight()) / 2f, 0.25f), Actions.fadeIn(0.5f))));
        stage.addActor(container);
    }

    public void animarSalida(Actor container, Runnable callback) {
        float finalX = container.getX();
        float finalY = -container.getHeight();
        container.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(finalX, finalY, 0.25f), Actions.fadeOut(0.25f)), Actions.run(callback)));
    }

    public abstract void animarSalida(Runnable callback);
}
