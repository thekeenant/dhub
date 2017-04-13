package com.keenant.dhub.hub.plugins.zwave;

import com.keenant.dhub.hub.network.Device;
import com.keenant.dhub.zwave.Cmd;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.InboundCmd;
import com.keenant.dhub.zwave.ResponsiveCmd;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd;
import com.keenant.dhub.zwave.messages.ApplicationUpdateMsg;
import com.keenant.dhub.zwave.messages.DataMsg.SendDataMsg;
import com.keenant.dhub.zwave.messages.DataMsg.SendReceiveDataMsg;
import com.keenant.dhub.zwave.messages.RequestNodeInfoMsg;
import com.keenant.dhub.zwave.transaction.SendDataTxn;
import com.keenant.dhub.zwave.transaction.SendReceiveDataTxn;
import lombok.ToString;

import java.util.Optional;

@ToString(callSuper = true)
public class ZDevice extends Device<ZNetwork> {
    private final int nodeId;

    public ZDevice(ZNetwork network, int nodeId) {
        super(network);
        this.nodeId = nodeId;
    }

    public void send(Cmd cmd) {
        SendDataMsg<?> msg = new SendDataMsg<>(nodeId, cmd);
        SendDataTxn<?> txn = getNetwork().send(msg);
        txn.await(5000);
    }

    public <C extends ResponsiveCmd<R>, R extends InboundCmd> Optional<R> send(C cmd) {
        SendReceiveDataMsg<C, R> msg = new SendReceiveDataMsg<>(nodeId, cmd);
        SendReceiveDataTxn<C, R> txn = getNetwork().send(msg);
        txn.await(5000);
        return txn.getResponse();
    }

    @Override
    public void start() {
        ApplicationUpdateMsg info = getNetwork()
                .send(new RequestNodeInfoMsg(nodeId))
                .await(5000)
                .getCallback()
                .orElse(null);

        for (CmdClass cmd : info.getCmdClasses()) {
            if (cmd instanceof SwitchBinaryCmd) {
                ZSwitchBinaryProvider provider = addProvider(new ZSwitchBinaryProvider(this));
                addAbility(new ZSwitchBinaryAbility(this, provider));
            }
        }
    }

    @Override
    public void stop() {

    }
}
