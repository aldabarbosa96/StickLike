package com.sticklike.core.gameplay.sistemas.eventBus.bus;

import com.sticklike.core.interfaces.GameEvent;
import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;

public class ImanEvent implements GameEvent {
    @Override public String getLogMessage() {
        return "Imán activado";
    }
    @Override public GameEventBus.EventType getType() {
        return GameEventBus.EventType.RECOLECCIÓN;
    }
}

