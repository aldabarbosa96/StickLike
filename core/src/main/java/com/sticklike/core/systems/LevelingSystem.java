package com.sticklike.core.systems;

import com.sticklike.core.entities.Player;
import com.sticklike.core.managers.UpgradeManager;

public class LevelingSystem {
    private final Player player;
    private final UpgradeManager upgradeManager;
    private float currentExperience = 0f;
    private float experienceToNextLevel = 100f;
    private int level = 1;

    public LevelingSystem(Player player, UpgradeManager upgradeManager) {
        this.player = player;
        this.upgradeManager = upgradeManager;
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

        // Delegamos en UpgradeManager para manejar las mejoras
        if (upgradeManager != null) {
            upgradeManager.promptUpgrade();
        }
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
