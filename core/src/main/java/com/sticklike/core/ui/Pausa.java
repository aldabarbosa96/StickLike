package com.sticklike.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.pantallas.menus.InputsMenu;

public class Pausa {
    private boolean isPaused;
    private boolean inputsBloqueados;
    private VentanaJuego1 ventanaJuego1;
    private InputsMenu inputsMenu;
    private RenderPausa renderPausa;

    public Pausa(VentanaJuego1 ventanaJuego1) {
        this.isPaused = false;
        this.inputsBloqueados = false;
        this.ventanaJuego1 = ventanaJuego1;
        inputsMenu = new InputsMenu(new InputsMenu.MenuInputListener() {
            @Override public void onNavigateUp()    { if(isPaused) renderPausa.navigateUp(); }
            @Override public void onNavigateDown()  { if(isPaused) renderPausa.navigateDown(); }
            @Override public void onSelect()        { if(isPaused) renderPausa.selectCurrent(); }
            @Override public void onBack()          { if(isPaused) alternarPausa(); }
            @Override public void onPauseToggle()   { alternarPausa(); }
        });
        Controllers.addListener(inputsMenu);
        renderPausa = new RenderPausa(this, ventanaJuego1);
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (isPaused) {
            renderPausa.drawOverlay();
        }
        renderPausa.drawPauseIcon(shapeRenderer);
        renderPausa.drawStartText();
        if (isPaused) {
            ventanaJuego1.getHud().renderizarHUD(Gdx.graphics.getDeltaTime());
            renderPausa.drawPauseText();
            renderPausa.drawStage();
        }
    }

    public void handleInput() {
        if (inputsBloqueados) return;
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            alternarPausa();
        }
    }

    public void alternarPausa() {
        isPaused = !isPaused;
        ventanaJuego1.setPausado(isPaused);
        if (isPaused) {
            ventanaJuego1.reproducirSonidoPausa();
            InputMultiplexer im = new InputMultiplexer(inputsMenu, renderPausa.getPauseStage());
            Gdx.input.setInputProcessor(im);
            renderPausa.setCurrentIndex(0);
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

    public void dispose() {
        Controllers.removeListener(inputsMenu);
        renderPausa.dispose();
    }

    public RenderPausa getRenderPausa() {
        return renderPausa;
    }

    public InputsMenu getInputsMenu() {
        return inputsMenu;
    }

}
