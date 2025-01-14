package com.sticklike.core.managers;

import com.sticklike.core.entities.Player;
import com.sticklike.core.enums.UpgradeOptions;
import com.sticklike.core.upgrades.Upgrade;
import com.sticklike.core.utils.GameConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpgradeManager {
    private final Player player;
    private final List<Upgrade> allUpgrades; // Todas las mejoras disponibles en el juego
    private final List<Upgrade> currentUpgradeOptions; // Opciones actuales de mejoras

    public UpgradeManager(Player player) {
        this.player = player;
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
            "Reducir Intervalo de Disparo",
            "Reduce el intervalo de disparo un 15%",
            () -> player.reduceShootInterval(0.15f)
        ));
        /*allUpgrades.add(new Upgrade(
            "Aumentar Salud Máxima",
            "Aumenta la salud máxima en 10 puntos",
            () -> player.increaseMaxHealth(10)
        ));*/
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
