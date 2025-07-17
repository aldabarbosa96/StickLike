package com.sticklike.core.gameplay.sistemas.eventBus.bus;

import com.sticklike.core.interfaces.GameEvent;
import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;

public class UpgradeEvent implements GameEvent {
    private final String name;
    public UpgradeEvent(String name) { this.name = name; }
    @Override
    public String getLogMessage() {
        return name + " obtenida";
    }

    @Override
    public GameEventBus.EventType getType() {
        return GameEventBus.EventType.MEJORA;
    }
}
