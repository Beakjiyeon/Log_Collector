package com.collector.log.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {
    INVALID_PARAMETER("유효하지 인자 값 입니다."),
    INVALID_SEARCH_DATE_PARAMETER("유효하지 않은 날짜 인자 값 입니다."),
    INVALID_ORDER_PARAMETER("유효하지 않은 정렬 인자 값 입니다."),
    INVALID_PAGE_PARAMETER("페이지네이션 인자 값은 양의 정수 입니다."),
    NULL_PARAMETER("필수 인자가 '' 또는 NULL 값 입니다."),
    TARGET_NOT_FOUND("대상이 없습니다."),
    INVALID_SEARCH_HOUR_PARAMETER("유효하지 않은 시각 인자 값 입니다.");

    private final String message;

}
