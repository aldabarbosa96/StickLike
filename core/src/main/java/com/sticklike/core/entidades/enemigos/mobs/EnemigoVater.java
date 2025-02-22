package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionVater;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoVater;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;

import static com.sticklike.core.utilidades.GestorConstantes.*;

public class EnemigoVater implements Enemigo {
    private Sprite sprite;
    private Sprite spriteTapaLevantada;
    private Sprite spriteTapaBajada;
    private float vidaEnemigo = VIDA_ENEMIGO_VATER;
    private MovimientoVater movimientoVater;
    private AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private AnimacionVater animacionVater;



    @Override
    public void actualizar(float delta) {

    }

    @Override
    public void renderizar(SpriteBatch batch) {
        boolean mostrarSprite = vidaEnemigo > 0 || animacionesBaseEnemigos.estaEnFade();

    }

    @Override
    public void reducirSalud(float amount) {

    }

    @Override
    public boolean estaMuerto() {
        return false;
    }

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return false;
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        return null;
    }

    @Override
    public Sprite getSprite() {
        return null;
    }

    @Override
    public void reseteaTemporizadorDanyo() {

    }

    @Override
    public boolean puedeAplicarDanyo() {
        return false;
    }

    @Override
    public boolean haSoltadoXP() {
        return false;
    }

    @Override
    public void setProcesado(boolean procesado) {

    }

    @Override
    public boolean isProcesado() {
        return false;
    }

    @Override
    public void activarParpadeo(float duracion) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {

    }

    @Override
    public float getVida() {
        return 0;
    }

    @Override
    public float getDamageAmount() {
        return 0;
    }

    @Override
    public AnimacionesBaseEnemigos getAnimaciones() {
        return null;
    }
}
