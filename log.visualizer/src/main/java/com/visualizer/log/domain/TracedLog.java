package com.visualizer.log.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name="traced_log")
public class TracedLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(name = "trace_id", nullable = false)
    private String traceId; // 추적 ID

    @Column(name = "tag_id", length = 100, nullable = false)
    private String tagId; // 서비스 이름

    @Column(name = "log_type", length = 20, nullable = false)
    private String logType; // 로그 레벨

    @Column(name = "log_data", length = 65535, columnDefinition = "TEXT")
    private String logData; // 로그 내용

    @Column(name = "logging_time", nullable = false)
    private LocalDateTime loggingTime; // 로깅 일시

    @Column(name = "logging_date", nullable = false)
    private LocalDate loggingDate; // 로깅 날짜

    @Builder
    public TracedLog(Long id, String traceId, String tagId, String logType, String logData, LocalDateTime loggingTime, LocalDate loggingDate) {
        this.id = id;
        this.traceId = traceId;
        this.tagId = tagId;
        this.logType = logType;
        this.logData = logData;
        this.loggingTime = loggingTime;
        this.loggingDate = loggingDate;
        System.out.println("엔티티" + loggingTime);
    }

}

