package com.keenant.dhub.hub.plugins.zwave.ability;

import com.keenant.dhub.hub.network.ability.IntegerAbility;
import com.keenant.dhub.hub.plugins.zwave.ZDevice;
import com.keenant.dhub.hub.plugins.zwave.provider.ZMultilevelProvider;
import com.keenant.dhub.zwave.CmdClass;

public class ZMultilevelAbility extends IntegerAbility {
    public ZMultilevelAbility(ZDevice device, ZMultilevelProvider provider) {
        super((level) -> {
            device.send(CmdClass.SWITCH_MULTILEVEL.set(level));
            provider.update();
        }, 0, 99);
    }
}
