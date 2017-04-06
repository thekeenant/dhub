package com.keenant.dhub.hub.plugins.zwave.feature;

import com.keenant.dhub.hub.network.level.LevelSetFeature;
import com.keenant.dhub.hub.plugins.zwave.ZFeature;
import com.keenant.dhub.hub.plugins.zwave.ZNode;
import com.keenant.dhub.zwave.CmdClass;
import lombok.ToString;

@ToString
public class LevelSetZFeature extends ZFeature implements LevelSetFeature {
    public LevelSetZFeature(ZNode node) {
        super(node);
    }

    @Override
    public void setLevel(int value) {
        getNode().send(CmdClass.SWITCH_MULTILEVEL.set(value));
    }
}
