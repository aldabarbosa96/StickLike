package com.sticklike.core.gameplay.sistemas.eventBus.bus;

import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;
import com.sticklike.core.interfaces.GameEvent;

public class BossEvent implements GameEvent {
    private final String name;
    public BossEvent(String name) { this.name = name; }
    @Override public String getLogMessage() {
        return "Aparece : " + name;
    }
    @Override public GameEventBus.EventType getType() {
        return GameEventBus.EventType.BOSS;
    }
}
