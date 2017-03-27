package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg;
import com.keenant.dhub.zwave.transaction.ReqTransaction;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ZControllerTest {
    private ZController createController() {
        return new ZController(SerialPort.getCommPort(null));
    }

    @Test
    public void startStop() {
        ZController control = createController();
        assertFalse(control.isAlive());

        control.start();
        assertTrue(control.isAlive());

        control.stop();
        assertFalse(control.isAlive());
    }

    @Test
    public void currentTxn() {
        ZController control = createController();
        assertFalse(control.updateCurrent().isPresent());

        control.queue(new ReqTransaction(null, new MemoryGetIdMsg()));
        assertTrue(control.updateCurrent().isPresent());
    }
}
