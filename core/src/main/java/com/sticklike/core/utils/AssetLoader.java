package com.sticklike.core.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class AssetLoader {
    public static Texture stickman, enemy01, life, weapon01, xpIcon, xpObject;
    public static HashMap<String, Animation<TextureRegion>> animations;

    public static void load() {
        loadTextures();
        loadAnimations();
    }

    public static void loadAnimations() {
        animations = new HashMap<>();

        animations.put("idle", createAnimation("actions/movement/stickman_idle", 3, 0.4f));
        animations.put("moveRight", createAnimation("actions/movement/stickman_movementD", 5, 0.2f));
        animations.put("moveLeft", createAnimation("actions/movement/stickman_movementI", 5, 0.2f));
    }

    public static void loadTextures(){
        stickman = new Texture("player/01stickman.png");
        enemy01 = new Texture("enemies/01culo.png");
        life = new Texture("hud/life.png");
        weapon01 = new Texture("weapons/01piedra.png");
        xpIcon = new Texture("hud/xp.png");
        xpObject = new Texture("drops/caca.png");
    }

    private static Animation<TextureRegion> createAnimation(String basePath, int frameCount, float frameDuration) {
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = new TextureRegion(new Texture(basePath + "0" + i + ".png"));
        }
        return new Animation<>(frameDuration, frames);
    }

    public static void dispose() {
        if (stickman != null) stickman.dispose();
        if (enemy01 != null) enemy01.dispose();
        if (life != null) life.dispose();

        if (weapon01 != null) {
            weapon01.dispose();
            weapon01 = null;
        }
        if (xpIcon != null) xpIcon.dispose();
        if (xpObject != null) xpObject.dispose();

        for (Animation<TextureRegion> animation : animations.values()) {
            for (TextureRegion frame : animation.getKeyFrames()) {
                frame.getTexture().dispose();
            }
        }
    }

}
