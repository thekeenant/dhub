package com.keenant.dhub.hub.plugin.web;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public abstract class WebSocketServer<T extends WebSocketClient> {
    private final Map<Session, T> clients = new HashMap<>();

    protected abstract T createClient(Session session);

    public Collection<T> getClients() {
        return clients.values();
    }

    public void broadcast(JsonObject data) {
        getClients().forEach(client -> client.send(data));
    }

    @OnWebSocketConnect
    public final void connected(Session session) {
        T client = createClient(session);
        clients.put(session, client);
        client.onOpen();
    }

    @OnWebSocketClose
    public final void closed(Session session, int statusCode, String reason) {
        T client = clients.get(session);
        client.onClose(statusCode, reason);
        clients.remove(session);
    }

    @OnWebSocketMessage
    public final void message(Session session, String message) throws IOException {
        T client = clients.get(session);

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(message).getAsJsonObject();

        client.onReceive(json);
    }
}
