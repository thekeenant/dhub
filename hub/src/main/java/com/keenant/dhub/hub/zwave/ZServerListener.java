package com.keenant.dhub.hub.zwave;

import com.keenant.dhub.core.util.EventListener;
import com.keenant.dhub.zwave.event.message.NodeListReplyEvent;
import net.engio.mbassy.listener.Handler;

public class ZServerListener implements EventListener {
    @Handler
    public void onNodeList(NodeListReplyEvent event) {

    }
}
