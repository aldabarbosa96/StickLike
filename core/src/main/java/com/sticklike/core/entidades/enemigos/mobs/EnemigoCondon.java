package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoRegla;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.DANYO_REGLA;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class EnemigoCondon extends EnemigoBase{
    private MovimientoRegla movimientoRegla;

    public EnemigoCondon(Jugador jugador,float x, float y, float velocidadEnemigo, OrthographicCamera orthographicCamera) {
        super(jugador);
        sprite = new Sprite(manager.get(ENEMIGO_CONDON, Texture.class));
        sprite.setSize(16, 42);
        sprite.setPosition(x, y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.movimientoRegla = new MovimientoRegla(velocidadEnemigo, 500, orthographicCamera, true);
        this.animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        this.damageTexture = manager.get(DAMAGE_CONDON, Texture.class);
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
        Animation<TextureRegion> animMuerteRegla = GestorDeAssets.animations.get("condonMuerte");
        animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteRegla);
        animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
        animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
    }

    @Override
    protected void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY) {
        movimientoRegla.aplicarKnockback(fuerza, dirX, dirY);
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
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
