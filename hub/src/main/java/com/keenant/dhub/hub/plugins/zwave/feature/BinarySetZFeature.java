package com.keenant.dhub.hub.plugins.zwave.feature;

import com.keenant.dhub.hub.network.binary.BinarySetFeature;
import com.keenant.dhub.hub.plugins.zwave.ZFeature;
import com.keenant.dhub.hub.plugins.zwave.ZNode;
import com.keenant.dhub.zwave.CmdClass;
import lombok.ToString;

@ToString
public class BinarySetZFeature extends ZFeature implements BinarySetFeature {
    public BinarySetZFeature(ZNode node) {
        super(node);
    }

    @Override
    public void setBinary(boolean value) {
        getNode().send(CmdClass.SWITCH_BINARY.set(value));
    }
}
