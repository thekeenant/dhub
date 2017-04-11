package com.keenant.dhub.hub.zwave;

import com.keenant.dhub.core.Lifecycle;
import com.keenant.dhub.core.util.ControllerListener;
import com.keenant.dhub.hub.network.Feature;
import com.keenant.dhub.hub.zwave.feature.BinaryZFeature;
import com.keenant.dhub.hub.zwave.feature.ChildrenZFeature;
import com.keenant.dhub.hub.zwave.feature.LevelZFeature;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd;
import com.keenant.dhub.zwave.cmd.SwitchMultilevelCmd;

import java.util.Optional;

public interface ZFeature extends Feature, ControllerListener, Lifecycle {
    static Optional<ZFeature> fromCmdClass(ZDevice device, CmdClass cmdClass) {
        if (cmdClass instanceof SwitchBinaryCmd) {
            return Optional.of(new BinaryZFeature(device));
        }
        else if (cmdClass instanceof MultiChannelCmd) {
            if (device instanceof ZNode) {
                return Optional.of(new ChildrenZFeature((ZNode) device));
            }
        }
        else if (cmdClass instanceof SwitchMultilevelCmd) {
            return Optional.of(new LevelZFeature(device));
        }

        return Optional.empty();
    }
}
