package com.sticklike.core.gameplay.sistemas.eventBus.bus;

import com.sticklike.core.interfaces.GameEvent;
import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;

public class BoostEvent implements GameEvent {
    private final String boostName;
    private final float duration;

    public BoostEvent(String boostName, float duration) {
        this.boostName = boostName;
        this.duration = duration;
    }

    @Override
    public String getLogMessage() {
        return String.format("%s activo +%.0fs", boostName, duration);
    }

    @Override
    public GameEventBus.EventType getType() {
        return GameEventBus.EventType.BOOST;
    }
}
