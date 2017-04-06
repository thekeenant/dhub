package com.keenant.dhub.zwave.exception;

public class DataFrameException extends RuntimeException {
    public DataFrameException(String msg) {
        super(msg);
    }

    public DataFrameException() {

    }

    public DataFrameException(Exception e) {
        super(e);
    }
}
