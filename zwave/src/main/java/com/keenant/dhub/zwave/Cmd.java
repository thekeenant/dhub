package com.keenant.dhub.zwave;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.core.util.Byteable;
import com.keenant.dhub.zwave.cmd.BasicCmd;
import com.keenant.dhub.zwave.cmd.SwitchBinaryCmd;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A ZWave command.
 */
public interface Cmd extends Byteable {
}
