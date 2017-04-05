package com.keenant.dhub.zwave;

import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.cmd.MultiChannelCmd;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd;
import com.keenant.dhub.zwave.cmd.SwitchMultilevelCmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * A Z-Wave command class, which may have multiple
 * different commands within it.
 */
public interface CmdClass<I extends InboundCmd> extends CmdParser<I> {
    BasicCmd BASIC = BasicCmd.INSTANCE;
    MultiChannelCmd MULTI_CHANNEL = MultiChannelCmd.INSTANCE;
    SwitchBinaryCmd SWITCH_BINARY = SwitchBinaryCmd.INSTANCE;
    SwitchMultilevelCmd SWITCH_MULTILEVEL = SwitchMultilevelCmd.INSTANCE;

    List<? extends CmdClass> ALL_CMDS = Arrays.asList(
            BASIC,
            MULTI_CHANNEL,
            SWITCH_BINARY,
            SWITCH_MULTILEVEL
    );

    /**
     * Get a command class instance from it's id.
     * @param id The command class id.
     * @return The command class, or empty if we don't understand it.
     */
    static Optional<CmdClass> getCmdClass(int id) {
        for (CmdClass cmdClass : ALL_CMDS) {
            if (cmdClass.getId() == id) {
                return Optional.of(cmdClass);
            }
        }
        return Optional.empty();
    }

    /**
     * Convert a list of command class id's to the corresponding class objects.
     * @param cmdClassIds Collection of command class ids.
     * @return
     */
    static List<CmdClass> getCmdClasses(Iterable<Integer> cmdClassIds) {
        // Todo: Maybe this should be a map instead?
        // So as to indicate which command classes we understand and dont (null).
        List<CmdClass> classes = new ArrayList<>();
        for (int id : cmdClassIds) {
            getCmdClass(id).ifPresent(classes::add);
        }
        return classes;
    }

    /**
     * @return The unique command class id, as specified by Z-Wave specifications.
     */
    byte getId();
}
