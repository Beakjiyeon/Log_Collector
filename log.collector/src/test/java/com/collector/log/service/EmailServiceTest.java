package com.collector.log.service;

import com.collector.log.config.ConstantYml;
import com.collector.log.domain.TracedLog;
import com.collector.log.domain.UndeliveredMail;
import com.collector.log.domain.UndeliveredMailRepository;
import com.collector.log.domain.UndeliveredMailRepositoryImpl;
import com.collector.log.dto.UndeliveredMailDto;
import com.collector.log.handler.CustomResultMessage;
import com.collector.log.service.EmailService;
import com.querydsl.core.QueryResults;
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
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

/**
 * 메일전송처리 동작 여부 테스트 [emailService.java]
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private UndeliveredMailRepositoryImpl undeliveredMailRepositoryImpl;

    @Mock
    private UndeliveredMailRepository undeliveredMailRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ConstantYml constantYml; // required

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();


    @Test
    public void 실패메일조회___성공(){
        // data preparation
        UndeliveredMailDto undeliveredMail = UndeliveredMailDto.builder()
                .causeKeyword("실패원인 키워드")
                .causeContent("실패원인 상세내용")
                .logId(100L)
                .traceId("t100")
                .build();

        List<UndeliveredMailDto> failedMailDtos = new ArrayList<>();
        failedMailDtos.add(undeliveredMail);
        Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("regDate", LocalDate.now());
        String order = "createAt";
        Pageable pageable = PageRequest.of(1, 10);
        QueryResults<UndeliveredMailDto> repoResult = new QueryResults<>(failedMailDtos, 10L, 0L, 1);
        // mocking
        // 유효성 검사 로직은 빈으로 등록하지 않아서 mock 필요없다.
        // 하나의 서비스 로직에 유효성 검사가 포함되어 있는 것이므로 @Service 어노테이션을 제거했다.
        when(undeliveredMailRepositoryImpl.findMails(any(), any(), any())).thenReturn(repoResult);

        // when
        Page<UndeliveredMailDto> result = emailService.getFailedMails(searchVariable, order, pageable);

        // then
        Assertions.assertThat(result.getContent().size()).isEqualTo(1);
        Assertions.assertThat(result.getContent().get(0)).isSameAs(undeliveredMail);
    }


    @Test
    public void 실패메일조회___실패___유효하지않은정렬조건() {
        exceptionRule.expect(Exception.class);
        // data preparation
        UndeliveredMailDto undeliveredMail = UndeliveredMailDto.builder()
                .causeKeyword("실패원인 키워드")
                .causeContent("실패원인 상세내용")
                .logId(100L)
                .traceId("t100")
                .build();

        List<UndeliveredMailDto> failedMailDtos = new ArrayList<>();
        failedMailDtos.add(undeliveredMail);
        Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("regDate", LocalDate.now());
        String order = "regDate";
        Pageable pageable = PageRequest.of(1, 10);

        // when
        emailService.getFailedMails(searchVariable, order, pageable);
    }

    @Test
    public void 실패메일조회___실패__음수페이지넘버() {
        // PageRequest.of(-1, 10) 부터 컴파일 에러발생
    }



    @Test
    public void 메일전송처리___성공() throws InterruptedException, ExecutionException {
        // data preparation
        TracedLog tracedLog = TracedLog.builder()
                .id(1L)
                .traceId("t1")
                .tagId("s1")
                .logType("ERROR")
                .logData("LOG_DATA")
                .loggingTime(LocalDateTime.now())
                .build();

        // when
        Future<String> futureResult = (Future<String>) emailService.sendMail(tracedLog);
        // get() : 비동기 스레드가 끝날 때까지 block
        // get(long timeout, TimeUnit unit) : 대기시간 지정가능
        String result = futureResult.get();

        // then
        Assertions.assertThat(result).isEqualTo(CustomResultMessage.MAIL_OK.getMessage());
    }

    @Test
    public void 메일전송처리___실패() throws MailException {

        // data preparation
        TracedLog tracedLog = TracedLog.builder()
                .id(10L)
                .traceId("t1")
                .tagId("s1")
                .logType("ERROR")
                .logData("LOG_DATA")
                .loggingTime(LocalDateTime.now())
                .build();
        UndeliveredMail undeliveredMail = UndeliveredMail.builder()
                .id(1L)
                .causeKeyword("실패원인 키워드")
                .causeContent("실패원인 상세내용")
                .tracedLog(tracedLog)
                .build();

        // mocking
        when(undeliveredMailRepository.save(any())).thenReturn(undeliveredMail);
        // 메일센더 send 에서 예외를 목업시키는작업
        doThrow(new MailSendException("메세지", new Throwable("원인"))).when(mailSender).send((SimpleMailMessage) any());
        // MailSendException e = new MailSendException(null, new Throwable("원인"));
        // System.out.println(e.getCause());
        // System.out.println(e.getCause().getLocalizedMessage());

        // when
        Object rawResult = emailService.sendMail(tracedLog);

        // MailException 이 발생하기 때문에 Future 을 리턴하지 않고(null), db 저장한 failMail 엔티티를 리턴한다.
        UndeliveredMail result = (UndeliveredMail) rawResult;
        Assertions.assertThat((result.getTracedLog().getTraceId())).isEqualTo(tracedLog.getTraceId());

    }
}
