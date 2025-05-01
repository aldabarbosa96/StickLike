package com.sticklike.core.entidades.enemigos.mobs.escuela;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoRegla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoBase;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoBase;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Enemigo Regla; gestiona su comportamiento y daño.
 */
public class EnemigoRegla extends EnemigoBase {
    private MovimientoRegla movimientoRegla;

    public EnemigoRegla(float x, float y, Jugador jugador, float velocidadEnemigo, OrthographicCamera orthographicCamera) {
        super(jugador);
        sprite = new Sprite(manager.get(ENEMIGO_REGLA_CRUZADA, Texture.class));
        sprite.setSize(26, 26);
        sprite.setPosition(x, y);
        this.movimientoRegla = new MovimientoRegla(velocidadEnemigo, 666, orthographicCamera, true);
        this.animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        this.damageTexture = manager.get(DAMAGE_REGLA_TEXTURE, Texture.class);
        this.renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
        this.vidaEnemigo = VIDA_ENEMIGOREGLA;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;
        this.coolDownDanyo = COOLDOWN_ENEMIGOREGLA;
        this.damageAmount = DANYO_REGLA;
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        animacionesBaseEnemigos.actualizarParpadeo(sprite, delta);
        movimientoRegla.actualizarMovimiento(delta, sprite, jugador);
    }

    @Override
    protected void actualizarKnockback(float delta) {
        movimientoRegla.actualizarSoloKnockback(delta, sprite, true);
    }

    @Override
    protected void iniciarAnimacionMuerte() {
        Animation<TextureRegion> animMuerteRegla = GestorDeAssets.animations.get("reglaMuerte");
        animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteRegla);
        animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
        animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
    }

    @Override
    protected void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY) {
        movimientoRegla.aplicarKnockback(fuerza, dirX, dirY);
    }

    @Override
    public ObjetoBase sueltaObjetoXP() {
        // todo --> añadir variedades de droppeo
        float randomNum = MathUtils.random(100);
        if (!haSoltadoXP && randomNum <= 1f) {
            haSoltadoXP = true;
            return new ObjetoVida(posXMuerte, posYMuerte);
        } else if (randomNum <= 20) {
            haSoltadoXP = true;
            ObjetoXp objetoXp;
            objetoXp = new ObjetoXp(posXMuerte, posYMuerte);
            objetoXp.setTipo(2);
            return objetoXp;
        }
        return null;
    }
}
