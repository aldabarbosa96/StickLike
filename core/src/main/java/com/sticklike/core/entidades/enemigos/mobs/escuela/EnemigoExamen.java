package com.sticklike.core.entidades.enemigos.mobs.escuela;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionExamen;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoParabola;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoBase;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class EnemigoExamen extends EnemigoBase {

    private AnimacionExamen animacionExamen;
    private MovimientoParabola movimientoParabola;
    private static float velocidadBase = VEL_BASE_EXAMEN;

    public EnemigoExamen(float x, float y, Jugador jugador, float velocidadEnemigo) {
        super(jugador);
        sprite = new Sprite(manager.get(ENEMIGO_EXAMEN, Texture.class));
        sprite.setSize(44f, 46f);
        sprite.setPosition(x, y);
        movimientoParabola = new MovimientoParabola();
        animacionExamen = new AnimacionExamen(animacionBaseEnemigos, manager.get(ENEMIGO_EXAMEN, Texture.class), manager.get(ENEMIGO_EXAMEN2, Texture.class), 0.25f);

        setVelocidad(velocidadEnemigo);

        this.vidaEnemigo = VIDA_ENEMIGO_EXAMEN;
        this.damageAmount = DANYO_EXAMEN;
        this.coolDownDanyo = COOLDOWN_EXAMEN;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;

        this.damageTexture = manager.get(DAMAGE_EXAMEN_TEXTURE, Texture.class);
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        if (movimientoParabola != null) {
            movimientoParabola.actualizarMovimiento(delta, sprite, jugador);
        }
        animacionExamen.actualizarAnimacion(delta, sprite);
        animacionBaseEnemigos.flipearEnemigo(jugador, sprite);
    }

    @Override
    protected void actualizarKnockback(float delta) {
        if (movimientoParabola != null) {
            movimientoParabola.actualizarSoloKnockback(delta, sprite, true);
        }
    }

    @Override
    protected void iniciarAnimacionMuerte() {
        Animation<TextureRegion> animMuerteExamen = GestorDeAssets.animations.get("examenMuerte");
        animacionBaseEnemigos.iniciarAnimacionMuerte(animMuerteExamen);
        animacionBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
        animacionBaseEnemigos.reproducirSonidoMuerteGenerico();
    }

    @Override
    protected void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY) {
        if (movimientoParabola != null) {
            movimientoParabola.aplicarKnockback(fuerza, dirX, dirY);
        }
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
        return movimientoParabola.getKnockbackTimer() > 0f;
    }

    @Override
    public MovimientoBaseEnemigos getMovimiento() {
        return movimientoParabola;
    }

    public void setVelocidad(float nuevaVelocidad) {
        if (movimientoParabola != null) {
            movimientoParabola.setVelocidadEnemigo(nuevaVelocidad);
        }
    }

    public MovimientoParabola getMovimientoExamen() {
        return movimientoParabola;
    }

    public float getFadeAlpha() {
        return animacionBaseEnemigos.getAlphaActual();
    }

    public static void resetStats() {
        velocidadBase = VEL_BASE_EXAMEN;
    }

    public static void setVelocidadBase(float nuevaVelocidadBase) {
        velocidadBase = nuevaVelocidadBase;
    }

    public static float getVelocidadBase() {
        return velocidadBase;
    }

}
