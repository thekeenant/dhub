package com.keenant.dhub.zwave.exception;

import com.keenant.dhub.zwave.frame.DataFrame;

public class DataFrameException extends RuntimeException {
    public DataFrameException(String msg) {
        super(msg);
    }

    public DataFrameException() {

    }
}
