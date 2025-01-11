package com.sticklike.core.utils;

import com.badlogic.gdx.graphics.Texture;

public class AssetLoader {
    public static Texture stickman,enemy01,life,weapon01;

    public static void load() {
        stickman = new Texture("stickman.png");
        enemy01 = new Texture("enemy01.png");
        life = new Texture("life.png");
        weapon01 = new Texture("bow_arrow.png");
    }

    public static void dispose() {
        if (stickman != null) stickman.dispose();
        if (enemy01 != null) enemy01.dispose();
        if (life != null) life.dispose();
        if (weapon01 != null) weapon01.dispose();

    }
}
