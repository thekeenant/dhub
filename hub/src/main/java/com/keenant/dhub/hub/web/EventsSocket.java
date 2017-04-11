package com.keenant.dhub.hub.web;

import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.NetworkListener;
import com.keenant.dhub.hub.event.NetworkEvent;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.network.feature.ChildrenFeature;
import com.keenant.dhub.hub.web.exception.DeviceNotFoundException;
import com.keenant.dhub.hub.web.exception.NetworkNotFoundException;
import com.keenant.dhub.hub.web.exception.UnsupportedFeatureException;
import net.engio.mbassy.listener.Handler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@WebSocket
public class EventsSocket {
    private final String basePath;
    private final List<SocketClient> clients = new ArrayList<>();

    public EventsSocket(String basePath) {
        this.basePath = basePath;
    }

    public class SocketClient implements NetworkListener {
        private final Session session;
        private final Network network;

        public SocketClient(Session session, Network network) {
            this.session = session;
            this.network = network;
        }

        @Handler
        public void onNetworkEvent(NetworkEvent event) {
            if (!event.getNetwork().equals(network)) {
                return;
            }

            try {
                session.getRemote().sendString(event.toJson().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnWebSocketConnect
    public void connected(Session session) {
        String path = session.getUpgradeRequest().getRequestURI().getPath();
        String[] split = path.split(this.basePath);
        String splat = split.length == 0 ? "" : split[split.length - 1];
        if (splat.startsWith("/")) {
            splat = splat.substring(1, splat.length());
        }

        if (splat.length() == 0) {
            return;
        }

        Network network = findNetwork(splat);

        SocketClient client = new SocketClient(session, network);
        network.subscribe(client);
    }

    private Network findNetwork(String splat) {
        String[] split = splat.split("/");
        String networkName = split[0];

        Network network = Hub.getHub().getNetwork(networkName).orElseThrow(NetworkNotFoundException::new);

        if (split.length == 2) {
            String deviceName = split[1];

            System.out.println("GOIN downt his road");

            Device device = network.getDevices().getById(deviceName).orElseThrow(DeviceNotFoundException::new);
            return (ChildrenFeature<?>) device.getFeature(ChildrenFeature.class).orElseThrow(UnsupportedFeatureException::new);
        }

        return network;
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        clients.removeIf(socketClient -> socketClient.session.equals(session));
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println(message);
    }
}

