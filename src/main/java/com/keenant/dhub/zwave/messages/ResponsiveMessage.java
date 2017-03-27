package com.keenant.dhub.zwave.messages;

import com.keenant.dhub.util.ByteList;
import com.keenant.dhub.zwave.frame.IncomingDataFrame;
import com.keenant.dhub.zwave.transaction.Transaction;

import java.util.Optional;

public interface ResponsiveMessage<Txn extends Transaction, Res extends IncomingDataFrame> extends Message<Txn> {
    /**
     * Attempt to parse a response for this message.
     *
     * @param data The raw data, not including the data frame type.
     * @return Empty if the data does not correspond to this message.
     *         Otherwise, return the response for this message.
     */
    Optional<Res> parseResponse(ByteList data);
}
