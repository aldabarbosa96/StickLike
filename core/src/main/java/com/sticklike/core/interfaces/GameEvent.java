package com.sticklike.core.interfaces;

import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;

public interface GameEvent {
    String getLogMessage();
    GameEventBus.EventType getType();
}
