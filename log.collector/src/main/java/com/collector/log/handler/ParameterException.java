package com.collector.log.handler;

public class ParameterException extends CustomException{

    public ParameterException(CustomErrorCode errorCode) {
        super(errorCode);
    }

    public ParameterException(CustomErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }

}

