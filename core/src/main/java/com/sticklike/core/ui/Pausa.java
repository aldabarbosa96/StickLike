package com.sticklike.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sticklike.core.pantallas.menus.MenuPrincipal;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Gestiona el menú de pausa del juego, mostrando botones de navegación y un popup de opciones.
 */
public class Pausa extends ControllerAdapter {
    private float pauseWidth, pauseHeight, pauseSpacing, menuWidth, marginRight, marginTop;
    private boolean isPaused;
    private boolean inputsBloqueados;
    private VentanaJuego1 ventanaJuego1;
    private BitmapFont font;
    private SpriteBatch spriteBatch;
    private OrthographicCamera hudCamera;
    private Viewport hudViewport;
    private Stage pauseStage;
    private Skin pauseSkin;
    private Table pauseTable;
    private Window windowOpciones;
    private Texture blankTexture;

    public Pausa(VentanaJuego1 ventanaJuego1) {
        // Parámetros del icono
        this.pauseWidth = 4;
        this.pauseHeight = 12;
        this.pauseSpacing = 4;
        this.menuWidth = 22.5f;
        this.marginRight = 20;
        this.marginTop = 55;

        this.isPaused = false;
        this.inputsBloqueados = false;
        this.ventanaJuego1 = ventanaJuego1;

        // Fuentes y batch
        this.font = new BitmapFont();
        this.spriteBatch = new SpriteBatch();

        // Configuración de cámara y viewport
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        hudViewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, hudCamera);
        hudViewport.apply();

        pauseStage = new Stage(hudViewport);

        // Creamos una textura sólida para el overlay
        blankTexture = createSolidTexture(1, 1, Color.WHITE);

        // Skin y UI
        crearSkin();
        crearMenuPausa();
        crearVentanaOpciones();

        Controllers.addListener(this);
    }

    // Crea una textura de color sólido
    private Texture createSolidTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    // Crea una textura con relleno y borde (usada en CheckBox)
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

    private void crearSkin() {
        pauseSkin = new Skin();

        BitmapFont skinFont = new BitmapFont();
        pauseSkin.add("default-font", skinFont);

        // LabelStyle
        Label.LabelStyle labelStyle = new Label.LabelStyle(skinFont, Color.WHITE);
        pauseSkin.add("default", labelStyle);

        // Estilo por defecto para botones
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = skinFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = Color.BLUE;
        pauseSkin.add("default-btn", btnStyle);

        TextButton.TextButtonStyle volverStyle = new TextButton.TextButtonStyle(btnStyle);
        volverStyle.fontColor = Color.BLACK;
        pauseSkin.add("volver-btn", volverStyle);

        // SliderStyle
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
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

        pauseSkin.add("default-horizontal", sliderStyle, Slider.SliderStyle.class);

        // CheckBoxStyle
        CheckBox.CheckBoxStyle cbStyle = new CheckBox.CheckBoxStyle();
        Texture cbTextureOff = createTextureWithRectangle(25, 25, Color.WHITE, Color.BLUE);
        cbStyle.checkboxOff = new TextureRegionDrawable(new TextureRegion(cbTextureOff));
        Texture cbTextureOn = createTextureWithRectangle(25, 25, Color.BLUE, Color.WHITE);
        cbStyle.checkboxOn = new TextureRegionDrawable(new TextureRegion(cbTextureOn));
        cbStyle.font = skinFont;
        cbStyle.fontColor = Color.WHITE;
        pauseSkin.add("default", cbStyle, CheckBox.CheckBoxStyle.class);
    }

    // Crea un botón usando el estilo "default-btn"
    private TextButton createButton(String text, ClickListener listener) {
        TextButton btn = new TextButton(text, pauseSkin, "default-btn");
        btn.addListener(listener);
        btn.pad(10);
        return btn;
    }

    // Crea la tabla del menú de pausa, colocando "Volver" debajo de "Salir del Juego"
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
        pauseTable.add(btnMenuPrincipal).width(220).height(25).pad(10);
        pauseTable.row();

        TextButton btnOpciones = createButton("Opciones", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseTable.setVisible(false);
                mostrarOpcionesPopup();
            }
        });
        pauseTable.add(btnOpciones).width(220).height(25).pad(10);
        pauseTable.row();

        TextButton btnSalir = createButton("Salir del Juego", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        pauseTable.add(btnSalir).width(220).height(25).pad(10);
        pauseTable.row();

        // En el menú principal el botón "Volver" se deja con el estilo por defecto (blanco)
        TextButton btnVolver = createButton("Volver", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                alternarPausa();
            }
        });
        pauseTable.add(btnVolver).width(220).height(25).pad(10);

        pauseStage.addActor(pauseTable);
    }

    // Crea una fila de control de volumen (Label, Slider y etiqueta de porcentaje)
    private Table createVolumeControlRow(String labelText) {
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

        // Listener para actualizar el porcentaje y el volumen
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

        row.add(label).colspan(2).align(Align.left);
        row.row();
        row.add(slider).width(200).padRight(10);
        row.add(percentLabel).width(40);
        return row;
    }


    private void crearVentanaOpciones() {
        // Se crea el estilo de la ventana con título en negro
        Window.WindowStyle windowStyle = new Window.WindowStyle(pauseSkin.getFont("default-font"), Color.BLACK, crearFondoPapelDrawable());
        windowOpciones = new Window("OPCIONES", windowStyle);
        // Centrar el título y ajustar su alineación
        windowOpciones.getTitleTable().center();
        windowOpciones.getTitleLabel().setAlignment(Align.center);
        // Reducir la separación entre el título y el contenido
        windowOpciones.padTop(20);
        windowOpciones.setModal(true);
        windowOpciones.setMovable(false);

        Table contentTable = new Table();
        // Menor separación entre título y contenido
        contentTable.padTop(10);
        contentTable.defaults().pad(10);

        contentTable.add(createVolumeControlRow("Volumen Música:")).colspan(2).align(Align.left);
        contentTable.row();
        contentTable.add(createVolumeControlRow("Volumen Efectos:")).colspan(2).align(Align.left);
        contentTable.row();

        Table checkTable = new Table();
        Label chkLabel = new Label("Modo Ventana", pauseSkin);
        // Ajuste para el texto del checkbox: tamaño menor y color negro
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
                    Gdx.graphics.setWindowedMode(1920, 1080);
                } else {
                    Graphics.DisplayMode dm = Gdx.graphics.getDisplayMode();
                    Gdx.graphics.setWindowedMode(dm.width, dm.height);
                }
            }
        });

        TextButton btnVolver = new TextButton("Volver", pauseSkin, "volver-btn");
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

    private Drawable crearFondoPapelDrawable() {
        Pixmap pixmap = new Pixmap(200, 40, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.985f, 0.91f, 0.7f, 1.0f)); // color "post-it"
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TiledDrawable(new TextureRegion(texture));
    }

    private void mostrarOpcionesPopup() {
        windowOpciones.setPosition((pauseStage.getWidth() - windowOpciones.getWidth()) / 2f, (pauseStage.getHeight() - windowOpciones.getHeight()) / 2f + 20f);
        pauseStage.addActor(windowOpciones);
    }

    public void render(ShapeRenderer shapeRenderer) {
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);

        float extraVerticalOffset = -50f;
        float pauseButtonX = VIRTUAL_WIDTH - marginRight - menuWidth - START_BUTTON_CORRECTION;
        float pauseButtonY = VIRTUAL_HEIGHT - marginTop - menuWidth - BUTTON_PAUSE_Y_CORRECTION - extraVerticalOffset;

        if (isPaused) {
            spriteBatch.begin();
            spriteBatch.setColor(0, 0, 0, 0.5f);
            spriteBatch.draw(blankTexture, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
            spriteBatch.end();
        }

        // Dibuja el icono de pausa
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.65f));
        shapeRenderer.rect(pauseButtonX, pauseButtonY, menuWidth, menuWidth);

        float pauseX = pauseButtonX + (menuWidth - (pauseWidth * 2 + pauseSpacing)) / 2;
        float pauseY = pauseButtonY + (menuWidth - pauseHeight) / 2;

        shapeRenderer.setColor(isPaused ? Color.WHITE : Color.BLUE);
        shapeRenderer.rect(pauseX - BORDER_NEGATIVE, pauseY - BORDER_NEGATIVE, pauseWidth + BORDER_POSITIVE, pauseHeight + BORDER_POSITIVE);
        shapeRenderer.rect(pauseX + pauseWidth + pauseSpacing - BORDER_NEGATIVE, pauseY - BORDER_NEGATIVE, pauseWidth + BORDER_POSITIVE, pauseHeight + BORDER_POSITIVE);

        shapeRenderer.setColor(isPaused ? Color.BLUE : Color.WHITE);
        shapeRenderer.rect(pauseX, pauseY, pauseWidth, pauseHeight);
        shapeRenderer.rect(pauseX + pauseWidth + pauseSpacing, pauseY, pauseWidth, pauseHeight);
        shapeRenderer.end();


        Gdx.gl.glDisable(GL20.GL_BLEND);

        spriteBatch.begin();
        font.getData().setScale(0.8f);
        GlyphLayout layoutStart = new GlyphLayout(font, START);
        float startTextX = pauseButtonX + (menuWidth - layoutStart.width) / 2 - START_BUTTON_CORRECTION;
        float startTextY = pauseButtonY - 10;
        if (!isPaused) {
            font.setColor(Color.BLUE);
            font.draw(spriteBatch, START, startTextX - BASIC_OFFSET, startTextY);
            font.draw(spriteBatch, START, startTextX + BASIC_OFFSET, startTextY);
            font.draw(spriteBatch, START, startTextX, startTextY - BASIC_OFFSET);
            font.draw(spriteBatch, START, startTextX, startTextY + BASIC_OFFSET);
            font.setColor(Color.WHITE);
            font.draw(spriteBatch, START, startTextX, startTextY);
        } else {
            font.setColor(Color.BLUE);
            font.draw(spriteBatch, START, startTextX - BASIC_OFFSET, startTextY);
            font.draw(spriteBatch, START, startTextX + BASIC_OFFSET, startTextY);
            font.draw(spriteBatch, START, startTextX, startTextY - BASIC_OFFSET);
            font.draw(spriteBatch, START, startTextX, startTextY + BASIC_OFFSET);
            font.draw(spriteBatch, START, startTextX, startTextY);
        }
        spriteBatch.end();


        if (isPaused) {
            ventanaJuego1.getHud().renderizarHUD(Gdx.graphics.getDeltaTime());

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

            pauseStage.act(Gdx.graphics.getDeltaTime());
            pauseStage.draw();
        }
    }

    public void handleInput() {
        if (inputsBloqueados) return;
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            alternarPausa();
        }
    }

    private void alternarPausa() {
        isPaused = !isPaused;
        ventanaJuego1.setPausado(isPaused);
        if (isPaused) {
            ventanaJuego1.reproducirSonidoPausa();
            Gdx.input.setInputProcessor(pauseStage);
        } else {
            Gdx.input.setInputProcessor(null);
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void bloquearInputs(boolean bloquear) {
        inputsBloqueados = bloquear;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        if (inputsBloqueados) return false;
        if (buttonIndex == BUTTON_START) {
            alternarPausa();
            return true;
        }
        return false;
    }

    public Viewport getViewport() {
        return hudViewport;
    }

    public void dispose() {
        Controllers.removeListener(this);
        spriteBatch.dispose();
        font.dispose();
        pauseStage.dispose();
        pauseSkin.dispose();
        blankTexture.dispose();
    }
}
