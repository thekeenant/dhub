package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.Controller;

public class ZController extends Controller {
    private final SerialPort port;

    public ZController(SerialPort port) {
        this.port = port;
    }

    public void start() {

    }

    public void stop() {

    }
}
