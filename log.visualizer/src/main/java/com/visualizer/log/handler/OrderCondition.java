package com.visualizer.log.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum OrderCondition {

    TAG_ID_PROCESS_TIME("tagId-processTime"),
    ERROR_OCCUR_COUNT_TAG_ID("errorOccurCount-tagId");
    private final String message;

}
