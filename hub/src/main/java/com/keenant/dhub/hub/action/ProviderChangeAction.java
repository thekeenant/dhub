package com.keenant.dhub.hub.action;

import com.keenant.dhub.hub.network.NetworkListener;
import com.keenant.dhub.hub.network.Provider;
import com.keenant.dhub.hub.network.event.ProviderChangeEvent;
import net.engio.mbassy.listener.Handler;

public class ProviderChangeAction implements Action, NetworkListener {
    private final Provider provider;
    private final Action action;

    private boolean changed;
    private boolean stopped;

    public ProviderChangeAction(Provider provider, Action action) {
        this.provider = provider;
        this.action = action;
    }

    @Override
    public void execute() {
        // Subscribe
        provider.getDevice().getNetwork().subscribe(this);

        changed = false;
        stopped = false;

        // Wait until an event is received, or we are told to stop.
        while (!changed && !stopped) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Unsubscribe
        provider.getDevice().getNetwork().unsubscribe(this);

        if (!stopped) {
            // Execute action
            action.execute();
        }
    }

    @Override
    public void stop() {
        stopped = true;
        action.stop();
    }

    @Handler
    public void onProviderChange(ProviderChangeEvent event) {
        if (!event.getProvider().equals(provider)) {
            return;
        }

        changed = true;
    }
}
