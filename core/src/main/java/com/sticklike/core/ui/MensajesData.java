package com.sticklike.core.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.Collections;
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
    private List<ChatOption> mensajesCulosPorMostrar;
    private int nextMessageCulosIndex;
    private float lastCuloMessageTime;

    // Estados y listas para mensajes de pollas
    private boolean mensajesPollasActivado;
    private List<ChatOption> opcionesChatPollas;
    private List<ChatOption> mensajesPollasPorMostrar;
    private int nextMessagePollasIndex;
    private float lastPollaMessageTime;

    // Estados y listas para mensajes de alarmas
    private boolean mensajeAlarmasActivado;
    private List<ChatOption> opcionesAlarmasAlarma;
    private List<ChatOption> opcionesAlarmasCrono;
    private List<ChatOption> mensajesAlarmasAlarmaPorMostrar;
    private List<ChatOption> mensajesAlarmasCronoPorMostrar;
    private int nextMessageAlarmasAlarmaIndex;
    private int nextMessageAlarmasCronoIndex;
    private float lastAlarmaAlarmaMessageTime;
    private float lastAlarmaCronoMessageTime;

    private float delayBetweenMessages = 0;

    private MensajesData() {
        mensajeCulosActivado = false;
        mensajesPollasActivado = false;
        mensajeAlarmasActivado = false;

        // Inicializamos mensajes de culos
        opcionesChatCulos = new ArrayList<>();
        mensajesCulos();
        resetMensajesCulos();

        // Inicializamos mensajes de pollas
        opcionesChatPollas = new ArrayList<>();
        mensajesPollas();
        resetMensajesPollas();

        // Inicializamos mensajes de alarmas
        opcionesAlarmasAlarma = new ArrayList<>();
        opcionesAlarmasCrono = new ArrayList<>();
        initMensajesAlarmas();
        resetMensajesAlarmas();
    }

    public static MensajesData getInstance() {
        if (instance == null) {
            instance = new MensajesData();
        }
        return instance;
    }

    private Enemigo getRandomEnemyOfType(RenderHUDComponents renderHUDComponents, Class<?> enemyClass) {
        Array<Enemigo> filtered = new Array<>();
        for (Enemigo e : renderHUDComponents.getControladorEnemigos().getEnemigos()) {
            if (enemyClass.isInstance(e)) {
                filtered.add(e);
            }
        }
        return filtered.size > 0 ? filtered.random() : null;
    }

    private void resetMensajesCulos() {
        mensajesCulosPorMostrar = new ArrayList<>(opcionesChatCulos);
        Collections.shuffle(mensajesCulosPorMostrar);
        nextMessageCulosIndex = 0;
        lastCuloMessageTime = 0;
    }

    private void resetMensajesPollas() {
        mensajesPollasPorMostrar = new ArrayList<>(opcionesChatPollas);
        Collections.shuffle(mensajesPollasPorMostrar);
        nextMessagePollasIndex = 0;
        lastPollaMessageTime = 0;
    }

    private void resetMensajesAlarmas() {
        mensajesAlarmasAlarmaPorMostrar = new ArrayList<>(opcionesAlarmasAlarma);
        Collections.shuffle(mensajesAlarmasAlarmaPorMostrar);
        nextMessageAlarmasAlarmaIndex = 0;
        lastAlarmaAlarmaMessageTime = 0;

        mensajesAlarmasCronoPorMostrar = new ArrayList<>(opcionesAlarmasCrono);
        Collections.shuffle(mensajesAlarmasCronoPorMostrar);
        nextMessageAlarmasCronoIndex = 0;
        lastAlarmaCronoMessageTime = 0;
    }

    public void mostrarMensajeCulos(RenderHUDComponents renderHUDComponents) {
        if (renderHUDComponents.getTiempoTranscurrido() >= 10 && !mensajeCulosActivado) {
            Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoCulo.class);
            if (enemy != null) {
                ChatOption option = mensajesCulosPorMostrar.getFirst();
                Mensajes.getInstance().addEnemyMessage(option.nombre, option.mensaje, enemy);
                nextMessageCulosIndex = 1;
                lastCuloMessageTime = renderHUDComponents.getTiempoTranscurrido();
            }
            mensajeCulosActivado = true;
        }
    }

    public void mostrarMensajePollas(RenderHUDComponents renderHUDComponents) {
        if (renderHUDComponents.getTiempoTranscurrido() >= 15 && !mensajesPollasActivado) {
            Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoPolla.class);
            if (enemy != null) {
                ChatOption option = mensajesPollasPorMostrar.getFirst();
                Mensajes.getInstance().addEnemyMessage(option.nombre, option.mensaje, enemy);
                nextMessagePollasIndex = 1;
                lastPollaMessageTime = renderHUDComponents.getTiempoTranscurrido();
            }
            mensajesPollasActivado = true;
        }
    }


    public void mostrarMensajeAlarmas(RenderHUDComponents renderHUDComponents) {
        if (renderHUDComponents.getTiempoTranscurrido() >= 20 && !mensajeAlarmasActivado) {
            Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoAlarma.class);
            if (enemy != null) {
                EnemigoAlarma alarma = (EnemigoAlarma) enemy;
                ChatOption option;
                if (!alarma.isEsCrono()) {
                    option = mensajesAlarmasAlarmaPorMostrar.getFirst();
                    nextMessageAlarmasAlarmaIndex = 1;
                    lastAlarmaAlarmaMessageTime = renderHUDComponents.getTiempoTranscurrido();
                } else {
                    option = mensajesAlarmasCronoPorMostrar.getFirst();
                    nextMessageAlarmasCronoIndex = 1;
                    lastAlarmaCronoMessageTime = renderHUDComponents.getTiempoTranscurrido();
                }
                Mensajes.getInstance().addEnemyMessage(option.nombre, option.mensaje, enemy);
            }
            mensajeAlarmasActivado = true;
        }
    }

    public void updateCulos(RenderHUDComponents renderHUDComponents) {
        delayBetweenMessages = MathUtils.random(10, 20f);
        if (mensajeCulosActivado && nextMessageCulosIndex < mensajesCulosPorMostrar.size()) {
            float currentTime = renderHUDComponents.getTiempoTranscurrido();
            if (currentTime - lastCuloMessageTime >= delayBetweenMessages) {
                Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoCulo.class);
                if (enemy != null) {
                    ChatOption option = mensajesCulosPorMostrar.get(nextMessageCulosIndex);
                    Mensajes.getInstance().addEnemyMessage(option.nombre, option.mensaje, enemy);
                    nextMessageCulosIndex++;
                    lastCuloMessageTime = currentTime;
                }
            }
        }
    }

    public void updatePollas(RenderHUDComponents renderHUDComponents) {
        delayBetweenMessages = MathUtils.random(15, 30f);
        if (mensajesPollasActivado && nextMessagePollasIndex < mensajesPollasPorMostrar.size()) {
            float currentTime = renderHUDComponents.getTiempoTranscurrido();
            if (currentTime - lastPollaMessageTime >= delayBetweenMessages) {
                Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoPolla.class);
                if (enemy != null) {
                    ChatOption option = mensajesPollasPorMostrar.get(nextMessagePollasIndex);
                    Mensajes.getInstance().addEnemyMessage(option.nombre, option.mensaje, enemy);
                    nextMessagePollasIndex++;
                    lastPollaMessageTime = currentTime;
                }
            }
        }
    }

    public void updateAlarmas(RenderHUDComponents renderHUDComponents) {
        float currentTime = renderHUDComponents.getTiempoTranscurrido();
        // Actualización para alarma
        if (mensajeAlarmasActivado && nextMessageAlarmasAlarmaIndex < mensajesAlarmasAlarmaPorMostrar.size()) {
            float delay = MathUtils.random(10f, 15f);
            if (currentTime - lastAlarmaAlarmaMessageTime >= delay) {
                Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoAlarma.class);
                if (enemy != null) {
                    EnemigoAlarma alarma = (EnemigoAlarma) enemy;
                    if (!alarma.isEsCrono()) {
                        ChatOption option = mensajesAlarmasAlarmaPorMostrar.get(nextMessageAlarmasAlarmaIndex);
                        Mensajes.getInstance().addEnemyMessage(option.nombre, option.mensaje, enemy);
                        nextMessageAlarmasAlarmaIndex++;
                        lastAlarmaAlarmaMessageTime = currentTime;
                    }
                }
            }
        }
        // Actualización para crono
        if (mensajeAlarmasActivado && nextMessageAlarmasCronoIndex < mensajesAlarmasCronoPorMostrar.size()) {
            float delay = MathUtils.random(10f, 15f);
            if (currentTime - lastAlarmaCronoMessageTime >= delay) {
                Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoAlarma.class);
                if (enemy != null) {
                    EnemigoAlarma alarma = (EnemigoAlarma) enemy;
                    if (alarma.isEsCrono()) {
                        ChatOption option = mensajesAlarmasCronoPorMostrar.get(nextMessageAlarmasCronoIndex);
                        Mensajes.getInstance().addEnemyMessage(option.nombre, option.mensaje, enemy);
                        nextMessageAlarmasCronoIndex++;
                        lastAlarmaCronoMessageTime = currentTime;
                    }
                }
            }
        }
    }

    public void reset() {
        mensajeCulosActivado = false;
        mensajesPollasActivado = false;
        mensajeAlarmasActivado = false;
        resetMensajesCulos();
        resetMensajesPollas();
        resetMensajesAlarmas();
    }

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
        opcionesAlarmasCrono.add(new ChatOption("Crono3", "1..2..3..4..5..6..7..8..9.. <<algo está mal>>")); // comprobar si funciona el troleito
        opcionesAlarmasCrono.add(new ChatOption("Crono3", "1..2..3..4..5..6..7..8..9.. <<algo está mal>>")); // todo --> podría acabar enlazándose con el easter egg de alguna manera
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


    // La clase interna para representar una opción de mensaje
    private static class ChatOption {
        String nombre;
        String mensaje;

        public ChatOption(String nombre, String mensaje) {
            this.nombre = nombre;
            this.mensaje = mensaje;
        }
    }
}
