package com.keenant.dhub.zwave.exception;

import com.keenant.dhub.zwave.frame.DataFrameType;

public class IllegalDataFrameTypeException extends DataFrameException {
    public IllegalDataFrameTypeException(DataFrameType typeGiven) {
        super("Unexpected data frame type: " + typeGiven + ".");
    }
}
