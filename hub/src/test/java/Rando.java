import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.hub.Reaction;
import com.keenant.dhub.hub.action.*;
import com.keenant.dhub.hub.network.NetworkListener;
import com.keenant.dhub.hub.network.ability.BooleanAbility;
import com.keenant.dhub.hub.network.event.ProviderChangeEvent;
import com.keenant.dhub.hub.network.provider.BooleanProvider;
import com.keenant.dhub.hub.network.provider.DateTimeProvider;
import com.keenant.dhub.hub.network.rules.BooleanTrueRule;
import com.keenant.dhub.hub.network.rules.TimeOfDayRule;
import com.keenant.dhub.hub.plugins.clock.ClockDevice;
import com.keenant.dhub.hub.plugins.clock.ClockNetwork;
import com.keenant.dhub.hub.plugins.dummy.DummyDevice;
import com.keenant.dhub.hub.plugins.dummy.DummyNetwork;
import com.keenant.dhub.hub.plugins.zwave.ZDevice;
import com.keenant.dhub.hub.plugins.zwave.ZNetwork;
import net.engio.mbassy.listener.Handler;
import org.junit.Test;

import java.time.ZoneId;
import java.util.logging.Logger;

public class Rando implements NetworkListener {
    @Test
    public void znetwork() {
        ClockNetwork clocks = new ClockNetwork();
        clocks.start();
        ClockDevice clock = new ClockDevice(clocks, ZoneId.systemDefault());
        clocks.addDevice(clock);
        DateTimeProvider timeProvider = clock.getProvider(DateTimeProvider.class).orElse(null);

        ZNetwork network = null;

        for (SerialPort port : SerialPort.getCommPorts()) {
            if (!port.getSystemPortName().startsWith("ttyA"))
                continue;
            network = new ZNetwork(port, Logger.getLogger("test"));
            network.start();
        }

        if (network == null) {
            System.out.println("No networks.");
            return;
        }

        network.subscribe(this);

        ZDevice device1 = network.getDevices().get(0);
        BooleanProvider provider1 = device1.getProvider(BooleanProvider.class).orElse(null);
        BooleanAbility ability1 = device1.getAbility(BooleanAbility.class).orElse(null);

        ZDevice device2 = network.getDevices().get(2);
        BooleanProvider provider2 = device2.getProvider(BooleanProvider.class).orElse(null);
        BooleanAbility ability2 = device2.getAbility(BooleanAbility.class).orElse(null);

        Action actionIf = new AbilityAction<>(ability2, true);
        StrictAction strictIf = new StrictAction(actionIf, () -> provider2.get().orElse(false));
        Action actionElse = new AbilityAction<>(ability2, false);
        StrictAction strictElse = new StrictAction(actionElse, () -> !provider2.get().orElse(false));

        IfElseAction action = new IfElseAction(new BooleanTrueRule(provider1), strictIf, strictElse);

        Reaction reaction = new Reaction(timeProvider, action);
        new Thread(() -> {
            new Thread(() -> {
                reaction.execute();
            }).start();
        }).start();


        for (int i = 0; i < 50; i++) {
            ability1.perform(!provider1.get().orElse(false));
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {

            }
        }
    }

    @Handler
    public void onEvent(ProviderChangeEvent event) {
        System.out.println(event.getDevice() + " ... " + event.getProvider());
    }

    @Test
    public void test() {
        ClockNetwork clocks = new ClockNetwork();
        clocks.start();

        ClockDevice clock = new ClockDevice(clocks, ZoneId.systemDefault());
        clocks.addDevice(clock);


        DummyNetwork dummies = new DummyNetwork();
        dummies.start();

        DummyDevice dummy = new DummyDevice(dummies);
        dummies.addDevice(dummy);

        BooleanAbility binaryAbility = dummy.getAbility(BooleanAbility.class).orElse(null);
        BooleanProvider binaryProvider = dummy.getProvider(BooleanProvider.class).orElse(null);

        DateTimeProvider timeProvider = clock.getProvider(DateTimeProvider.class).orElse(null);
        TimeOfDayRule timeRule = new TimeOfDayRule(timeProvider, 21, 43, 0, 23, 23, 0);

        // Aciton that sets binaryAbility to true
        AbilityAction<Boolean> toTrue = new AbilityAction<>(binaryAbility, true);
        AbilityAction<Boolean> toFalse = new AbilityAction<>(binaryAbility, false);

        LogAction log = new LogAction(() -> "Value = " + binaryProvider.get());
        WaitAction delay = new WaitAction(2);

        IfElseAction ifElse = new IfElseAction(timeRule, toTrue, toFalse);

        new Thread(new Runnable() {
            @Override
            public void run() {
                toFalse.execute();
                log.execute();

                System.out.println("executing...");
                while (true) {
                    Reaction action = new Reaction(timeProvider, new ActionSet(toTrue, log));
                    action.execute();
                }
            }
        }).start();

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
