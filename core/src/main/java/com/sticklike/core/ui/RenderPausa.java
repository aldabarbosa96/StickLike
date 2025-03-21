package com.sticklike.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.pantallas.menus.ventanas.MenuPrincipal;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class RenderPausa {
    private Pausa pausa;
    private VentanaJuego1 ventanaJuego1;
    private OrthographicCamera hudCamera;
    private FillViewport hudViewport;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private Stage pauseStage;
    private Skin pauseSkin;
    private Table pauseTable;
    private Window windowOpciones;
    private float pauseWidth, pauseHeight, pauseSpacing, menuWidth, marginRight, marginTop;
    private Texture blankTexture;
    private Array<TextButton> menuButtons = new Array<>();
    private int currentIndex = 0;

    public RenderPausa(Pausa pausa, VentanaJuego1 ventanaJuego1) {
        this.pausa = pausa;
        this.ventanaJuego1 = ventanaJuego1;
        this.pauseWidth = 4;
        this.pauseHeight = 12;
        this.pauseSpacing = 4;
        this.menuWidth = 22.5f;
        this.marginRight = 20;
        this.marginTop = 55;
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        hudViewport = new FillViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, hudCamera);
        hudViewport.apply();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        pauseStage = new Stage(hudViewport);
        blankTexture = createSolidTexture(1, 1, Color.WHITE);
        crearSkin();
        crearMenuPausa();
        crearVentanaOpciones();
    }

    public void drawOverlay() {
        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        spriteBatch.setColor(0, 0, 0, 0.5f);
        spriteBatch.draw(blankTexture, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.end();
    }

    public void drawPauseIcon(ShapeRenderer shapeRenderer) {
        hudViewport.apply();
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        float extraVerticalOffset = -50f;
        float pauseButtonX = VIRTUAL_WIDTH - marginRight - menuWidth - START_BUTTON_CORRECTION;
        float pauseButtonY = VIRTUAL_HEIGHT - marginTop - menuWidth - BUTTON_PAUSE_Y_CORRECTION - extraVerticalOffset;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.65f));
        shapeRenderer.rect(pauseButtonX, pauseButtonY, menuWidth, menuWidth);
        float pauseX = pauseButtonX + (menuWidth - (pauseWidth * 2 + pauseSpacing)) / 2;
        float pauseY = pauseButtonY + (menuWidth - pauseHeight) / 2;
        shapeRenderer.setColor(pausa.isPaused() ? Color.WHITE : Color.BLUE);
        shapeRenderer.rect(pauseX - BORDER_NEGATIVE, pauseY - BORDER_NEGATIVE, pauseWidth + BORDER_POSITIVE, pauseHeight + BORDER_POSITIVE);
        shapeRenderer.rect(pauseX + pauseWidth + pauseSpacing - BORDER_NEGATIVE, pauseY - BORDER_NEGATIVE, pauseWidth + BORDER_POSITIVE, pauseHeight + BORDER_POSITIVE);
        shapeRenderer.setColor(pausa.isPaused() ? Color.BLUE : Color.WHITE);
        shapeRenderer.rect(pauseX, pauseY, pauseWidth, pauseHeight);
        shapeRenderer.rect(pauseX + pauseWidth + pauseSpacing, pauseY, pauseWidth, pauseHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void drawStartText() {
        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        font.getData().setScale(0.8f);
        float extraVerticalOffset = -50f;
        float pauseButtonX = VIRTUAL_WIDTH - marginRight - menuWidth - START_BUTTON_CORRECTION;
        float pauseButtonY = VIRTUAL_HEIGHT - marginTop - menuWidth - BUTTON_PAUSE_Y_CORRECTION - extraVerticalOffset;
        GlyphLayout layoutStart = new GlyphLayout(font, START);
        float startTextX = pauseButtonX + (menuWidth - layoutStart.width) / 2 - START_BUTTON_CORRECTION;
        float startTextY = pauseButtonY - 10;
        font.setColor(Color.BLUE);
        font.draw(spriteBatch, START, startTextX - BASIC_OFFSET, startTextY);
        font.draw(spriteBatch, START, startTextX + BASIC_OFFSET, startTextY);
        font.draw(spriteBatch, START, startTextX, startTextY - BASIC_OFFSET);
        font.draw(spriteBatch, START, startTextX, startTextY + BASIC_OFFSET);
        if (!pausa.isPaused()) {
            font.setColor(Color.WHITE);
        } else {
            font.setColor(Color.BLUE);
        }
        font.draw(spriteBatch, START, startTextX, startTextY);
        spriteBatch.end();
    }

    public void drawPauseText() {
        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        font.getData().setScale(2.5f);
        GlyphLayout layoutPausa = new GlyphLayout(font, PAUSA);
        float pauseTextX = (VIRTUAL_WIDTH - layoutPausa.width) / 2;
        float pauseTextY = (VIRTUAL_HEIGHT + layoutPausa.height) / 2 + 250f;
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, PAUSA, pauseTextX - BASIC_OFFSET, pauseTextY);
        font.draw(spriteBatch, PAUSA, pauseTextX + BASIC_OFFSET, pauseTextY);
        font.draw(spriteBatch, PAUSA, pauseTextX, pauseTextY - BASIC_OFFSET);
        font.draw(spriteBatch, PAUSA, pauseTextX, pauseTextY + BASIC_OFFSET);
        font.setColor(Color.BLUE);
        font.draw(spriteBatch, PAUSA, pauseTextX, pauseTextY);
        spriteBatch.end();
    }

    public void drawStage() {
        hudViewport.apply();
        pauseStage.act(Gdx.graphics.getDeltaTime());
        pauseStage.draw();
    }

    public void navegar(int dir) {
        currentIndex += dir;
        if (currentIndex < 0) currentIndex = menuButtons.size - 1;
        if (currentIndex >= menuButtons.size) currentIndex = 0;
        selectButton(currentIndex);
    }

    public void navigateUp() {
        navegar(-1);
    }

    public void navigateDown() {
        navegar(1);
    }

    public void selectCurrent() {
        if (currentIndex < 0 || currentIndex >= menuButtons.size) return;
        TextButton btn = menuButtons.get(currentIndex);
        InputEvent click = new InputEvent();
        click.setType(InputEvent.Type.touchDown);
        btn.fire(click);
        InputEvent release = new InputEvent();
        release.setType(InputEvent.Type.touchUp);
        btn.fire(release);
    }

    public void selectButton(int idx) {
        for (int i = 0; i < menuButtons.size; i++) {
            menuButtons.get(i).setChecked(i == idx);
        }
    }

    public void setCurrentIndex(int index) {
        currentIndex = Math.min(Math.max(0, index), menuButtons.size - 1);
        selectButton(currentIndex);
    }

    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
        pauseStage.dispose();
        pauseSkin.dispose();
        blankTexture.dispose();
    }

    private void crearSkin() {
        pauseSkin = new Skin();
        BitmapFont skinFont = new BitmapFont();
        pauseSkin.add("default-font", skinFont);
        LabelStyle labelStyle = new LabelStyle(skinFont, Color.WHITE);
        pauseSkin.add("default", labelStyle);
        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.font = skinFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = Color.BLUE;
        btnStyle.checkedFontColor = Color.BLUE;
        pauseSkin.add("default-btn", btnStyle);
        SliderStyle sliderStyle = new SliderStyle();
        Texture sliderBgTexture = createSolidTexture(100, 4, Color.LIGHT_GRAY);
        sliderStyle.background = new TextureRegionDrawable(new TextureRegion(sliderBgTexture));
        Pixmap knobPixmap = new Pixmap(15, 15, Pixmap.Format.RGBA8888);
        knobPixmap.setColor(Color.BLUE);
        knobPixmap.fillCircle(7, 7, 7);
        knobPixmap.setColor(Color.WHITE);
        knobPixmap.fillCircle(7, 7, 5);
        Texture knobTexture = new Texture(knobPixmap);
        knobPixmap.dispose();
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));
        pauseSkin.add("default-horizontal", sliderStyle, SliderStyle.class);
        CheckBoxStyle cbStyle = new CheckBoxStyle();
        Texture cbTextureOff = createTextureWithRectangle(25, 25, Color.WHITE, Color.BLUE);
        cbStyle.checkboxOff = new TextureRegionDrawable(new TextureRegion(cbTextureOff));
        Texture cbTextureOn = createTextureWithRectangle(25, 25, Color.BLUE, Color.WHITE);
        cbStyle.checkboxOn = new TextureRegionDrawable(new TextureRegion(cbTextureOn));
        cbStyle.font = skinFont;
        cbStyle.fontColor = Color.WHITE;
        pauseSkin.add("default", cbStyle, CheckBoxStyle.class);
    }

    private void crearMenuPausa() {
        pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseTable.center();
        pauseTable.add().height(125);
        pauseTable.row();
        TextButton btnMenuPrincipal = createButton("Menú Principal", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ventanaJuego1.reproducirSonidoPausa();
                ventanaJuego1.getGame().setScreen(new MenuPrincipal(ventanaJuego1.getGame()));
            }
        });
        TextButton btnOpciones = createButton("Opciones", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseTable.setVisible(false);
                mostrarOpcionesPopup();
            }
        });
        TextButton btnSalir = createButton("Salir del Juego", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        TextButton btnVolver = createButton("Volver", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pausa.alternarPausa();
            }
        });
        pauseTable.add(btnMenuPrincipal).width(220).height(25).pad(10);
        pauseTable.row();
        pauseTable.add(btnOpciones).width(220).height(25).pad(10);
        pauseTable.row();
        pauseTable.add(btnSalir).width(220).height(25).pad(10);
        pauseTable.row();
        pauseTable.add(btnVolver).width(220).height(25).pad(10);
        pauseStage.addActor(pauseTable);
        menuButtons.add(btnMenuPrincipal);
        menuButtons.add(btnOpciones);
        menuButtons.add(btnSalir);
        menuButtons.add(btnVolver);
    }

    private void crearVentanaOpciones() {
        WindowStyle windowStyle = new WindowStyle(pauseSkin.getFont("default-font"), Color.BLACK, crearFondoPapelDrawable());
        windowOpciones = new Window("OPCIONES", windowStyle);
        windowOpciones.getTitleTable().padTop(25);
        windowOpciones.getTitleTable().center();
        windowOpciones.getTitleLabel().setAlignment(Align.center);
        windowOpciones.padTop(25);
        windowOpciones.setModal(true);
        windowOpciones.setMovable(false);
        Table contentTable = new Table();
        contentTable.padTop(10);
        contentTable.defaults().pad(10);
        contentTable.add(createVolumeControlRow("Volumen Música:")).colspan(2).align(Align.left);
        contentTable.row();
        contentTable.add(createVolumeControlRow("Volumen Efectos:")).colspan(2).align(Align.left);
        contentTable.row();
        Table checkTable = new Table();
        Label chkLabel = new Label("Modo Ventana", pauseSkin);
        chkLabel.setFontScale(0.7f);
        chkLabel.setColor(Color.BLACK);
        final CheckBox chkVentana = new CheckBox("", pauseSkin);
        checkTable.add(chkLabel).padRight(10).align(Align.left);
        checkTable.add(chkVentana).size(40, 40);
        contentTable.add(checkTable).colspan(2).padTop(15);
        contentTable.row();
        chkVentana.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (chkVentana.isChecked()) {
                    Gdx.graphics.setWindowedMode((int) (Gdx.graphics.getWidth() / 1.33f), (int) (Gdx.graphics.getHeight() / 1.33f));
                } else {
                    Graphics.DisplayMode dm = Gdx.graphics.getDisplayMode();
                    Gdx.graphics.setWindowedMode(dm.width, dm.height);
                }
            }
        });
        TextButton btnVolver = new TextButton("Volver", pauseSkin, "default-btn");
        btnVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                windowOpciones.remove();
                pauseTable.setVisible(true);
            }
        });
        btnVolver.pad(10);
        contentTable.row().padTop(20);
        contentTable.add(btnVolver).colspan(2).width(140).height(45);
        windowOpciones.add(contentTable).pad(20);
        windowOpciones.pack();
    }

    private Table createVolumeControlRow(final String labelText) {
        Table row = new Table();
        Label label = new Label(labelText, pauseSkin);
        label.setFontScale(0.7f);
        label.setColor(Color.BLACK);
        final Slider slider = new Slider(0, 1, 0.01f, false, pauseSkin);
        if (labelText.contains("Música")) {
            slider.setValue(GestorDeAudio.getInstance().getVolumenMusica());
        } else if (labelText.contains("Efectos")) {
            slider.setValue(GestorDeAudio.getInstance().getVolumenEfectos());
        }
        final Label percentLabel = new Label(String.format("%d%%", (int) (slider.getValue() * 100)), pauseSkin);
        percentLabel.setFontScale(0.7f);
        percentLabel.setColor(Color.BLACK);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float sliderValue = slider.getValue();
                int porcentaje = (int) (sliderValue * 100);
                percentLabel.setText(porcentaje + "%");
                if (labelText.contains("Música")) {
                    GestorDeAudio.getInstance().setVolumenMusica(sliderValue);
                } else if (labelText.contains("Efectos")) {
                    GestorDeAudio.getInstance().setVolumenEfectos(sliderValue);
                }
            }
        });
        row.add(label).colspan(2).align(Align.left).padBottom(10);
        row.row();
        row.add(slider).width(200).padRight(10);
        row.add(percentLabel).width(40);
        return row;
    }

    private void mostrarOpcionesPopup() {
        windowOpciones.setPosition((pauseStage.getWidth() - windowOpciones.getWidth()) / 2f, (pauseStage.getHeight() - windowOpciones.getHeight()) / 2f + 20f);
        pauseStage.addActor(windowOpciones);
    }

    private TextButton createButton(String text, ClickListener listener) {
        TextButton btn = new TextButton(text, pauseSkin, "default-btn");
        btn.addListener(listener);
        btn.pad(10);
        return btn;
    }

    private Texture createSolidTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createTextureWithRectangle(int width, int height, Color fillColor, Color borderColor) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(fillColor);
        pixmap.fill();
        pixmap.setColor(borderColor);
        pixmap.drawRectangle(0, 0, width, height);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Drawable crearFondoPapelDrawable() {
        Pixmap pixmap = new Pixmap(200, 40, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.985f, 0.91f, 0.7f, 1.0f));
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TiledDrawable(new TextureRegion(texture));
    }

    public Viewport getHudViewport() {
        return hudViewport;
    }

    public Stage getPauseStage() {
        return pauseStage;
    }
}
