package com.collector.log.handler;

public class TargetException extends CustomException{

    public TargetException(CustomErrorCode errorCode) {
        super(errorCode);
    }

    public TargetException(CustomErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }

}
