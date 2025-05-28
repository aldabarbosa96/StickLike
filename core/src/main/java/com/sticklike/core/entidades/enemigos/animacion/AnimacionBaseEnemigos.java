package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Gestiona las animaciones visuales de los enemigos, como el efecto de parpadeo al recibir daño, la animación y fade de muerte y el volteo.
 */

public class AnimacionBaseEnemigos {
    private boolean enParpadeo = false;
    private float tiempoParpadeoRestante = TIEMPO_PARPADEO_RESTANTE;
    private Texture texturaOriginal;
    private boolean enFade = false;
    private float tiempoFadeRestante = TIEMPO_FADE_RESTANTE;
    private float tiempoTotalFade = TIEMPO_FADE_TOTAL;
    private float alphaActual = ALPHA_ACTUAL;
    private boolean estaFlipped;

    private Animation<TextureRegion> animacionMuerte;
    private float stateTimeMuerte = 0;
    private boolean enAnimacionMuerte = false;

    public void activarParpadeo(Sprite sprite, float duracion, Texture damageTexture) {
        if (!enParpadeo) {
            texturaOriginal = sprite.getTexture();
        }
        enParpadeo = true;
        tiempoParpadeoRestante = duracion;
        sprite.setTexture(damageTexture);
    }

    public void actualizarParpadeo(Sprite sprite, float delta) {
        if (enParpadeo) {
            tiempoParpadeoRestante -= delta;
            if (tiempoParpadeoRestante <= 0) {
                enParpadeo = false;
                //  mantenemos la textura de damage
                if (!enFade) {
                    sprite.setTexture(texturaOriginal);
                }
            }
        }
    }


    public boolean estaEnParpadeo() {
        return enParpadeo;
    }

    public void iniciarFadeMuerte(float duracion) {
        enFade = true;
        tiempoTotalFade = duracion;
        tiempoFadeRestante = duracion;
        alphaActual = ALPHA_ACTUAL;
    }

    public void actualizarFade(float delta) {
        if (enFade) {
            tiempoFadeRestante -= delta;
            if (tiempoFadeRestante <= 0) {
                enFade = false;
                alphaActual = 0f;
            } else {
                alphaActual = tiempoFadeRestante / tiempoTotalFade;
            }
        }
    }

    public boolean estaEnFade() {
        return enFade;
    }

    public float getAlphaActual() {
        return alphaActual;
    }

    public void restaurarColor(Sprite sprite, Color originalColor) {
        if (enParpadeo || enFade) {
            sprite.setColor(originalColor);
        }
    }

    public void flipearEnemigo(Jugador jugador, Sprite sprite) {
        // Lógica para flipear el sprite del enemigo según la posición del jugador
        if (jugador != null) {
            boolean estaALaIzquierda = sprite.getX() + sprite.getWidth() / 2 > jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
            if (sprite.isFlipX() != estaALaIzquierda) {
                sprite.flip(true, false);
                estaFlipped = estaALaIzquierda;
            }
        }
    }

    public void iniciarAnimacionMuerte(Animation<TextureRegion> animacionMuerte) {
        this.animacionMuerte = animacionMuerte;
        stateTimeMuerte = 0;
        enAnimacionMuerte = true;
    }

    public void actualizarAnimacionMuerte(Sprite sprite, float delta) {
        if (enAnimacionMuerte && animacionMuerte != null) {
            stateTimeMuerte += delta;
            TextureRegion frame = animacionMuerte.getKeyFrame(stateTimeMuerte, false);
            float oldX = sprite.getX();
            float oldY = sprite.getY();
            float oldWidth = sprite.getWidth();
            float oldHeight = sprite.getHeight();
            boolean oldFlipX = sprite.isFlipX();
            boolean oldFlipY = sprite.isFlipY();

            // Calculamos el centro original del sprite
            float centerX = oldX + oldWidth / 2f;
            float centerY = oldY + oldHeight / 2f;

            float scaleFactor = 1.011f; // factor de escalado de los sprites mientras se realiza la animación
            float newWidth = oldWidth * scaleFactor;
            float newHeight = oldHeight * scaleFactor;

            // Establecemos la nueva región y tamaño
            sprite.setRegion(frame);
            sprite.setSize(newWidth, newHeight);
            sprite.setOriginCenter();

            // Recalculamos la posición de modo que el centro se mantenga igual
            sprite.setPosition(centerX - newWidth / 2f, centerY - newHeight / 2f);

            if (sprite.isFlipX() != oldFlipX) {
                sprite.flip(true, false);
            }
            if (sprite.isFlipY() != oldFlipY) {
                sprite.flip(false, true);
            }

            // De momento no aplicamos fade-out a la animación (todo --> valorar si se cambia en un futuro)
            //sprite.setColor(1f, 0, 0, getAlphaActual());

            if (animacionMuerte.isAnimationFinished(stateTimeMuerte)) {
                enAnimacionMuerte = false;
            }
        }
    }


    public void actualizarAnimacionMuerteSinEscala(Sprite sprite, float delta) { // para poder animar de formas especiales a los bosses sin que se deforme la imagen
        if (enAnimacionMuerte && animacionMuerte != null) {
            stateTimeMuerte += delta;
            TextureRegion frame = animacionMuerte.getKeyFrame(stateTimeMuerte, false);
            // Solo actualizamos la región, sin cambiar el tamaño, posición u origen.
            sprite.setRegion(frame);
            if (animacionMuerte.isAnimationFinished(stateTimeMuerte)) {
                enAnimacionMuerte = false;
            }
        }
    }

    public void reproducirSonidoMuerteGenerico(){
        float sonidoAleatorioMuerte = MathUtils.random(10);
        if (sonidoAleatorioMuerte >= 5) {
            GestorDeAudio.getInstance().reproducirEfecto("muerteGenerico2", 0.2f);
        } else {
            GestorDeAudio.getInstance().reproducirEfecto("muerteGenerico", 0.2f);
        }
    }

    public boolean enAnimacionMuerte() {
        return enAnimacionMuerte;
    }

    public boolean isEstaFlipped() {
        return estaFlipped;
    }

}
