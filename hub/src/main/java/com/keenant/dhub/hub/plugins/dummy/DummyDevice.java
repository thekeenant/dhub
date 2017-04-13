package com.keenant.dhub.hub.plugins.dummy;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.hub.network.ability.BooleanAbility;
import com.keenant.dhub.hub.network.provider.BooleanProvider;
import lombok.ToString;

import java.util.Optional;

@ToString
public class DummyDevice extends Device<DummyNetwork> {
    private boolean binaryState;
    private int level;

    public DummyDevice(DummyNetwork network) {
        super(network);
    }

    @Override
    public void start() {
        BooleanProvider binary = addProvider(new BooleanProvider(this, () -> Optional.of(binaryState)));
        addAbility(new BooleanAbility((value) -> {
            binaryState = value;
            binary.update();
        }));


    }

    @Override
    public void stop() {

    }
}
