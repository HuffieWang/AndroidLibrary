package com.musheng.android.fetcher;

public class MSFetcherThrowable extends Throwable {

    private int errorCode;
    private String errorMessage;

    public MSFetcherThrowable(int errorCode) {
        this.errorCode = errorCode;
    }

    public MSFetcherThrowable(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MSFetcherThrowable(int errorCode, String errorMessage) {
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
