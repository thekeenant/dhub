package com.keenant.dhub.hub.plugin.web;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public abstract class WebSocketClient {
    private final Session session;

    public WebSocketClient(Session session) {
        this.session = session;
    }

    public void send(JsonElement data) {
        try {
            session.getRemote().sendString(data.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void onReceive(JsonObject data);

    public abstract void onOpen();

    public abstract void onClose(int code, String reason);
}
