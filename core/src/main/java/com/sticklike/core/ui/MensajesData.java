// MensajesData.java
package com.sticklike.core.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import java.util.ArrayList;
import java.util.List;

import com.sticklike.core.entidades.enemigos.mobs.EnemigoAlarma;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoPolla;
import com.sticklike.core.interfaces.Enemigo;

public class MensajesData {

    private static MensajesData instance;

    // Estados y listas para mensajes de culos
    private boolean mensajeCulosActivado;
    private List<ChatOption> opcionesChatCulos;
    private int nextMessageCulosIndex;
    private Timer.Task culoTask;

    // Estados y listas para mensajes de pollas
    private boolean mensajesPollasActivado;
    private List<ChatOption> opcionesChatPollas;
    private int nextMessagePollasIndex;
    private Timer.Task pollaTask;

    // Estados y listas para mensajes de alarmas
    private boolean mensajeAlarmasActivado;
    private List<ChatOption> opcionesAlarmasAlarma;
    private List<ChatOption> opcionesAlarmasCrono;
    private int nextMessageAlarmasAlarmaIndex;
    private int nextMessageAlarmasCronoIndex;
    private Timer.Task alarmaAlarmaTask;
    private Timer.Task alarmaCronoTask;

    private MensajesData() {
        mensajeCulosActivado = false;
        mensajesPollasActivado = false;
        mensajeAlarmasActivado = false;

        // Inicializamos mensajes de culos
        opcionesChatCulos = new ArrayList<>();
        mensajesCulos();

        // Inicializamos mensajes de pollas
        opcionesChatPollas = new ArrayList<>();
        mensajesPollas();

        // Inicializamos mensajes de alarmas
        opcionesAlarmasAlarma = new ArrayList<>();
        opcionesAlarmasCrono = new ArrayList<>();
        initMensajesAlarmas();
    }

    public static MensajesData getInstance() {
        if (instance == null) {
            instance = new MensajesData();
        }
        return instance;
    }

    /**
     * Punto A: en cuanto arranque la fase de "culos", llamamos a este método
     * en lugar de hacer polling cada frame.
     */
    public void activarMensajesCulos(final RenderHUDComponents renderHUD) {
        if (mensajeCulosActivado) return;
        mensajeCulosActivado = true;
        nextMessageCulosIndex = 0;
        scheduleNextCulo(renderHUD);
    }

    private void scheduleNextCulo(final RenderHUDComponents renderHUD) {
        if (nextMessageCulosIndex >= opcionesChatCulos.size()) return;
        float delay = MathUtils.random(10f, 20f);
        if (culoTask != null) culoTask.cancel();
        culoTask = Timer.schedule(new Timer.Task() {
            @Override public void run() {
                lanzarMensajeCulos(renderHUD);
                nextMessageCulosIndex++;
                scheduleNextCulo(renderHUD);
            }
        }, delay);
    }

    private void lanzarMensajeCulos(RenderHUDComponents renderHUD) {
        Enemigo e = pickRandom(renderHUD, EnemigoCulo.class);
        if (e != null) {
            ChatOption opt = opcionesChatCulos.get(nextMessageCulosIndex);
            Mensajes.getInstance().addEnemyMessage(opt.nombre, opt.mensaje, e);
        }
    }

    public void activarMensajesPollas(final RenderHUDComponents renderHUD) {
        if (mensajesPollasActivado) return;
        mensajesPollasActivado = true;
        nextMessagePollasIndex = 0;
        scheduleNextPolla(renderHUD);
    }

    private void scheduleNextPolla(final RenderHUDComponents renderHUD) {
        if (nextMessagePollasIndex >= opcionesChatPollas.size()) return;
        float delay = MathUtils.random(15f, 30f);
        if (pollaTask != null) pollaTask.cancel();
        pollaTask = Timer.schedule(new Timer.Task() {
            @Override public void run() {
                lanzarMensajePollas(renderHUD);
                nextMessagePollasIndex++;
                scheduleNextPolla(renderHUD);
            }
        }, delay);
    }

    private void lanzarMensajePollas(RenderHUDComponents renderHUD) {
        Enemigo e = pickRandom(renderHUD, EnemigoPolla.class);
        if (e != null) {
            ChatOption opt = opcionesChatPollas.get(nextMessagePollasIndex);
            Mensajes.getInstance().addEnemyMessage(opt.nombre, opt.mensaje, e);
        }
    }

    public void activarMensajesAlarmas(final RenderHUDComponents renderHUD) {
        if (mensajeAlarmasActivado) return;
        mensajeAlarmasActivado = true;
        nextMessageAlarmasAlarmaIndex = 0;
        nextMessageAlarmasCronoIndex = 0;
        scheduleNextAlarmaAlarma(renderHUD);
        scheduleNextAlarmaCrono(renderHUD);
    }

    private void scheduleNextAlarmaAlarma(final RenderHUDComponents renderHUD) {
        if (nextMessageAlarmasAlarmaIndex >= opcionesAlarmasAlarma.size()) return;
        float delay = MathUtils.random(10f, 15f);
        if (alarmaAlarmaTask != null) alarmaAlarmaTask.cancel();
        alarmaAlarmaTask = Timer.schedule(new Timer.Task() {
            @Override public void run() {
                lanzarMensajeAlarma(renderHUD, false);
                nextMessageAlarmasAlarmaIndex++;
                scheduleNextAlarmaAlarma(renderHUD);
            }
        }, delay);
    }

    private void scheduleNextAlarmaCrono(final RenderHUDComponents renderHUD) {
        if (nextMessageAlarmasCronoIndex >= opcionesAlarmasCrono.size()) return;
        float delay = MathUtils.random(10f, 15f);
        if (alarmaCronoTask != null) alarmaCronoTask.cancel();
        alarmaCronoTask = Timer.schedule(new Timer.Task() {
            @Override public void run() {
                lanzarMensajeAlarma(renderHUD, true);
                nextMessageAlarmasCronoIndex++;
                scheduleNextAlarmaCrono(renderHUD);
            }
        }, delay);
    }

    private void lanzarMensajeAlarma(RenderHUDComponents renderHUD, boolean crono) {
        Enemigo e = pickRandom(renderHUD, EnemigoAlarma.class);
        if (e != null) {
            EnemigoAlarma alarma = (EnemigoAlarma)e;
            if (alarma.isEsCrono() == crono) {
                ChatOption opt = crono
                    ? opcionesAlarmasCrono.get(nextMessageAlarmasCronoIndex)
                    : opcionesAlarmasAlarma.get(nextMessageAlarmasAlarmaIndex);
                Mensajes.getInstance().addEnemyMessage(opt.nombre, opt.mensaje, e);
            }
        }
    }

    private <T extends Enemigo> T pickRandom(RenderHUDComponents renderHUD, Class<T> cls) {
        com.badlogic.gdx.utils.Array<T> candidates = new com.badlogic.gdx.utils.Array<>();
        for (Enemigo en : renderHUD.getControladorEnemigos().getEnemigos()) {
            if (cls.isInstance(en)) candidates.add(cls.cast(en));
        }
        return candidates.size > 0 ? candidates.random() : null;
    }

    /**
     * Cancela cualquier task pendiente y resetea los estados para volver
     * a un estado limpio (por ejemplo, al reiniciar nivel o salir de pantalla).
     */
    public void reset() {
        mensajeCulosActivado = false;
        mensajesPollasActivado = false;
        mensajeAlarmasActivado = false;
        if (culoTask != null) culoTask.cancel();
        if (pollaTask != null) pollaTask.cancel();
        if (alarmaAlarmaTask != null) alarmaAlarmaTask.cancel();
        if (alarmaCronoTask != null) alarmaCronoTask.cancel();
    }

    // ---------------------- Mensajes de Cul os ----------------------

    private void mensajesCulos() {
        opcionesChatCulos.add(new ChatOption("Ojete1", "¡Todos a dejarle la cara de culo!"));
        opcionesChatCulos.add(new ChatOption("Ojete2", "¡Mira mis almorranas!"));
        opcionesChatCulos.add(new ChatOption("Ojete3", "¡¡¡Mira sus almorranas!!!"));
        opcionesChatCulos.add(new ChatOption("Ojete4", "¡Cagadle en el pecho!"));
        opcionesChatCulos.add(new ChatOption("Ojete5", "¡Diarreadlo!"));
        opcionesChatCulos.add(new ChatOption("Ojete6", "Yo me llamo Ralph"));
        opcionesChatCulos.add(new ChatOption("Ojete7", "Caca, culo, pedo, pis, nalga"));
        opcionesChatCulos.add(new ChatOption("Ojete8", "¡Me cago en tu estampa!"));
        opcionesChatCulos.add(new ChatOption("Ojete9", "¿Quién quiere caquita?"));
        opcionesChatCulos.add(new ChatOption("Ojete10", "1 StickMans 2 cups"));
        opcionesChatCulos.add(new ChatOption("Ojete11", "¡Palmea mis nalgas!"));
        opcionesChatCulos.add(new ChatOption("Ojete12", "¿Tienes cara de culo o culo de cara?"));
        opcionesChatCulos.add(new ChatOption("Ojete13", "¡Achuuusssss! <<se le sale liquidillo>>"));
        opcionesChatCulos.add(new ChatOption("Ojete14", "¡Nalgas a mí!"));
        opcionesChatCulos.add(new ChatOption("Ojete15", "¡Pedos fuera!"));
        opcionesChatCulos.add(new ChatOption("Ojete16", "A veces me confunden con Elon Musk..."));
        opcionesChatCulos.add(new ChatOption("Ojete 17", "¡Choca esas nalgas!"));
        opcionesChatCulos.add(new ChatOption("Ojete 18", "Un, dos, tres ¡Pedo, pastel, diarrea!"));
        opcionesChatCulos.add(new ChatOption("Ojete 19", "¡¿Dónde está mi caca?!"));
        opcionesChatCulos.add(new ChatOption("Ojete 20", "Culet culet"));
        opcionesChatCulos.add(new ChatOption("Ojete 21", "¿Alguna polla quiere entrar?"));
    }

    // ---------------------- Mensajes de Pollas ----------------------

    private void mensajesPollas() {
        opcionesChatPollas.add(new ChatOption("Polla1", "¡Chúpame!"));
        opcionesChatPollas.add(new ChatOption("Polla2", "¡Po po polla!"));
        opcionesChatPollas.add(new ChatOption("Polla3", "No me toques los huevos..."));
        opcionesChatPollas.add(new ChatOption("Polla4", "¡Meadle la boca!"));
        opcionesChatPollas.add(new ChatOption("Polla5", "¡Servicio de correos!"));
        opcionesChatPollas.add(new ChatOption("Polla6", "Mi gato se llama guantes"));
        opcionesChatPollas.add(new ChatOption("Polla7", "Pene, cipote, tula, nardo, anacardo"));
        opcionesChatPollas.add(new ChatOption("Polla8", "Y yo soy un clítoris"));
        opcionesChatPollas.add(new ChatOption("Polla9", "¿Quién quiere lefita?"));
        opcionesChatPollas.add(new ChatOption("Polla10", "¿Te gusta la merladura?"));
        opcionesChatPollas.add(new ChatOption("Polla11", "¿Te traga er seme?"));
        opcionesChatPollas.add(new ChatOption("Polla12", "¿Alguien tiene ruedas?"));
        opcionesChatPollas.add(new ChatOption("Polla13", "Creo que tengo varicocele..."));
        opcionesChatPollas.add(new ChatOption("Polla14", "Menudo escrotinio"));
        opcionesChatPollas.add(new ChatOption("Polla15", "Soy la dierna"));
        opcionesChatPollas.add(new ChatOption("Polla16", "A veces me confunden con Donald Trump..."));
        opcionesChatPollas.add(new ChatOption("Polla17", "¡Choca ese escroto!"));
        opcionesChatPollas.add(new ChatOption("Polla18", "Un, dos, tres ¡Polla, motel, gonorrea!"));
        opcionesChatPollas.add(new ChatOption("Polla19", "¡¿Dónde está mi próstata?!"));
        opcionesChatPollas.add(new ChatOption("Polla20", "Tula llevas"));
        opcionesChatPollas.add(new ChatOption("Polla21", "Mamá me lavó las negras"));
        opcionesChatPollas.add(new ChatOption("Polla22", "To impress a chick do the helicopter dick"));
    }

    // ---------------------- Mensajes de Alarmas ----------------------

    private void initMensajesAlarmas() {
        // Mensajes para la variante Alarma (esCrono == false)
        opcionesAlarmasAlarma.add(new ChatOption("Alarma1", "Chilli dos alarmitas"));
        opcionesAlarmasAlarma.add(new ChatOption("Alarma2", "¡Arriba pedazo de basura!"));
        opcionesAlarmasAlarma.add(new ChatOption("Alarma3", "Cualquier parecido es pura coincidencia"));
        opcionesAlarmasAlarma.add(new ChatOption("Alarma4", "No me llames Wanda que te reviento"));
        opcionesAlarmasAlarma.add(new ChatOption("Alarma5", "¿¡Te levantas o te levanto!?"));
        opcionesAlarmasAlarma.add(new ChatOption("Alarma6", "Paja y a empezar un nuevo día"));
        opcionesAlarmasAlarma.add(new ChatOption("Alarma7", "Tira pa clase mamonazo"));
        opcionesAlarmasAlarma.add(new ChatOption("Alarma8", "¡¡¡RIIIIIIIIIIIIINGGGGG!!!"));
        opcionesAlarmasAlarma.add(new ChatOption("Alarma9", "Tira pa clase mamonazo"));
        opcionesAlarmasAlarma.add(new ChatOption("Alarma10", "Hora de que te calles para siempre"));
        opcionesAlarmasAlarma.add(new ChatOption("Alarma11", "10..9..8..7..5digo6...4..mierda..."));

        // Mensajes para la variante Crono (esCrono == true)
        opcionesAlarmasCrono.add(new ChatOption("Crono1", "¡TIC-TAC-HIJO-DE-PUTA!"));
        opcionesAlarmasCrono.add(new ChatOption("Crono2", "Según mis cálculos, vas a morir"));
        opcionesAlarmasCrono.add(new ChatOption("Crono3", "1..2..3..4..5..6..7..8..9.. <<algo está mal>>"));
        opcionesAlarmasCrono.add(new ChatOption("Crono3", "1..2..3..4..5..6..7..8..9.. <<algo está mal>>"));
        opcionesAlarmasCrono.add(new ChatOption("Crono3", "1..2..3..4..5..6..7...8..9.. <<algo está mal>>"));
        opcionesAlarmasCrono.add(new ChatOption("Crono3", "1..2..3..4..5..6..7..8..9..1 <<algo está mal>>"));
        opcionesAlarmasCrono.add(new ChatOption("Crono3", "0..2..3..4..5..6..7..8..9.. <<algo está mal>>"));
        opcionesAlarmasCrono.add(new ChatOption("Crono4", "Es la hora de las tortas"));
        opcionesAlarmasCrono.add(new ChatOption("Crono5", "TIC-TAC-TOC-A-ME-LA-PO-LLA"));
        opcionesAlarmasCrono.add(new ChatOption("Crono6", "¡A que te cronomato!"));
        opcionesAlarmasCrono.add(new ChatOption("Crono7", "¡Socórro!¡Soy Cosmo!¡Me han secuestrado!"));
        opcionesAlarmasCrono.add(new ChatOption("Crono8", "Voy a contar cuanto tardas en morir :)"));
        opcionesAlarmasCrono.add(new ChatOption("Crono9", "Wanda no te alarmes pero tengo cronopatía..."));
        opcionesAlarmasCrono.add(new ChatOption("Crono10", "Recuerda: a las 2 serán las 3 y a las 3 te chupo las tetas"));
    }

    // Clase interna para representar una opción de mensaje
    private static class ChatOption {
        String nombre;
        String mensaje;

        ChatOption(String nombre, String mensaje) {
            this.nombre = nombre;
            this.mensaje = mensaje;
        }
    }
}
