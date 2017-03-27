package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.google.common.eventbus.Subscribe;
import com.keenant.dhub.Server;
import com.keenant.dhub.logging.Logging;
import com.keenant.dhub.zwave.event.IncomingMessageEvent;
import com.keenant.dhub.zwave.event.Listener;
import com.keenant.dhub.zwave.event.TransactionCompleteEvent;
import com.keenant.dhub.zwave.messages.InitDataMsg;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg;
import com.keenant.dhub.zwave.messages.VersionMsg;
import com.keenant.dhub.zwave.transaction.Transaction;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@ToString
public class ZServer implements Server<ZController>, Listener {
    private static final Logger log = Logging.getLogger("ZServer");

    private List<ZController> controllers;
    private ZWatchdog watchdog;
    private boolean started;

    public ZServer() {

    }

    /**
     * Initializes this server with all the serial ports available.
     */
    @Override
    public void init() {
        List<ZController> controllers = new ArrayList<>();
        for (SerialPort port : SerialPort.getCommPorts()) {
            controllers.add(new ZController(port));
        }
        init(controllers);
    }

    /**
     * Initializes this server with specific controllers.
     * @param controllers The controllers this server should manage.
     */
    public void init(Collection<ZController> controllers) {
        if (started) {
            throw new UnsupportedOperationException("Server already started.");
        }
        this.controllers = new ArrayList<>();
        this.controllers.addAll(controllers);
        watchdog = new ZWatchdog(this);
    }

    public void init(ZController... controllers) {
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
            controller.register(this);

            controller.queue(new VersionMsg());
            controller.queue(new MemoryGetIdMsg());
            controller.queue(new InitDataMsg());

        });
    }

    @Subscribe
    public void onTransactionComplete(TransactionCompleteEvent event) {
        log.info(event + " Complete");
    }

    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event) {
        log.info(event + " Received");
    }

    @Override
    public void stop() {
        started = false;
        controllers.forEach(ZController::stop);
    }

    @Override
    public List<ZController> getControllers() {
        return controllers;
    }
}
