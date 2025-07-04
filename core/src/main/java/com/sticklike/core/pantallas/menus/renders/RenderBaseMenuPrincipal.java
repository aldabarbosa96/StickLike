package com.sticklike.core.pantallas.menus.renders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;
import com.sticklike.core.entidades.objetos.texto.FontManager;

import java.util.ArrayList;

public class RenderBaseMenuPrincipal extends RenderBaseMenus {
    private Container<Stack> buttonContainer;
    private ArrayList<TextButton> menuButtons;
    private int selectedIndex = 0;
    private Container<Table> washersContainer;
    private static final Color COLOR_PIXMAP_SOMBRA_CHINCHETA = new Color(0.65f, 0, 0, 1);
    private static final Color COLOR_PIXMAP_CHINCHETA = new Color(0, 0, 1, 0.85f);
    private static final Color COLOR_PIXMAP_CHINCHETA_BRILLO = new Color(1, 1, 1, 0.85f);

    public interface MenuListener {
        void onSelectButton(int index);
    }

    private MenuListener menuListener;

    public void setMenuListener(MenuListener listener) {
        this.menuListener = listener;
    }

    public RenderBaseMenuPrincipal() {
        super();
        menuButtons = new ArrayList<>();
        crearElementosUI();
    }

    private void crearElementosUI() {
        Label titleActor = new Label("STICK-LIKE", uiSkin, "title");
        titleActor.getColor().a = 0;
        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top();
        titleTable.add(titleActor).padTop(75).padBottom(50).center();
        stage.addActor(titleTable);
        titleActor.addAction(Actions.sequence(Actions.delay(0.75f), Actions.fadeIn(0.5f)));

        TextButton btnJugar = crearBotonesNumerados(1, "Jugar", "default-button");
        TextButton btnNiveles = crearBotonesNumerados(2, "Niveles", "default-button");
        TextButton btnPersonaje = crearBotonesNumerados(3, "Personaje", "default-button");
        TextButton btnOpciones = crearBotonesNumerados(4, "Opciones", "default-button");
        TextButton btnLogros = crearBotonesNumerados(5, "Logros", "default-button");
        TextButton btnCreditos = crearBotonesNumerados(6, "Créditos", "default-button");
        TextButton btnSalir = crearBotonesNumerados(7, "Salir", "default-button");

        btnJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(0);
                if (menuListener != null) menuListener.onSelectButton(0);
            }
        });
        btnNiveles.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(1);
                if (menuListener != null) menuListener.onSelectButton(1);
            }
        });
        btnPersonaje.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(2);
                if (menuListener != null) menuListener.onSelectButton(2);
            }
        });
        btnOpciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(3);
                if (menuListener != null) menuListener.onSelectButton(3);
            }
        });
        btnLogros.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(4);
                if (menuListener != null) menuListener.onSelectButton(4);
            }
        });
        btnCreditos.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(5);
                if (menuListener != null) menuListener.onSelectButton(5);
            }
        });
        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(6);
                if (menuListener != null) menuListener.onSelectButton(6);
            }
        });

        menuButtons.add(btnJugar);
        menuButtons.add(btnNiveles);
        menuButtons.add(btnPersonaje);
        menuButtons.add(btnOpciones);
        menuButtons.add(btnLogros);
        menuButtons.add(btnCreditos);
        menuButtons.add(btnSalir);

        Table buttonTable = new Table();
        buttonTable.add(btnJugar).pad(8).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnNiveles).pad(8).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnPersonaje).pad(8).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnOpciones).pad(8).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnLogros).pad(8).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnCreditos).pad(8).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnSalir).pad(8).center().width(VIRTUAL_WIDTH / 4).height(45f);

        Container<Table> innerContainer = new Container<>(buttonTable);
        innerContainer.setBackground(papelFondo());
        innerContainer.pad(22.5f, 50, 22.5f, 40);
        innerContainer.pack();

        Container<Actor> pushPinContainer = new Container<>(crearChincheta());
        pushPinContainer.align(Align.topLeft);
        pushPinContainer.pad(12.5f);

        Container<Actor> bottomLeftContainer = new Container<>(crearChinchetaConCordel());
        bottomLeftContainer.align(Align.bottomLeft);
        bottomLeftContainer.pad(12.5f);

        Stack stack = new Stack();
        stack.add(innerContainer);
        washersContainer = crearVariasArandelas(6);
        stack.add(washersContainer);
        stack.add(pushPinContainer);
        stack.add(bottomLeftContainer);

        buttonContainer = new Container<>(stack);
        buttonContainer.setBackground(crearSombraConBorde(Color.DARK_GRAY, 10, Color.BLUE, 2));
        buttonContainer.pack();
        animarEntrada(buttonContainer, 2.5f);

        efectoHover(menuButtons, () -> selectedIndex);
        actualizarBotonResaltado(menuButtons, selectedIndex);

        Actor iconos = crearContenedorDeIconos();
        if (iconos instanceof Table) {
            ((Table) iconos).pack();
        }
        iconos.setPosition(245, stage.getViewport().getWorldHeight() / 2);
        stage.addActor(iconos);
        iconos.addAction(Actions.sequence(Actions.delay(0.75f), Actions.fadeIn(0.5f)));

        stage.addAction(Actions.sequence(Actions.delay(0.7f), Actions.run(new Runnable() {
            @Override
            public void run() {
                GestorDeAudio.getInstance().cambiarMusica("fondoMenu2");
            }
        })));
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < menuButtons.size()) {
            selectedIndex = index;
            actualizarBotonResaltado(menuButtons, selectedIndex);
        }
    }

    public void incrementSelectedIndex() {
        if (selectedIndex < menuButtons.size() - 1) {
            selectedIndex++;
            actualizarBotonResaltado(menuButtons, selectedIndex);
        }
    }

    public void decrementSelectedIndex() {
        if (selectedIndex > 0) {
            selectedIndex--;
            actualizarBotonResaltado(menuButtons, selectedIndex);
        }
    }

    private Actor crearChincheta() {
        int size = 15;
        int shadowOffset = 2;
        int totalSize = size + shadowOffset * 2;
        Pixmap pixmap = new Pixmap(totalSize, totalSize, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.SourceOver);
        int mainCenterX = shadowOffset + size / 2;
        int mainCenterY = shadowOffset + size / 2;
        int shadowCenterX = mainCenterX + shadowOffset;
        int shadowCenterY = mainCenterY + shadowOffset;
        int radius = size / 2;
        pixmap.setColor(COLOR_PIXMAP_SOMBRA_CHINCHETA);
        pixmap.fillCircle(shadowCenterX, shadowCenterY, radius);
        pixmap.setColor(COLOR_PIXMAP_CHINCHETA);
        pixmap.fillCircle(mainCenterX, mainCenterY, radius);
        int innerRadius = (int) (size * 0.2f);
        pixmap.setColor(COLOR_PIXMAP_CHINCHETA_BRILLO);
        pixmap.fillCircle(mainCenterX, mainCenterY, innerRadius);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new Image(texture);
    }

    private Container<Table> crearVariasArandelas(int cantidad) {
        Texture arandelaTexture = manager.get(ARANDELA, Texture.class);
        Table washersTable = new Table();
        washersTable.align(Align.left);
        float desiredWidth = 10;
        float desiredHeight = 70;
        for (int i = 0; i < cantidad; i++) {
            Stack rowStack = new Stack();
            Image washer = new Image(arandelaTexture);
            washer.setSize(desiredWidth, desiredHeight);
            rowStack.add(washer);
            if (i > 0) {
                Image c = (Image) crearChincheta();
                c.setScaling(Scaling.none);
                Container<Actor> chinchetaContainer = new Container<>(c);
                chinchetaContainer.align(Align.topLeft);
                chinchetaContainer.padTop(-10f);
                rowStack.add(chinchetaContainer);
            }
            washersTable.add(rowStack).width(desiredWidth).height(desiredHeight).left().padLeft(17.5f);
            washersTable.row();
        }
        Container<Table> container = new Container<>(washersTable);
        container.setFillParent(false);
        container.align(Align.left);
        return container;
    }

    private Actor crearChinchetaConCordel() {
        Group group = new Group();
        Texture cordelTexture = manager.get(CORDEL, Texture.class);
        Image cordel = new Image(cordelTexture);
        group.addActor(cordel);
        cordel.setPosition(-0.5f, -cordel.getHeight());
        Image chincheta = (Image) crearChincheta();
        group.addActor(chincheta);
        chincheta.setPosition(0, 0);
        return group;
    }

    private Actor crearContenedorDeIconos() {
        Group iconGroup = new Group();
        iconGroup.getColor().a = 0;
        Texture iconCacadoradaTexture = manager.get(RECOLECTABLE_CACA_DORADA, Texture.class);
        Texture iconPowerupTexture = manager.get(RECOLECTABLE_POWER_UP, Texture.class);
        Texture iconPowerupTexture2 = manager.get(RECOLECTABLE_POWER_UP, Texture.class);
        Texture iconTrofeoTexture = manager.get(TROFEO2, Texture.class);
        Image iconCacadorada = new Image(iconCacadoradaTexture);
        Image iconPowerup = new Image(iconPowerupTexture);
        Image iconPowerup2 = new Image(iconPowerupTexture2);
        Image iconTrofeo = new Image(iconTrofeoTexture);
        iconCacadorada.setSize(32, 32);
        iconCacadorada.setPosition(2, 65);
        iconTrofeo.setSize(22.5f, 45);
        iconTrofeo.setPosition(7, -87.5f);
        float powerupWidth = 10f;
        float powerupHeight = 37.5f;
        float basePosX = 12;
        float basePosY = -10f;
        float centerX = basePosX + (powerupWidth * 0.5f);
        float centerY = basePosY + (powerupHeight * 0.5f);
        iconPowerup.setSize(powerupWidth, powerupHeight);
        iconPowerup.setOrigin(iconPowerup.getWidth() / 2, iconPowerup.getHeight() / 2);
        iconPowerup.setPosition(centerX - iconPowerup.getWidth() / 2, centerY - iconPowerup.getHeight() / 2);
        iconPowerup.setRotation(45f);
        iconPowerup2.setSize(powerupWidth, powerupHeight);
        iconPowerup2.setOrigin(iconPowerup2.getWidth() / 2, iconPowerup2.getHeight() / 2);
        iconPowerup2.setPosition(centerX - iconPowerup2.getWidth() / 2, centerY - iconPowerup2.getHeight() / 2);
        iconPowerup2.setRotation(-45f);
        BitmapFont font = FontManager.getMenuFont();
        font.getData().markupEnabled = true;
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label labelCacadorada = new Label("[BLACK]x []" + "[BLUE]" + Jugador.getOroGanado() + "[]", labelStyle);
        Label labelPowerup = new Label("[BLACK]x []" + "[BLUE]" + Jugador.getTrazosGanados() + "[]", labelStyle);
        Label labelTrofeo = new Label("[BLUE]? [][BLACK]/ ?[]", labelStyle);
        float fixedLabelX = 77.5f;
        float offsetLabel = 2.5f;
        labelCacadorada.setPosition(fixedLabelX, iconCacadorada.getY() + (iconCacadorada.getHeight() - labelCacadorada.getHeight()) / 2);
        labelPowerup.setPosition(fixedLabelX, centerY - labelPowerup.getHeight() / 2);
        labelTrofeo.setPosition(fixedLabelX - offsetLabel, iconTrofeo.getY() + (iconTrofeo.getHeight() - labelTrofeo.getHeight()) / 2);
        iconGroup.addActor(iconCacadorada);
        iconGroup.addActor(labelCacadorada);
        iconGroup.addActor(iconPowerup);
        iconGroup.addActor(iconPowerup2);
        iconGroup.addActor(labelPowerup);
        iconGroup.addActor(iconTrofeo);
        iconGroup.addActor(labelTrofeo);
        return iconGroup;
    }

    @Override
    public void animarSalida(Runnable callback) {
    }
}
