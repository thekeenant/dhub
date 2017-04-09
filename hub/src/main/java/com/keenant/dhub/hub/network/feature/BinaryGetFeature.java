package com.keenant.dhub.hub.network.feature;

import com.keenant.dhub.hub.network.Feature;

public interface BinaryGetFeature extends Feature {
    boolean getValue();

    void requestUpdate();
}
