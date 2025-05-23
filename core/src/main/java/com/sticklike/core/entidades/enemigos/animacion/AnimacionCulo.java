package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.enemigos.mobs.sexo.EnemigoCulo;

/**
 * Parpadeo del ojo del EnemigoCulo.
 */
public class AnimacionCulo {

    private final EnemigoCulo enemigo;
    private final AnimacionesBaseEnemigos base;
    private final AnimacionDosFrames anim;

    public AnimacionCulo(EnemigoCulo enemigo, AnimacionesBaseEnemigos base, TextureRegion ojoAbierto, TextureRegion ojoCerrado) {

        this.enemigo = enemigo;
        this.base = base;
        this.anim = new AnimacionDosFrames(ojoAbierto, ojoCerrado, 0.5f, 0.1f, null, null);
    }

    public void actualizarAnimacion(float delta, Sprite sprite) {
        if (enemigo.isTieneOjo() && !base.estaEnParpadeo()) {
            anim.update(delta, sprite);
        }
    }
}
