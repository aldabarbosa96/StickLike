package com.sticklike.core.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class InGameText {
    private String text;
    private float x,y;
    private float duration;
    private BitmapFont font;

    public InGameText(String text, float x, float y, float duration) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.duration = duration;

        font = new BitmapFont();
        font.setColor(Color.RED);
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    public void render(SpriteBatch batch){
        font.draw(batch,text,x,y);
    }
    public void update(float delta){
        duration -= delta;
        y += delta * 30;
    }
    public void dispose() {
        font.dispose();
    }
}

