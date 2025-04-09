package com.sticklike.core.pantallas.menus.renders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.ArrayList;
import java.util.List;

public class RenderBaseMenuOpciones extends RenderBaseMenus {
    private MenuOpcionesListener listener;
    private Container<Container<Table>> mainContainer;
    private Slider sliderMusica;
    private Slider sliderEfectos;
    private CheckBox chkPantallaCompleta;
    private TextButton btnVolver;
    private List<Actor> focus;

    public interface MenuOpcionesListener {
        void onVolver();
    }

    public void setMenuOpcionesListener(MenuOpcionesListener listener) {
        this.listener = listener;
    }

    public RenderBaseMenuOpciones() {
        super();
        crearElementosUI();
    }

    private void crearElementosUI() {
        Actor titleActor = tituloConReborde("OPCIONES", 2.25f);
        titleActor.getColor().a = 0;
        fadeInActor(titleActor, 0.25f, 0.25f);
        stage.addActor(crearTitulo(titleActor));

        // Creamos tabla de opciones con sliders, checkbox y botón Volver
        Table optionsTable = crearTablaOpciones();

        // Usamos los métodos comunes de RenderMenus para fondo y borde
        Container<Table> innerContainer = new Container<>(optionsTable);
        innerContainer.setBackground(papelFondo());
        innerContainer.pad(20);
        innerContainer.pack();

        Container<Container<Table>> borderContainer = new Container<>(innerContainer);
        borderContainer.setBackground(crearSombraConBorde(Color.DARK_GRAY,10,Color.BLUE,2));
        borderContainer.pack();
        mainContainer = borderContainer;

        // Posicionamos el contenedor fuera de la pantalla para animar su entrada
       animarEntrada(mainContainer,2);
    }

    private Table crearTablaOpciones() {
        Table optionsTable = new Table();
        optionsTable.defaults().center().pad(8).width(VIRTUAL_WIDTH / 4).height(40f);

        // Slider de música
        sliderMusica = new Slider(0, 1, 0.01f, false, uiSkin);
        sliderMusica.setValue(GestorDeAudio.getInstance().getVolumenMusica());
        Table sliderMusicaTable = crearSliderMusica(sliderMusica);
        Label volMusicaLabel = new Label("Volumen Música:", uiSkin);
        volMusicaLabel.setAlignment(Align.center);
        optionsTable.add(volMusicaLabel).center().colspan(1).width(140);
        optionsTable.row();
        optionsTable.add(sliderMusicaTable).colspan(1);
        optionsTable.row();

        // Slider de efectos
        sliderEfectos = new Slider(0, 1, 0.01f, false, uiSkin);
        sliderEfectos.setValue(GestorDeAudio.getInstance().getVolumenEfectos());
        Table sliderEfectosTable = crearSliderEfectos(sliderEfectos);
        Label volEfectosLabel = new Label("Volumen Efectos:", uiSkin);
        volEfectosLabel.setAlignment(Align.center);
        optionsTable.add(volEfectosLabel).center().colspan(1).width(140);
        optionsTable.row();
        optionsTable.add(sliderEfectosTable).colspan(1);
        optionsTable.row();

        // Checkbox de modo ventana
        chkPantallaCompleta = new CheckBox("", uiSkin);
        chkPantallaCompleta.getLabel().setAlignment(Align.center);
        chkPantallaCompleta.getImageCell().size(40, 40);
        chkPantallaCompleta.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (chkPantallaCompleta.isChecked()) {
                    Gdx.graphics.setWindowedMode((int) (Gdx.graphics.getWidth() / 1.33f), (int) (Gdx.graphics.getHeight() / 1.33f));
                } else {
                    Graphics.DisplayMode dm = Gdx.graphics.getDisplayMode();
                    Gdx.graphics.setWindowedMode(dm.width, dm.height);
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
        btnVolver.getLabel().setColor(Color.BLACK);
        optionsTable.add(btnVolver).colspan(1);

        return optionsTable;
    }

    private Table crearSliderMusica(final Slider slider) {
        final Label percentageLabel = new Label(String.format("%d%%", (int) (slider.getValue() * 100)), uiSkin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float sliderValue = slider.getValue();
                percentageLabel.setText(String.format("%d%%", (int) (sliderValue * 100)));
                GestorDeAudio.getInstance().setVolumenMusica(sliderValue);
            }
        });
        Table table = new Table();
        table.add(slider).width(VIRTUAL_WIDTH / 4 - 40).padRight(10);
        table.add(percentageLabel).width(40);
        return table;
    }

    private Table crearSliderEfectos(final Slider slider) {
        final Label percentageLabel = new Label(String.format("%d%%", (int) (slider.getValue() * 100)), uiSkin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float sliderValue = slider.getValue();
                percentageLabel.setText(String.format("%d%%", (int) (sliderValue * 100)));
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
                button.setStyle(uiSkin.get("hover-button", TextButtonStyle.class));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setStyle(uiSkin.get("default-button", TextButtonStyle.class));
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

    private void fadeInActor(Actor actor, float delay, float duration) {
        actor.addAction(Actions.sequence(Actions.delay(delay), Actions.fadeIn(duration)));
    }

    @Override
    protected Skin crearSkinBasico() {
        Skin skin = new Skin();
        skin.add("default-font", getFont());
        skin.add("default", new Label.LabelStyle(getFont(), Color.BLACK), Label.LabelStyle.class);
        skin.add("default-button", crearBotonDefault(), TextButton.TextButtonStyle.class);
        skin.add("default-horizontal", crearEstiloSliders(), Slider.SliderStyle.class);
        // Aquí se registra el estilo para CheckBox con el nombre "default"
        skin.add("default", crearEstiloCheckbox(), CheckBox.CheckBoxStyle.class);
        skin.add("hover-button", crearHoverButton(), TextButton.TextButtonStyle.class);
        skin.add("selected-button", crearSelectedButton(), TextButton.TextButtonStyle.class);
        return skin;
    }


    private TextButtonStyle crearBotonDefault() {
        TextButtonStyle style = new TextButtonStyle();
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
        SliderStyle style = new SliderStyle();

        // Fondo del slider
        Pixmap sliderBgPixmap = new Pixmap(100, 4, Pixmap.Format.RGBA8888);
        sliderBgPixmap.setColor(Color.LIGHT_GRAY);
        sliderBgPixmap.fill();
        Texture sliderBgTexture = new Texture(sliderBgPixmap);
        sliderBgPixmap.dispose();
        style.background = new TextureRegionDrawable(new TextureRegion(sliderBgTexture));

        // Knob del slider con borde
        Pixmap knobPixmap = new Pixmap(15, 15, Pixmap.Format.RGBA8888);
        knobPixmap.setColor(Color.BLUE);
        knobPixmap.fillCircle(7, 7, 7);
        knobPixmap.setColor(Color.WHITE);
        knobPixmap.fillCircle(7, 7, 5);
        Texture knobTexture = new Texture(knobPixmap);
        knobPixmap.dispose();
        style.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));

        return style;
    }

    private CheckBoxStyle crearEstiloCheckbox() {
        CheckBoxStyle style = new CheckBoxStyle();

        Pixmap cbPixmapOff = new Pixmap(25, 25, Pixmap.Format.RGBA8888);
        cbPixmapOff.setColor(Color.WHITE);
        cbPixmapOff.fill();
        cbPixmapOff.setColor(Color.BLUE);
        cbPixmapOff.drawRectangle(0, 0, 25, 25);
        Texture cbTextureOff = new Texture(cbPixmapOff);
        cbPixmapOff.dispose();

        Pixmap cbPixmapOn = new Pixmap(25, 25, Pixmap.Format.RGBA8888);
        cbPixmapOn.setColor(Color.BLUE);
        cbPixmapOn.fill();
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
        TextButtonStyle style = new TextButtonStyle();
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
        TextButtonStyle style = new TextButtonStyle();
        style.font = getFont();
        Pixmap glowPixmap = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        glowPixmap.setColor(new Color(1f, 1f, 1f, 0.8f));
        glowPixmap.fill();
        Texture glowTexture = new Texture(glowPixmap);
        glowPixmap.dispose();
        NinePatch glowNinePatch = new NinePatch(glowTexture, 5, 5, 5, 5);
        style.up = new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable(glowNinePatch);
        style.fontColor = Color.BLUE;
        return style;
    }

    private BitmapFont getFont() {
        return new BitmapFont();
    }

    @Override
    public void animarSalida(Runnable callback) {
        float finalX = mainContainer.getX();
        float finalY = -mainContainer.getHeight();
        mainContainer.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(finalX, finalY, 0.25f), Actions.fadeOut(0.25f)), Actions.run(callback)));
    }
}
