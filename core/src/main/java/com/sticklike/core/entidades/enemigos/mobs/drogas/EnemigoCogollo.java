package com.sticklike.core.entidades.enemigos.mobs.drogas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionExamen;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoOscilante;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoBase;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.ObjetosXP;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class EnemigoCogollo extends EnemigoBase {
    private Texture textura1, textura2;
    private MovimientoOscilante movimientoOscilante;
    private AnimacionExamen animacionCogollo;
    private static float velocidadBase = VEL_BASE_COGOLLO;

    public EnemigoCogollo(Jugador jugador, float x, float y) {
        super(jugador);
        this.vidaEnemigo = VIDA_ENEMIGO_COGOLLO;
        this.damageAmount = DANYO_COGOLLO;
        this.coolDownDanyo = COOLDOWN_ENEMIGOCULO;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;
        sprite = new Sprite(colorCogollos());
        sprite.setSize(40, 38);
        sprite.setPosition(x, y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.movimientoOscilante = new MovimientoOscilante(velocidadBase, true);
        this.animacionCogollo = new AnimacionExamen(animacionBaseEnemigos, textura1, textura2, 0.125f);
        this.damageTexture = manager.get(DAMAGE_COGOLLO, Texture.class);

    }

    private Sprite colorCogollos() {
        int random = MathUtils.random(1, 3);
        Texture t1, t2;
        switch (random) {
            case 1:
                t1 = manager.get(ENEMIGO_COGOLLO, Texture.class);
                t2 = manager.get(ENEMIGO_COGOLLO2, Texture.class);
                break;
            case 2:
                t1 = manager.get(ENEMIGO_COGOLLO_LILA, Texture.class);
                t2 = manager.get(ENEMIGO_COGOLLO_LILA2, Texture.class);
                break;
            default:
                t1 = manager.get(ENEMIGO_COGOLLO_NARANJA, Texture.class);
                t2 = manager.get(ENEMIGO_COGOLLO_NARANJA2, Texture.class);
        }
        this.textura1 = t1;
        this.textura2 = t2;
        return new Sprite(t1);
    }


    @Override
    protected void actualizarMovimiento(float delta) {
        movimientoOscilante.actualizarMovimiento(delta, sprite, jugador);
        animacionCogollo.actualizarAnimacion(delta, sprite);
        animacionBaseEnemigos.flipearEnemigo(jugador, sprite);
    }

    @Override
    protected void actualizarKnockback(float delta) {
        movimientoOscilante.actualizarSoloKnockback(delta, sprite, true);
    }

    @Override
    protected void iniciarAnimacionMuerte() {
        Animation<TextureRegion> animMuerteExamen = animations.get("cogollinMuerte");
        animacionBaseEnemigos.iniciarAnimacionMuerte(animMuerteExamen);
        animacionBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
        animacionBaseEnemigos.reproducirSonidoMuerteGenerico();
    }

    @Override
    protected void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY) {
        movimientoOscilante.aplicarKnockback(fuerza, dirX, dirY);
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        float randomXP = (float) (Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 0.75f) {
            haSoltadoXP = true;
            return new ObjetoVida(posXMuerte, posYMuerte);
        }
        if (!haSoltadoXP && randomXP >= 30f) {
            haSoltadoXP = true;
            return new ObjetoXp(posXMuerte, posYMuerte);
        }
        return null;
    }

    @Override
    public boolean estaEnKnockback() {
        return movimientoOscilante.getKnockbackTimer() > 0f;
    }

    @Override
    public MovimientoBaseEnemigos getMovimiento() {
        return movimientoOscilante;
    }
}
