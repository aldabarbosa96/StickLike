package com.sticklike.core.ui;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MensajesChatData {

    private static MensajesChatData instance;
    private boolean mensajeCulosActivado;
    private List<ChatOption> opcionesChat;
    private List<ChatOption> mensajesPorMostrar;
    private int nextMessageIndex;
    private float delayBetweenMessages = 0;
    private float lastMessageTime;

    private MensajesChatData() {
        mensajeCulosActivado = false;
        opcionesChat = new ArrayList<ChatOption>();
        // Agregamos todas las opciones de mensajes disponibles
        opcionesChat.add(new ChatOption("Ojete 1", "¡Todos a dejarle la cara de culo!"));
        opcionesChat.add(new ChatOption("Ojete 2", "¡Mira mis almorranas!"));
        opcionesChat.add(new ChatOption("Ojete 3", "¡¡¡Mira sus almorranas!!!"));
        opcionesChat.add(new ChatOption("Ojete 4", "¡Cagadle en el pecho!"));
        opcionesChat.add(new ChatOption("Ojete 5", "¡Cagadle en la cara!"));
        opcionesChat.add(new ChatOption("Ojete 6", "Yo me llamo Ralph"));
        opcionesChat.add(new ChatOption("Ojete 7", "Caca, culo, pedo, pis, nalga"));
        opcionesChat.add(new ChatOption("Ojete 8", "¡Me cago en tu estampa!"));
        opcionesChat.add(new ChatOption("Ojete 9", "¿Quién quiere caquita?"));
        opcionesChat.add(new ChatOption("Ojete 10", "1 StickMans 2 cups"));
        opcionesChat.add(new ChatOption("Ojete 11", "¡Palmea mis nalgas!"));
        opcionesChat.add(new ChatOption("Ojete 12", "¿Tienes cara de culo o culo de cara?"));
        opcionesChat.add(new ChatOption("Ojete 13", "Prrrraapprr pprrprprr prraaaA <<se le sale el liquidillo>>"));
        opcionesChat.add(new ChatOption("Ojete 14", "¡Nalgas a mí!"));
        opcionesChat.add(new ChatOption("Ojete 15", "¡Pedos fuera!"));
        opcionesChat.add(new ChatOption("Ojete 16", "A veces me confunden con Elon Musk..."));
        opcionesChat.add(new ChatOption("Ojete 17", "¡Choca esas nalgas!"));
        opcionesChat.add(new ChatOption("Ojete 18", "Un, dos, tres ¡Pedo, pastel, diarrea!"));
        opcionesChat.add(new ChatOption("Ojete 19", "¡¿Dónde está mi caca?!"));
        opcionesChat.add(new ChatOption("Ojete 20", "Culet culet"));
        // Preparamos la lista de mensajes a mostrar
        resetMensajes();
    }

    public static MensajesChatData getInstance() {
        if (instance == null) {
            instance = new MensajesChatData();
        }
        return instance;
    }

    private void resetMensajes() {
        mensajesPorMostrar = new ArrayList<>(opcionesChat);
        Collections.shuffle(mensajesPorMostrar);
        nextMessageIndex = 0;
        lastMessageTime = 0;
    }

    public void mostrarMensajeCulos(RenderHUDComponents renderHUDComponents) {
        if (renderHUDComponents.getTiempoTranscurrido() >= 10 && !mensajeCulosActivado) {
            // Muestra el primer mensaje inmediatamente
            if (!mensajesPorMostrar.isEmpty()) {
                ChatOption option = mensajesPorMostrar.getFirst();
                MensajesChat.getInstance().addMessage(option.nombre, option.mensaje);
                nextMessageIndex = 1;
                lastMessageTime = renderHUDComponents.getTiempoTranscurrido();
            }
            mensajeCulosActivado = true;
        }
    }

    public void update(RenderHUDComponents renderHUDComponents) {
        delayBetweenMessages = MathUtils.random(7.5f, 12.5f);
        if (mensajeCulosActivado && nextMessageIndex < mensajesPorMostrar.size()) {
            float currentTime = renderHUDComponents.getTiempoTranscurrido();
            if (currentTime - lastMessageTime >= delayBetweenMessages) {
                ChatOption option = mensajesPorMostrar.get(nextMessageIndex);
                MensajesChat.getInstance().addMessage(option.nombre, option.mensaje);
                nextMessageIndex++;
                lastMessageTime = currentTime;
            }
        }
    }

    public void reset() {
        mensajeCulosActivado = false;
        resetMensajes();
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
