package com.sticklike.core.entidades.enemigos.ia;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.Jugador;

import static com.sticklike.core.utilidades.GestorConstantes.*;

public class MovimientoExamen extends MovimientoBaseEnemigos {
    private boolean enFaseCarga = true;
    private boolean enFaseDisparo = false;
    private boolean enFaseParabola = false;

    private float tiempoFaseCargaBase = TIEMPO_CARGA_EXAMEN;
    private float tiempoFaseDisparoBase = TIEMPO_LINEAL_EXAMEN;
    private float tiempoFaseParabolaBase = TIEMPO_PARABOLA_EXAMEN;

    private float tiempoFaseCarga;
    private float tiempoFaseDisparo;
    private float tiempoFaseParabola;

    private float velocidadDisparo = VEL_BASE_EXAMEN;

    private float temporizadorFase = 0f;

    private float direccionX, direccionY;
    private boolean direccionCalculada = false;

    private float objetivoX;
    private float objetivoY;

    private float tParabola = 0f;
    private Vector2 inicioParabola;
    private Vector2 controlParabola;
    private Vector2 finParabola;

    private float temporizadorAnimacion = 0f;
    private float intervaloAnimacion = 0.2f;
    private boolean usandoSprite2 = false;

    private static final float CARGA_RANDOM_OFFSET = 1.5f;
    private static final float DISPARO_RANDOM_OFFSET = 1.25f;
    private static final float PARABOLA_RANDOM_OFFSET = 1.75f;

    private static final float CONTROL_POINT_RANDOM_OFFSET = 80f;

    public MovimientoExamen() {
        super(true);

        this.tiempoFaseCarga = tiempoFaseCargaBase + (float) ((Math.random() - 0.5f) * 2.0f * CARGA_RANDOM_OFFSET);
        this.tiempoFaseDisparo = tiempoFaseDisparoBase + (float) ((Math.random() - 0.5f) * 2.0f * DISPARO_RANDOM_OFFSET);
        this.tiempoFaseParabola = tiempoFaseParabolaBase + (float) ((Math.random() - 0.5f) * 2.0f * PARABOLA_RANDOM_OFFSET);

        // Evitamos que salgan tiempos negativos si el offset es grande
        if (tiempoFaseCarga < 0.2f) tiempoFaseCarga = 0.2f;
        if (tiempoFaseDisparo < 0.2f) tiempoFaseDisparo = 0.2f;
        if (tiempoFaseParabola < 0.2f) tiempoFaseParabola = 0.2f;
    }

    @Override
    protected void actualizarMovimientoEspecifico(float delta, Sprite sprite, Jugador jugador) {
        manejarAnimacionSprite(delta);

        if (enFaseCarga) {
            faseCarga(delta);
        } else if (enFaseDisparo) {
            faseDisparo(delta, sprite, jugador);
        } else if (enFaseParabola) {
            faseParabola(delta, sprite, jugador);
        }
    }

    private void manejarAnimacionSprite(float delta) {
        temporizadorAnimacion += delta;
        if (temporizadorAnimacion >= intervaloAnimacion) {
            usandoSprite2 = !usandoSprite2;
            temporizadorAnimacion = 0f;
        }
    }

    public boolean isUsandoSprite2() {
        return usandoSprite2;
    }

    private void faseCarga(float delta) {
        temporizadorFase += delta;
        if (temporizadorFase >= tiempoFaseCarga) {
            enFaseCarga = false;
            enFaseDisparo = true;
            temporizadorFase = 0f;
            direccionCalculada = false;
        }
    }


    private void faseDisparo(float delta, Sprite sprite, Jugador jugador) {
        if (!direccionCalculada) {
            objetivoX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f;
            objetivoY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f;

            calcularDireccion(sprite, objetivoX, objetivoY);
            direccionCalculada = true;
        }

        // Movimiento lineal hacia el objetivo
        float movX = direccionX * velocidadDisparo * delta;
        float movY = direccionY * velocidadDisparo * delta;
        sprite.translate(movX, movY);

        temporizadorFase += delta;
        // Comprobamos si se agotó el tiempo de faseDisparo o si hemos sobrepasado la posición
        if (temporizadorFase >= tiempoFaseDisparo || haSobrepasadoElObjetivo(sprite)) {
            iniciarParabola(sprite, jugador);
        }
    }

    private boolean haSobrepasadoElObjetivo(Sprite sprite) {
        float enemigoCentroX = sprite.getX() + sprite.getWidth() / 2;

        if (direccionX > 0 && enemigoCentroX >= objetivoX) {
            return true;
        }
        if (direccionX < 0 && enemigoCentroX <= objetivoX) {
            return true;
        }
        return false;
    }


    private void iniciarParabola(Sprite sprite, Jugador jugador) {
        enFaseDisparo = false;
        enFaseParabola = true;
        temporizadorFase = 0f;
        tParabola = 0f;

        // Punto de partida: donde está el enemigo ahora
        inicioParabola = new Vector2(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2);

        // Punto final: la posición del jugador en ese momento
        float jugadorActualX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f;
        float jugadorActualY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f;
        finParabola = new Vector2(jugadorActualX , jugadorActualY);

        // Control: para dar la curvatura “por encima”
        float xControl = (inicioParabola.x + finParabola.x) / 2f;

        // Añadimos un factor aleatorio para que no todas las curvas sean iguales
        float randomExtra = (float) ((Math.random() - 0.5f) * 2.0f * CONTROL_POINT_RANDOM_OFFSET);

        float yControl = Math.max(inicioParabola.y, finParabola.y)
            + 100f  // la base de la curva
            + randomExtra; // un plus aleatorio para que cada uno sea algo distinto

        // Si vamos a la izquierda, también sumamos algo (o restamos) para variar
        if (direccionX < 0) {
            yControl += (float) (30f * (Math.random() - 0.5f));
        }

        controlParabola = new Vector2(xControl, yControl);
    }

    private void faseParabola(float delta, Sprite sprite, Jugador jugador) {
        temporizadorFase += delta;
        tParabola = temporizadorFase / tiempoFaseParabola;
        if (tParabola > 1f) {
            tParabola = 1f;
        }

        // Calculamos punto en la curva
        Vector2 nuevaPos = getPuntoBezierCuadratico(tParabola, inicioParabola, controlParabola, finParabola);
        // Centramos el sprite
        sprite.setPosition(nuevaPos.x - sprite.getWidth() / 2f, nuevaPos.y - sprite.getHeight() / 2f);

        // Cuando acabamos la parábola (t >= 1)
        if (tParabola >= 1f) {
            enFaseParabola = false;
            // Reiniciamos y pasamos a la faseCarga para repetir el ciclo
            enFaseCarga = true;
            temporizadorFase = 0f;
        }
    }

    private void calcularDireccion(Sprite sprite, float targetX, float targetY) {
        float enemyX = sprite.getX() + sprite.getWidth() / 2f;
        float enemyY = sprite.getY() + sprite.getHeight() / 2f;

        float difX = targetX - enemyX;
        float difY = targetY - enemyY;
        float dist = (float) Math.sqrt(difX * difX + difY * difY);

        if (dist != 0) {
            direccionX = difX / dist;
            direccionY = difY / dist;

            // Añadimos un pequeño ángulo aleatorio para que no sea exacto +/- 5 grados
            float angleNoise = (float) ((Math.random() - 0.5f) * Math.toRadians(10.0));
            float cosA = (float) Math.cos(angleNoise);
            float sinA = (float) Math.sin(angleNoise);

            float newDirX = direccionX * cosA - direccionY * sinA;
            float newDirY = direccionX * sinA + direccionY * cosA;
            direccionX = newDirX;
            direccionY = newDirY;
        } else {
            direccionX = 0f;
            direccionY = 0f;
        }
    }


    private Vector2 getPuntoBezierCuadratico(float t, Vector2 start, Vector2 control, Vector2 end) {
        float oneMinusT = 1f - t;

        float x = oneMinusT * oneMinusT * start.x
            + 2f * oneMinusT * t * control.x
            + t * t * end.x;

        float y = oneMinusT * oneMinusT * start.y
            + 2f * oneMinusT * t * control.y
            + t * t * end.y;

        return new Vector2(x, y);
    }


    public void setVelocidadEnemigo(float nuevaVelocidad) {
        this.velocidadDisparo = nuevaVelocidad;
    }
}
