package com.sticklike.core.ui;

import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MensajesChatData {

    private static MensajesChatData instance;

    // Estados y listas para mensajes de "culos"
    private boolean mensajeCulosActivado;
    private List<ChatOption> opcionesChatCulos;
    private List<ChatOption> mensajesCulosPorMostrar;
    private int nextMessageCulosIndex;
    private float lastCuloMessageTime;

    // Estados y listas para mensajes de "pollas"
    private boolean mensajesPollasActivado;
    private List<ChatOption> opcionesChatPollas;
    private List<ChatOption> mensajesPollasPorMostrar;
    private int nextMessagePollasIndex;
    private float lastPollaMessageTime;

    // Delay común para ambos (podrías separarlo si fuera necesario)
    private float delayBetweenMessages = 0;

    private MensajesChatData() {
        mensajeCulosActivado = false;
        mensajesPollasActivado = false;

        // Inicializamos y llenamos las listas de mensajes para "culos"
        opcionesChatCulos = new ArrayList<>();
        mensajesCulos();
        resetMensajesCulos();

        // Inicializamos y llenamos las listas de mensajes para "pollas"
        opcionesChatPollas = new ArrayList<>();
        mensajesPollas();
        resetMensajesPollas();
    }

    public static MensajesChatData getInstance() {
        if (instance == null) {
            instance = new MensajesChatData();
        }
        return instance;
    }

    // Reinicia la lista de mensajes de "culos"
    private void resetMensajesCulos() {
        mensajesCulosPorMostrar = new ArrayList<>(opcionesChatCulos);
        Collections.shuffle(mensajesCulosPorMostrar);
        nextMessageCulosIndex = 0;
        lastCuloMessageTime = 0;
    }

    // Reinicia la lista de mensajes de "pollas"
    private void resetMensajesPollas() {
        mensajesPollasPorMostrar = new ArrayList<>(opcionesChatPollas);
        Collections.shuffle(mensajesPollasPorMostrar);
        nextMessagePollasIndex = 0;
        lastPollaMessageTime = 0;
    }

    // Muestra el primer mensaje de "culos" y activa el flujo
    public void mostrarMensajeCulos(RenderHUDComponents renderHUDComponents) {
        if (renderHUDComponents.getTiempoTranscurrido() >= 10 && !mensajeCulosActivado) {
            if (!mensajesCulosPorMostrar.isEmpty()) {
                ChatOption option = mensajesCulosPorMostrar.get(0);
                MensajesChat.getInstance().addMessage(option.nombre, option.mensaje);
                nextMessageCulosIndex = 1;
                lastCuloMessageTime = renderHUDComponents.getTiempoTranscurrido();
            }
            mensajeCulosActivado = true;
        }
    }

    // Muestra el primer mensaje de "pollas" y activa el flujo
    public void mostrarMensajePollas(RenderHUDComponents renderHUDComponents) {
        if (renderHUDComponents.getTiempoTranscurrido() >= 10 && !mensajesPollasActivado) {
            if (!mensajesPollasPorMostrar.isEmpty()) {
                ChatOption option = mensajesPollasPorMostrar.get(0);
                MensajesChat.getInstance().addMessage(option.nombre, option.mensaje);
                nextMessagePollasIndex = 1;
                lastPollaMessageTime = renderHUDComponents.getTiempoTranscurrido();
            }
            mensajesPollasActivado = true;
        }
    }

    // Actualiza y muestra los mensajes de "culos" conforme pasa el tiempo
    public void updateCulos(RenderHUDComponents renderHUDComponents) {
        delayBetweenMessages = MathUtils.random(10f, 15f);
        if (mensajeCulosActivado && nextMessageCulosIndex < mensajesCulosPorMostrar.size()) {
            float currentTime = renderHUDComponents.getTiempoTranscurrido();
            if (currentTime - lastCuloMessageTime >= delayBetweenMessages) {
                ChatOption option = mensajesCulosPorMostrar.get(nextMessageCulosIndex);
                MensajesChat.getInstance().addMessage(option.nombre, option.mensaje);
                nextMessageCulosIndex++;
                lastCuloMessageTime = currentTime;
            }
        }
    }

    // Actualiza y muestra los mensajes de "pollas" conforme pasa el tiempo
    public void updatePollas(RenderHUDComponents renderHUDComponents) {
        delayBetweenMessages = MathUtils.random(17.5f, 26.25f);
        if (mensajesPollasActivado && nextMessagePollasIndex < mensajesPollasPorMostrar.size()) {
            float currentTime = renderHUDComponents.getTiempoTranscurrido();
            if (currentTime - lastPollaMessageTime >= delayBetweenMessages) {
                ChatOption option = mensajesPollasPorMostrar.get(nextMessagePollasIndex);
                MensajesChat.getInstance().addMessage(option.nombre, option.mensaje);
                nextMessagePollasIndex++;
                lastPollaMessageTime = currentTime;
            }
        }
    }

    // Reinicia ambos flujos de mensajes
    public void reset() {
        mensajeCulosActivado = false;
        mensajesPollasActivado = false;
        resetMensajesCulos();
        resetMensajesPollas();
    }

    // Define los mensajes disponibles para "culos"
    private void mensajesCulos() {
        opcionesChatCulos.add(new ChatOption("Ojete 1", "¡Todos a dejarle la cara de culo!"));
        opcionesChatCulos.add(new ChatOption("Ojoete 2", "¡Mira mis almorranas!"));
        opcionesChatCulos.add(new ChatOption("Ojete 3", "¡¡¡Mira sus almorranas!!!"));
        opcionesChatCulos.add(new ChatOption("Ojoete 4", "¡Cagadle en el pecho!"));
        opcionesChatCulos.add(new ChatOption("Ojete 5", "¡Cagadle en la cara!"));
        opcionesChatCulos.add(new ChatOption("Ojoete 6", "Yo me llamo Ralph"));
        opcionesChatCulos.add(new ChatOption("Ojete 7", "Caca, culo, pedo, pis, nalga"));
        opcionesChatCulos.add(new ChatOption("Ojoete 8", "¡Me cago en tu estampa!"));
        opcionesChatCulos.add(new ChatOption("Ojete 9", "¿Quién quiere caquita?"));
        opcionesChatCulos.add(new ChatOption("Ojoete 10", "1 StickMans 2 cups"));
        opcionesChatCulos.add(new ChatOption("Ojete 11", "¡Palmea mis nalgas!"));
        opcionesChatCulos.add(new ChatOption("Ojoete 12", "¿Tienes cara de culo o culo de cara?"));
        opcionesChatCulos.add(new ChatOption("Ojete 13", "Prrrraapprr pprrprprr prraaaA <<se le sale el liquidillo>>"));
        opcionesChatCulos.add(new ChatOption("Ojoete 14", "¡Nalgas a mí!"));
        opcionesChatCulos.add(new ChatOption("Ojete 15", "¡Pedos fuera!"));
        opcionesChatCulos.add(new ChatOption("Ojoete 16", "A veces me confunden con Elon Musk..."));
        opcionesChatCulos.add(new ChatOption("Ojete 17", "¡Choca esas nalgas!"));
        opcionesChatCulos.add(new ChatOption("Ojoete 18", "Un, dos, tres ¡Pedo, pastel, diarrea!"));
        opcionesChatCulos.add(new ChatOption("Ojete 19", "¡¿Dónde está mi caca?!"));
        opcionesChatCulos.add(new ChatOption("Ojoete 20", "Culet culet"));
    }

    // Define los mensajes disponibles para "pollas"
    private void mensajesPollas() {
        opcionesChatPollas.add(new ChatOption("Polla 1", "¡Chúpame!"));
        opcionesChatPollas.add(new ChatOption("Polla 2", "¡Po po polla!"));
        opcionesChatPollas.add(new ChatOption("Polla 3", "No me toques los huevos..."));
        opcionesChatPollas.add(new ChatOption("Polla 4", "¡Meadle la boca!"));
        opcionesChatPollas.add(new ChatOption("Polla 5", "¡Correos en su cara!"));
        opcionesChatPollas.add(new ChatOption("Polla 6", "Mi gato se llama guantes"));
        opcionesChatPollas.add(new ChatOption("Polla 7", "Pene, cipote, tula, nardo, anacardo"));
        opcionesChatPollas.add(new ChatOption("Polla 8", "Y yo soy un clítoris"));
        opcionesChatPollas.add(new ChatOption("Polla 9", "¿Quién quiere lefita?"));
        opcionesChatPollas.add(new ChatOption("Polla 10", "¿Te gusta la merladura?"));
        opcionesChatPollas.add(new ChatOption("Polla 11", "¿Te traga er seme?"));
        opcionesChatPollas.add(new ChatOption("Polla 12", "¿Alguien tiene ruedas?"));
        opcionesChatPollas.add(new ChatOption("Polla 13", "Creo que tengo varicocele..."));
        opcionesChatPollas.add(new ChatOption("Polla 14", "Menudo escrotinio"));
        opcionesChatPollas.add(new ChatOption("Polla 15", "Tengo dierna"));
        opcionesChatPollas.add(new ChatOption("Polla 16", "A veces me confunden con Donald Trump..."));
        opcionesChatPollas.add(new ChatOption("Polla 17", "¡Choca ese escroto!"));
        opcionesChatPollas.add(new ChatOption("Polla 18", "Un, dos, tres ¡Polla, motel, gonorrea!"));
        opcionesChatPollas.add(new ChatOption("Polla 19", "¡¿Dónde está mi caca?!"));
        opcionesChatPollas.add(new ChatOption("Polla 20", "Tula llevas"));
    }

    // Clase interna para representar una opción de mensaje
    private static class ChatOption {
        String nombre;
        String mensaje;

        public ChatOption(String nombre, String mensaje) {
            this.nombre = nombre;
            this.mensaje = mensaje;
        }
    }
}
