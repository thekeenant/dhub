package com.keenant.dhub.hub.plugins.zwave.feature;

import com.google.gson.JsonElement;
import com.keenant.dhub.core.util.JsonUtil;
import com.keenant.dhub.hub.network.feature.BinaryFeature;
import com.keenant.dhub.hub.plugins.zwave.ZDevice;
import com.keenant.dhub.hub.plugins.zwave.ZFeature;
import com.keenant.dhub.hub.plugins.zwave.ZPoll;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd.Report;
import lombok.ToString;

import java.util.Optional;

@ToString(exclude = "device")
public class BinaryZFeature extends BinaryFeature implements ZFeature {
    private final ZDevice device;

    /**
     * The last known state of the binary (null = unknown).
     */
    private Boolean latestValue;

    private ZPoll poll;

    public BinaryZFeature(ZDevice device) {
        this.device = device;
        this.poll = new ZPoll(this::updateState);
    }

    @Override
    public Optional<Boolean> getState() {
        return Optional.ofNullable(latestValue);
    }

    @Override
    public void updateState() {
        latestValue = device.send(CmdClass.SWITCH_BINARY.get())
                .map(Report::getValue)
                .orElse(null);
    }

    @Override
    public void setState(boolean state) {
        device.send(CmdClass.SWITCH_BINARY.set(state));
        updateState();
    }

    @Override
    public void start() {
        updateState();
        poll.start();
    }

    @Override
    public void stop() {
        poll.stop();
    }

    @Override
    public JsonElement toJson() {
        return JsonUtil.merge(super.toJson().getAsJsonObject(), poll.toJson().getAsJsonObject());
    }

    @Override
    public void respondTo(JsonElement el) {
        poll.respondTo(el);
        super.respondTo(el);
    }
}
