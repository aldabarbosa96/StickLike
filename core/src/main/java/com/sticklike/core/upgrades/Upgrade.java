package com.sticklike.core.upgrades;

public class Upgrade {
    private final String name;
    private final String description;
    private final Runnable efectoMejora;

    public Upgrade(String name, String description, Runnable efectoMejora) {
        this.name = name;
        this.description = description;
        this.efectoMejora = efectoMejora;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    public void apply(){
        efectoMejora.run();
    }
}
