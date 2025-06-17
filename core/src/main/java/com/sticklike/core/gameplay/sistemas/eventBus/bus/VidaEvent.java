package com.sticklike.core.gameplay.sistemas.eventBus.bus;

import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;
import com.sticklike.core.interfaces.GameEvent;

public class VidaEvent implements GameEvent {
    private final int vidaGanada;

    public VidaEvent(int vidaGanada) {
        this.vidaGanada = vidaGanada;
    }

    @Override
    public String getLogMessage() {
        return "Recuperas +" + vidaGanada + " de salud";
    }

    @Override
    public GameEventBus.EventType getType() {
        return GameEventBus.EventType.RECOLECCIÓN;
    }
}
