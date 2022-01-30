package com.visualizer.log.service;

import com.querydsl.core.QueryResults;
import com.visualizer.log.domain.TracedLogRepositoryImpl;
import com.visualizer.log.dto.*;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VisualizeLogServiceTest {

    @InjectMocks
    private VisualizeLogService visualizeLogService;

    @Mock
    private TracedLogRepositoryImpl tracedLogRepositoryImpl;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void 서비스별_에러발생횟수조회___성공() {
        // data preparation
        List<ErrorServiceDto> errorServiceDtos = new ArrayList<>();

        ErrorServiceDto errorServiceDto1 = new ErrorServiceDto();
        errorServiceDto1.setTagId("service_a");
        errorServiceDto1.setErrorOccurCount(1L);
        errorServiceDtos.add(errorServiceDto1);

        ErrorServiceDto errorServiceDto2 = new ErrorServiceDto();
        errorServiceDto2.setTagId("service_b");
        errorServiceDto2.setErrorOccurCount(3L);
        errorServiceDtos.add(errorServiceDto2);

        Long limit = 2L;
        Long offset = 0L;
        Long total = 2L;
        QueryResults<ErrorServiceDto> queryResults = new QueryResults<>(errorServiceDtos, limit, offset, total);

        // mocking
        when(tracedLogRepositoryImpl.findErrorOccurServicesByLogType(any(), any(), any())).thenReturn(queryResults);

        // when
        final String order = "-errorOccurCount,tagId";
        final LocalDate searchDate = LocalDate.of(2021, 12, 14);
        final Pageable pageable = PageRequest.of(0, 2);

        Page<ErrorServiceDto> result = visualizeLogService.getErrorServices(searchDate, order, pageable);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent().size()).isEqualTo(2);
        Assertions.assertThat(result.getContent().get(0)).isEqualTo(errorServiceDto1);
        Assertions.assertThat(result.getContent().get(1)).isEqualTo(errorServiceDto2);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(2);
        Assertions.assertThat(result.getPageable().getOffset()).isEqualTo(offset);
        Assertions.assertThat(result.isLast()).isEqualTo(true);
    }


    @Test
    public void 서비스별_평균처리시간조회___성공() {
        // data preparation
        List<ServiceProcessTimeInTraceIdDto> listTrace = new ArrayList<>();
        ServiceProcessTimeInTraceIdDto dto1 = new ServiceProcessTimeInTraceIdDto();
        dto1.setTagId("service_a");
        dto1.setProcessTime(1000000L); // db 시간계산 처리된 값. 서비스 단위에서 /1000000 처리.
        listTrace.add(dto1);

        ServiceProcessTimeInTraceIdDto dto2 = new ServiceProcessTimeInTraceIdDto();
        dto2.setTagId("service_b");
        dto2.setProcessTime(2000000L);
        listTrace.add(dto2);

        ServiceProcessTimeInTraceIdDto dto3 = new ServiceProcessTimeInTraceIdDto();
        dto3.setTagId("service_b");
        dto3.setProcessTime(5000000L);
        listTrace.add(dto3);

        // mocking
        when(tracedLogRepositoryImpl.findServiceProcessTime(any())).thenReturn(listTrace);

        // when
        final String order = "-tagId";
        final LocalDate searchDate = LocalDate.of(2021, 12, 14);
        final Pageable pageable = PageRequest.of(0, 10);
        Map<String, Object> result = visualizeLogService.getAvgProcessTime(searchDate, order, pageable);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.get("avgProcessTimes")).isNotNull();
        Assertions.assertThat(((Page<ServiceAvgProcessTimeDto>)result.get("avgProcessTimes")).getContent().size()).isEqualTo(2);
        Assertions.assertThat(((Page<ServiceAvgProcessTimeDto>)result.get("avgProcessTimes")).getContent().get(0).getTagId()).isEqualTo("service_b");
        Assertions.assertThat(((Page<ServiceAvgProcessTimeDto>)result.get("avgProcessTimes")).getContent().get(1).getTagId()).isEqualTo("service_a");
        Assertions.assertThat(((Page<ServiceAvgProcessTimeDto>)result.get("avgProcessTimes")).getContent().get(0).getAvgProcessTime()).isEqualTo(3.5);
        Assertions.assertThat(((Page<ServiceAvgProcessTimeDto>)result.get("avgProcessTimes")).getContent().get(1).getAvgProcessTime()).isEqualTo(1.0);
        Assertions.assertThat(((Page<ServiceAvgProcessTimeDto>)result.get("avgProcessTimes")).getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(((Page<ServiceAvgProcessTimeDto>)result.get("avgProcessTimes")).getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(((Page<ServiceAvgProcessTimeDto>)result.get("avgProcessTimes")).getTotalElements()).isEqualTo(2);
    }


    @Test
    public void 서비스별_최대최소처리시간_간극조회___성공() {
        // data preparation
        List<ServiceProcessTimeInTraceIdDto> listTrace = new ArrayList<>();
        ServiceProcessTimeInTraceIdDto dto11 = new ServiceProcessTimeInTraceIdDto();
        dto11.setTagId("service_a");
        dto11.setProcessTime(1000000L); // db 시간계산 처리된 값. 서비스 단위에서 /1000000 처리.
        listTrace.add(dto11);
        ServiceProcessTimeInTraceIdDto dto22 = new ServiceProcessTimeInTraceIdDto();
        dto22.setTagId("service_b");
        dto22.setProcessTime(2000000L);
        listTrace.add(dto22);
        ServiceProcessTimeInTraceIdDto dto33 = new ServiceProcessTimeInTraceIdDto();
        dto33.setTagId("service_b");
        dto33.setProcessTime(5000000L);
        listTrace.add(dto33);

        // mocking
        when(tracedLogRepositoryImpl.findServiceProcessTime(any())).thenReturn(listTrace);

        // when
        final String order = "tagId,processTime";
        final LocalDate searchDate = LocalDate.of(2021, 12, 14);
        final Pageable pageable = PageRequest.of(0, 10);
        Map<String, Object> result = visualizeLogService.getMaxProcessTimeDiff(searchDate, order, pageable);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.get("maxProcessTimeDiffs")).isNotNull();
        Assertions.assertThat(((Page<ServiceMaxProcessTimeDiffDto>)result.get("maxProcessTimeDiffs")).getContent().size()).isEqualTo(2);
        Assertions.assertThat(((Page<ServiceMaxProcessTimeDiffDto>)result.get("maxProcessTimeDiffs")).getContent().get(0).getTagId()).isEqualTo("service_a");
        Assertions.assertThat(((Page<ServiceMaxProcessTimeDiffDto>)result.get("maxProcessTimeDiffs")).getContent().get(1).getTagId()).isEqualTo("service_b");
        // service_a 는 처리시간 기록이 1개 뿐이므로 간극이 0 초
        Assertions.assertThat(((Page<ServiceMaxProcessTimeDiffDto>)result.get("maxProcessTimeDiffs")).getContent().get(0).getMaxProcessTimeDiff()).isEqualTo(0);
        Assertions.assertThat(((Page<ServiceMaxProcessTimeDiffDto>)result.get("maxProcessTimeDiffs")).getContent().get(1).getMaxProcessTimeDiff()).isEqualTo((dto33.getProcessTime() - dto22.getProcessTime())/1000000);
        Assertions.assertThat(((Page<ServiceAvgProcessTimeDto>)result.get("maxProcessTimeDiffs")).getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(((Page<ServiceAvgProcessTimeDto>)result.get("maxProcessTimeDiffs")).getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(((Page<ServiceAvgProcessTimeDto>)result.get("maxProcessTimeDiffs")).getTotalElements()).isEqualTo(2);
    }


    @Test
    public void 가장많이호출된_서비스_횟수조회___성공() {
        // mocking
        List<ServiceCallCountDto> list = new ArrayList<>();
        ServiceCallCountDto dto1 = new ServiceCallCountDto();
        dto1.setTagId("service_a");
        dto1.setServiceCallCount(10L);
        list.add(dto1);
        ServiceCallCountDto dto2 = new ServiceCallCountDto();
        dto2.setTagId("service_b");
        dto2.setServiceCallCount(10L);
        list.add(dto2);
        ServiceCallCountDto dto3 = new ServiceCallCountDto();
        dto3.setTagId("service_c");
        dto3.setServiceCallCount(1L);
        list.add(dto3);

        when(tracedLogRepositoryImpl.findServicesCallCount(any())).thenReturn(list); // 호출횟수가 많은 순으로 정렬하여 리턴

        // when
        final LocalDate searchDate = LocalDate.of(2021, 11, 17);
        MostCallServiceDto result = visualizeLogService.getMostCallServices(searchDate);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getServices()).size().isEqualTo(2);
        Assertions.assertThat(result.getServices().get(0)).isEqualTo("service_a");
        Assertions.assertThat(result.getServices().get(1)).isEqualTo("service_b");
        Assertions.assertThat(result.getCallCount()).isEqualTo(10L);
    }


    @Test
    public void 추적ID내용검색___성공() {
        // data preparation
        TracedLogDto tracedLogDto1 = TracedLogDto.builder()
                .traceId("t1")
                .tagId("service_a")
                .logType("INFO")
                .logData("")
                .loggingTime(LocalDateTime.of(2021,11,17,11,14,17,164321000))
                .build();
        TracedLogDto tracedLogDto2 = TracedLogDto.builder()
                .traceId("t1")
                .tagId("service_a")
                .logType("INFO")
                .logData("")
                .loggingTime(LocalDateTime.of(2021,11,17,11,14,18,164322000))
                .build();
        List<TracedLogDto> list = new ArrayList<>();

        // 시간순으로 정렬했다고 가정
        list.add(tracedLogDto2);
        list.add(tracedLogDto1);

        Long limit = 10L;
        Long offset = 0L;
        Long total = 2L;
        QueryResults<TracedLogDto> queryResults = new QueryResults<>(list, limit, offset, total);

        // mocking
        when(tracedLogRepositoryImpl.findLogsByTraceId(any(), any(), any(), any())).thenReturn(queryResults);

        // when
        final String order = "loggingTime";
        final Pageable pageable = PageRequest.of(0, 10);
        final Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("traceId", "t1");
        searchVariable.put("tagId", "service_a");
        searchVariable.put("logType", "INFO");
        searchVariable.put("logData", "");
        searchVariable.put("loggingDate", LocalDate.of(2021, 11, 17));
        Map<String, Object> result = visualizeLogService.getLogsByTraceId(searchVariable, order, pageable);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.get("logs")).isNotNull();
        Assertions.assertThat(((Page<TracedLogDto>)result.get("logs")).getContent().size()).isEqualTo(2);
        Assertions.assertThat(((Page<TracedLogDto>)result.get("logs")).getContent().get(1).getLoggingTime()).isEqualTo(LocalDateTime.of(2021,11,17,11,14,17,164321000));
        Assertions.assertThat(((Page<TracedLogDto>)result.get("logs")).getContent().get(1).getLoggingTime()).isEqualTo(LocalDateTime.of(2021,11,17,11,14,17,164321000));
        Assertions.assertThat(((Page<TracedLogDto>)result.get("logs")).getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(((Page<TracedLogDto>)result.get("logs")).getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(((Page<TracedLogDto>)result.get("logs")).getTotalElements()).isEqualTo(2);
    }


    @Test
    public void 서비스ID내용검색___성공() {
        // data preparation
        TracedLogDto tracedLogDto1 = TracedLogDto.builder()
                .traceId("t1")
                .tagId("service_a")
                .logType("ERROR")
                .logData("")
                .loggingTime(LocalDateTime.of(2021,11,17,11,14,17,164321000))
                .build();
        TracedLogDto tracedLogDto2 = TracedLogDto.builder()
                .traceId("t1")
                .tagId("service_a")
                .logType("INFO")
                .logData("")
                .loggingTime(LocalDateTime.of(2021,11,17,11,14,17,164322000))
                .build();
        List<TracedLogDto> list = new ArrayList<>();
        list.add(tracedLogDto1);
        list.add(tracedLogDto2);

        Long limit = 10L;
        Long offset = 0L;
        Long total = 2L;
        QueryResults<TracedLogDto> queryResults = new QueryResults<>(list, limit, offset, total);

        // mocking
        when(tracedLogRepositoryImpl.findLogsByTagId(any(), any(), any(), any())).thenReturn(queryResults);

        // when
        final String order = "-logType";
        final Pageable pageable = PageRequest.of(0, 10);
        final Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("tagId", "service_a");
        searchVariable.put("logType", "INFO");
        searchVariable.put("logData", "");
        searchVariable.put("loggingDate", LocalDate.of(2021, 11, 17));
        Map<String, Object> result = visualizeLogService.getLogsByTagId(searchVariable, order, pageable);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.get("logs")).isNotNull();
        Assertions.assertThat(((Page<TracedLogDto>)result.get("logs")).getContent().size()).isEqualTo(2);
        Assertions.assertThat(((Page<TracedLogDto>)result.get("logs")).getContent().get(0).getLogType()).isEqualTo("ERROR");
        Assertions.assertThat(((Page<TracedLogDto>)result.get("logs")).getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(((Page<TracedLogDto>)result.get("logs")).getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(((Page<TracedLogDto>)result.get("logs")).getTotalElements()).isEqualTo(2);
    }

}
