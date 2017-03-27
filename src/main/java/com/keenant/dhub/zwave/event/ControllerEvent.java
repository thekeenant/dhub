package com.keenant.dhub.zwave.event;

import com.keenant.dhub.zwave.ZController;

public class ControllerEvent {
    private final ZController controller;

    public ControllerEvent(ZController controller) {
        this.controller = controller;
    }

    public ZController getController() {
        return controller;
    }
}
