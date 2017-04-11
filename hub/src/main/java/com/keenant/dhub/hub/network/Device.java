package com.keenant.dhub.hub.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

public interface Device extends Data, Responsive {
    String getUniqueId();

    List<? extends Feature> getFeatures();

    Network getNetwork();

    Optional<Network> getSubNetwork();

    @SuppressWarnings("unchecked")
    default <T extends Feature> Optional<T> getFeature(Class<T> type) {
        for (Feature feature : getFeatures()) {
            if (type.isInstance(feature)) {
                return Optional.of((T) feature);
            }
        }
        return Optional.empty();
    }

    default Optional<Feature> getFeature(String id) {
        for (Feature feature : getFeatures()) {
            if (feature.getUniqueId().equals(id)) {
                return Optional.of(feature);
            }
        }
        return Optional.empty();
    }

    @Override
    default void respondTo(JsonElement el) {
        JsonObject json = el.getAsJsonObject();

        for (Entry<String, JsonElement> entry : json.entrySet()) {
            String name = entry.getKey();
            JsonObject data = entry.getValue().getAsJsonObject();

            getFeature(name).ifPresent((feature) -> {
                if (feature instanceof ResponsiveFeature) {
                    ResponsiveFeature responsive = (ResponsiveFeature) feature;
                    responsive.respondTo(data);
                }
            });
        }
    }

    @Override
    default JsonElement toJson() {
        JsonObject features = new JsonObject();
        for (Feature feature : getFeatures()) {
            if (feature instanceof DataFeature) {
                DataFeature data = (DataFeature) feature;
                features.add(feature.getUniqueId(), data.toJson());
            }
        }

        JsonObject json = new JsonObject();
        json.add("features", features);

        return json;
    }
}
