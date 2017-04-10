package com.keenant.dhub.hub.network.feature;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.keenant.dhub.hub.network.DataFeature;
import com.keenant.dhub.hub.network.ResponsiveFeature;

import java.util.Optional;

public abstract class LevelFeature implements ResponsiveFeature, DataFeature {
    public abstract Optional<Integer> getLevel();

    public abstract void updateLevel();

    public abstract void setLevel(int level);

    @Override
    public String getUniqueId() {
        return "level";
    }

    @Override
    public void respondTo(JsonElement json) {
        int level = json.getAsJsonObject().get("level").getAsInt();
        setLevel(level);
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("level", getLevel().orElse(null));
        return json;
    }
}
