package com.sticklike.core.entidades.enemigos.movimiento;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.entidades.jugador.Jugador;

public class MovimientoBossPolla extends MovimientoBaseEnemigos {
    private float velocidadAproximacion, distanciaOrbita, velocidadOrbital, velocidadCarga;
    private float tiempoOrbita, tiempoCarga, temporizador;
    private boolean enOrbita;
    private boolean haAlcanzadoDistanciaOrbita;
    private float centroX, centroY;
    private float angulo;
    private float chargeDirX, chargeDirY;

    public MovimientoBossPolla(boolean puedeEmpujar) {
        super(puedeEmpujar);

        this.velocidadAproximacion = 50f;
        this.distanciaOrbita = 175f;
        this.velocidadOrbital = 100f;
        this.velocidadCarga = 300f;
        this.tiempoOrbita = 3f;
        this.tiempoCarga = 1.5f;

        this.haAlcanzadoDistanciaOrbita = false;
        this.enOrbita = true;  // cuando termine la aproximación, inicia orbitando
        this.temporizador = 0;
        this.angulo = 0;

        // Dirección de embestida (se recalculará cada vez que inicie una carga)
        this.chargeDirX = 0;
        this.chargeDirY = 0;
    }

    @Override
    protected void actualizarMovimientoEspecifico(float delta, Sprite sprite, Jugador jugador) {
        if (jugador == null || sprite == null) return;

        // Posición centro boss
        float bossCenterX = sprite.getX() + sprite.getWidth() / 2f;
        float bossCenterY = sprite.getY() + sprite.getHeight() / 2f;

        // Centro del jugador
        float playerCenterX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f;
        float playerCenterY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f;

        // Distancia del boss al Jugador
        float difX = playerCenterX - bossCenterX;
        float difY = playerCenterY - bossCenterY;
        float dist = (float) Math.sqrt(difX * difX + difY * difY);

        // --- FASE 1: Aproximación ---
        if (!haAlcanzadoDistanciaOrbita) {
            // Moverse lentamente hacia el jugador
            if (dist != 0) {
                difX /= dist;
                difY /= dist;
            }

            float moveX = difX * velocidadAproximacion * delta;
            float moveY = difY * velocidadAproximacion * delta;
            sprite.translate(moveX, moveY);

            // Si está dentro de 'distanciaOrbita', pasamos a fase 2
            if (dist <= distanciaOrbita) {
                haAlcanzadoDistanciaOrbita = true;
                temporizador = 0;
                enOrbita = true;

                // Ajustamos el centro a la posición del jugador
                centroX = playerCenterX;
                centroY = playerCenterY;

                // IMPORTANTE: establecer 'angulo' según la posición actual del Boss respecto a centro
                float dx = bossCenterX - centroX;
                float dy = bossCenterY - centroY;
                angulo = (float) Math.toDegrees(Math.atan2(dy, dx));
            }
        }

        // --- FASE 2: Orbitar / Cargar ---
        else {
            // El centro de la órbita se actualiza a la posición del jugador (puedes dejarlo estático si prefieres)
            centroX = playerCenterX;
            centroY = playerCenterY;

            temporizador += delta;

            if (enOrbita) {
                // MODO ÓRBITA
                orbitar(delta, sprite);

                if (temporizador >= tiempoOrbita) {
                    // Iniciamos la embestida
                    enOrbita = false;
                    temporizador = 0;

                    // Guardamos la dirección hacia el jugador SOLO en el momento de empezar la carga
                    float dx = playerCenterX - bossCenterX;
                    float dy = playerCenterY - bossCenterY;
                    float d = (float) Math.sqrt(dx * dx + dy * dy);
                    if (d != 0) {
                        dx /= d;
                        dy /= d;
                    }

                    chargeDirX = dx;
                    chargeDirY = dy;
                }

            } else {
                // MODO CARGA (embestida)
                cargar(delta, sprite, bossCenterX, bossCenterY);

                if (temporizador >= tiempoCarga) {
                    // Volvemos a modo órbita
                    enOrbita = true;
                    temporizador = 0;

                    // Al acabar la embestida, calculamos la posición actual del boss
                    float newBossCenterX = sprite.getX() + sprite.getWidth() / 2f;
                    float newBossCenterY = sprite.getY() + sprite.getHeight() / 2f;

                    // Ajustar el ángulo para que orbite sin saltos
                    float dx = newBossCenterX - centroX;
                    float dy = newBossCenterY - centroY;
                    angulo = (float) Math.toDegrees(Math.atan2(dy, dx));
                }
            }
        }
    }

    private void orbitar(float delta, Sprite sprite) {
        // Incrementamos el ángulo en función de la velocidad orbital
        angulo += velocidadOrbital * delta;

        // Convertimos el ángulo a radianes
        float rad = (float) Math.toRadians(angulo);

        float radio = distanciaOrbita;

        float posX = centroX + MathUtils.cos(rad) * radio;
        float posY = centroY + MathUtils.sin(rad) * radio;

        sprite.setPosition(posX - sprite.getWidth() / 2f, posY - sprite.getHeight() / 2f);
    }

    private void cargar(float delta, Sprite sprite, float bossCenterX, float bossCenterY) {
        // Se traslada usando la 'chargeDirX, chargeDirY' ya calculadas
        float moveX = chargeDirX * velocidadCarga * delta;
        float moveY = chargeDirY * velocidadCarga * delta;

        sprite.translate(moveX , moveY );
    }
}
