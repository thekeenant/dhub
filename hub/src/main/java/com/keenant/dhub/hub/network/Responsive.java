package com.keenant.dhub.hub.network;

import com.google.gson.JsonElement;

public interface Responsive {
    void respondTo(JsonElement json);
}
