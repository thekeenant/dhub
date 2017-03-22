package com.keenant.dhub;

import java.util.List;

public interface Server<T extends Controller> {
    void start();

    void stop();

    List<T> getControllers();

}
