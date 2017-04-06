package com.keenant.dhub.hub.plugins.zwave.feature;

import com.keenant.dhub.hub.network.binary.BinaryGetFeature;
import com.keenant.dhub.hub.plugins.zwave.ZFeature;
import com.keenant.dhub.hub.plugins.zwave.ZNode;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd.Report;
import lombok.ToString;

import java.util.Optional;

@ToString
public class BinaryGetZFeature extends ZFeature implements BinaryGetFeature {
    public BinaryGetZFeature(ZNode node) {
        super(node);
    }

    @Override
    public boolean getBinary() {
        Optional<Report> report = getNode().send(CmdClass.SWITCH_BINARY.get()).await(5000).getResponse();

        // Todo: Exception
        return report.orElseThrow(RuntimeException::new).getValue();
    }
}
