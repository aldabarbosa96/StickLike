package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

public class AnimacionBossPolla {
    private final Sprite spriteBocaAbierta;
    private final Sprite spriteBocaCerrada;
    private final float tiempoBocaAbierta;
    private final float duracionBocaCerrada;
    private float tiempoAcumuladoBoca = 0;
    private boolean bocaCerrada = false;

    public AnimacionBossPolla(Sprite spriteBocaAbierta, Sprite spriteBocaCerrada, float tiempoBocaAbierta, float duracionBocaCerrada) {
        this.spriteBocaAbierta = spriteBocaAbierta;
        this.spriteBocaCerrada = spriteBocaCerrada;
        this.tiempoBocaAbierta = tiempoBocaAbierta;
        this.duracionBocaCerrada = duracionBocaCerrada;
    }

    public void actualizarAnimacion(float delta, Sprite sprite) {
        tiempoAcumuladoBoca += delta;
        if (!bocaCerrada && tiempoAcumuladoBoca >= tiempoBocaAbierta) {
            // Cambia a boca cerrada
            sprite.setRegion(spriteBocaCerrada);
            bocaCerrada = true;
            tiempoAcumuladoBoca = 0;
        } else if (bocaCerrada && tiempoAcumuladoBoca >= duracionBocaCerrada) {
            // Cambia a boca abierta y reproduce un sonido aleatorio
            sprite.setRegion(spriteBocaAbierta);
            reproducirSonido();
            bocaCerrada = false;
            tiempoAcumuladoBoca = 0;
        }
    }

    private void reproducirSonido() {
        int sonidoRandom = MathUtils.random(3);
        switch (sonidoRandom) {
            case 0:
                GestorDeAudio.getInstance().reproducirEfecto("sonidoBossPolla2", 0.65f);
                break;
            case 1:
                GestorDeAudio.getInstance().reproducirEfecto("sonidoBossPolla", 0.65f);
                break;
            case 2:
                GestorDeAudio.getInstance().reproducirEfecto("sonidoBossPolla3", 0.65f);
                break;
            case 3:
                GestorDeAudio.getInstance().reproducirEfecto("sonidoBossPolla4", 0.65f);
                break;
        }
    }
}
