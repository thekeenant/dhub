import com.keenant.dhub.hub.Hub;
import com.keenant.dhub.hub.Reaction;
import com.keenant.dhub.hub.action.Action;
import com.keenant.dhub.hub.action.IfElseAction;
import com.keenant.dhub.hub.action.LogAction;
import com.keenant.dhub.hub.network.rules.TimeIntervalRule;
import com.keenant.dhub.hub.plugin.clock.ClockDevice;
import com.keenant.dhub.hub.plugin.clock.ClockNetwork;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import org.junit.Test;

import java.util.logging.Logger;

public class FeatureTests {
    @Test
    public void test() {
        Logger logger = Logger.getLogger("Hub");
        Hub hub = new Hub(logger);
        hub.load();
        hub.start();

        System.out.println(hub.getNetworkManager().getNetworks());

        ClockNetwork clocks = hub.getNetworkManager().getNetwork(ClockNetwork.class).orElse(null);
        ClockDevice clock = clocks.getDevices().get(0);


        Action log = new LogAction(() -> clock.getProvider().get().toString());

        IfElseAction action = new IfElseAction(new TimeIntervalRule(clock.getProvider(), 5), log, null);

        hub.getReactionManager().register(new Reaction(clock.getProvider(), action));


        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }
    }
}
