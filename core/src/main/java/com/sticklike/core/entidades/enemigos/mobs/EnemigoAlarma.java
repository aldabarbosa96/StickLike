package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.entidades.enemigos.ia.MovimientoCulo;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Enemigo Alarma; puede aparecer Crono (Cosmo) o Alarma (Wanda), alterando su vida y animación. Gestiona su comportamiento y daño.
 */
public class EnemigoAlarma extends EnemigoBase {

    private MovimientoCulo movimientoAlarma;
    private float cooldownDanyo = COOLDOWN_ENEMIGOCULO;
    private static float velocidadBase = VEL_BASE_ALARMA;
    private boolean esCrono;   // Determina si es la versión verde (true) o rosa (false)

    public EnemigoAlarma(float x, float y, Jugador jugador) {
        super(jugador);
        sprite = new Sprite(escogerTextura());
        sprite.setPosition(x, y);
        sprite.setSize(40, 40);
        this.movimientoAlarma = new MovimientoCulo(velocidadBase, true);
        this.damageTexture = manager.get(DAMAGE_ALARMA_TEXTURE, Texture.class);
        this.vidaEnemigo = VIDA_ENEMIGO_ALARMA;
        this.damageAmount = DANYO_CULO;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;
        this.coolDownDanyo = COOLDOWN_ENEMIGOCULO;
    }

    private Texture escogerTextura() {
        float texturaAleatoria = MathUtils.random(10);
        if (texturaAleatoria <= 5) {
            esCrono = false; // Rosa
            return manager.get(ENEMIGO_ALARMA, Texture.class);
        } else {
            esCrono = true;  // Verde
            return manager.get(ENEMIGO_ALARMA2, Texture.class);
        }
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        movimientoAlarma.actualizarMovimiento(delta, sprite, jugador);
        animacionesBaseEnemigos.flipearEnemigo(jugador, sprite);
    }

    @Override
    protected void actualizarKnockback(float delta) {
        movimientoAlarma.actualizarSoloKnockback(delta, sprite, true);
    }

    @Override
    protected void iniciarAnimacionMuerte() {
        Animation<TextureRegion> animMuerteAlarma;
        if (!esCrono) {
            animMuerteAlarma = GestorDeAssets.animations.get("alarmaMuerte2");
        } else {
            animMuerteAlarma = GestorDeAssets.animations.get("alarmaMuerte");
        }
        animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteAlarma);
        animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
        animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
    }

    @Override
    protected void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY) {
        movimientoAlarma.aplicarKnockback(fuerza, dirX, dirY);
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        float randomXP = (float) (Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 0.25f) {
            haSoltadoXP = true;
            return new ObjetoVida(posXMuerte, posYMuerte);
        }
        if (!haSoltadoXP && randomXP >= 15f) {
            haSoltadoXP = true;
            return new ObjetoXp(posXMuerte, posYMuerte);
        }
        return null;
    }

    public boolean isEsCrono() {
        return esCrono;
    }
}
