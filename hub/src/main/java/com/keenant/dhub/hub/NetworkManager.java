package com.keenant.dhub.hub;

import com.keenant.dhub.hub.network.Network;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ToString
public class NetworkManager {
    private final List<Network> networks = new ArrayList<>();

    public NetworkManager() {

    }

    public List<Network> getNetworks() {
        return networks;
    }

    public void register(Network network) throws IllegalArgumentException {
        if (getNetwork(network.getUniqueId()).isPresent()) {
            throw new IllegalArgumentException("Network with id " + network.getUniqueId() + " already registered.");
        }

        networks.add(network);
        network.start();
    }

    public void unregister(Network network) {
        networks.remove(network);
        network.stop();
    }

    @SuppressWarnings("unchecked")
    public <N extends Network> Optional<N> getNetwork(Class<N> type) {
        for (Network network : networks) {
            if (type.isInstance(network)) {
                return Optional.of((N) network);
            }
        }
        return Optional.empty();
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
