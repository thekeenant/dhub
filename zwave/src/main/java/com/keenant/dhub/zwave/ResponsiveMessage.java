package com.keenant.dhub.zwave;

import com.keenant.dhub.core.util.ByteList;
import com.keenant.dhub.zwave.transaction.Transaction;

import java.util.Optional;

/**
 * A type of message that expects a response.
 * @param <Txn> The transaction type this message should create.
 * @param <Res> The response type.
 */
public interface ResponsiveMessage<Txn extends Transaction, Res extends IncomingMessage> extends Message<Txn> {
    /**
     * Attempt to parseReport a response for this message.
     *
     * @param data The raw data, not including the data frame type.
     * @return Empty if the data does not correspond to this message.
     *         Otherwise, return the response for this message.
     */
    Optional<Res> parseResponse(ByteList data);
}
