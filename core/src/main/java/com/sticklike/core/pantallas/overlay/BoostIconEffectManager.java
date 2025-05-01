package com.sticklike.core.pantallas.overlay;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.ui.RenderHUDComponents;

public class BoostIconEffectManager {
    private static BoostIconEffectManager instance;
    private BoostIconEffect effect;

    private BoostIconEffectManager() {
        effect = new BoostIconEffect();
    }

    public static BoostIconEffectManager getInstance() {
        if (instance == null) {
            instance = new BoostIconEffectManager();
        }
        return instance;
    }

    public BoostIconEffect getEffect() {
        return effect;
    }

    public void update(float delta, RenderHUDComponents renderHUDComponents) {
        effect.update(delta, renderHUDComponents);
    }

    public void render(SpriteBatch batch) {
        effect.render(batch);
    }

    public void dispose() {
        effect.dispose();
        instance = null;
    }
}
