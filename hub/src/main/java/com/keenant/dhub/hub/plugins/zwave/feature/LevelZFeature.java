package com.keenant.dhub.hub.plugins.zwave.feature;

import com.google.gson.JsonElement;
import com.keenant.dhub.core.util.JsonUtil;
import com.keenant.dhub.hub.network.feature.LevelFeature;
import com.keenant.dhub.hub.plugins.zwave.ZDevice;
import com.keenant.dhub.hub.plugins.zwave.ZFeature;
import com.keenant.dhub.hub.plugins.zwave.ZPoll;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.SwitchMultilevelCmd.Report;
import lombok.ToString;

import java.util.Optional;

@ToString(exclude = "device")
public class LevelZFeature extends LevelFeature implements ZFeature {
    private ZDevice device;
    private Integer latestLevel;
    private ZPoll poll;

    public LevelZFeature(ZDevice device) {
        this.device = device;
        poll = new ZPoll(this::updateLevel);
    }

    @Override
    public void start() {
        updateLevel();
        poll.start();
    }

    @Override
    public void stop() {
        poll.stop();
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

    @Override
    public JsonElement toJson() {
        return JsonUtil.merge(super.toJson().getAsJsonObject(), poll.toJson().getAsJsonObject());
    }

    @Override
    public void respondTo(JsonElement json) {
        poll.respondTo(json);
        super.respondTo(json);
    }
}
