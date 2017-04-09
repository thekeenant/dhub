package com.keenant.dhub.hub.plugins.zwave.feature;

import com.keenant.dhub.hub.network.feature.BinarySetFeature;
import com.keenant.dhub.hub.plugins.zwave.ZDevice;
import com.keenant.dhub.hub.plugins.zwave.ZFeature;
import com.keenant.dhub.zwave.CmdClass;

public class BinarySetZFeature implements BinarySetFeature, ZFeature {
    private final ZDevice device;

    public BinarySetZFeature(ZDevice device) {
        this.device = device;
    }

    public void start() {

    }

    public void stop() {

    }

    public void setValue(boolean value) {
        device.send(CmdClass.SWITCH_BINARY.set(value));
    }
}
