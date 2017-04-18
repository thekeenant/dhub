package com.keenant.dhub.hub.network;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ToString(exclude = "network")
public abstract class Device<T extends Network> {
    private final T network;
    private final List<Feature> features = new ArrayList<>();
    private final List<Provider> providers = new ArrayList<>();

    public Device(T network) {
        this.network = network;
    }

    public abstract void start();

    public abstract void stop();

    public abstract String getUniqueId();

    public T getNetwork() {
        return network;
    }

    protected <F extends Feature> F addFeature(F feature) {
        this.features.add(feature);
        return feature;
    }

    protected <P extends Provider> P addProvider(P provider) {
        this.providers.add(provider);
        return provider;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public Optional<Feature<?, ?>> getFeature(String id) {
        for (Feature<?, ?> feature : features) {
            if (feature.getUniqueId().equals(id)) {
                return Optional.of(feature);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <F extends Feature> Optional<F> getFeature(Class<F> type) {
        for (Feature feature : features) {
            if (type.isInstance(feature)) {
                return Optional.of((F) feature);
            }
        }
        return Optional.empty();
    }

    public Optional<Provider<?, ?>> getProvider(String id) {
        for (Provider<?, ?> provider : providers) {
            if (provider.getUniqueId().equals(id)) {
                return Optional.of(provider);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <P extends Provider> Optional<P> getProvider(Class<P> type) {
        for (Provider provider : providers) {
            if (type.isInstance(provider)) {
                return Optional.of((P) provider);
            }
        }
        return Optional.empty();
    }
}
