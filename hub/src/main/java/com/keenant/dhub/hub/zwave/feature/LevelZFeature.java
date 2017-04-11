package com.keenant.dhub.hub.zwave.feature;

import com.keenant.dhub.hub.network.feature.LevelFeature;
import com.keenant.dhub.hub.zwave.ZDevice;
import com.keenant.dhub.hub.zwave.ZFeature;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.SwitchMultilevelCmd.Report;
import lombok.ToString;

import java.util.Optional;

@ToString(exclude = "device")
public class LevelZFeature extends LevelFeature implements ZFeature {
    private ZDevice device;
    private Integer latestLevel;

    public LevelZFeature(ZDevice device) {
        this.device = device;
    }

    @Override
    public void start() {
        updateLevel();
    }

    @Override
    public void stop() {

    }

    @Override
    public void setLevel(int level) {
        device.send(CmdClass.SWITCH_MULTILEVEL.set(level));
        updateLevel();
    }

    @Override
    public Optional<Integer> getLevel() {
        return Optional.ofNullable(latestLevel);
    }

    @Override
    public void updateLevel() {
        latestLevel = device.send(CmdClass.SWITCH_MULTILEVEL.get())
                .map(Report::getCurrent)
                .orElse(null);
    }
}
