package com.sticklike.core.gameplay.sistemas.eventBus.bus;

import com.sticklike.core.interfaces.GameEvent;
import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;

public class PhaseEvent implements GameEvent {
    private final String phase;

    public PhaseEvent(String phase) {
        this.phase = phase;
    }

    @Override
    public String getLogMessage() {
        return phase;
    }

    @Override
    public GameEventBus.EventType getType() {
        return GameEventBus.EventType.FASE;
    }
}
