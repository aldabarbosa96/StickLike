package com.sticklike.core.enums;


public enum UpgradeOptions {
    AUMENTAR_VELOCIDAD("Aumentar Velocidad"),
    AUMENTAR_DANYO("Aumentar Daño"),
    REDUCIR_INTERVALO_DISPARO("Aumentar Velocidad de Ataque"),
    AUMENTAR_ATTACK_RANGE("Aumentar Rango de Ataque");

    private final String description;

    UpgradeOptions(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

