package com.keenant.dhub.hub.plugins.zwave;

import com.keenant.dhub.hub.network.ability.BooleanAbility;
import com.keenant.dhub.zwave.CmdClass;
import lombok.ToString;

@ToString(callSuper = true)
public class ZSwitchBinaryAbility extends BooleanAbility {
    public ZSwitchBinaryAbility(ZDevice device, ZSwitchBinaryProvider provider) {
        super((value) -> {
            device.send(CmdClass.SWITCH_BINARY.set(value));
            provider.update();
        });
    }
}
