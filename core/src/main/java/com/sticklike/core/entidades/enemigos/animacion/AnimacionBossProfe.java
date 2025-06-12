package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;


public class AnimacionBossProfe {
    private final AnimacionBaseDosFrames anim12;
    private final AnimacionBaseDosFrames anim13;
    private boolean usandoAnim12;
    private Runnable onFrame3;
    private float audioTimer = 0f;


    public AnimacionBossProfe(TextureRegion frame1, TextureRegion frame2, TextureRegion frame3, float dur12, float dur13) {
        // anim12 entre frame1 ↔ frame2
        this.anim12 = new AnimacionBaseDosFrames(frame1, frame2, dur12, dur12, null, null);
        // anim13 entre frame1 ↔ frame3
        this.anim13 = new AnimacionBaseDosFrames(frame2, frame3, dur13, dur13, () -> {
            if (onFrame3 != null) onFrame3.run();
        }, null);

        this.usandoAnim12 = true;
    }


    public void actualizarAnimacion(float delta, Sprite sprite, boolean useAnim12) {
        if (useAnim12) {
            if (!usandoAnim12) {
                anim12.reset(sprite);
                audioTimer = 0f;
                usandoAnim12 = true;
            }
            anim12.update(delta, sprite);
            reproducirAudioBoss(delta);
        } else {
            if (usandoAnim12) {
                anim13.reset(sprite);
                usandoAnim12 = false;
            }
            anim13.update(delta, sprite);
        }
    }

    public void reproducirAudioBoss(float delta) {
        audioTimer += delta;
        if (audioTimer >= (float) 1.0) {
            GestorDeAudio.getInstance().reproducirEfecto("sonidoBossProfe", 0.75f);
            audioTimer -= (float) 1.0;
        }
    }

    public void setOnFrame3(Runnable r) {
        this.onFrame3 = r;
    }
}
