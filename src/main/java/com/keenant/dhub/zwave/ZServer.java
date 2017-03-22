package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.Server;

import java.util.ArrayList;
import java.util.List;

public class ZServer implements Server<ZController> {
    private final List<ZController> controllers;

    public ZServer() {
        controllers = new ArrayList<>();
    }

    @Override
    public void start() {
        for (SerialPort port : SerialPort.getCommPorts()) {
            port.setBaudRate(115200);
            port.setParity(0);
            port.setNumDataBits(8);
            port.setNumStopBits(1);

            controllers.add(new ZController(port));
        }

        controllers.forEach(ZController::start);
    }

    @Override
    public void stop() {
        controllers.forEach(ZController::stop);
    }

    @Override
    public List<ZController> getControllers() {
        return controllers;
    }
}
