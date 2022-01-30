package com.collector.log.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Builder // test code
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UndeliveredMailDto {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") // for test
    private LocalDateTime createAt;

    private String causeKeyword;
    private String causeContent;

    // private TracedLog tracedLog;
    private Long logId;
    private String tagId;
    private String traceId;
    private String logData;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime loggingTime;

}
