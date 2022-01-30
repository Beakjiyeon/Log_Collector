package com.collector.log.dto;


import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/*
 * @RequestBody 바인딩 ObjectMapper 처리에서 기본 생성자가 이용된다.
 */

@Setter
@NoArgsConstructor
@Getter
public class LogSaveRequestDto implements Serializable {

    private String traceId;

    private String tagId;

    private String logType;

    private String logData;

    private LocalDateTime loggingTime;


    @Builder
    public LogSaveRequestDto(final String traceId, final String tagId, final String logType, final String logData, final LocalDateTime loggingTime) {
        this.traceId = traceId;
        this.tagId = tagId;
        this.logType = logType;
        this.logData = logData;
        this.loggingTime = loggingTime;
    }


}
