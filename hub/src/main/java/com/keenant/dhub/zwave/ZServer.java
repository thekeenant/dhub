package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.google.common.eventbus.Subscribe;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.core.util.Listener;
import com.keenant.dhub.Server;
import com.keenant.dhub.zwave.event.TransactionCompleteEvent;
import com.keenant.dhub.zwave.event.message.MemoryGetIdEvent;
import com.keenant.dhub.zwave.messages.InitDataMsg;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg;
import com.keenant.dhub.zwave.messages.VersionMsg;
import lombok.ToString;

import java.util.*;
import java.util.logging.Logger;

@ToString
public class ZServer implements Server {
    private static final Logger log = Logging.getLogger("ZServer");

    private List<Controller> controllers;
    private ZWatchdog watchdog;
    private boolean started;

    public ZServer() {

    }

    public Optional<Controller> getByName(String name) throws NullPointerException {
        if (name == null) {
            throw new NullPointerException("Name cannot be null.");
        }
        return controllers.stream().filter(c -> c.getName().equals(name)).findAny();
    }

    /**
     * Initializes this server with all the serial ports available.
     */
    @Override
    public void init() {
        List<Controller> controllers = new ArrayList<>();
        for (SerialPort port : SerialPort.getCommPorts()) {
            controllers.add(new Controller(port));
        }
        init(controllers);
    }

    /**
     * Initializes this server with specific controllers.
     * @param controllers The controllers this server should manage.
     */
    public void init(Collection<Controller> controllers) {
        if (started) {
            throw new UnsupportedOperationException("Server already started.");
        }
        this.controllers = new ArrayList<>();
        this.controllers.addAll(controllers);
        watchdog = new ZWatchdog(this);
    }

    public void init(Controller... controllers) {
        init(Arrays.asList(controllers));
    }

    @Override
    public void start() {
        if (controllers == null) {
            throw new UnsupportedOperationException("Server not initialized.");
        }
        else if (started) {
            throw new UnsupportedOperationException("Server already started.");
        }
        started = true;

        watchdog.start();

        controllers.forEach(controller -> {
            controller.start();
            controller.register(new ZServerListener());

            controller.queue(new VersionMsg());
            controller.queue(new MemoryGetIdMsg());
            controller.queue(new InitDataMsg());
        });
    }

    @Override
    public void stop() {
        started = false;
        controllers.forEach(Controller::stop);
    }

    public List<Controller> getControllers() {
        return controllers;
    }

    private final class ZServerListener implements Listener {
        @Subscribe
        public void onTransactionComplete(TransactionCompleteEvent event) {
            log.info(event + " Complete");
        }

        @Subscribe
        public void onMemoryGetIdEvent(MemoryGetIdEvent event) {
            long homeId = event.getMessage().getHomeId();
            int nodeId = event.getMessage().getNodeId();

            log.info("---" + event.getController() + "---");
            log.info("Home ID: " + homeId);
            log.info("Node ID: " + nodeId);
        }
    }
}
