package com.sticklike.core.pantallas.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class RenderMenuOpciones {
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private Skin uiSkin;
    private MenuOpcionesListener listener;
    private Container<Container<Table>> mainContainer;
    private Slider sliderMusica;
    private Slider sliderEfectos;
    private CheckBox chkPantallaCompleta;
    private TextButton btnVolver;

    public RenderMenuOpciones() {
        stage = new Stage(new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        shapeRenderer = new ShapeRenderer();
        uiSkin = crearSkinBasico();
        crearElementosUI();

    }
    public interface MenuOpcionesListener {
        void onVolver();
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0.89f, 0.89f, 0.89f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(0.64f, 0.80f, 0.9f, 1f);
        float cellSize = 75;
        float startX = stage.getCamera().position.x - (VIRTUAL_WIDTH / 2f);
        float endX = stage.getCamera().position.x + (VIRTUAL_WIDTH / 2f);
        float startY = stage.getCamera().position.y - (VIRTUAL_HEIGHT / 2f);
        float endY = stage.getCamera().position.y + (VIRTUAL_HEIGHT / 2f);
        for (float x = startX - (startX % cellSize); x <= endX; x += cellSize)
            shapeRenderer.line(x, startY, x, endY);
        for (float y = startY - (startY % cellSize); y <= endY; y += cellSize)
            shapeRenderer.line(startX, y, endX, y);
        shapeRenderer.end();
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

    private void crearElementosUI() {
        // Crear y configurar el título
        Actor titleActor = createTitleWithOutline();
        titleActor.getColor().a = 0;
        fadeInActor(titleActor, 0.25f, 0.25f);
        stage.addActor(crearTitulo(titleActor));

        // Crear la tabla de opciones con sliders y checkbox
        Table optionsTable = crearTablaOpciones();

        // Configurar contenedores y agregar al stage
        Container<Table> innerContainer = new Container<>(optionsTable);
        innerContainer.setBackground(crearFondoPapel());
        innerContainer.pad(20);
        innerContainer.pack();

        Container<Container<Table>> borderContainer = new Container<>(innerContainer);
        borderContainer.setBackground(crearBordeAzul());
        borderContainer.pack();
        mainContainer = borderContainer;

        stage.addActor(mainContainer);
        centerActor(mainContainer, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        mainContainer.getColor().a = 0;
        fadeInActor(mainContainer, 0.25f, 0.5f);
    }

    private Table crearTablaOpciones() {
        Table optionsTable = new Table();
        optionsTable.defaults().center().pad(8).width(VIRTUAL_WIDTH / 4).height(40f);

        // Slider de música
        sliderMusica = new Slider(0, 1, 0.01f, false, uiSkin);
        sliderMusica.setValue(1);
        Table sliderMusicaTable = crearSliderMusica(sliderMusica);
        Label volMusicaLabel = new Label("Volumen Música:", uiSkin);
        volMusicaLabel.setAlignment(Align.center);
        optionsTable.add(volMusicaLabel).center().colspan(1).width(140);
        optionsTable.row();
        optionsTable.add(sliderMusicaTable).colspan(1);
        optionsTable.row();

        // Slider de efectos
        sliderEfectos = new Slider(0, 1, 0.01f, false, uiSkin);
        sliderEfectos.setValue(1);
        Table sliderEfectosTable = crearSliderEfectos(sliderEfectos);
        Label volEfectosLabel = new Label("Volumen Efectos:", uiSkin);
        volEfectosLabel.setAlignment(Align.center);
        optionsTable.add(volEfectosLabel).center().colspan(1).width(140);
        optionsTable.row();
        optionsTable.add(sliderEfectosTable).colspan(1);
        optionsTable.row();

        // Checkbox de pantalla completa
        chkPantallaCompleta = new CheckBox("", uiSkin);
        chkPantallaCompleta.getLabel().setAlignment(Align.center);
        chkPantallaCompleta.getImageCell().size(40, 40);

        chkPantallaCompleta.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (chkPantallaCompleta.isChecked()) {
                    Gdx.graphics.setWindowedMode(1920, 1080);

                } else {
                    Graphics.DisplayMode dm = Gdx.graphics.getDisplayMode();
                    Gdx.graphics.setWindowedMode((dm.width),(dm.height));
                }
            }
        });

        Table checkTable = new Table();
        Label chkLabel = new Label("Modo Ventana", uiSkin);
        chkLabel.setAlignment(Align.center);
        checkTable.add(chkLabel).width(140).height(40).padRight(10).left();
        checkTable.add(chkPantallaCompleta).size(40, 40).left();
        optionsTable.add(checkTable).colspan(1).width(220);
        optionsTable.row().padTop(35);

        // Botón Volver
        btnVolver = new TextButton("Volver", uiSkin, "default-button");
        addButtonListeners(btnVolver);
        optionsTable.add(btnVolver).colspan(1);

        return optionsTable;
    }

    private Table crearSliderMusica(final Slider slider) {
        sliderMusica.setValue(GestorDeAudio.getInstance().getVolumenMusica());
        final Label percentageLabel = new Label(String.format("%d%%", (int)(slider.getValue() * 100)), uiSkin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float sliderValue = slider.getValue();
                percentageLabel.setText(String.format("%d%%", (int)(sliderValue * 100)));
                GestorDeAudio.getInstance().setVolumenMusica(sliderValue);
            }
        });
        Table table = new Table();
        table.add(slider).width(VIRTUAL_WIDTH / 4 - 40).padRight(10);
        table.add(percentageLabel).width(40);
        return table;
    }

    private Table crearSliderEfectos(final Slider slider) {
        sliderEfectos.setValue(GestorDeAudio.getInstance().getVolumenEfectos());
        final Label percentageLabel = new Label(String.format("%d%%", (int)(slider.getValue() * 100)), uiSkin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float sliderValue = slider.getValue();
                percentageLabel.setText(String.format("%d%%", (int)(sliderValue * 100)));
                // Aquí actualizamos el volumen de los efectos directamente
                GestorDeAudio.getInstance().setVolumenEfectos(sliderValue);
            }
        });
        Table table = new Table();
        table.add(slider).width(VIRTUAL_WIDTH / 4 - 40).padRight(10);
        table.add(percentageLabel).width(40);
        return table;
    }

    private void addButtonListeners(TextButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (listener != null) listener.onVolver();
            }
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
                    if (listener != null) {
                        listener.onVolver();
                        return true;
                    }
                }
                return super.keyDown(event, keycode);
            }
        });
        button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.setStyle(uiSkin.get("hover-button", TextButton.TextButtonStyle.class));
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setStyle(uiSkin.get("default-button", TextButton.TextButtonStyle.class));
            }
        });
    }

    private Table crearTitulo(Actor titleActor) {
        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.add(titleActor).padTop(75).padBottom(50).center();
        return table;
    }

    private void centerActor(Actor actor, float totalWidth, float totalHeight) {
        actor.setPosition((totalWidth - actor.getWidth()) / 2f, (totalHeight - actor.getHeight()) / 2f);
    }

    private void fadeInActor(Actor actor, float delay, float duration) {
        actor.addAction(Actions.sequence(Actions.delay(delay), Actions.fadeIn(duration)));
    }



    private Actor createTitleWithOutline() {
        Label.LabelStyle mainStyle = new Label.LabelStyle(getFont(), Color.WHITE);
        Label mainLabel = new Label("OPCIONES", mainStyle);
        mainLabel.setFontScale(2.25f);
        Label.LabelStyle outlineStyle = new Label.LabelStyle(getFont(), Color.BLUE);
        outlineStyle.font.getData().setScale(2.25f);
        Group outlineGroup = new Group();
        float offset = 2f;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Label outlineLabel = new Label("OPCIONES", outlineStyle);
                outlineLabel.setPosition(dx * offset, dy * offset);
                outlineGroup.addActor(outlineLabel);
            }
        }
        Stack stack = new Stack();
        stack.add(outlineGroup);
        stack.add(mainLabel);
        return stack;
    }

    private Drawable crearFondoPapel() {
        Pixmap pixmap = new Pixmap(200, 40, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.985f, 0.91f, 0.7f, 1.0f));
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TiledDrawable(new TextureRegion(texture));
    }

    private Drawable crearBordeAzul() {
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

    private Skin crearSkinBasico() {
        Skin skin = new Skin();
        skin.add("default-font", getFont());
        skin.add("default", new Label.LabelStyle(getFont(), Color.DARK_GRAY));
        skin.add("default-button", crearBotonDefault());
        skin.add("default-horizontal", crearEstiloSliders());
        skin.add("default", crearEstiloCheckbox());
        skin.add("hover-button", crearHoverButton());
        skin.add("selected-button", crearSelectedButton());
        return skin;
    }

    private TextButton.TextButtonStyle crearBotonDefault() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = getFont();
        style.up = crearBotonDrawable();
        style.fontColor = Color.DARK_GRAY;
        return style;
    }

    private TextureRegionDrawable crearBotonDrawable() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(new Color(0.97f, 0.88f, 0.6f, 1f));
        pm.fill();
        Texture tex = new Texture(pm);
        pm.dispose();
        return new TextureRegionDrawable(new TextureRegion(tex));
    }

    private SliderStyle crearEstiloSliders() {
        SliderStyle style = new Slider.SliderStyle();

        // Fondo del slider
        Pixmap sliderBgPixmap = new Pixmap(100, 4, Pixmap.Format.RGBA8888);
        sliderBgPixmap.setColor(Color.LIGHT_GRAY);
        sliderBgPixmap.fill();
        Texture sliderBgTexture = new Texture(sliderBgPixmap);
        sliderBgPixmap.dispose();
        style.background = new TextureRegionDrawable(new TextureRegion(sliderBgTexture));

        // Knob del slider con borde
        Pixmap knobPixmap = new Pixmap(15, 15, Pixmap.Format.RGBA8888);
        // Dibuja el círculo completo en negro (borde)
        knobPixmap.setColor(Color.BLUE);
        knobPixmap.fillCircle(7, 7, 7);
        // Dibuja el círculo interior en blanco
        knobPixmap.setColor(Color.WHITE);
        knobPixmap.fillCircle(7, 7, 5);
        Texture knobTexture = new Texture(knobPixmap);
        knobPixmap.dispose();
        style.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));

        return style;
    }

    private CheckBoxStyle crearEstiloCheckbox() {
        CheckBoxStyle style = new CheckBox.CheckBoxStyle();

        // Checkbox en estado "off" (desactivado)
        Pixmap cbPixmapOff = new Pixmap(25, 25, Pixmap.Format.RGBA8888);
        cbPixmapOff.setColor(Color.WHITE);
        cbPixmapOff.fill();
        // Dibujar el borde en negro
        cbPixmapOff.setColor(Color.BLUE);
        cbPixmapOff.drawRectangle(0, 0, 25, 25);
        Texture cbTextureOff = new Texture(cbPixmapOff);
        cbPixmapOff.dispose();

        // Checkbox en estado "on" (activado)
        Pixmap cbPixmapOn = new Pixmap(25, 25, Pixmap.Format.RGBA8888);
        cbPixmapOn.setColor(Color.BLUE);
        cbPixmapOn.fill();
        // Dibujar el borde en negro
        cbPixmapOn.setColor(Color.WHITE);
        cbPixmapOn.drawRectangle(0, 0, 25, 25);
        Texture cbTextureOn = new Texture(cbPixmapOn);
        cbPixmapOn.dispose();

        style.checkboxOff = new TextureRegionDrawable(new TextureRegion(cbTextureOff));
        style.checkboxOn = new TextureRegionDrawable(new TextureRegion(cbTextureOn));
        style.font = getFont();

        return style;
    }

    private TextButtonStyle crearHoverButton() {
        TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = getFont();
        Pixmap hoverPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        hoverPixmap.setColor(new Color(1, 1, 1, 0.3f));
        hoverPixmap.fill();
        Texture hoverTexture = new Texture(hoverPixmap);
        hoverPixmap.dispose();
        style.up = new TextureRegionDrawable(new TextureRegion(hoverTexture));
        style.fontColor = Color.DARK_GRAY;
        return style;
    }

    private TextButtonStyle crearSelectedButton() {
        TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = getFont();
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

    public void setMenuOpcionesListener(MenuOpcionesListener listener) {
        this.listener = listener;
    }

    private BitmapFont getFont() {
        return new BitmapFont();
    }
}
