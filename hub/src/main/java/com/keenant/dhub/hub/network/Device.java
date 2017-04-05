package com.keenant.dhub.hub.network;

import java.util.List;
import java.util.Optional;

/**
 * A single device within a network.
 * @param <T> The type of features it may have.
 */
public interface Device<T extends Feature> {
    String getId();

    void load();

    void reload();

    boolean isConnected();

    boolean isReady();

    List<T> getFeatures();

    default Optional<T> getFeature(String id) {
        for (T feature : getFeatures()) {
            if (feature.getId().equals(id)) {
                return Optional.of(feature);
            }
        }
        return Optional.empty();
    }
}
