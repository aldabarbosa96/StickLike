package com.sticklike.core.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoPolla;
import com.sticklike.core.interfaces.Enemigo;

public class MensajesData {

    private static MensajesData instance;
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

    private float delayBetweenMessages = 0;

    private MensajesData() {
        mensajeCulosActivado = false;
        mensajesPollasActivado = false;

        opcionesChatCulos = new ArrayList<>();
        mensajesCulos();
        resetMensajesCulos();

        opcionesChatPollas = new ArrayList<>();
        mensajesPollas();
        resetMensajesPollas();
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
            // Aquí nos aseguramos de que el enemigo sea de la clase indicada.
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

    public void mostrarMensajeCulos(RenderHUDComponents renderHUDComponents) {
        if (renderHUDComponents.getTiempoTranscurrido() >= 10 && !mensajeCulosActivado) {
            Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoCulo.class);
            if (enemy != null) {
                ChatOption option = mensajesCulosPorMostrar.getFirst();
                // Usamos las coordenadas del sprite del enemigo
                Mensajes.getInstance().addEnemyMessage(option.nombre, option.mensaje,enemy);
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
                Mensajes.getInstance().addEnemyMessage(option.nombre, option.mensaje,enemy);
                nextMessagePollasIndex = 1;
                lastPollaMessageTime = renderHUDComponents.getTiempoTranscurrido();
            }
            mensajesPollasActivado = true;
        }
    }

    public void updateCulos(RenderHUDComponents renderHUDComponents) {
        delayBetweenMessages = MathUtils.random(7.5f, 15f);
        if (mensajeCulosActivado && nextMessageCulosIndex < mensajesCulosPorMostrar.size()) {
            float currentTime = renderHUDComponents.getTiempoTranscurrido();
            if (currentTime - lastCuloMessageTime >= delayBetweenMessages) {
                Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoCulo.class);
                if (enemy != null) {
                    ChatOption option = mensajesCulosPorMostrar.get(nextMessageCulosIndex);
                    Mensajes.getInstance().addEnemyMessage(option.nombre, option.mensaje,enemy);
                    nextMessageCulosIndex++;
                    lastCuloMessageTime = currentTime;
                }
            }
        }
    }

    public void updatePollas(RenderHUDComponents renderHUDComponents) {
        delayBetweenMessages = MathUtils.random(10, 20f);
        if (mensajesPollasActivado && nextMessagePollasIndex < mensajesPollasPorMostrar.size()) {
            float currentTime = renderHUDComponents.getTiempoTranscurrido();
            if (currentTime - lastPollaMessageTime >= delayBetweenMessages) {
                Enemigo enemy = getRandomEnemyOfType(renderHUDComponents, EnemigoPolla.class);
                if (enemy != null) {
                    ChatOption option = mensajesPollasPorMostrar.get(nextMessagePollasIndex);
                    Mensajes.getInstance().addEnemyMessage(option.nombre, option.mensaje,enemy);
                    nextMessagePollasIndex++;
                    lastPollaMessageTime = currentTime;
                }
            }
        }
    }

    public void reset() {
        mensajeCulosActivado = false;
        mensajesPollasActivado = false;
        resetMensajesCulos();
        resetMensajesPollas();
    }

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
        opcionesChatCulos.add(new ChatOption("Ojete13", "¡Achuuusssss! <<se le sale liquidillo>>"));
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
    }

    private void mensajesAlarmas(){ // todo --> por gestionar
        opcionesChatPollas.add(new ChatOption("Alarma1", ""));
        opcionesChatPollas.add(new ChatOption("Crono2", ""));
        opcionesChatPollas.add(new ChatOption("Alarma3", ""));
        opcionesChatPollas.add(new ChatOption("Crono4", ""));
        opcionesChatPollas.add(new ChatOption("Alarma5", ""));
        opcionesChatPollas.add(new ChatOption("Crono6", ""));
        opcionesChatPollas.add(new ChatOption("Alarma7", ""));
        opcionesChatPollas.add(new ChatOption("Crono8", ""));
        opcionesChatPollas.add(new ChatOption("Alarma9", ""));
        opcionesChatPollas.add(new ChatOption("Crono10", ""));
        opcionesChatPollas.add(new ChatOption("Alarma11", ""));
        opcionesChatPollas.add(new ChatOption("Crono12", ""));
        opcionesChatPollas.add(new ChatOption("Alarma13", ""));
        opcionesChatPollas.add(new ChatOption("Crono14", ""));
        opcionesChatPollas.add(new ChatOption("Alarma15", ""));
        opcionesChatPollas.add(new ChatOption("Crono16", ""));
        opcionesChatPollas.add(new ChatOption("Alarma17", ""));
        opcionesChatPollas.add(new ChatOption("Crono18", ""));
        opcionesChatPollas.add(new ChatOption("Alarma19", ""));
        opcionesChatPollas.add(new ChatOption("Crono20", ""));
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
