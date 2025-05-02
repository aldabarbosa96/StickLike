package com.sticklike.core.entidades.jugador;

/**
 * Clase modelo para encapsular las estadísticas del jugador
 */
public class StatsJugador {
    private float velocidad;
    private float vida;
    private float maxVida;
    private float rangoAtaque;
    private float danyo;
    private float intervaloDisparo;
    private float velocidadAtaque;
    private int proyectilesPorDisparo;
    private float resistencia;
    private float critico;
    private float regVida;
    private float poder;
    private int oroGanado;
    private int trazosGanados;

    public StatsJugador(float velocidad, float vida, float maxVida, float rangoAtaque, float danyo, float intervaloDisparo, float velocidadAtaque, int proyectilesPorDisparo, float resistencia, float critico, float regVida, float poder) {
        this.velocidad = velocidad;
        this.vida = vida;
        this.maxVida = maxVida;
        this.rangoAtaque = rangoAtaque;
        this.danyo = danyo;
        this.intervaloDisparo = intervaloDisparo;
        this.velocidadAtaque = velocidadAtaque;
        this.proyectilesPorDisparo = proyectilesPorDisparo;
        this.resistencia = resistencia;
        this.critico = critico;
        this.regVida = regVida;
        this.poder = poder;
        this.oroGanado = 0;
        this.trazosGanados = 0;
    }

    // constructor copia para no alterar las stats base después de una partida
    public StatsJugador(StatsJugador other) {
        this.velocidad = other.velocidad;
        this.vida = other.vida;
        this.maxVida = other.maxVida;
        this.rangoAtaque = other.rangoAtaque;
        this.danyo = other.danyo;
        this.intervaloDisparo = other.intervaloDisparo;
        this.velocidadAtaque = other.velocidadAtaque;
        this.proyectilesPorDisparo = other.proyectilesPorDisparo;
        this.resistencia = other.resistencia;
        this.critico = other.critico;
        this.regVida = other.regVida;
        this.poder = other.poder;
        this.oroGanado = other.oroGanado;
        this.trazosGanados = other.trazosGanados;
    }

    public float getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
    }

    public float getVida() {
        return vida;
    }

    public void setVida(float vida) {
        this.vida = vida;
    }

    public float getMaxVida() {
        return maxVida;
    }

    public void setMaxVida(float maxVida) {
        this.maxVida = maxVida;
    }

    public float getRangoAtaque() {
        return rangoAtaque;
    }

    public void setRangoAtaque(float rangoAtaque) {
        this.rangoAtaque = rangoAtaque;
    }

    public float getDanyo() {
        return danyo;
    }

    public void setDanyo(float danyo) {
        this.danyo = danyo;
    }

    public float getIntervaloDisparo() {
        return intervaloDisparo;
    }

    public void setIntervaloDisparo(float intervaloDisparo) {
        this.intervaloDisparo = intervaloDisparo;
    }

    public int getProyectilesPorDisparo() {
        return proyectilesPorDisparo;
    }

    public void setProyectilesPorDisparo(int proyectilesPorDisparo) {
        this.proyectilesPorDisparo = proyectilesPorDisparo;
    }

    public float getResistencia() {
        return resistencia;
    }

    public void setResistencia(float resistencia) {
        this.resistencia = resistencia;
    }

    public float getCritico() {
        return critico;
    }

    public void setCritico(float critico) {
        this.critico = critico;
    }

    public float getRegVida() {
        return regVida;
    }

    public void setRegVida(float regVida) {
        this.regVida = regVida;
    }

    public float getPoder() {
        return poder;
    }

    public void setPoder(float poder) {
        this.poder = poder;
    }

    public int getOroGanado() {
        return oroGanado;
    }

    public void setOroGanado(int oroGanado) {
        this.oroGanado = oroGanado;
    }

    public int getTrazosGanados() {
        return trazosGanados;
    }

    public void setTrazosGanados(int trazosGanados) {
        this.trazosGanados = trazosGanados;
    }

    public float getVelocidadAtaque() {
        return velocidadAtaque;
    }

    public void setVelocidadAtaque(float velocidadAtaque) {
        this.velocidadAtaque = velocidadAtaque;
    }
}
