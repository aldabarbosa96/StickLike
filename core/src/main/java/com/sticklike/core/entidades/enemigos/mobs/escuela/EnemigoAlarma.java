package com.sticklike.core.entidades.enemigos.mobs.escuela;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionAlarma;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoOscilante;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoBase;
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
    private Sprite alarma, alarmaOjo, crono, cronoCerrado;
    private AnimacionAlarma animacionAlarma, animacionCrono;
    private MovimientoOscilante movimientoAlarma;
    private float cooldownDanyo = COOLDOWN_ENEMIGOCULO;
    private static float velocidadBase = VEL_BASE_ALARMA;
    private boolean esCrono;

    public EnemigoAlarma(float x, float y, Jugador jugador) {
        super(jugador);
        this.alarma = new Sprite(manager.get(ENEMIGO_ALARMA, Texture.class));
        this.alarmaOjo = new Sprite(manager.get(ENEMIGO_ALARMA_OJO, Texture.class));
        this.crono = new Sprite(manager.get(ENEMIGO_ALARMA2, Texture.class));
        this.cronoCerrado = new Sprite(manager.get(ENEMIGO_ALARMA2_CERRADA, Texture.class));
        sprite = new Sprite(escogerTextura());
        animacionCrono = new AnimacionAlarma(animacionBaseEnemigos, crono, cronoCerrado, 1, 0.25f);
        animacionAlarma = new AnimacionAlarma(animacionBaseEnemigos, alarma, alarmaOjo, 0.33f, 0.25f);
        sprite.setPosition(x, y);
        if (esCrono) sprite.setSize(48, 48);
        else sprite.setSize(42, 48);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.movimientoAlarma = new MovimientoOscilante(velocidadBase, true);
        this.damageTexture = manager.get(esCrono ? DAMAGE_ALARMA2_TEXTURE : DAMAGE_ALARMA_TEXTURE, Texture.class);
        this.vidaEnemigo = VIDA_ENEMIGO_ALARMA;
        this.damageAmount = DANYO_CULO;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;
        this.coolDownDanyo = COOLDOWN_ENEMIGOCULO;
    }

    private Sprite escogerTextura() {
        float texturaAleatoria = MathUtils.random(10);
        if (texturaAleatoria <= 5) {
            esCrono = false; // Rosa
            return alarma;
        } else {
            esCrono = true;  // Verde
            return crono;
        }
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        movimientoAlarma.actualizarMovimiento(delta, sprite, jugador);
        if (esCrono) {
            animacionCrono.actualizar(delta, sprite);
        } else {
            animacionAlarma.actualizar(delta, sprite);
        }
        animacionBaseEnemigos.flipearEnemigo(jugador, sprite);
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
        animacionBaseEnemigos.iniciarAnimacionMuerte(animMuerteAlarma);
        animacionBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
        animacionBaseEnemigos.reproducirSonidoMuerteGenerico();
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

    @Override
    public boolean estaEnKnockback() {
        return movimientoAlarma.getKnockbackTimer() > 0;
    }

    @Override
    public MovimientoBaseEnemigos getMovimiento() {
        return movimientoAlarma;
    }

    public boolean isEsCrono() {
        return esCrono;
    }
}
