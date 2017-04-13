package com.keenant.dhub.hub;

import com.keenant.dhub.hub.network.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NetworkManager {
    private final List<Network> networks = new ArrayList<>();

    public NetworkManager() {

    }

    public void register(Network network) {
        networks.add(network);
        network.start();
    }

    public void unregister(Network network) {
        networks.remove(network);
        network.stop();
    }

    public Optional<Network> getNetwork(String id) {
        return networks.stream()
                .filter((network) -> network.getUniqueId().equals(id))
                .findAny();
    }

    public void stopAll() {
        networks.forEach(Network::stop);
    }
}
