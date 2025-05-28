package com.sticklike.core.entidades.mobiliario.tragaperras;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBaseEnemigos;
import com.sticklike.core.entidades.pools.RectanglePoolManager;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Entidad del mundo (mobiliario) que, al romperse, abre el pop-up de tragaperras.
 */
public class Tragaperras implements Enemigo {

    public enum Direccion {NORTE, SUR, ESTE, OESTE}

    private final Sprite sprite;
    private final TragaperrasLogic logic;
    private final RenderBaseEnemigos render;
    private final AnimacionBaseEnemigos anim = new AnimacionBaseEnemigos();
    private final Direccion dir;

    private final VentanaJuego1 vj1;
    private Texture damageTexture;

    private float vida = VIDA_TRAGAPERRAS;
    private boolean haSoltadoXP = false, procesado = false;
    private boolean popupMostrado = false;

    public Tragaperras(float x, float y, RenderBaseEnemigos render, VentanaJuego1 vj1, Direccion dir) {
        this.render = render;
        this.vj1 = vj1;
        this.dir = dir;

        sprite = new Sprite(manager.get(TRAGAPERRAS, Texture.class));
        sprite.setSize(ANCHO_TRAGAPERRAS, ALTO_TRAGAPERRAS);
        sprite.setPosition(x, y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        damageTexture = manager.get(TRAGAPERRAS_DMG, Texture.class);
        logic = new TragaperrasLogic(3, 10);
    }

    @Override
    public void actualizar(float dt) {
        anim.actualizarParpadeo(sprite, dt);
        anim.actualizarFade(dt);
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        render.dibujarEnemigos(batch, this);
    }

    @Override
    public void reducirSalud(float dmg) {
        vida -= dmg;
        GestorDeAudio.getInstance().reproducirEfecto("tragaperras",0.5f);
        if (vida <= 0 && !popupMostrado) {
            anim.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
            activarParpadeo(DURACION_PARPADEO_ENEMIGO);
            vj1.mostrarPopUpTragaperras(logic);
            popupMostrado = true;
        }
    }

    @Override
    public boolean estaMuerto() {
        return vida <= 0 && !anim.estaEnFade();
    }

    @Override
    public boolean esGolpeadoPorProyectil(float px, float py, float pw, float ph) {
        Rectangle r = RectanglePoolManager.obtenerRectangulo(px, py, pw, ph);
        boolean hit = sprite.getBoundingRectangle().overlaps(r);
        RectanglePoolManager.liberarRectangulo(r);
        return hit;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public float getX() {
        return sprite.getX();
    }

    @Override
    public float getY() {
        return sprite.getY();
    }

    @Override
    public void activarParpadeo(float d) {
        anim.activarParpadeo(sprite, d, damageTexture);
    }

    @Override
    public float getVida() {
        return vida;
    }

    @Override
    public AnimacionBaseEnemigos getAnimaciones() {
        return anim;
    }

    @Override
    public boolean isMostrandoDamageSprite() {
        return false;
    }

    @Override
    public boolean estaEnKnockback() {
        return false;
    }

    @Override
    public MovimientoBaseEnemigos getMovimiento() {
        return null;
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        return null;
    }

    @Override
    public boolean haSoltadoXP() {
        return haSoltadoXP;
    }

    @Override
    public void setProcesado(boolean p) {
        procesado = p;
    }

    @Override
    public boolean isProcesado() {
        return procesado;
    }

    @Override
    public void reseteaTemporizadorDanyo() {
    }

    @Override
    public boolean puedeAplicarDanyo() {
        return false;
    }

    @Override
    public float getDamageAmount() {
        return 0;
    }

    @Override
    public void aplicarKnockback(float f, float dx, float dy) {
    }

    @Override
    public void dispose() {
    }
    public Direccion getDir() { return dir; }
}
