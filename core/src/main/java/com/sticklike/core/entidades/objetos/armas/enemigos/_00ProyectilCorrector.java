package com.sticklike.core.entidades.objetos.armas.enemigos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.enemigos.mobs.escuela.EnemigoCorrector;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class _00ProyectilCorrector implements Proyectiles {

    private static Texture TEXTURE;
    private static final float SPEED = 666f;
    private static final float MAX_DISTANCE = 750f;
    private static final float DAMAGE = 15f;

    private final Sprite sprite;
    private final Rectangle collisionRect = new Rectangle();
    private final Vector2 direction = new Vector2();
    private float distanciaRecorrida = 0f;
    private boolean activo = true;
    private final Jugador jugador;

    public _00ProyectilCorrector(float startX, float startY, float dirX, float dirY, Jugador jugador) {
        this.jugador = jugador;

        if (TEXTURE == null) TEXTURE = manager.get(ENEMIGO_CORRECTOR, Texture.class);

        sprite = new Sprite(TEXTURE);
        sprite.setSize(15f, 40);
        sprite.setOrigin(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
        sprite.setPosition(startX - sprite.getOriginX(), startY - sprite.getOriginY());

        // Si el vector entrante no está normalizado, lo normalizamos
        float mag = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        if (mag != 0) {
            direction.set(dirX / mag, dirY / mag);
        } else {
            direction.set(1f, 0f);
        }

        // Orientamos el sprite para que apunte en la dirección del movimiento
        float angDeg = (float) Math.toDegrees(Math.atan2(direction.y, direction.x));
        sprite.setRotation(angDeg + 90f);

        // Inicializar rectángulo de colisión
        collisionRect.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;

        // 1) Mover proyectil
        float move = SPEED * delta;
        sprite.translate(direction.x * move, direction.y * move);
        distanciaRecorrida += move;

        // 2) Actualizar rectángulo de colisión
        collisionRect.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());

        // 3) Verificar colisión contra el jugador
        Rectangle jugadorRect = jugador.getSprite().getBoundingRectangle();
        if (collisionRect.overlaps(jugadorRect)) {
            jugador.getColisionesJugador().recibeDanyo(DAMAGE,jugador, GestorDeAudio.getInstance());
            activo = false;
            return;
        }

        // 4) Si superó su distancia máxima sin impactar, “se convierte” en EnemigoCorrector
        if (distanciaRecorrida >= MAX_DISTANCE) {
            float spawnX = sprite.getX() + sprite.getWidth() * 0.5f;
            float spawnY = sprite.getY() + sprite.getHeight() * 0.5f;

            // Crear el enemigo y añadirlo al controlador de enemigos:
            jugador.getControladorEnemigos().getEnemigos().add(new EnemigoCorrector(jugador, spawnX, spawnY));

            activo = false;
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (activo) {
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        // No liberamos TEXTURE aquí para no interferir con otros proyectiles
        activo = false;
    }

    @Override
    public float getX() {
        return sprite.getX();
    }

    @Override
    public float getY() {
        return sprite.getY();
    }

    @Override
    public Rectangle getRectanguloColision() {
        return collisionRect;
    }

    @Override
    public boolean isProyectilActivo() {
        return activo;
    }

    @Override
    public void desactivarProyectil() {
        activo = false;
    }

    @Override
    public float getBaseDamage() {
        return DAMAGE;
    }

    @Override
    public float getKnockbackForce() {
        return 100f;
    }

    @Override
    public boolean isPersistente() {
        return false;
    }

    @Override
    public void registrarImpacto(Enemigo enemigo) {
        // No aplica, este proyectil solo daña jugador, no enemigos
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return false;
    }

    @Override
    public boolean esCritico() {
        return false;
    }
}
