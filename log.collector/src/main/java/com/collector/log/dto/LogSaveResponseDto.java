package com.collector.log.dto;

import com.collector.log.domain.TracedLog;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@NoArgsConstructor
@Getter
public class LogSaveResponseDto {

    private Long id;

    private String traceId;

    private String tagId;

    private String logType;

    private String logData;


    private LocalDateTime loggingTime;


    public LogSaveResponseDto(TracedLog entity) {
        this.id = entity.getId();
        this.traceId = entity.getTraceId();
        this.tagId = entity.getTagId();
        this.logType = entity.getLogType();
        this.logData = entity.getLogData();
        this.loggingTime = entity.getLoggingTime();
    }

}
