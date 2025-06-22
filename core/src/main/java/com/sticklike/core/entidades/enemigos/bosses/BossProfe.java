package com.sticklike.core.entidades.enemigos.bosses;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionBaseEnemigos;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionBossProfe;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBossProfe;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.enemigos._00ProyectilCorrector;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class BossProfe implements Enemigo {
    private Sprite sprite;
    private final AnimacionBossProfe animBossProfe;
    private final Jugador jugador;
    private final MovimientoBossProfe movimientoBoss;
    private final AnimacionBaseEnemigos animaciones;
    private float vida = 2750;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private final float damageAmount = 26.5f;
    private final float coolDownDanyo = 1f;
    private float temporizadorDanyo = 0f;
    private boolean estaMuerto = false;
    private final Texture damageTexture;
    private final RenderBaseEnemigos renderBaseEnemigos;
    private float temporizadorDisparoProyectil = 0f;
    private final VentanaJuego1 pantalla;
    private _00ProyectilCorrector proyectil;

    public BossProfe(Jugador jugador, float x, float y, VentanaJuego1 ventanaJuego1) {
        this.jugador = jugador;
        this.pantalla = ventanaJuego1;

        // 1) Cargar texturas y crear el Sprite
        Texture texFrame1 = manager.get(BOSS_PROFE, Texture.class);
        Texture texFrame2 = manager.get(BOSS_PROFE_CORRIENDO, Texture.class);
        Texture texFrame3 = manager.get(BOSS_PROFE_LANZANDO, Texture.class);

        TextureRegion frame1 = new TextureRegion(texFrame1);
        TextureRegion frame2 = new TextureRegion(texFrame2);
        TextureRegion frame3 = new TextureRegion(texFrame3);

        sprite = new Sprite(frame1);
        sprite.setSize(105, 135);
        sprite.setPosition(x, y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        //2) Crear la animación BossProfe (frame1↔frame2 para chase, frame2↔frame3 para lanzar)
        float dur12 = 0.1f;
        float dur13 = 0.2f;
        animBossProfe = new AnimacionBossProfe(frame1, frame2, frame3, dur12, dur13);

        //3) Registrar callback para disparar justo al pasar a frame3
        animBossProfe.setOnFrame3(() -> {
            // Calculamos el centro del boss:
            float bossCenterX = sprite.getX() + sprite.getWidth() * 0.5f;
            float bossCenterY = sprite.getY() + sprite.getHeight() * 0.5f;
            // Calculamos la dirección al jugador:
            float playerCenterX = jugador.getSprite().getX() + jugador.getSprite().getWidth() * 0.5f;
            float playerCenterY = jugador.getSprite().getY() + jugador.getSprite().getHeight() * 0.5f;
            float dx = playerCenterX - bossCenterX;
            float dy = playerCenterY - bossCenterY;
            float len = (float) Math.sqrt(dx * dx + dy * dy);
            if (len == 0f) len = 1f;
            dx /= len;
            dy /= len;

            // Creamos y añadimos el proyectil
            proyectil = new _00ProyectilCorrector(bossCenterX, bossCenterY, dx, dy, jugador);
            pantalla.anyadirProyectilEnemigo(proyectil);
            randomAudioBoss();
        });

        //4) Crear demás componentes (IA, efectos, etc)
        animaciones = new AnimacionBaseEnemigos();
        movimientoBoss = new MovimientoBossProfe(true);

        // Arrancamos en la animación chase por defecto
        animBossProfe.actualizarAnimacion(0f, sprite, true);

        this.damageTexture = manager.get(DAMAGE_BOSS_PROFE, Texture.class);
        this.renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
    }

    @Override
    public void actualizar(float delta) {
        // 1) Actualizar fade (muerte)
        animaciones.actualizarFade(delta);

        if (vida > 0f) {
            // 2) Reducir temporizador de cooldown de daño
            if (temporizadorDanyo > 0f) {
                temporizadorDanyo -= delta;
            }

            // 3) IA: avanza o se queda quieto según el estado de “enFaseChase”
            movimientoBoss.actualizarMovimiento(delta, sprite, jugador);

            // 4) Determinar fase de animación y actualizar animación normal
            boolean useAnim12 = movimientoBoss.isEnFaseChase();
            animBossProfe.actualizarAnimacion(delta, sprite, useAnim12);

            // 5) Parpadeo por daño (cubre el sprite normal si está activo)
            animaciones.actualizarParpadeo(sprite, delta);

            // 6) Flip horizontal según posición del jugador
            animaciones.flipearEnemigo(jugador, sprite);

        } else {
            animaciones.actualizarParpadeo(sprite, delta);
            if (animaciones.enAnimacionMuerte()) {
                animaciones.actualizarAnimacionMuerteSinEscala(sprite, delta);
            }
        }
    }

    private void randomAudioBoss() { // todo --> añadir más audios
        int random = MathUtils.random(1, 3);
        switch (random) {
            case 1:
                GestorDeAudio.getInstance().reproducirEfecto("sonidoBossProfe2", 0.75f);
                break;
            case 2:
                GestorDeAudio.getInstance().reproducirEfecto("sonidoBossProfe3", 0.75f);
                break;
            case 3:
                GestorDeAudio.getInstance().reproducirEfecto("sonidoBossProfe4", 0.75f);
                break;
        }
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        if (vida > 0f) {
            renderBaseEnemigos.dibujarEnemigos(batch, this);
        } else {
            if (animaciones.enAnimacionMuerte()) {
                sprite.draw(batch);
            }
        }
    }

    @Override
    public void reducirSalud(float amount) {
        vida -= amount;
        if (vida > 0f) {
            activarParpadeo(DURACION_PARPADEO_ENEMIGO);
            reseteaTemporizadorDanyo();
        }
        if (vida <= 0f && !animaciones.estaEnFade() && !animaciones.enAnimacionMuerte()) {
            Animation<TextureRegion> animMuerte = GestorDeAssets.animations.get("bossProfeMuerte");
            animaciones.iniciarAnimacionMuerte(animMuerte);
            animaciones.iniciarFadeMuerte(DURACION_FADE_BOSS_PROFE);
            animaciones.activarParpadeo(sprite, DURACION_PARPADEO_ENEMIGO, damageTexture);

            GestorDeAudio.getInstance().pausarMusica();
            GestorDeAudio.getInstance().reproducirEfecto("sonidoMuerteBossProfe", 1f);
        }
    }

    @Override
    public boolean estaMuerto() {
        estaMuerto = true;
        return (vida <= 0f && !animaciones.estaEnFade());
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        Rectangle rect = sprite.getBoundingRectangle();
        return rect.overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        if (!haSoltadoXP) {
            haSoltadoXP = true;
            return new ObjetoXp(getX(), getY());
        }
        return null;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void reseteaTemporizadorDanyo() {
        temporizadorDanyo = coolDownDanyo;
    }

    @Override
    public boolean puedeAplicarDanyo() {
        return vida > 0f && temporizadorDanyo <= 0f;
    }

    @Override
    public boolean haSoltadoXP() {
        return haSoltadoXP;
    }

    @Override
    public void setProcesado(boolean procesado) {
        this.procesado = procesado;
    }

    @Override
    public boolean isProcesado() {
        return procesado;
    }

    @Override
    public void activarParpadeo(float duracion) {
        animaciones.activarParpadeo(sprite, duracion, damageTexture);
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        // Este boss no recibe knockback en esta versión
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
    public float getVida() {
        return vida;
    }

    @Override
    public float getDamageAmount() {
        return damageAmount;
    }

    public AnimacionBaseEnemigos getAnimaciones() {
        return animaciones;
    }

    @Override
    public boolean isMostrandoDamageSprite() {
        return animaciones.estaEnParpadeo();
    }

    @Override
    public boolean estaEnKnockback() {
        return false;
    }

    @Override
    public MovimientoBaseEnemigos getMovimiento() {
        return movimientoBoss;
    }

    @Override
    public void dispose() {
        sprite = null;
    }
}
