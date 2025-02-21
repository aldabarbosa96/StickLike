package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.utilidades.GestorDeAssets;

public class AnimacionCulo {
    private final Sprite spriteOjoAbierto;
    private final Sprite spriteOjoCerrado;
    private final EnemigoCulo enemigo;
    private boolean ojoCerrado = false;
    private float tiempoAcumulado = 0;
    private final float tiempoParpadeo = 0.5f;
    private final float duracionCerrado = 0.1f;
    private AnimacionesBaseEnemigos animacionesBaseEnemigos;

    public AnimacionCulo(EnemigoCulo enemigo, AnimacionesBaseEnemigos animacionesBaseEnemigos, Sprite ojoabierto, Sprite ojocerrado) {
        this.enemigo = enemigo;
        this.animacionesBaseEnemigos = animacionesBaseEnemigos;
        this.spriteOjoAbierto = ojoabierto;
        this.spriteOjoCerrado = ojocerrado;

    }

    public void actualizarAnimacion(float delta, Jugador jugador, Sprite sprite) {
        // Animación de parpadeo del ojo
        if (enemigo.isTieneOjo() && !animacionesBaseEnemigos.estaEnParpadeo()) {
            tiempoAcumulado += delta;
            if (!ojoCerrado && tiempoAcumulado >= tiempoParpadeo) {
                sprite.setRegion(spriteOjoCerrado);
                ojoCerrado = true;
                tiempoAcumulado = 0;
            } else if (ojoCerrado && tiempoAcumulado >= duracionCerrado) {
                sprite.setRegion(spriteOjoAbierto);
                ojoCerrado = false;
                tiempoAcumulado = 0;
            }
        }
    }
}

