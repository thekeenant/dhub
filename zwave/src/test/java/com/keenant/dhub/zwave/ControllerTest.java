package com.keenant.dhub.zwave;

import com.fazecast.jSerialComm.SerialPort;
import com.keenant.dhub.zwave.messages.MemoryGetIdMsg;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ControllerTest {
    private Controller createController() {
        return new Controller(SerialPort.getCommPort(null));
    }

    @Test
    public void startStop() {
        Controller control = createController();
        assertFalse(control.isAlive());

        control.start();
        assertTrue(control.isAlive());

        control.stop();
        assertFalse(control.isAlive());
    }

    @Test
    public void currentTxn() {
        Controller control = createController();
        assertFalse(control.updateCurrent().isPresent());

        control.send(MemoryGetIdMsg.get());
        assertTrue(control.updateCurrent().isPresent());
    }
}
