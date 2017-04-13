package com.keenant.dhub.hub.plugins.zwave.ability;

import com.keenant.dhub.hub.network.ability.BooleanAbility;
import com.keenant.dhub.hub.plugins.zwave.ZDevice;
import com.keenant.dhub.hub.plugins.zwave.provider.ZBinaryProvider;
import com.keenant.dhub.zwave.CmdClass;
import lombok.ToString;

@ToString(callSuper = true)
public class ZBinaryAbility extends BooleanAbility {
    public ZBinaryAbility(ZDevice device, ZBinaryProvider provider) {
        super((value) -> {
            device.send(CmdClass.SWITCH_BINARY.set(value));
            provider.update();
        });
    }
}
