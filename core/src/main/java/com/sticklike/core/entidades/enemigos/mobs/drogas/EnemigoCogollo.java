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
    // 1) Precreamos las regiones de cada color para no volver a hacer new TextureRegion()
    private static final TextureRegion[] C1 = {new TextureRegion(manager.get(ENEMIGO_COGOLLO, Texture.class)), new TextureRegion(manager.get(ENEMIGO_COGOLLO2, Texture.class))};
    private static final TextureRegion[] C2 = {new TextureRegion(manager.get(ENEMIGO_COGOLLO_LILA, Texture.class)), new TextureRegion(manager.get(ENEMIGO_COGOLLO_LILA2, Texture.class))};
    private static final TextureRegion[] C3 = {new TextureRegion(manager.get(ENEMIGO_COGOLLO_NARANJA, Texture.class)), new TextureRegion(manager.get(ENEMIGO_COGOLLO_NARANJA2, Texture.class))};

    private final MovimientoOscilante movimientoOscilante;
    private final AnimacionExamen animacionCogollo;

    public EnemigoCogollo(Jugador jugador, float x, float y) {
        super(jugador);

        // 2) Stats
        this.vidaEnemigo = VIDA_ENEMIGO_COGOLLO;
        this.damageAmount = DANYO_COGOLLO;
        this.coolDownDanyo = COOLDOWN_ENEMIGOCULO;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;

        // 3) Elegir color al azar y asignar sprite + animación
        TextureRegion[] rs = MathUtils.randomBoolean() && MathUtils.randomBoolean() ? C3 : MathUtils.randomBoolean() ? C2 : C1;
        sprite = new Sprite(rs[0]);
        sprite.setSize(62, 60);
        sprite.setPosition(x, y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // 4) Movimiento y animación
        movimientoOscilante = new MovimientoOscilante(VEL_BASE_COGOLLO, true);
        // AnimacionExamen sólo necesita la base (para blink/fade) y las dos texturas originales
        animacionCogollo = new AnimacionExamen(animacionBaseEnemigos, rs[0].getTexture(), rs[1].getTexture(), 0.125f);

        // 5) Texture de daño
        this.damageTexture = manager.get(DAMAGE_COGOLLO, Texture.class);
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        // Abstraemos la lógica exactamente como tenías:
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
        // Mismo código tuyo, usando el mapa `animations` que ya existía en EnemigoBase
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
        float randomXP = MathUtils.random(100f);
        if (!haSoltadoXP && randomXP <= 0.75f) {
            haSoltadoXP = true;
            return new ObjetoVida(posXMuerte, posYMuerte);
        }
        if (!haSoltadoXP && randomXP >= 30f) {
            haSoltadoXP = true;
            return ObjetoXp.obtain(posXMuerte, posYMuerte);
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
