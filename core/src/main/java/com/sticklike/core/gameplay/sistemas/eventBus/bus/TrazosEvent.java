package com.sticklike.core.gameplay.sistemas.eventBus.bus;

import com.sticklike.core.interfaces.GameEvent;
import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;

public class TrazosEvent implements GameEvent {
    private final int amount;
    public TrazosEvent(int amount) { this.amount = amount; }
    @Override public String getLogMessage() {
        return "Recoges +" + amount + " trazo/s";
    }
    @Override public GameEventBus.EventType getType() {
        return GameEventBus.EventType.RECOLECCIÓN;
    }
}
