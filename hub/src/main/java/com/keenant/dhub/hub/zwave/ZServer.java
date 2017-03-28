package com.keenant.dhub.hub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.core.logging.Logging;
import com.keenant.dhub.core.util.Listener;
import com.keenant.dhub.hub.Server;
import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.event.TransactionCompleteEvent;
import com.keenant.dhub.zwave.event.message.MemoryGetIdEvent;
import com.keenant.dhub.zwave.messages.InitDataMsg;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg;
import com.keenant.dhub.zwave.messages.VersionMsg;
import lombok.ToString;
import net.engio.mbassy.listener.Handler;

import java.util.*;
import java.util.logging.Logger;

@ToString
public class ZServer implements Server {
    private static final Logger log = Logging.getLogger("ZServer");

    private List<Controller> controllers;
    private boolean started;

    public Optional<Controller> getByName(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
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
     * @throws IllegalStateException If the server is already started.
     */
    public void init(Collection<Controller> controllers) throws IllegalStateException {
        if (started) {
            throw new IllegalStateException("Server already started.");
        }
        this.controllers = new ArrayList<>();
        this.controllers.addAll(controllers);
    }

    public void init(Controller... controllers) {
        init(Arrays.asList(controllers));
    }

    @Override
    public void start() {
        if (controllers == null) {
            throw new IllegalStateException("Server not initialized.");
        }
        else if (started) {
            throw new IllegalStateException("Server already started.");
        }
        started = true;

        controllers.forEach(controller -> {
            controller.start();
            controller.subscribe(new ZServerListener());

            controller.send(VersionMsg.get());
            controller.send(MemoryGetIdMsg.get());
            controller.send(InitDataMsg.get());
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
        @Handler
        public void onTransactionComplete(TransactionCompleteEvent event) {
            log.info(event + " Complete");
        }

        @Handler
        public void onMemoryGetIdEvent(MemoryGetIdEvent event) {
            long homeId = event.getMessage().getHomeId();
            int nodeId = event.getMessage().getNodeId();

            log.info("---" + event.getController() + "---");
            log.info("Home ID: " + homeId);
            log.info("Node ID: " + nodeId);
        }
    }
}
