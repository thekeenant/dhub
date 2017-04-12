package com.keenant.dhub.zwave.event;

import com.keenant.dhub.zwave.Controller;
import com.keenant.dhub.zwave.InboundCmd;
import com.keenant.dhub.zwave.util.EndPoint;
import lombok.ToString;

/**
 * Called when a command class frame is received by us. This INCLUDES multi channel encapsulated
 * command class frames - #{@link CmdEvent#endPoint} will then have an end point specified.
 *
 * See the package, #{@link com.keenant.dhub.zwave.event.cmd} for all possible cmd events.
 *
 * @param <T> The type of command.
 */
@ToString
public abstract class CmdEvent<T extends InboundCmd> extends ControllerEvent {
    private final EndPoint endPoint;
    private final T cmd;

    public CmdEvent(Controller controller, EndPoint endPoint, T cmd) {
        super(controller);
        this.endPoint = endPoint;
        this.cmd = cmd;
    }

    public EndPoint getEndPoint() {
        return endPoint;
    }

    public T getCmd() {
        return cmd;
    }
}
