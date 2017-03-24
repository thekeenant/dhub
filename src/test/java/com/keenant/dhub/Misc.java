package com.keenant.dhub;

import com.keenant.dhub.logging.Level;
import com.keenant.dhub.util.ByteList;
import com.keenant.dhub.zwave.ZController;
import com.keenant.dhub.zwave.ZStick;
import com.keenant.dhub.zwave.cmd.BasicGetCmd;
import com.keenant.dhub.zwave.cmd.BasicSetCmd;
import com.keenant.dhub.zwave.frame.DataFrameType;
import com.keenant.dhub.zwave.frame.IncomingDataFrame;
import com.keenant.dhub.zwave.messages.SendDataMsg;
import com.keenant.dhub.zwave.messages.SendDataMsg.Response;
import com.keenant.dhub.zwave.frame.Status;
import com.keenant.dhub.zwave.transaction.ReqResTransaction;
import com.keenant.dhub.zwave.transaction.Transaction;
import com.sun.media.jfxmedia.logging.Logger;
import org.junit.Test;

public class Misc {
    @Test
    public void testTransaction() {
        SendDataMsg msg = new SendDataMsg(8, new BasicSetCmd(50));

        ReqResTransaction<Response> txn = new ReqResTransaction<>(msg);
        txn.start();
        txn.handle(Status.ACK);
        txn.handle(new IncomingDataFrame(new ByteList(0x13, 0x01, 0xE8), DataFrameType.REQ));
        assert txn.isFinished();

        txn = new ReqResTransaction<>(msg);
        txn.start();
        txn.handle(Status.ACK);
        txn.handle(new IncomingDataFrame(new ByteList(0x12), DataFrameType.REQ));
        assert txn.isFinished();
    }

    @Test
    public void misc() throws InterruptedException {

        Hub hub = new Hub();
        hub.start();

        ZController controller = hub.getZServer().getControllers().get(0);

        SendDataMsg msg = new SendDataMsg(8, new BasicSetCmd(99));
        Transaction txn = new ReqResTransaction<>(msg);


        SendDataMsg msg2 = new SendDataMsg(8, new BasicSetCmd(0));
        Transaction txn2 = new ReqResTransaction<>(msg2);


        controller.queue(txn);
        controller.queue(txn2);
        controller.queue(txn);
        controller.queue(txn2);
        controller.queue(txn);
        controller.queue(txn2);
        controller.queue(txn);
        controller.queue(txn2);
        controller.queue(txn);
        controller.queue(txn2);
        controller.queue(txn);
        controller.queue(txn2);
        controller.queue(txn);
        controller.queue(txn2);
        controller.queue(txn);
        controller.queue(txn2);
        controller.queue(txn);
        controller.queue(txn2);
        controller.queue(txn);
        controller.queue(txn2);
        controller.queue(txn);
        controller.queue(txn2);
        controller.queue(txn);
        controller.queue(txn2);

        while (true) {

        }
    }
}
