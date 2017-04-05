package com.keenant.dhub.hub.plugins.web;

import spark.Service;

/**
 * Like {@link spark.RouteGroup}, but routes should be added to a specific
 * Spark service.
 */
public interface ServiceRouteGroup {
    /**
     * Add routes to the service provided, via service.get(), service.post(), etc.
     * @param http The service.
     */
    void addRoutes(Service http);
}
