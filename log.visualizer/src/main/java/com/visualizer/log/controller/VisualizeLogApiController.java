package com.visualizer.log.controller;


import com.visualizer.log.dto.ApiResult;
import com.visualizer.log.handler.CustomResultMessage;
import com.visualizer.log.service.VisualizeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/visualize-api/v2")
@RestController("v2.VisualizeLogApiController")
public class VisualizeLogApiController {

    private final VisualizeLogService visualizeLogService;

    // http://localhost:8082/visualize-api/v2/error-occur-services?page=0&size=2&loggingDate=2021-11-17
    @GetMapping("/error-occur-services")
    public ResponseEntity getErrorServices(
            @RequestParam(value = "loggingDate", required = false, defaultValue= "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate loggingDate,
            @RequestParam(value = "order", required = false, defaultValue = "-errorOccurCount,tagId") String order,
            Pageable pageable) {
        return ApiResult.builder()
                .status(HttpStatus.OK.value())
                .message(CustomResultMessage.OK.getMessage())
                .data(visualizeLogService.getErrorServices(loggingDate, order, pageable))
                .build()
                .getResponse(HttpStatus.OK);
    }


    // http://localhost:8082/visualize-api/v2/avg-process-time?loggingDate=2021-11-17&order=tagId,-processTime&page=0&size=3
    @GetMapping("/avg-process-time")
    public ResponseEntity getAvgProcessTime(
            @RequestParam(value = "loggingDate", required = false, defaultValue= "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate loggingDate,
            @RequestParam(value = "order", required = false, defaultValue = "tagId,processTime") String order,
            @PageableDefault Pageable pageable) {
        return ApiResult.builder()
                .status(HttpStatus.OK.value())
                .message(CustomResultMessage.OK.getMessage())
                .data(visualizeLogService.getAvgProcessTime(loggingDate, order, pageable))
                .build()
                .getResponse(HttpStatus.OK);
    }


    // http://localhost:8082/visualize-api/v2/process-time-diff?loggingDate=2021-11-17&order=-tagId&page=0&size=3
    @GetMapping("/process-time-diff")
    public ResponseEntity getMaxProcessTimeDiff(
            @RequestParam(value = "loggingDate", required = false, defaultValue= "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate loggingDate,
            @RequestParam(value = "order", required = false, defaultValue = "tagId") String order,
            @PageableDefault Pageable pageable) {
        return ApiResult.builder()
                .status(HttpStatus.OK.value())
                .message(CustomResultMessage.OK.getMessage())
                .data(visualizeLogService.getMaxProcessTimeDiff(loggingDate, order, pageable))
                .build()
                .getResponse(HttpStatus.OK);
    }


    // 최대 호출 서비스 개수 -> 페이지네이션 불필요
    // http://localhost:8082/visualize-api/v2/most-call-services?loggingDate=2021-11-17&order=-tagId,-processTime
    @GetMapping("/most-call-services")
    public ResponseEntity getMostCallServices(
            @RequestParam(value = "loggingDate", required = false, defaultValue= "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate loggingDate) {
        return ApiResult.builder()
                .status(HttpStatus.OK.value())
                .message(CustomResultMessage.OK.getMessage())
                .data(visualizeLogService.getMostCallServices(loggingDate))
                .build()
                .getResponse(HttpStatus.OK);
    }


    // http://localhost:8082/visualize-api/v2/logs/trace-id/t2?loggingDate=2021-11-17&page=1&size=10&order=-logType
    @GetMapping("/logs/trace-id/{traceId}")
    public ResponseEntity getLogsByTraceId(
            @PathVariable("traceId") String traceId,
            @RequestParam(value = "loggingDate", required = false, defaultValue= "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate loggingDate,
            @RequestParam(value = "tagId", required = false, defaultValue = "") String tagId,
            @RequestParam(value = "logType", required = false, defaultValue = "") String logType,
            @RequestParam(value = "logData", required = false, defaultValue = "") String logData,
            @RequestParam(value = "order", required = false, defaultValue = "loggingTime") String order,
            @PageableDefault Pageable pageable) {
        // 조회 요소 추가, 삭제 시 constructor 수정이 필요 없도록 Map 객체 이용
        Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("traceId", traceId);
        searchVariable.put("tagId", tagId);
        searchVariable.put("logType", logType);
        searchVariable.put("logData", logData);
        searchVariable.put("loggingDate", loggingDate);
        return ApiResult.builder()
                .status(HttpStatus.OK.value())
                .message(CustomResultMessage.OK.getMessage())
                .data(visualizeLogService.getLogsByTraceId(searchVariable, order, pageable))
                .build()
                .getResponse(HttpStatus.OK);
    }


    // http://localhost:8082/visualize-api/logs/tag-id/service_b?loggingDate=2021-11-17&page=1&size=0&order=-logType
    @GetMapping("/logs/tag-id/{tagId}")
    public ResponseEntity getLogsByTagId(
            @PathVariable("tagId") String tagId,
            @RequestParam(value = "loggingDate", required = false, defaultValue= "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate loggingDate,
            @RequestParam(value = "logType", required = false, defaultValue = "") String logType,
            @RequestParam(value = "logData", required = false, defaultValue = "") String logData,
            @RequestParam(value = "order", required = false, defaultValue = "loggingTime") String order,
            @PageableDefault Pageable pageable) {
        Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("tagId", tagId);
        searchVariable.put("logType", logType);
        searchVariable.put("logData", logData);
        searchVariable.put("loggingDate", loggingDate);
        return ApiResult.builder()
                .status(HttpStatus.OK.value())
                .message(CustomResultMessage.OK.getMessage())
                .data(visualizeLogService.getLogsByTagId(searchVariable, order, pageable))
                .build()
                .getResponse(HttpStatus.OK);
    }
}
