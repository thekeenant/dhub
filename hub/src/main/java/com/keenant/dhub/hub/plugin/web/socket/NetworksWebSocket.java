package com.keenant.dhub.hub.plugin.web.socket;

import com.google.gson.JsonObject;
import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.action.ProviderChangeAction;
import com.keenant.dhub.hub.network.Feature;
import com.keenant.dhub.hub.network.Network;
import com.keenant.dhub.hub.network.NetworkListener;
import com.keenant.dhub.hub.network.event.ProviderChangeEvent;
import com.keenant.dhub.hub.plugin.web.WebSocketClient;
import com.keenant.dhub.hub.plugin.web.WebSocketServer;
import com.keenant.dhub.hub.plugin.web.socket.NetworksWebSocket.Client;
import net.engio.mbassy.listener.Handler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class NetworksWebSocket extends WebSocketServer<Client> {
    @Override
    protected Client createClient(Session session) {
        String path = session.getUpgradeRequest().getRequestURI().getPath();
        String[] pathSplit = path.split("/");

        String networkId = pathSplit[pathSplit.length - 1];

        Network network = Hub.getHub().getNetworkManager().getNetwork(networkId).orElseThrow(RuntimeException::new);

        return new Client(session, network);
    }

    public static class Client extends WebSocketClient implements NetworkListener {
        private final Network network;

        public Client(Session session, Network network) {
            super(session);
            this.network = network;
        }

        @Override
        public void onReceive(JsonObject data) {

        }

        @Handler
        public void onProviderChange(ProviderChangeEvent event) {
            JsonObject json = new JsonObject();
            json.addProperty("network", event.getProvider().getDevice().getNetwork().getUniqueId());
            json.addProperty("device", event.getProvider().getDevice().getUniqueId());
            if (event.getProvider() instanceof Feature) {
                json.addProperty("feature", event.getProvider().getUniqueId());
            }
            else {
                json.addProperty("provider", event.getProvider().getUniqueId());
            }
            json.add("value", event.getProvider().jsonGet());

            send(json);
        }

        @Override
        public void onOpen() {
            network.subscribe(this);
        }

        @Override
        public void onClose(int code, String reason) {
            network.unsubscribe(this);
        }
    }
}
