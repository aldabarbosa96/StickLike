package com.sticklike.core.systems;

import com.sticklike.core.entities.Player;
import com.sticklike.core.enums.UpgradeOptions;

public class LevelingSystem {
    private final Player player;
    private float currentExperience = 0f;
    private float experienceToNextLevel = 100f;
    private int level = 1;

    public LevelingSystem(Player player) {
        this.player = player;
    }

    public void addExperience(float amount) {
        currentExperience += amount;
        if (currentExperience >= experienceToNextLevel) {
            levelUp();
        }
    }

    private void levelUp() {
        currentExperience -= experienceToNextLevel;
        level++;
        experienceToNextLevel *= 1.50f;

        // Notificar al GameScreen o clase principal que se requiere UpgradeScreen
        if (onLevelUpListener != null) {
            onLevelUpListener.onLevelUp();
        }
    }


    private void applyUpgrade(UpgradeOptions upgrade) {
        switch (upgrade) {
            case AUMENTAR_VELOCIDAD -> player.increaseSpeed(15f);
            case AUMENTAR_DANYO -> player.getProjectileManager().increaseDamage(0.2f);
            case REDUCIR_INTERVALO_DISPARO -> player.reduceShootInterval(5f);
        }
    }

    private UpgradeOptions selectUpgrade() {
        int opcionSeleccionada = 1;
        if (opcionSeleccionada < 1 || opcionSeleccionada > UpgradeOptions.values().length) {
            opcionSeleccionada = 1;
        }
        return UpgradeOptions.values()[opcionSeleccionada];
    }


    private OnLevelUpListener onLevelUpListener;

    public void setOnLevelUpListener(OnLevelUpListener listener) {
        this.onLevelUpListener = listener;
    }

    // Interfaz para manejar eventos de nivelación
    public interface OnLevelUpListener {
        void onLevelUp();
    }

    public int getLevel() {
        return level;
    }

    public float getCurrentExperience() {
        return currentExperience;
    }

    public float getExperienceToNextLevel() {
        return experienceToNextLevel;
    }
}
