package com.sticklike.core.entidades.enemigos.destructibles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.entidades.objetos.recolectables.Boost;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.ui.RenderHUDComponents;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class Destructibles implements Enemigo {
    private Sprite sprite;
    private float vidaDestructible = VIDA_DESTRUCTIBLE;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private RenderBaseEnemigos renderBaseEnemigos;
    private Texture damageTexture;
    private TipoDestructible tipo;

    // Enum interno para definir los parámetros de cada tipo de destructible, incluyendo propiedades de sombra
    public enum TipoDestructible {
        TIPO1(DESTRUCTIBLE, DESTRUCTIBLE_DMG, ANCHO_DESTRUCT, ALTO_DESTRUCT, 0.85f, 0.25f, -6f), TIPO2(DESTRUCTIBLE1, DESTRUCTIBLE1_DMG, ANCHO_DESTRUCT1, ALTO_DESTRUCT1, 0.67f, 0.35f, -5f), TIPO3(DESTRUCTIBLE2, DESTRUCTIBLE2_DMG, ANCHO_DESTRUCT2, ALTO_DESTRUCT2, 0.85f, 0.25f, -3.5f), TIPO4(DESTRUCTIBLE3, DESTRUCTIBLE3_DMG, ANCHO_DESTRUCT3, ALTO_DESTRUCT3, 0.92f, 0.2f, -3.5f);

        private final String textureKey;
        private final String damageTextureKey;
        private final float width;
        private final float height;
        private final float shadowWidthMultiplier;
        private final float shadowHeightMultiplier;
        private final float shadowYOffset;

        TipoDestructible(String textureKey, String damageTextureKey, float width, float height, float shadowWidthMultiplier, float shadowHeightMultiplier, float shadowYOffset) {
            this.textureKey = textureKey;
            this.damageTextureKey = damageTextureKey;
            this.width = width;
            this.height = height;
            this.shadowWidthMultiplier = shadowWidthMultiplier;
            this.shadowHeightMultiplier = shadowHeightMultiplier;
            this.shadowYOffset = shadowYOffset;
        }

        public String getTextureKey() {
            return textureKey;
        }

        public String getDamageTextureKey() {
            return damageTextureKey;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public float getShadowWidthMultiplier() {
            return shadowWidthMultiplier;
        }

        public float getShadowHeightMultiplier() {
            return shadowHeightMultiplier;
        }

        public float getShadowYOffset() {
            return shadowYOffset;
        }
    }

    public Destructibles(float x, float y, RenderBaseEnemigos renderBaseEnemigos) {
        // Seleccionar aleatoriamente un tipo de destructible de la enum interna
        TipoDestructible[] tipos = TipoDestructible.values();
        int indiceAleatorio = MathUtils.random(tipos.length - 1);
        this.tipo = tipos[indiceAleatorio];

        // Asignar la textura y la textura de daño según el tipo seleccionado
        this.sprite = new Sprite(manager.get(tipo.getTextureKey(), Texture.class));
        this.damageTexture = manager.get(tipo.getDamageTextureKey(), Texture.class);

        // Configurar el tamaño y la posición del sprite
        sprite.setSize(tipo.getWidth(), tipo.getHeight());
        sprite.setPosition(x, y);

        this.animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        this.renderBaseEnemigos = renderBaseEnemigos;
    }

    @Override
    public void actualizar(float delta) {
        animacionesBaseEnemigos.actualizarParpadeo(sprite, delta);
        animacionesBaseEnemigos.actualizarFade(delta);
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        renderBaseEnemigos.dibujarEnemigos(batch, this);
    }

    @Override
    public void reducirSalud(float amount) {
        vidaDestructible -= amount;
        if (vidaDestructible <= 0) {
            if (!animacionesBaseEnemigos.estaEnFade()) {
                animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                activarParpadeo(DURACION_PARPADEO_ENEMIGO);
            }
        }
    }

    public Boost sueltaBoost(RenderHUDComponents renderHUDComponents) {
        Boost.BoostType[] tipos = Boost.BoostType.values();
        int indiceAleatorio = MathUtils.random(tipos.length - 1);
        Boost.BoostType tipo = tipos[indiceAleatorio];

        float duracion = 20f;
        Texture boostTexture = null;

        switch (tipo) {
            case VELOCIDAD:
                boostTexture = manager.get(ICONO_VEL_MOV, Texture.class);
                break;
            case ATAQUE:
                boostTexture = manager.get(ICONO_FUERZA, Texture.class);
                break;
            case MUNICION:
                boostTexture = manager.get(ICONO_PROYECTILES, Texture.class);
                break;
            case INVULNERABILIDAD:
                boostTexture = manager.get(ICONO_RESISTENCIA, Texture.class);
                break;
            case VELATAQUE:
                boostTexture = manager.get(ICONO_VEL_ATAQUE, Texture.class);
            default:
                break;
        }
        return new Boost(boostTexture, duracion, tipo, getX(), getY(), renderHUDComponents);
    }

    @Override
    public boolean estaMuerto() {
        return (vidaDestructible <= 0 && !animacionesBaseEnemigos.estaEnFade());
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
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        return null; // Se maneja desde el métdo sueltaBoost para gestionar por parámetro el renderHud y mantener claridad en el código
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void reseteaTemporizadorDanyo() {
    }

    @Override
    public boolean puedeAplicarDanyo() {
        return false;
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
        animacionesBaseEnemigos.activarParpadeo(sprite, duracion, damageTexture);
    }

    @Override
    public void dispose() {
        sprite = null;
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
    }

    @Override
    public float getVida() {
        return vidaDestructible;
    }

    @Override
    public float getDamageAmount() {
        return 0;
    }

    @Override
    public AnimacionesBaseEnemigos getAnimaciones() {
        return animacionesBaseEnemigos;
    }

    @Override
    public boolean isMostrandoDamageSprite() {
        return false;
    }

    public float getShadowWidthMultiplier() {
        return tipo.getShadowWidthMultiplier();
    }

    public float getShadowHeightMultiplier() {
        return tipo.getShadowHeightMultiplier();
    }

    public float getShadowYOffset() {
        return tipo.getShadowYOffset();
    }
}
