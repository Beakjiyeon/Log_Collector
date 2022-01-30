package com.collector.log.service.v1;

import com.collector.log.domain.TracedLog;
import com.collector.log.domain.TracedLogRepository;
import com.collector.log.dto.LogSaveRequestDto;
import com.collector.log.service.CollectLogService;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


/**
 * 로그저장 및 메일처리요청 (비즈니스 로직처리) 동작 여부 테스트 [collectLogService.java]
 * - MockitoJUnitRunner: 스프링 컨테이너 로드 안함
 * - SpringRunner: 스프링 컨테이너 로드
 * - @Mock, @MockBean, @Spy, @SpyBean: DB 의존없는 테스트가능
 */
@RunWith(MockitoJUnitRunner.class)
public class CollectLogServiceTest {

    @InjectMocks // @Mock 으로 선언된 가짜 객체들은 의존함을 선언
    private CollectLogService collectLogService;

    @Mock // 실제 의존하고 있는 서비스, 리포지터리 Bean 에 의존하지 않고 단위 테스트가 가능
    private TracedLogRepository tracedLogRepository;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void 로그저장___성공___INFO타입() {
        // data preparation
        LogSaveRequestDto saveRequestDto = LogSaveRequestDto.builder()
                .traceId("T1")
                .tagId("S1")
                .logType("INFO")
                .logData("DATA")
                .loggingTime(LocalDateTime.now())
                .build();

        TracedLog tracedLog = TracedLog.builder()
                .id(6L)
                .traceId(saveRequestDto.getTraceId())
                .tagId(saveRequestDto.getTagId())
                .logType(saveRequestDto.getLogType())
                .logData(saveRequestDto.getLogData())
                .loggingTime(saveRequestDto.getLoggingTime())
                .loggingDate(saveRequestDto.getLoggingTime().toLocalDate())
                .build();

        // mocking
        when(tracedLogRepository.save(any())).thenReturn(tracedLog);

        // when
        Map<String, Long> result = collectLogService.save(saveRequestDto);

        // then
        Assertions.assertThat(result.get("id")).isNotNull();
        Assertions.assertThat(result.get("id")).isEqualTo(6L);
    }



    @Test()
    public void 로그저장___성공___ERROR타입() throws InterruptedException {
        /*
         * 로그 타입이 ERROR 인 경우,
         * 서비스 로직에서 EmailService.sendMail 이라는 비동기 메소드가 호출되지만
         * EmailService 를 목업 처리했기 때문에 failedMailRepositoryImpl.save() 실햏되는 일이 없다.
         * -> FailedMailRepository 목업은 필요 없다.
         * -> 비동기 관련 테스트 코드는 해당하는 메소드가 속한 서비스 단위테스트 안에 작성한다.
         *
         * 로그 타입이 ERROR 이여도 최종 반환 값은 DB에 저장된 TracedLog 엔티티 response 이다.
         * 메일 전송은 다른 스레드에서 처리되므로 이 테스트 메소드에선 신경쓰지 않는다.
         */

        // data preparation
        LogSaveRequestDto saveRequestDto = LogSaveRequestDto.builder()
                .traceId("T1")
                .tagId("S1")
                .logType("error")
                .logData("DATA")
                .loggingTime(LocalDateTime.now())
                .build();

        TracedLog tracedLog = TracedLog.builder()
                .id(6L)
                .traceId(saveRequestDto.getTraceId())
                .tagId(saveRequestDto.getTagId())
                .logType(saveRequestDto.getLogType())
                .logData(saveRequestDto.getLogData())
                .loggingTime(saveRequestDto.getLoggingTime())
                .loggingDate(saveRequestDto.getLoggingTime().toLocalDate())
                .build();

        // mocking
        when(tracedLogRepository.save(any())).thenReturn(tracedLog);

        // when
        Map<String, Long> result = collectLogService.save(saveRequestDto);

        // then
        Assertions.assertThat(result.get("id")).isNotNull();
        Assertions.assertThat(result.get("id")).isEqualTo(6L);
    }


    @Test
    public void 로그저장___실패___유효하지않은인자값() {
        // assert exception message
        exceptionRule.expect(Exception.class);

        // data preparation
        LogSaveRequestDto saveRequestDto = LogSaveRequestDto.builder()
                .traceId("T1")
                .tagId("S1")
                .logType("INFO_TOO_LONG_LENGTH")
                .logData("DATA")
                .loggingTime(LocalDateTime.now())
                .build();

        /* TracedLog 인스턴스를 생성하고, 스텁 처리를 하지 않는 이유는
         * 리포지터리.save() 문이 실행되기 전에 validate 과정에서 throw 발생하기 때문이다.
         * 필요없는 스텁 처리 시, 테스트 fail()
        */

        // when
        collectLogService.save(saveRequestDto);
    }
}
