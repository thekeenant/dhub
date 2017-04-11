package com.keenant.dhub.zwave;

import com.keenant.dhub.core.util.ControllerListener;
import com.keenant.dhub.zwave.event.Event;

/**
 * A listener, but for a specific event.
 *
 * @param <T> The type of event to handle.
 */
@FunctionalInterface
public interface ControllerEventHandler<T extends Event> {
    /**
     * Called upon receiving the event for this handler.
     *
     * @param listener The listener for this handler. Useful for unsubscribing it from the
     *                 controller.
     * @param event The event.
     */
    void handle(ControllerListener listener, T event);
}
