package com.keenant.dhub.hub.zwave;

import com.keenant.dhub.core.util.Listener;
import com.keenant.dhub.zwave.event.message.NodeListReplyEvent;
import net.engio.mbassy.listener.Handler;

public class ZServerListener implements Listener {
    @Handler
    public void onNodeList(NodeListReplyEvent event) {

    }
}
