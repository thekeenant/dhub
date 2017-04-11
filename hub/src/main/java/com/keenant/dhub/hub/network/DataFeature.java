package com.keenant.dhub.hub.network;

import com.keenant.dhub.hub.event.FeatureChangeEvent;

public interface DataFeature extends Feature, Data {
    default void publishFeatureChange() {
        FeatureChangeEvent call = new FeatureChangeEvent(this);
        getDevice().getNetwork().publish(call);
    }
}
