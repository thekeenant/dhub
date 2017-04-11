package com.keenant.dhub.hub.web;

import com.keenant.dhub.hub.network.Network;
import spark.Request;

public interface NetworkProvider {
    Network getNetwork(Request req);
}
