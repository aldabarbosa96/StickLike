package com.sticklike.core.gameplay.sistemas.eventBus;

import com.sticklike.core.interfaces.GameEvent;
import com.sticklike.core.interfaces.GameEventListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameEventBus {
    private static final List<GameEventListener> listeners = new CopyOnWriteArrayList<>();

    public static void register(GameEventListener l) {
        listeners.add(l);
    }

    public static void unregister(GameEventListener l) {
        listeners.remove(l);
    }

    public static void publish(GameEvent event) {
        for (GameEventListener l : listeners) {
            l.onEvent(event);
        }
    }

    public enum EventType {
        LVL, RECOLECCIÓN, BOOST, FASE, MEJORA, BOSS
    }
}
