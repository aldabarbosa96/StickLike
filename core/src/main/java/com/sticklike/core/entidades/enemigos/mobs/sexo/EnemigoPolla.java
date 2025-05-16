package com.sticklike.core.entidades.enemigos.mobs.sexo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBotes;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoBase;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Enemigo Polla; gestiona su comportamiento, daño.
 */
public class EnemigoPolla extends EnemigoBase {

    private MovimientoBotes movimientoBotes;
    private static float velocidadBase = VEL_BASE_POLLA;

    public EnemigoPolla(float x, float y, Jugador jugador, float velocidadEnemigo) {
        super(jugador);
        sprite = new Sprite(manager.get(ENEMIGO_POLLA, Texture.class));
        sprite.setSize(34, 34);
        sprite.setPosition(x, y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        movimientoBotes = new MovimientoBotes(velocidadBase, 0.75f, 25f, true);
        this.damageTexture = manager.get(DAMAGE_POLLA_TEXTURE, Texture.class);
        this.vidaEnemigo = VIDA_ENEMIGO_POLLA;
        this.coolDownDanyo = COOLDOWN_POLLA;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;
        this.damageAmount = DANYO_POLLA;
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        movimientoBotes.actualizarMovimiento(delta, sprite, jugador);
        animacionesBaseEnemigos.flipearEnemigo(jugador, sprite);
    }

    @Override
    protected void actualizarKnockback(float delta) {
        movimientoBotes.actualizarSoloKnockback(delta, sprite, true);
    }

    @Override
    protected void iniciarAnimacionMuerte() {
        Animation<TextureRegion> animMuertePolla = GestorDeAssets.animations.get("muertePolla");
        animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
        animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuertePolla);
        animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
    }

    @Override
    protected void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY) {
        movimientoBotes.aplicarKnockback(fuerza, dirX, dirY);
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        float randomXP = (float) (Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 0.25f) {
            haSoltadoXP = true;
            return new ObjetoVida(posXMuerte, posYMuerte);
        }
        if (!haSoltadoXP && randomXP >= 20f) {
            haSoltadoXP = true;
            return new ObjetoXp(posXMuerte, posYMuerte);
        }
        return null;
    }

    public void setVelocidad(float nuevaVelocidad) {
        movimientoBotes.setVelocidadEnemigo(nuevaVelocidad);
    }

    public MovimientoBotes getMovimientoPolla() {
        return movimientoBotes;
    }

    public float getFadeAlpha() {
        return animacionesBaseEnemigos.getAlphaActual();
    }

    public static void resetStats() {
        velocidadBase = VEL_BASE_POLLA;
    }

    public static void setVelocidadBase(float nuevaVelocidadBase) {
        velocidadBase = nuevaVelocidadBase;
    }

    public static float getVelocidadBase() {
        return velocidadBase;
    }
}
