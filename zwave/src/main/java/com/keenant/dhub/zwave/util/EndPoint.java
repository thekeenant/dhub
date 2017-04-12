package com.keenant.dhub.zwave.util;

import com.keenant.dhub.zwave.Controller;

import java.util.Optional;

public class EndPoint {
    private final Controller controller;
    private final int nodeId;
    private final Integer endPoint;

    public EndPoint(Controller controller, int nodeId, Integer endPoint) {
        this.controller = controller;
        this.nodeId = nodeId;
        this.endPoint = endPoint;
    }

    public EndPoint(Controller controller, int nodeId) {
        this(controller, nodeId, null);
    }
    
    public int getNodeId() {
        return nodeId;
    }
    
    public Optional<Integer> getEndPoint() {
        return Optional.ofNullable(endPoint);
    }
}
