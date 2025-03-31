package com.sticklike.core.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoPolla;
import com.sticklike.core.interfaces.Enemigo;

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

    // Delay común para ambos
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

    private Enemigo getRandomEnemyOfType(RenderHUDComponents renderHUDComponents, Class<?> enemyClass) {
        Array<Enemigo> filtered = new Array<>();
        for (Enemigo e : renderHUDComponents.getControladorEnemigos().getEnemigos()) {
            // Aquí nos aseguramos de que el enemigo sea de la clase indicada.
            if (enemyClass.isInstance(e)) {
                filtered.add(e);
            }
        }
        return filtered.size > 0 ? filtered.random() : null;
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
            Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoCulo.class);
            if (enemy != null) {
                ChatOption option = mensajesCulosPorMostrar.getFirst();
                // Usamos las coordenadas del sprite del enemigo
                MensajesChat.getInstance().addEnemyMessage(option.nombre, option.mensaje,enemy);
                nextMessageCulosIndex = 1;
                lastCuloMessageTime = renderHUDComponents.getTiempoTranscurrido();
            }
            mensajeCulosActivado = true;
        }
    }

    // Muestra el primer mensaje de "pollas" y activa el flujo
    public void mostrarMensajePollas(RenderHUDComponents renderHUDComponents) {
        if (renderHUDComponents.getTiempoTranscurrido() >= 15 && !mensajesPollasActivado) {
            Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoPolla.class);
            if (enemy != null) {
                ChatOption option = mensajesPollasPorMostrar.getFirst();
                MensajesChat.getInstance().addEnemyMessage(option.nombre, option.mensaje,enemy);
                nextMessagePollasIndex = 1;
                lastPollaMessageTime = renderHUDComponents.getTiempoTranscurrido();
            }
            mensajesPollasActivado = true;
        }
    }

    // Actualiza y muestra los mensajes de "culos" conforme pasa el tiempo
    public void updateCulos(RenderHUDComponents renderHUDComponents) {
        delayBetweenMessages = MathUtils.random(7.5f, 15f);
        if (mensajeCulosActivado && nextMessageCulosIndex < mensajesCulosPorMostrar.size()) {
            float currentTime = renderHUDComponents.getTiempoTranscurrido();
            if (currentTime - lastCuloMessageTime >= delayBetweenMessages) {
                Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoCulo.class);
                if (enemy != null) {
                    ChatOption option = mensajesCulosPorMostrar.get(nextMessageCulosIndex);
                    MensajesChat.getInstance().addEnemyMessage(option.nombre, option.mensaje,enemy);
                    nextMessageCulosIndex++;
                    lastCuloMessageTime = currentTime;
                }
            }
        }
    }

    // Actualiza y muestra los mensajes de "pollas" conforme pasa el tiempo
    public void updatePollas(RenderHUDComponents renderHUDComponents) {
        delayBetweenMessages = MathUtils.random(10, 20f);
        if (mensajesPollasActivado && nextMessagePollasIndex < mensajesPollasPorMostrar.size()) {
            float currentTime = renderHUDComponents.getTiempoTranscurrido();
            if (currentTime - lastPollaMessageTime >= delayBetweenMessages) {
                Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoPolla.class);
                if (enemy != null) {
                    ChatOption option = mensajesPollasPorMostrar.get(nextMessagePollasIndex);
                    MensajesChat.getInstance().addEnemyMessage(option.nombre, option.mensaje,enemy);
                    nextMessagePollasIndex++;
                    lastPollaMessageTime = currentTime;
                }
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
    // Define los mensajes disponibles para "culos"
    private void mensajesCulos() {
        opcionesChatCulos.add(new ChatOption("Ojete1", "¡Todos a dejarle la cara de culo!"));
        opcionesChatCulos.add(new ChatOption("Ojete2", "¡Mira mis almorranas!"));
        opcionesChatCulos.add(new ChatOption("Ojete3", "¡¡¡Mira sus almorranas!!!"));
        opcionesChatCulos.add(new ChatOption("Ojete4", "¡Cagadle en el pecho!"));
        opcionesChatCulos.add(new ChatOption("Ojete5", "¡Cagadle en la cara!"));
        opcionesChatCulos.add(new ChatOption("Ojete6", "Yo me llamo Ralph"));
        opcionesChatCulos.add(new ChatOption("Ojete7", "Caca, culo, pedo, pis, nalga"));
        opcionesChatCulos.add(new ChatOption("Ojete8", "¡Me cago en tu estampa!"));
        opcionesChatCulos.add(new ChatOption("Ojete9", "¿Quién quiere caquita?"));
        opcionesChatCulos.add(new ChatOption("Ojete10", "1 StickMans 2 cups"));
        opcionesChatCulos.add(new ChatOption("Ojete11", "¡Palmea mis nalgas!"));
        opcionesChatCulos.add(new ChatOption("Ojete12", "¿Tienes cara de culo o culo de cara?"));
        opcionesChatCulos.add(new ChatOption("Ojete13", "Prrpprr pprrprpr prraaA <<se le sale el liquidillo>>"));
        opcionesChatCulos.add(new ChatOption("Ojete14", "¡Nalgas a mí!"));
        opcionesChatCulos.add(new ChatOption("Ojete15", "¡Pedos fuera!"));
        opcionesChatCulos.add(new ChatOption("Ojete16", "A veces me confunden con Elon Musk..."));
        opcionesChatCulos.add(new ChatOption("Ojete 17", "¡Choca esas nalgas!"));
        opcionesChatCulos.add(new ChatOption("Ojete 18", "Un, dos, tres ¡Pedo, pastel, diarrea!"));
        opcionesChatCulos.add(new ChatOption("Ojete19", "¡¿Dónde está mi caca?!"));
        opcionesChatCulos.add(new ChatOption("Ojete20", "Culet culet"));
    }

    // Define los mensajes disponibles para "pollas"
    private void mensajesPollas() {
        opcionesChatPollas.add(new ChatOption("Polla1", "¡Chúpame!"));
        opcionesChatPollas.add(new ChatOption("Polla2", "¡Po po polla!"));
        opcionesChatPollas.add(new ChatOption("Polla3", "No me toques los huevos..."));
        opcionesChatPollas.add(new ChatOption("Polla4", "¡Meadle la boca!"));
        opcionesChatPollas.add(new ChatOption("Polla5", "¡Correos en su cara!"));
        opcionesChatPollas.add(new ChatOption("Polla6", "Mi gato se llama guantes"));
        opcionesChatPollas.add(new ChatOption("Polla7", "Pene, cipote, tula, nardo, anacardo"));
        opcionesChatPollas.add(new ChatOption("Polla8", "Y yo soy un clítoris"));
        opcionesChatPollas.add(new ChatOption("Polla9", "¿Quién quiere lefita?"));
        opcionesChatPollas.add(new ChatOption("Polla10", "¿Te gusta la merladura?"));
        opcionesChatPollas.add(new ChatOption("Polla11", "¿Te traga er seme?"));
        opcionesChatPollas.add(new ChatOption("Polla12", "¿Alguien tiene ruedas?"));
        opcionesChatPollas.add(new ChatOption("Polla13", "Creo que tengo varicocele..."));
        opcionesChatPollas.add(new ChatOption("Polla14", "Menudo escrotinio"));
        opcionesChatPollas.add(new ChatOption("Polla15", "Tengo dierna"));
        opcionesChatPollas.add(new ChatOption("Polla16", "A veces me confunden con Donald Trump..."));
        opcionesChatPollas.add(new ChatOption("Polla17", "¡Choca ese escroto!"));
        opcionesChatPollas.add(new ChatOption("Polla18", "Un, dos, tres ¡Polla, motel, gonorrea!"));
        opcionesChatPollas.add(new ChatOption("Polla19", "¡¿Dónde está mi caca?!"));
        opcionesChatPollas.add(new ChatOption("Polla20", "Tula llevas"));
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
