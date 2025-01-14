package com.sticklike.core.managers;

import com.sticklike.core.entities.Player;
import com.sticklike.core.upgrades.Upgrade;
import com.sticklike.core.screens.UpgradeScreen;
import com.sticklike.core.MainGame;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpgradeManager {
    private final Player player;
    private final List<Upgrade> allUpgrades;
    private final List<Upgrade> currentUpgradeOptions;
    private final MainGame game;

    public UpgradeManager(Player player, MainGame game) {
        this.player = player;
        this.game = game;
        this.allUpgrades = new ArrayList<>();
        this.currentUpgradeOptions = new ArrayList<>();
        initializeUpgrades();
    }

    private void initializeUpgrades() {
        allUpgrades.add(new Upgrade(
            "Aumentar Velocidad",
            "Aumenta la velocidad un 15%",
            () -> player.increaseSpeed(0.15f)
        ));
        allUpgrades.add(new Upgrade(
            "Aumentar Rango de Ataque",
            "Aumenta el rango de ataque un 10%",
            () -> player.increaseAttackRange(0.10f)
        ));
        allUpgrades.add(new Upgrade(
            "Aumentar Velocidad de Ataque",
            "Reduce el intervalo de disparo un 9%",
            () -> player.reduceShootInterval(0.15f)
        ));
        allUpgrades.add(new Upgrade(
            "Daño Aumentado",
            "Aumenta el daño del Ataque Básico un 6%",
            () -> player.increaseDamage(1.06f)
        ));
        allUpgrades.add(new Upgrade(
            "Proyectil Múltiple",
            "Aumenta el número de Proyectiles en 1",
            () -> player.increaseProjectilesPerShot(1)
        ));
    }

    public void promptUpgrade() {
        game.setScreen(new UpgradeScreen(this, game));
    }

    public List<Upgrade> generateUpgradeOptions(int count) {
        currentUpgradeOptions.clear();
        Collections.shuffle(allUpgrades);
        for (int i = 0; i < Math.min(count, allUpgrades.size()); i++) {
            currentUpgradeOptions.add(allUpgrades.get(i));
        }
        return new ArrayList<>(currentUpgradeOptions);
    }

    public void applyUpgrade(Upgrade selectedUpgrade) {
        if (!currentUpgradeOptions.contains(selectedUpgrade)) {
            throw new IllegalArgumentException("La mejora seleccionada no es válida.");
        }

        selectedUpgrade.apply();
        System.out.println("Mejora aplicada: " + selectedUpgrade.getName());

        currentUpgradeOptions.clear();
    }
}
