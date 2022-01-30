package com.visualizer.log.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomResultMessage {

    OK("실행을 성공적으로 완료하였습니다."),
    CREATED("리소스 생성을 성공적으로 완료하였습니다."),
    MAIL_OK("메일전송을 성공적으로 완료하였습니다.");

    private final String message;
}
