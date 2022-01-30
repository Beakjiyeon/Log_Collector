package com.collector.log.dto;

import com.collector.log.domain.TracedLog;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/*
 * @RequestBody 바인딩 ObjectMapper 처리에서 기본 생성자가 이용된다.
 */
@NoArgsConstructor
@Getter
public class LogSaveRequestDto implements Serializable {

    @Size(max = 100, min = 1, message = "문자열 길이는 1 이상 100 이하 여야 합니다.")
    @NotBlank(message = "null 을 허용하지 않고, 공백 문자를 제외한 길이가 0보다 커야 합니다.")
    private String traceId;

    @Size(max = 20, min = 1, message = "문자열 길이는 1 이상 20 이하 여야 합니다.")
    @NotBlank(message = "null 을 허용하지 않고, 공백 문자를 제외한 길이가 0보다 커야 합니다.")
    private String tagId;

    @Size(max = 5, min = 1, message = "문자열 길이는 1 이상 5 이하 여야 합니다.")
    @NotBlank(message = "null 을 허용하지 않고, 공백 문자를 제외한 길이가 0보다 커야 합니다.")
    private String logType;

    @Size(max = 65535, min = 0, message = "문자열 길이는 65535 이하 여야 합니다.")
    private String logData;

    // Now 의 기준 : ClockProvider 의 가상 머신에 따라 현재 시간을 정의하며 필요한 경우 default time zone 을 적용한다.
    @PastOrPresent(message = "유효하지 않은 날짜 정보 입니다.")
    @NotNull(message = "null 을 허용하지 않고, 공백 문자를 제외한 길이가 0보다 커야 합니다.") // NotBlank, NotEmpty 는 localDateTime 지원 안함
    private LocalDateTime loggingTime;


    @Builder
    public LogSaveRequestDto(final String traceId, final String tagId, final String logType, final String logData, final LocalDateTime loggingTime) {
        this.traceId = traceId;
        this.tagId = tagId;
        this.logType = logType;
        this.logData = logData;
        this.loggingTime = loggingTime;
    }

    public TracedLog toEntity() {
        return TracedLog.builder()
                .traceId(traceId)
                .tagId(tagId)
                .logType(logType)
                .logData(logData)
                .loggingTime(loggingTime)
                .loggingDate(loggingTime.toLocalDate())
                .build();
    }

}
