
package com.visualizer.log.domain;


import com.querydsl.core.QueryResults;
import com.visualizer.log.config.TestConfig;
import com.visualizer.log.dto.ErrorServiceDto;
import com.visualizer.log.dto.ServiceCallCountDto;
import com.visualizer.log.dto.ServiceProcessTimeInTraceIdDto;
import com.visualizer.log.dto.TracedLogDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 Main domain 에 jpa 를 상속받은 리포지터리가 없으면
 TracedLogRepositoryImpl 빈을 생성하지 못하여 nullPointerException 발생한다.
 - 조회서버에 쓰이지 않는 tracedLogRepository 클래스 생성
 */

@ActiveProfiles("test")
@Import(TestConfig.class) // @DataJpaTest 와 QueryDsl 함께 사용하기 위한 설정
@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TracedLogRepositoryImplTest {

    @Autowired
    private TracedLogRepositoryImpl tracedLogRepositoryImpl;

    /**
     * 서비스별 에러발생횟수 목록 조회 성공
     * select tag_id, count(tag_id)
     * from traced_log
     * where logging_date = '2021-12-14' and log_type = 'ERROR'
     * group by tag_id
     * order by errorOccurCount desc, tagId;
     *
     * 결과 :
     * service_x	4
     * service_y	3
     * service_z	2
     */
    @Test
    public void 서비스별_에러발생횟수조회___성공() {
        // given
        final LocalDate searchDate = LocalDate.of(2021, 12, 14);
        final Pageable pageable = PageRequest.of(0, 10);
        LinkedHashMap<String, String> orderInfo = new LinkedHashMap<>(); // 단위 테스트이므로 서비스 단위에서 처리되는 계산 값을 직접 입력
        orderInfo.put("errorOccurCount", "desc");
        orderInfo.put("tagId", "asc");

        // when
        QueryResults<ErrorServiceDto> result = tracedLogRepositoryImpl.findErrorOccurServicesByLogType(searchDate, orderInfo, pageable);

        // then
        Assertions.assertThat(result.getResults().size()).isEqualTo(3);
        Assertions.assertThat(result.getResults().get(0).getTagId()).isEqualTo("service_x");
        Assertions.assertThat(result.getResults().get(0).getErrorOccurCount()).isEqualTo(4);
        Assertions.assertThat(result.getResults().get(1).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(1).getErrorOccurCount()).isEqualTo(3);
        Assertions.assertThat(result.getResults().get(2).getTagId()).isEqualTo("service_z");
        Assertions.assertThat(result.getResults().get(2).getErrorOccurCount()).isEqualTo(2);
    }


    @Test
    public void 서비스별_에러발생횟수조회___성공___정렬() {
        // given
        final LocalDate searchDate = LocalDate.of(2021, 12, 14);
        final Pageable pageable = PageRequest.of(0, 10);
        LinkedHashMap<String, String> orderInfo = new LinkedHashMap<>();
        orderInfo.put("errorOccurCount", "asc");
        orderInfo.put("tagId", "asc");

        // when
        QueryResults<ErrorServiceDto> result = tracedLogRepositoryImpl.findErrorOccurServicesByLogType(searchDate, orderInfo, pageable);

        // then
        Assertions.assertThat(result.getResults().size()).isEqualTo(3);
        Assertions.assertThat(result.getResults().get(2).getTagId()).isEqualTo("service_x");
        Assertions.assertThat(result.getResults().get(2).getErrorOccurCount()).isEqualTo(4);
        Assertions.assertThat(result.getResults().get(1).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(1).getErrorOccurCount()).isEqualTo(3);
        Assertions.assertThat(result.getResults().get(0).getTagId()).isEqualTo("service_z");
        Assertions.assertThat(result.getResults().get(0).getErrorOccurCount()).isEqualTo(2);
    }


    @Test
    public void 서비스별_에러발생횟수조회___성공___페이지네이션() {
        // given
        final LocalDate searchDate = LocalDate.of(2021, 12, 14);
        final Pageable pageable = PageRequest.of(0, 1);
        LinkedHashMap<String, String> orderInfo = new LinkedHashMap<>();
        orderInfo.put("errorOccurCount", "asc");
        orderInfo.put("tagId", "asc");

        // when
        QueryResults<ErrorServiceDto> result = tracedLogRepositoryImpl.findErrorOccurServicesByLogType(searchDate, orderInfo, pageable);

        // then
        Assertions.assertThat(result.getResults().size()).isEqualTo(1);
        Assertions.assertThat(result.getResults().get(0).getTagId()).isEqualTo("service_z");
        Assertions.assertThat(result.getResults().get(0).getErrorOccurCount()).isEqualTo(2);
        Assertions.assertThat(result.getTotal()).isEqualTo(3);
    }


    @Test
    public void 서비스별_에러발생횟수조회___성공___대상없음() {
        // given
        final LocalDate searchDate = LocalDate.of(2021, 12, 1);
        final Pageable pageable = PageRequest.of(0, 1);
        LinkedHashMap<String, String> orderInfo = new LinkedHashMap<>();
        orderInfo.put("errorOccurCount", "asc");
        orderInfo.put("tagId", "asc");

        // when
        QueryResults<ErrorServiceDto> result = tracedLogRepositoryImpl.findErrorOccurServicesByLogType(searchDate, orderInfo, pageable);

        // then
        Assertions.assertThat(result.getResults().size()).isEqualTo(0);
    }


    /**
     * 서비스별 처리시간 목록 조회 성공
     * select tag_id, timediff(max(logging_time), min(logging_time)) as process_time
     * from traced_log t1
     * where logging_date = '2021-12-14'
     * group by trace_id, tag_id;
     *
     * 결과 : (traceId가 다름) - 서비스 단위에서 group by tagId
     * service_x	00:00:01
     * service_x	00:00:02.600000000
     * service_y	00:00:00.300000000
     * service_y	00:00:05
     * service_z	00:00:00.900000000
     * service_x	00:00:00.130000000
     */
    @Test
    public void 서비스별_처리시간조회___성공() {
        // given
        final LocalDate searchDate = LocalDate.of(2021, 12, 14);

        // when
        List<ServiceProcessTimeInTraceIdDto> result = tracedLogRepositoryImpl.findServiceProcessTime(searchDate);

        // then
        Assertions.assertThat(result.size()).isEqualTo(6);
        Assertions.assertThat(result.get(0).getTagId()).isEqualTo("service_x");
        Assertions.assertThat(result.get(0).getProcessTime()).isEqualTo(-1000000L);
        Assertions.assertThat(result.get(1).getTagId()).isEqualTo("service_x");
        Assertions.assertThat(result.get(1).getProcessTime()).isEqualTo(-2600000L);
        Assertions.assertThat(result.get(2).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.get(2).getProcessTime()).isEqualTo(-300000L);
        Assertions.assertThat(result.get(3).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.get(3).getProcessTime()).isEqualTo(-5000000L);
        Assertions.assertThat(result.get(4).getTagId()).isEqualTo("service_z");
        Assertions.assertThat(result.get(4).getProcessTime()).isEqualTo(-900000L);
        Assertions.assertThat(result.get(5).getTagId()).isEqualTo("service_x");
        Assertions.assertThat(result.get(5).getProcessTime()).isEqualTo(-130000L);
    }


    @Test
    public void 서비스별_처리시간조회___성공___대상없음() {
        // given
        final LocalDate searchDate = LocalDate.of(2021, 12, 1);

        // when
        List<ServiceProcessTimeInTraceIdDto> result = tracedLogRepositoryImpl.findServiceProcessTime(searchDate);

        // then
        Assertions.assertThat(result.size()).isEqualTo(0);
    }


    /**
     * 서비스별 호출횟수 목록 조회 성공
     * select tag_id, count(tag_id)
     * from traced_log
     * where logging_date = '2021-12-14'
     * group by tag_id
     * order by tag_id;
     *
     * 결과 :
     * service_x	6
     * service_y	4
     * service_z	2
     * 스캔 범위 : index
     */
    @Test
    public void 서비스별_호출횟수___성공() {
        // given
        final LocalDate searchDate = LocalDate.of(2021, 12, 14);

        // when
        List<ServiceCallCountDto> result = tracedLogRepositoryImpl.findServicesCallCount(searchDate);

        // then
        Assertions.assertThat(result.size()).isEqualTo(3);
        Assertions.assertThat(result.get(0).getTagId()).isEqualTo("service_x");
        Assertions.assertThat(result.get(0).getServiceCallCount()).isEqualTo(6);
        Assertions.assertThat(result.get(1).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.get(1).getServiceCallCount()).isEqualTo(4);
        Assertions.assertThat(result.get(2).getTagId()).isEqualTo("service_z");
        Assertions.assertThat(result.get(2).getServiceCallCount()).isEqualTo(2);
    }


    @Test
    public void 서비스별_호출횟수___성공___대상없음() {
        // given
        final LocalDate searchDate = LocalDate.of(2021, 12, 1);

        // when
        List<ServiceCallCountDto> result = tracedLogRepositoryImpl.findServicesCallCount(searchDate);

        // then
        Assertions.assertThat(result.size()).isEqualTo(0);
    }


    /**
     * 추적 ID 내용 검색 성공
     * select trace_id, tag_id, log_type, log_data, logging_time
     * from traced_log
     * where trace_id = "t2"
     * and tag_id like '%%'
     * and log_type like "%%"
     * and log_data like "%%"
     * and logging_date = '2021-12-14'
     * order by log_type desc;
     *
     * 결과 :
     * t2	service_y	INFO	{"service_y" : {"status":"START"}}	2021-12-14 02:14:23.264322000
     * t2	service_x	ERROR	service_x starts	2021-12-14 02:14:22.264322000
     * t2	service_y	ERROR	{"service_y" : {"status":"END"}}	2021-12-14 02:14:23.564322000
     * t2	service_x	ERROR	service_x ends	2021-12-14 02:14:24.864322000
     */
    @Test
    public void 추적ID내용검색___성공___로그타입정렬() {
        // given
        final Pageable pageable = PageRequest.of(0, 10);
        final String orderKey = "logType";

        final Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("traceId", "t2");
        searchVariable.put("tagId", "");
        searchVariable.put("logType", "");
        searchVariable.put("logData", "");
        searchVariable.put("loggingDate", LocalDate.of(2021, 12, 14));

        final LinkedHashMap<String, String> orderInfo = new LinkedHashMap<>();
        orderInfo.put("logType", "desc");

        // when
        QueryResults<TracedLogDto> result = tracedLogRepositoryImpl.findLogsByTraceId(searchVariable, orderInfo, orderKey, pageable);

        // then
        Assertions.assertThat(result.getResults().size()).isEqualTo(4);
        Assertions.assertThat(result.getResults().get(0).getTraceId()).isEqualTo("t2");
        Assertions.assertThat(result.getResults().get(0).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(0).getLogData()).isEqualTo("{\"service_y\" : {\"status\":\"START\"}}");
        Assertions.assertThat(result.getResults().get(0).getLogType()).isEqualTo("INFO");

        Assertions.assertThat(result.getResults().get(1).getTraceId()).isEqualTo("t2");
        Assertions.assertThat(result.getResults().get(1).getTagId()).isEqualTo("service_x");
        Assertions.assertThat(result.getResults().get(1).getLogData()).isEqualTo("service_x starts");
        Assertions.assertThat(result.getResults().get(1).getLogType()).isEqualTo("ERROR");

        Assertions.assertThat(result.getResults().get(2).getTraceId()).isEqualTo("t2");
        Assertions.assertThat(result.getResults().get(2).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(2).getLogData()).isEqualTo("{\"service_y\" : {\"status\":\"END\"}}");
        Assertions.assertThat(result.getResults().get(2).getLogType()).isEqualTo("ERROR");

        Assertions.assertThat(result.getResults().get(3).getTraceId()).isEqualTo("t2");
        Assertions.assertThat(result.getResults().get(3).getTagId()).isEqualTo("service_x");
        Assertions.assertThat(result.getResults().get(3).getLogData()).isEqualTo("service_x ends");
        Assertions.assertThat(result.getResults().get(3).getLogType()).isEqualTo("ERROR");
    }


    @Test // todo 타임존
    public void 추적ID내용검색___성공___로깅시각정렬() {
        // given
        final Pageable pageable = PageRequest.of(0, 10);
        final String orderKey = "loggingTime";

        final Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("traceId", "t2");
        searchVariable.put("tagId", "");
        searchVariable.put("logType", "");
        searchVariable.put("logData", "");
        searchVariable.put("loggingDate", LocalDate.of(2021, 12, 14));

        final LinkedHashMap<String, String> orderInfo = new LinkedHashMap<>();
        orderInfo.put("loggingTime", "desc");

        // when
        QueryResults<TracedLogDto> result = tracedLogRepositoryImpl.findLogsByTraceId(searchVariable, orderInfo, orderKey, pageable);

        // then
        Assertions.assertThat(result.getResults().size()).isEqualTo(4);

        Assertions.assertThat(result.getResults().get(0).getTraceId()).isEqualTo("t2");
        Assertions.assertThat(result.getResults().get(0).getTagId()).isEqualTo("service_x");
        Assertions.assertThat(result.getResults().get(0).getLogData()).isEqualTo("service_x ends");
        Assertions.assertThat(result.getResults().get(0).getLogType()).isEqualTo("ERROR");
        Assertions.assertThat(result.getResults().get(0).getLoggingTime()).isEqualTo(LocalDateTime.of(2021,12,14,2,14,24,864322000));

        Assertions.assertThat(result.getResults().get(1).getTraceId()).isEqualTo("t2");
        Assertions.assertThat(result.getResults().get(1).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(1).getLogData()).isEqualTo("{\"service_y\" : {\"status\":\"END\"}}");
        Assertions.assertThat(result.getResults().get(1).getLogType()).isEqualTo("ERROR");
        Assertions.assertThat(result.getResults().get(1).getLoggingTime()).isEqualTo(LocalDateTime.of(2021,12,14,2,14,23,564322000));

        Assertions.assertThat(result.getResults().get(2).getTraceId()).isEqualTo("t2");
        Assertions.assertThat(result.getResults().get(2).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(2).getLogData()).isEqualTo("{\"service_y\" : {\"status\":\"START\"}}");
        Assertions.assertThat(result.getResults().get(2).getLogType()).isEqualTo("INFO");
        Assertions.assertThat(result.getResults().get(2).getLoggingTime()).isEqualTo(LocalDateTime.of(2021,12,14,2,14,23,264322000));

        Assertions.assertThat(result.getResults().get(3).getTraceId()).isEqualTo("t2");
        Assertions.assertThat(result.getResults().get(3).getTagId()).isEqualTo("service_x");
        Assertions.assertThat(result.getResults().get(3).getLogData()).isEqualTo("service_x starts");
        Assertions.assertThat(result.getResults().get(3).getLogType()).isEqualTo("ERROR");
        Assertions.assertThat(result.getResults().get(3).getLoggingTime()).isEqualTo(LocalDateTime.of(2021,12,14,2,14,22,264322000));
    }


    @Test
    public void 추적ID내용검색___성공___서비스명정렬() {
        // given
        final Pageable pageable = PageRequest.of(0, 10);
        final String orderKey = "tagId";

        final Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("traceId", "t2");
        searchVariable.put("tagId", "");
        searchVariable.put("logType", "");
        searchVariable.put("logData", "");
        searchVariable.put("loggingDate", LocalDate.of(2021, 12, 14));

        final LinkedHashMap<String, String> orderInfo = new LinkedHashMap<>();
        orderInfo.put("tagId", "asc");

        // when
        QueryResults<TracedLogDto> result = tracedLogRepositoryImpl.findLogsByTraceId(searchVariable, orderInfo, orderKey, pageable);

        // then
        Assertions.assertThat(result.getResults().size()).isEqualTo(4);

        Assertions.assertThat(result.getResults().get(0).getTraceId()).isEqualTo("t2");
        Assertions.assertThat(result.getResults().get(0).getTagId()).isEqualTo("service_x");
        Assertions.assertThat(result.getResults().get(0).getLogData()).isEqualTo("service_x starts");
        Assertions.assertThat(result.getResults().get(0).getLogType()).isEqualTo("ERROR");

        Assertions.assertThat(result.getResults().get(1).getTraceId()).isEqualTo("t2");
        Assertions.assertThat(result.getResults().get(1).getTagId()).isEqualTo("service_x");
        Assertions.assertThat(result.getResults().get(1).getLogData()).isEqualTo("service_x ends");
        Assertions.assertThat(result.getResults().get(1).getLogType()).isEqualTo("ERROR");

        Assertions.assertThat(result.getResults().get(2).getTraceId()).isEqualTo("t2");
        Assertions.assertThat(result.getResults().get(2).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(2).getLogData()).isEqualTo("{\"service_y\" : {\"status\":\"START\"}}");
        Assertions.assertThat(result.getResults().get(2).getLogType()).isEqualTo("INFO");

        Assertions.assertThat(result.getResults().get(3).getTraceId()).isEqualTo("t2");
        Assertions.assertThat(result.getResults().get(3).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(3).getLogData()).isEqualTo("{\"service_y\" : {\"status\":\"END\"}}");
        Assertions.assertThat(result.getResults().get(3).getLogType()).isEqualTo("ERROR");
    }


    @Test
    public void 추적ID내용검색___성공___페이지네이션() {
        // given
        final Pageable pageable = PageRequest.of(0, 1);
        final String orderKey = "tagId";

        final Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("traceId", "t2");
        searchVariable.put("tagId", "");
        searchVariable.put("logType", "");
        searchVariable.put("logData", "");
        searchVariable.put("loggingDate", LocalDate.of(2021, 12, 14));

        final LinkedHashMap<String, String> orderInfo = new LinkedHashMap<>();
        orderInfo.put("tagId", "asc");

        // when
        QueryResults<TracedLogDto> result = tracedLogRepositoryImpl.findLogsByTraceId(searchVariable, orderInfo, orderKey, pageable);

        // then
        Assertions.assertThat(result.getResults().size()).isEqualTo(1);
        Assertions.assertThat(result.getResults().get(0).getTraceId()).isEqualTo("t2");
        Assertions.assertThat(result.getResults().get(0).getTagId()).isEqualTo("service_x");
        Assertions.assertThat(result.getResults().get(0).getLogData()).isEqualTo("service_x starts");
        Assertions.assertThat(result.getResults().get(0).getLogType()).isEqualTo("ERROR");
        Assertions.assertThat(result.getTotal()).isEqualTo(4);
    }


    @Test
    public void 추적ID내용검색___성공___대상없음() {
        // given
        final Pageable pageable = PageRequest.of(0, 1);
        final String orderKey = "tagId";

        final Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("traceId", "t2");
        searchVariable.put("tagId", "");
        searchVariable.put("logType", "");
        searchVariable.put("logData", "");
        searchVariable.put("loggingDate", LocalDate.of(2021, 12, 1));

        final LinkedHashMap<String, String> orderInfo = new LinkedHashMap<>();
        orderInfo.put("tagId", "asc");

        // when
        QueryResults<TracedLogDto> result = tracedLogRepositoryImpl.findLogsByTraceId(searchVariable, orderInfo, orderKey, pageable);

        // then
        Assertions.assertThat(result.getResults().size()).isEqualTo(0);
    }


    /**
     * 서비스 ID 내용 검색 성공
     * select trace_id, tag_id, log_type, log_data, logging_time
     * from traced_log
     * where tag_id = 'service_a'
     * and log_type like "%%"
     * and log_data like "%%"
     * and logging_date = '2021-11-14'
     * order by log_type;
     *
     * 결과 :
     * t2	service_y	INFO	{"service_y" : {"status":"START"}}	2021-12-14 02:14:23.264322000
     * t2	service_y	ERROR	{"service_y" : {"status":"END"}}	2021-12-14 02:14:23.564322000
     * t3	service_y	ERROR	{"service_y" : {"status":"START"}}	2021-12-14 02:14:25.264322000
     * t3	service_y	ERROR	{"service_y" : {"status":"END"}}	2021-12-14 02:14:30.264322000
     *
     * 스캔 범위위 : ref
     */
    @Test
    public void 서비스ID내용검색___성공___로그타입정렬() {
        // given
        final Pageable pageable = PageRequest.of(0, 10);
        final String orderKey = "logType";

        final Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("tagId", "service_y");
        searchVariable.put("logType", "");
        searchVariable.put("logData", "");
        searchVariable.put("loggingDate", LocalDate.of(2021, 12, 14));

        final LinkedHashMap<String, String> orderInfo = new LinkedHashMap<>();
        orderInfo.put("logType", "desc");

        // when
        QueryResults<TracedLogDto> result = tracedLogRepositoryImpl.findLogsByTagId(searchVariable, orderInfo, orderKey, pageable);

        // then
        Assertions.assertThat(result.getResults().size()).isEqualTo(4);
        Assertions.assertThat(result.getResults().get(0).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(0).getLogType()).isEqualTo("INFO");
        Assertions.assertThat(result.getResults().get(0).getLogData()).isEqualTo("{\"service_y\" : {\"status\":\"START\"}}");

        Assertions.assertThat(result.getResults().get(1).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(1).getLogType()).isEqualTo("ERROR");
        Assertions.assertThat(result.getResults().get(1).getLogData()).isEqualTo("{\"service_y\" : {\"status\":\"END\"}}");

        Assertions.assertThat(result.getResults().get(2).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(2).getLogType()).isEqualTo("ERROR");
        Assertions.assertThat(result.getResults().get(2).getLogData()).isEqualTo("{\"service_y\" : {\"status\":\"START\"}}");

        Assertions.assertThat(result.getResults().get(3).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(3).getLogType()).isEqualTo("ERROR");
        Assertions.assertThat(result.getResults().get(3).getLogData()).isEqualTo("{\"service_y\" : {\"status\":\"END\"}}");
    }


    @Test
    public void 서비스ID내용검색___성공___로깅시각정렬() {
        // given
        final Pageable pageable = PageRequest.of(0, 10);
        final String orderKey = "loggingTime";

        final Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("tagId", "service_y");
        searchVariable.put("logType", "");
        searchVariable.put("logData", "");
        searchVariable.put("loggingDate", LocalDate.of(2021, 12, 14));

        final LinkedHashMap<String, String> orderInfo = new LinkedHashMap<>();
        orderInfo.put("loggingTime", "desc");

        // when
        QueryResults<TracedLogDto> result = tracedLogRepositoryImpl.findLogsByTagId(searchVariable, orderInfo, orderKey, pageable);

        // then
        Assertions.assertThat(result.getResults().size()).isEqualTo(4);
        Assertions.assertThat(result.getResults().get(0).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(0).getLogType()).isEqualTo("ERROR");
        Assertions.assertThat(result.getResults().get(0).getLoggingTime()).isEqualTo(LocalDateTime.of(2021, 12, 14, 2, 14, 30, 264322000));

        Assertions.assertThat(result.getResults().get(1).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(1).getLogType()).isEqualTo("ERROR");
        Assertions.assertThat(result.getResults().get(1).getLoggingTime()).isEqualTo(LocalDateTime.of(2021, 12, 14, 2, 14, 25, 264322000));

        Assertions.assertThat(result.getResults().get(2).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(2).getLogType()).isEqualTo("ERROR");
        Assertions.assertThat(result.getResults().get(2).getLoggingTime()).isEqualTo(LocalDateTime.of(2021, 12, 14, 2, 14, 23, 564322000));

        Assertions.assertThat(result.getResults().get(3).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(3).getLogType()).isEqualTo("INFO");
        Assertions.assertThat(result.getResults().get(3).getLoggingTime()).isEqualTo(LocalDateTime.of(2021, 12, 14, 2, 14, 23, 264322000));
    }

    @Test
    public void 서비스ID내용검색___성공___페이지네이션() {
        // given
        final Pageable pageable = PageRequest.of(0, 1);
        final String orderKey = "loggingTime";

        final Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("tagId", "service_y");
        searchVariable.put("logType", "");
        searchVariable.put("logData", "");
        searchVariable.put("loggingDate", LocalDate.of(2021, 12, 14));

        final LinkedHashMap<String, String> orderInfo = new LinkedHashMap<>();
        orderInfo.put("loggingTime", "desc");

        // when
        QueryResults<TracedLogDto> result = tracedLogRepositoryImpl.findLogsByTagId(searchVariable, orderInfo, orderKey, pageable);

        // then
        Assertions.assertThat(result.getResults().size()).isEqualTo(1);
        Assertions.assertThat(result.getResults().get(0).getTagId()).isEqualTo("service_y");
        Assertions.assertThat(result.getResults().get(0).getLogType()).isEqualTo("ERROR");
        Assertions.assertThat(result.getTotal()).isEqualTo(4);
    }

    @Test
    public void 서비스ID내용검색___성공___대상없음() {
        // given
        final Pageable pageable = PageRequest.of(0, 1);
        final String orderKey = "loggingTime";

        final Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("tagId", "service_y");
        searchVariable.put("logType", "");
        searchVariable.put("logData", "");
        searchVariable.put("loggingDate", LocalDate.of(2021, 12, 1));

        final LinkedHashMap<String, String> orderInfo = new LinkedHashMap<>();
        orderInfo.put("loggingTime", "desc");

        // when
        QueryResults<TracedLogDto> result = tracedLogRepositoryImpl.findLogsByTagId(searchVariable, orderInfo, orderKey, pageable);

        // then
        Assertions.assertThat(result.getResults().size()).isEqualTo(0);
    }

}
