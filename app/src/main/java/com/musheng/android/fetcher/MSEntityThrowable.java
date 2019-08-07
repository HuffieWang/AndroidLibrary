package com.musheng.android.fetcher;

public class MSEntityThrowable extends Throwable {

    private int errorCode;
    private String errorMessage;

    public MSEntityThrowable(int errorCode) {
        this.errorCode = errorCode;
    }

    public MSEntityThrowable(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MSEntityThrowable(int errorCode, String errorMessage) {
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
