package com.sticklike.core.entidades.mobiliario.tragaperras;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Modelo: genera resultados al girar los carretes y notifica listeners
 */
public class TragaperrasLogic {
    public enum State {IDLE, SPINNING, SHOWING_RESULT}

    private final int reels;
    private final int symbolsPerReel;
    private final List<Integer> currentResult = new ArrayList<>();
    private final Random rng = new Random();
    private State state = State.IDLE;
    private final List<SlotListener> listeners = new ArrayList<>();
    private double winChance = 0.5;

    public TragaperrasLogic(int reels, int symbolsPerReel) {
        this.reels = reels;
        this.symbolsPerReel = symbolsPerReel;
        for (int i = 0; i < reels; i++)
            currentResult.add(0);
    }

    public void spin() {
        if (state != State.IDLE) return;
        state = State.SPINNING;

        boolean win = rng.nextDouble() < winChance;

        if (win) {
            // 2.a) Tirada ganadora: seleccionamos un símbolo y lo reproducimos en los 3 carretes
            int sym = rng.nextInt(symbolsPerReel);
            for (int i = 0; i < reels; i++) {
                currentResult.set(i, sym);
            }
        } else {
            // 2.b) Tirada perdedora: generamos hasta no crear una combinación triple idéntica
            do {
                for (int i = 0; i < reels; i++) {
                    currentResult.set(i, rng.nextInt(symbolsPerReel));
                }
                // seguimos repitiendo si por casualidad hemos generado una triple igual
            } while (isAllEqual(currentResult));
        }

        state = State.SHOWING_RESULT;
        notifyListeners();
        state = State.IDLE;
    }

    private boolean isAllEqual(List<Integer> res) {
        int first = res.getFirst();
        for (int i = 1; i < res.size(); i++) {
            if (res.get(i) != first) return false;
        }
        return true;
    }

    public List<Integer> getCurrentResult() {
        return currentResult;
    }

    public void addListener(SlotListener l) {
        listeners.add(l);
    }

    private void notifyListeners() {
        for (SlotListener l : listeners)
            l.onSpinComplete(currentResult);
    }

    @FunctionalInterface
    public interface SlotListener {
        void onSpinComplete(List<Integer> result);
    }

    public void setWinChance(double winChance) {
        this.winChance = Math.max(0, Math.min(1, winChance));
    }
}

