package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;
import com.sticklike.core.gameplay.sistemas.eventBus.bus.GoldEvent;
import com.sticklike.core.gameplay.sistemas.eventBus.bus.TrazosEvent;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class ObjetoBolsa extends ObjetoBase {
    private static final Texture TEXTURE = randomBagTexture();
    private final int randomOro;
    private final int randomTrazos;
    private boolean esDeOro;

    public ObjetoBolsa(float x, float y) {
        super(x, y, TEXTURE);
        setSpriteTexture(getTexture());
        randomOro = MathUtils.random(3, 9);
        randomTrazos = MathUtils.random(2, 8);

    }

    private static Texture randomBagTexture() {
        int randomBag = MathUtils.random(1, 2);
        return switch (randomBag) {
            case 1 -> manager.get(RECOLECTABLE_BOLSA_CACAS, Texture.class);
            case 2 -> manager.get(RECOLECTABLE_BOLSA_TRAZOS, Texture.class);
            default -> null;
        };
    }

    @Override
    public void recolectar(GestorDeAudio gestorDeAudio) {
        gestorDeAudio.reproducirEfecto("recogerOro", AUDIO_RECOLECCION_ORO);
        super.recolectar(gestorDeAudio);
        if (esDeOro(TEXTURE)) {
            GameEventBus.publish(new GoldEvent(randomOro));

        } else GameEventBus.publish(new TrazosEvent(randomTrazos));
    }

    @Override
    public void aplicarEfecto(Jugador jugador, GestorDeAudio audio, VentanaJuego1 game) {
        if (esDeOro(TEXTURE)) {
            jugador.setOroGanado(jugador.getOroGanado() + randomOro);
        } else jugador.setTrazosGanados(jugador.getTrazosGanados() + randomTrazos);

    }

    private boolean esDeOro(Texture texture) {
        esDeOro = texture == manager.get(RECOLECTABLE_BOLSA_CACAS, Texture.class);
        return esDeOro;
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
        return 36;
    }

    @Override
    protected float getHeight() {
        return 36;
    }
}
