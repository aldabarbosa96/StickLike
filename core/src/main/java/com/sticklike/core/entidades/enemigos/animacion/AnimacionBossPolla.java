package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

/**
 * Boca del boss: se abre y cierra y reproduce un sonido aleatorio al abrirse.
 */
public class AnimacionBossPolla {

    private final AnimacionBaseDosFrames anim;

    public AnimacionBossPolla(TextureRegion bocaAbierta, TextureRegion bocaCerrada, float tiempoBocaAbierta, float duracionBocaCerrada) {

        Runnable sonidoAlAbrir = () -> {
            int s = MathUtils.random(3);
            switch (s) {
                case 0 -> GestorDeAudio.getInstance().reproducirEfecto("sonidoBossPolla2", 1);
                case 1 -> GestorDeAudio.getInstance().reproducirEfecto("sonidoBossPolla", 1);
                case 2 -> GestorDeAudio.getInstance().reproducirEfecto("sonidoBossPolla3", 1);
                case 3 -> GestorDeAudio.getInstance().reproducirEfecto("sonidoBossPolla4", 1);
            }
        };

        this.anim = new AnimacionBaseDosFrames(bocaCerrada, bocaAbierta, duracionBocaCerrada, tiempoBocaAbierta, null, sonidoAlAbrir);
    }

    public void actualizarAnimacion(float delta, Sprite sprite) {
        anim.update(delta, sprite);
    }
}
