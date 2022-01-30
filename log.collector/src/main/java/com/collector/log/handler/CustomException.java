package com.collector.log.handler;

public class CustomException extends RuntimeException {

    private CustomErrorCode errorCode;

    private String detailMessage;

    public CustomException(CustomErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = errorCode.getMessage();
    }


    public CustomException(CustomErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }
}