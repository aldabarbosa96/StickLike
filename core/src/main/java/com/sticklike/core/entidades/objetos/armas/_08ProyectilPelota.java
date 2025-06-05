package com.sticklike.core.entidades.objetos.armas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.renderizado.particulas.ParticleManager;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.HashSet;
import java.util.Set;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Proyectil Pelota de tenis.
 * Rebota en un ángulo aleatorio distinto a la dirección de entrada cada vez que impacta,
 * hasta agotar el número máximo de rebotes o superar la distancia máxima.
 */
public final class _08ProyectilPelota implements Proyectiles {
    private static final float SIZE = 17.5f;
    private static final float ORIGIN = SIZE * 0.5f;
    private static final float SPEED = 350f;
    private static final float MAX_DISTANCE = 2500f;
    private static final int MAX_REBOTES = 5;
    private static final float SPIN_SPEED = 666f;
    private static final float PARTICLE_LEN_FACTOR = 25;
    private static final float PARTICLE_WID_FACTOR = 5f;
    private static final Color PARTICLE_COLOR = new Color(.55f, .85f, .25f, 0.9f);
    private static Texture TEXTURE;
    private final Sprite sprite;
    private final Rectangle collisionRect = new Rectangle();
    private final Vector2 center = new Vector2();
    //private final RenderParticulasProyectil trail;
    private final ParticleEffectPool.PooledEffect efecto;
    private final Set<Enemigo> impactados = new HashSet<>(4);
    private float dirX, dirY;
    private float distanciaRecorrida = 0f;
    private int rebotesRestantes = MAX_REBOTES;
    private boolean activo = true;
    private boolean esCritico = false;
    private float impactoTimer = 0f;
    private final Jugador jugador;
    private final GestorDeAudio audio = GestorDeAudio.getInstance();

    public _08ProyectilPelota(float x, float y, float dirX, float dirY, Jugador jugador) {
        this.jugador = jugador;

        if (TEXTURE == null) TEXTURE = manager.get(ARMA_PELOTA, Texture.class);

        sprite = new Sprite(TEXTURE);
        sprite.setSize(SIZE, SIZE);
        sprite.setColor(Color.GREEN);
        sprite.setOrigin(ORIGIN, ORIGIN);
        sprite.setPosition(x - ORIGIN, y - ORIGIN);
        sprite.setRotation(vectorToDegrees(dirX, dirY));

        /* dirección normalizada */
        float mag = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        this.dirX = (mag != 0) ? dirX / mag : 1f;
        this.dirY = (mag != 0) ? dirY / mag : 0f;

        /* partículas */
        float scale = Gdx.graphics.getWidth() / REAL_WIDTH;
        /*int maxLen = (int) (PARTICLE_LEN_FACTOR * scale);
        float partWid = PARTICLE_WID_FACTOR * scale;
        trail = new RenderParticulasProyectil(maxLen, partWid, PARTICLE_COLOR);
        trail.setAlphaMult(.65f);*/

        float originPos = SIZE * 0.5f;
        Vector2 initialCenter = new Vector2(x + originPos, y + originPos);
        efecto = ParticleManager.get().obtainEffect("pelota", initialCenter.x, initialCenter.y);

        collisionRect.set(sprite.getX(), sprite.getY(), SIZE, SIZE);
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;

        float move = SPEED * delta;
        sprite.translate(dirX * move, dirY * move);
        sprite.rotate(SPIN_SPEED * delta);
        comprobarReboteVentana();
        distanciaRecorrida += move;
        if (distanciaRecorrida >= MAX_DISTANCE) desactivarProyectil();

        center.set(sprite.getX() + ORIGIN, sprite.getY() + ORIGIN);
        //trail.update(center);
        //TrailRender.get().submit(trail);
        efecto.setPosition(center.x, center.y);

        if (impactoTimer < IMPACTO_DURACION) {
            impactoTimer += delta;
            if (impactoTimer >= IMPACTO_DURACION) {
                sprite.setColor(Color.WHITE);
                //trail.setColor(PARTICLE_COLOR);
                impactados.clear();
            }
        }

        collisionRect.set(sprite.getX(), sprite.getY(), SIZE, SIZE);
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (activo) sprite.draw(batch);
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
        //trail.reset();
        efecto.allowCompletion();
        activo = false;
    }

    @Override
    public float getBaseDamage() {
        float base = MathUtils.random(13, 26);
        if (MathUtils.random() < jugador.getCritico()) {
            esCritico = true;
            return base * 1.5f;
        }
        esCritico = false;
        return base;
    }

    @Override
    public float getKnockbackForce() {
        return 101;
    }

    @Override
    public boolean isPersistente() {
        return true;
    }

    @Override
    public boolean esCritico() {
        return esCritico;
    }

    @Override
    public void registrarImpacto(Enemigo enemigo) {
        /* evitamos multigolpe en el mismo enemigo durante este rebote */
        if (!impactados.add(enemigo)) return;

        sprite.setColor(Color.RED);
        //trail.setColor(Color.RED);
        audio.reproducirEfecto("impactoBase", 1f);
        impactoTimer = 0f;

        if (rebotesRestantes-- > 0) {
            elegirNuevaDireccionAleatoria();
        } else {
            desactivarProyectil();
        }
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return impactados.contains(enemigo);
    }

    /**
     * Genera una nueva dirección aleatoria que no sea “colinear” con la actual.
     * Esto evita que la pelota siga prácticamente la misma trayectoria.
     */
    private void elegirNuevaDireccionAleatoria() {
        float angActual = MathUtils.atan2(dirY, dirX);
        float nuevoAng;
        do {
            nuevoAng = MathUtils.random(0f, MathUtils.PI2);
            /* Rechazamos ángulos que formen menos de ~30° respecto al vector entrante */
        } while (Math.abs(MathUtils.cos(nuevoAng - angActual)) > 0.866f);  // cos 30° ≈ .866

        dirX = MathUtils.cos(nuevoAng);
        dirY = MathUtils.sin(nuevoAng);
        sprite.setRotation(vectorToDegrees(dirX, dirY));
    }

    private boolean comprobarReboteJugador() { // todo --> corregir el overlaps del sprite con el jugador (hay que darle un margen)
        Rectangle jugRect = jugador.getSprite().getBoundingRectangle();
        if (collisionRect.overlaps(jugRect)) {
            dirX = -dirX;
            dirY = -dirY;
            sprite.setRotation(vectorToDegrees(dirX, dirY));

            // descontamos un rebote
            rebotesRestantes--;

            // si no quedan rebotes, desactivamos
            if (rebotesRestantes <= 0) {
                desactivarProyectil();
            }

            return true;
        }
        return false;
    }

    private void comprobarReboteVentana() {
        OrthographicCamera cam = VentanaJuego1.getCamara();
        if (cam == null) return;

        float z = cam.zoom;
        float halfW = cam.viewportWidth * z * 0.5f;
        float halfH = cam.viewportHeight * z * 0.5f;
        float halfH2 = cam.viewportHeight * 0.5f;

        // límites visibles en coordenadas del mundo
        float left = cam.position.x - halfW;
        float right = cam.position.x + halfW - SIZE;
        float bottom = cam.position.y - (halfH2 - HUD_HEIGHT - HUD_BAR_Y_OFFSET);
        float top = cam.position.y + halfH - SIZE;

        boolean rebota = false;

        // --- eje X ---
        if (sprite.getX() < left) {
            sprite.setX(left);
            dirX = Math.abs(dirX);
            rebota = true;
        } else if (sprite.getX() > right) {
            sprite.setX(right);
            dirX = -Math.abs(dirX);
            rebota = true;
        }

        // --- eje Y ---
        if (sprite.getY() < bottom) {
            sprite.setY(bottom);
            dirY = Math.abs(dirY);
            rebota = true;
        } else if (sprite.getY() > top) {
            sprite.setY(top);
            dirY = -Math.abs(dirY);
            rebota = true;
        }

        if (rebota) {
            sprite.setRotation(vectorToDegrees(dirX, dirY));
            if (rebotesRestantes-- <= 0) desactivarProyectil();
        }
    }

    private float vectorToDegrees(float x, float y) {
        return MathUtils.atan2(y, x) * MathUtils.radiansToDegrees;
    }

    @Override
    public void dispose() {
        TEXTURE = null;
        //trail.dispose();
        efecto.free();
    }
}
