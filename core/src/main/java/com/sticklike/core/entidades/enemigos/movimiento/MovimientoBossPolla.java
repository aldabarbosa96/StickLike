package com.sticklike.core.entidades.enemigos.movimiento;

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

    public MovimientoBossPolla(boolean puedeEmpujar) {
        super(puedeEmpujar);
        this.velocidadAproximacion = 150f;
        this.distanciaOrbita = 175f;
        this.velocidadOrbital = 90f;
        this.velocidadCarga = 295f;
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

                // Cuando termina la carga, volvemos a orbitar
                if (temporizador >= tiempoCarga) {
                    // -- IMPORTANTE: Volvemos a aproximarnos la próxima vez
                    haAlcanzadoDistanciaOrbita = false;

                    // Reiniciamos contador
                    temporizador = 0;

                    // Nuevo centro de órbita: si quieres que la órbita se mueva con el jugador,
                    // puedes actualizarlo aquí
                    centroX = playerCenterX;
                    centroY = playerCenterY;

                    // Ajustar el ángulo para no saltar
                    float newBossCenterX = sprite.getX() + sprite.getWidth() / 2f;
                    float newBossCenterY = sprite.getY() + sprite.getHeight() / 2f;
                    float dx = newBossCenterX - centroX;
                    float dy = newBossCenterY - centroY;
                    angulo = (float) Math.toDegrees(Math.atan2(dy, dx));

                    // Cambiamos también el sentido de giro al azar
                    sentidoHorario = MathUtils.randomBoolean();
                }
            }
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
