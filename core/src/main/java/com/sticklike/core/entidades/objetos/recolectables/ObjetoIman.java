package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;
import com.sticklike.core.gameplay.sistemas.eventBus.bus.ImanEvent;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.IMAN;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.manager;

public class ObjetoIman extends ObjetoBase{
    private static final Texture TEXTURE = manager.get(IMAN, Texture.class);

    public ObjetoIman(float x, float y) {
        super(x, y, TEXTURE);
        setSpriteTexture(TEXTURE);
    }

    @Override
    public void aplicarEfecto(Jugador jugador, GestorDeAudio audio, VentanaJuego1 game) {
        audio.reproducirEfecto("recogerIman", AUDIO_RECOLECCION_CACA);

        for (ObjetosXP obj : game.getObjetosXP()) {
            if (obj instanceof ObjetoXp base) {
                base.forzarAtraccion();
            }
        }
    }
    @Override
    public void recolectar(GestorDeAudio audio){
        audio.reproducirEfecto("recogerOro", AUDIO_RECOLECCION_ORO); // todo --> crear efecto sonido imán
        super.recolectar(audio);
        GameEventBus.publish(new ImanEvent());
    }

    @Override
    public void particulas() {
    }

    @Override
    protected Texture getTexture() {
        return TEXTURE;
    }

    @Override
    protected float getWidth() {
        return OBJETO_IMAN_WIDTH;
    }

    @Override
    protected float getHeight() {
        return OBJETO_IMAN_HEIGHT;
    }
}
