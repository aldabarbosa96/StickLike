package com.sticklike.core.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.utils.AssetLoader;

public class XPobjects {
    private Sprite sprite;
    private boolean collected = false;

    public XPobjects(float x, float y) {
        Texture texture = AssetLoader.xpObject;
        sprite = new Sprite(texture);
        sprite.setSize(12, 12);
        sprite.setPosition(x, y);
    }


    public void update(float delta) {
    }

    public void render(SpriteBatch batch) {
        if (!collected) {
            sprite.draw(batch);
        }
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
        sprite = null;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public boolean overlapsWith(Sprite otherSprite) {
        return sprite.getBoundingRectangle().overlaps(otherSprite.getBoundingRectangle());
    }

    public void dispose() {
        sprite = null;
    }
}

