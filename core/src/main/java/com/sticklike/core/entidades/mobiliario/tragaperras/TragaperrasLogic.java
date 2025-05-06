package com.sticklike.core.entidades.mobiliario.tragaperras;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Modelo: genera resultados al girar los carretes y notifica listeners.
 */
public class TragaperrasLogic {

    public enum State { IDLE, SPINNING, SHOWING_RESULT }

    private final int reels;
    private final int symbolsPerReel;
    private final List<Integer> currentResult = new ArrayList<>();
    private final Random rng = new Random();
    private State state = State.IDLE;
    private final List<SlotListener> listeners = new ArrayList<>();

    public TragaperrasLogic(int reels, int symbolsPerReel) {
        this.reels = reels;
        this.symbolsPerReel = symbolsPerReel;
        for (int i = 0; i < reels; i++) currentResult.add(0);
    }

    public void spin() {
        if (state != State.IDLE) return;
        state = State.SPINNING;

        for (int i = 0; i < reels; i++) {
            currentResult.set(i, rng.nextInt(symbolsPerReel));
        }

        state = State.SHOWING_RESULT;
        notifyListeners();
        state = State.IDLE;
    }

    public List<Integer> getCurrentResult() { return currentResult; }

    public void addListener(SlotListener l) { listeners.add(l); }

    private void notifyListeners() {
        for (SlotListener l : listeners) l.onSpinComplete(currentResult);
    }

    @FunctionalInterface
    public interface SlotListener {
        void onSpinComplete(List<Integer> result);
    }
}
