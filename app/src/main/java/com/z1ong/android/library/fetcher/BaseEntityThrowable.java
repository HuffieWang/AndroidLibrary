package com.z1ong.android.library.fetcher;

public class BaseEntityThrowable extends Throwable {

    private int errorCode;
    private String errorMessage;

    public BaseEntityThrowable(int errorCode) {
        this.errorCode = errorCode;
    }

    public BaseEntityThrowable(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public BaseEntityThrowable(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage == null ? "" : errorMessage;
    }
}
