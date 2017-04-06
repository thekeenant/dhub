package com.keenant.dhub.hub.plugins.zwave.feature;

import com.keenant.dhub.hub.network.level.LevelGetFeature;
import com.keenant.dhub.hub.plugins.zwave.ZFeature;
import com.keenant.dhub.hub.plugins.zwave.ZNode;
import com.keenant.dhub.zwave.CmdClass;
import com.keenant.dhub.zwave.cmd.SwitchMultilevelCmd.Report;
import lombok.ToString;

import java.util.Optional;

@ToString
public class LevelGetZFeature extends ZFeature implements LevelGetFeature {
    public LevelGetZFeature(ZNode node) {
        super(node);
    }

    @Override
    public int getLevel() {
        Optional<Report> report = getNode().send(CmdClass.SWITCH_MULTILEVEL.get()).getResponse();

        // Todo: Exception
        return report.orElseThrow(RuntimeException::new).getCurrent();
    }
}
