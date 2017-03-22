package com.keenant.dhub;

import java.util.UUID;

public abstract class Controller {
    private final UUID uuid;

    public Controller() {
        this.uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }
}
