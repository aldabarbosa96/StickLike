package com.sticklike.core.entidades.enemigos.ia;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.entidades.jugador.Jugador;

public class MovimientoBossPolla extends MovimientoBaseEnemigos {
    private float velocidadAproximacion;
    private float distanciaOrbita;
    private float velocidadOrbital;
    private float velocidadCarga;
    private float tiempoOrbita;
    private float tiempoCarga;
    private float temporizador;
    private boolean enOrbita;
    private boolean haAlcanzadoDistanciaOrbita;
    private float centroX, centroY;
    private float angulo;
    private float chargeDirX, chargeDirY;
    private boolean sentidoHorario;
    private boolean ajustandoOrbita = false;
    private float tiempoAjuste = 0.5f; // Duración de la interpolación
    private float temporizadorAjuste = 0f;
    private float startX, startY;
    private float targetX, targetY;

    public MovimientoBossPolla(boolean puedeEmpujar) {
        super(puedeEmpujar);
        this.velocidadAproximacion = 150f;
        this.distanciaOrbita = 150f;
        this.velocidadOrbital = 90f;
        this.velocidadCarga = 290f;
        this.tiempoOrbita = 3.5f;
        this.tiempoCarga = 1.5f;

        // Estados iniciales
        this.haAlcanzadoDistanciaOrbita = false;
        this.enOrbita = true;
        this.temporizador = 0;
        this.angulo = 0;

        // Dirección inicial de la embestida recalculada cada vez
        this.chargeDirX = 0;
        this.chargeDirY = 0;

        // Sentido de giro aleatorio
        this.sentidoHorario = MathUtils.randomBoolean();
    }

    @Override
    protected void actualizarMovimientoEspecifico(float delta, Sprite sprite, Jugador jugador) {
        if (jugador == null || sprite == null) return;

        // Si estamos en el estado de ajuste, interpolamos la posición
        if (ajustandoOrbita) {
            ajustarOrbita(delta, sprite);
            return; // No se procesa el resto hasta finalizar la interpolación
        }

        // Centro actual del boss
        float bossCenterX = sprite.getX() + sprite.getWidth() / 2f;
        float bossCenterY = sprite.getY() + sprite.getHeight() / 2f;

        // Centro del jugador
        float playerCenterX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f;
        float playerCenterY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f;

        // Distancia boss -> jugador
        float difX = playerCenterX - bossCenterX;
        float difY = playerCenterY - bossCenterY;
        float dist = (float) Math.sqrt(difX * difX + difY * difY);

        // ====== FASE 1: APROXIMACIÓN ======
        if (!haAlcanzadoDistanciaOrbita) {
            // Moverse hacia el jugador
            if (dist != 0) {
                difX /= dist;
                difY /= dist;
            }
            float moveX = difX * velocidadAproximacion * delta;
            float moveY = difY * velocidadAproximacion * delta;
            sprite.translate(moveX, moveY);

            // Cuando entra en zona de órbita
            if (dist <= distanciaOrbita) {
                haAlcanzadoDistanciaOrbita = true;
                temporizador = 0;
                enOrbita = true;

                // El centro de la órbita será la posición del jugador en este instante
                centroX = playerCenterX;
                centroY = playerCenterY;

                // Ajustar el ángulo inicial según la posición actual del boss
                float dx = bossCenterX - centroX;
                float dy = bossCenterY - centroY;
                angulo = (float) Math.toDegrees(Math.atan2(dy, dx));

                // Escogemos el sentido de giro al azar
                sentidoHorario = MathUtils.randomBoolean();
            }
        }
        // ====== FASE 2: ÓRBITA / CARGA ======
        else {
            temporizador += delta;

            if (enOrbita) {
                // ORBITANDO alrededor de centroX, centroY (fijo desde que entramos)
                orbitar(delta, sprite);

                // Cuando pase el tiempo de órbita, embestimos
                if (temporizador >= tiempoOrbita) {
                    enOrbita = false;
                    temporizador = 0;

                    // Calculamos la dirección de la embestida
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
                // EMBESTIDA
                cargar(delta, sprite);

                // Cuando termina la carga, en vez de resetear inmediatamente, iniciamos la interpolación
                if (temporizador >= tiempoCarga) {
                    temporizador = 0;
                    ajustandoOrbita = true;
                    temporizadorAjuste = 0;

                    // Actualizamos el centro de la órbita al centro actual del jugador
                    centroX = playerCenterX;
                    centroY = playerCenterY;

                    // Obtenemos la posición actual del boss (centro)
                    float currentBossCenterX = sprite.getX() + sprite.getWidth() / 2f;
                    float currentBossCenterY = sprite.getY() + sprite.getHeight() / 2f;
                    startX = currentBossCenterX;
                    startY = currentBossCenterY;

                    // Calculamos el ángulo actual respecto al centro de órbita
                    float dx = currentBossCenterX - centroX;
                    float dy = currentBossCenterY - centroY;
                    angulo = (float) Math.toDegrees(Math.atan2(dy, dx));

                    // Calculamos la posición destino sobre la circunferencia (órbita)
                    float rad = MathUtils.degreesToRadians * angulo;
                    targetX = centroX + MathUtils.cos(rad) * distanciaOrbita;
                    targetY = centroY + MathUtils.sin(rad) * distanciaOrbita;

                    // Se elige un nuevo sentido de giro al azar
                    sentidoHorario = MathUtils.randomBoolean();
                }
            }
        }
    }


    private void ajustarOrbita(float delta, Sprite sprite) {
        temporizadorAjuste += delta;
        float t = Math.min(1, temporizadorAjuste / tiempoAjuste);
        // Interpolación lineal entre la posición de inicio y la destino
        float currentX = MathUtils.lerp(startX, targetX, t);
        float currentY = MathUtils.lerp(startY, targetY, t);

        // Colocamos el sprite de forma que su centro sea (currentX, currentY)
        sprite.setPosition(currentX - sprite.getWidth() / 2f, currentY - sprite.getHeight() / 2f);

        // Cuando finaliza la interpolación, volvemos al modo órbita
        if (t >= 1) {
            ajustandoOrbita = false;
            enOrbita = true;
            temporizador = 0;
        }
    }

    private void orbitar(float delta, Sprite sprite) {
        float ajusteAngulo = velocidadOrbital * delta;
        angulo += sentidoHorario ? ajusteAngulo : -ajusteAngulo;

        // Convertimos ángulo a radianes
        float rad = (float) Math.toRadians(angulo);

        // Radio de la órbita
        float radio = distanciaOrbita;

        float posX = centroX + MathUtils.cos(rad) * radio;
        float posY = centroY + MathUtils.sin(rad) * radio;

        sprite.setPosition(posX - sprite.getWidth() / 2f, posY - sprite.getHeight() / 2f);
    }

    private void cargar(float delta, Sprite sprite) {
        float moveX = chargeDirX * velocidadCarga * delta;
        float moveY = chargeDirY * velocidadCarga * delta;
        sprite.translate(moveX, moveY);
    }
}
