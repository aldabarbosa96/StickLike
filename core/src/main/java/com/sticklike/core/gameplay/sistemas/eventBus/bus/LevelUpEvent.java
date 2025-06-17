package com.sticklike.core.gameplay.sistemas.eventBus.bus;

import com.sticklike.core.interfaces.GameEvent;
import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;

public class LevelUpEvent implements GameEvent {
    private final int newLevel;
    public LevelUpEvent(int newLevel) { this.newLevel = newLevel; }
    @Override
    public String getLogMessage() {
        return "¡Subes al nivel " + newLevel + "!";
    }

    @Override
    public GameEventBus.EventType getType() {
        return GameEventBus.EventType.LVL;
    }
}
