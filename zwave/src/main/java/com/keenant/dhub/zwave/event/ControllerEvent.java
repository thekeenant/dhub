package com.keenant.dhub.zwave.event;

import com.keenant.dhub.zwave.Controller;

public class ControllerEvent extends Event {
    private final Controller controller;

    public ControllerEvent(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }
}
