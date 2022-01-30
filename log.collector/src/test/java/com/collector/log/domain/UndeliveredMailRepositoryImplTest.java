package com.collector.log.domain;


import com.collector.log.config.TestConfig;
import com.collector.log.dto.UndeliveredMailDto;
import com.querydsl.core.QueryResults;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import java.util.HashMap;
import java.util.Map;

/**
 * 전송에 실패한 메일목록 SELECT 테스트
 * TEST_DB 내용 기반 Assert
 */
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestConfig.class)
@RunWith(SpringRunner.class)
@DataJpaTest()
public class UndeliveredMailRepositoryImplTest {

    @Autowired
    private UndeliveredMailRepositoryImpl undeliveredMailRepositoryImpl;

    private final LocalDate REG_DATE = LocalDate.of(2021, 12, 15);
    private final String ORDER_CONDITION = "createAt";

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();


    /* ########################################## 실패 메일 조회 ########################################## */
    // 필수정보 : 조회날짜, 페이징
    // 옵션정보 : 정렬기준 - 조회날짜 ASC(디폴트), DESC. LocalDateTime 단위 정렬
    @Test
    public void 실패메일조회___성공___특정날짜에_등록된_메일기록_페이지네이션_조회() {
        // given
        Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("regDate", REG_DATE);
        String order = ORDER_CONDITION;
        Pageable pageable = PageRequest.of(0, 2);

        // when
        QueryResults<UndeliveredMailDto> result = undeliveredMailRepositoryImpl.findMails(searchVariable, order, pageable);

        // then
        Assertions.assertThat(result.getResults().get(0).getId()).isEqualTo(1);
        Assertions.assertThat(result.getResults().get(0).getTraceId()).isEqualTo("t1");
        Assertions.assertThat(result.getResults().get(0).getCauseKeyword()).isEqualTo("MailAuthentication");
        Assertions.assertThat(result.getResults().get(1).getId()).isEqualTo(2);
        Assertions.assertThat(result.getResults().get(1).getTraceId()).isEqualTo("t1");
        Assertions.assertThat(result.getResults().get(1).getCauseKeyword()).isEqualTo("MailAuthentication");
        Assertions.assertThat(result.getTotal()).isEqualTo(5);
    }

    @Test
    public void 실패메일조회___성공___내림차순정렬() {
        // given
        Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("regDate", REG_DATE);
        String order = "-" + ORDER_CONDITION;
        Pageable pageable = PageRequest.of(0, 2);

        // when
        QueryResults<UndeliveredMailDto> result = undeliveredMailRepositoryImpl.findMails(searchVariable, order, pageable);

        // then
        Assertions.assertThat(result.getResults().get(0).getId()).isEqualTo(5);
        Assertions.assertThat(result.getResults().get(0).getTraceId()).isEqualTo("t3");
        Assertions.assertThat(result.getResults().get(0).getCauseKeyword()).isEqualTo("MailAuthentication");
        Assertions.assertThat(result.getResults().get(0).getLogId()).isEqualTo(12);
        Assertions.assertThat(result.getResults().get(1).getId()).isEqualTo(4);
        Assertions.assertThat(result.getResults().get(1).getTraceId()).isEqualTo("t3");
        Assertions.assertThat(result.getResults().get(1).getCauseKeyword()).isEqualTo("MailAuthentication");
        Assertions.assertThat(result.getResults().get(1).getLogId()).isEqualTo(9);
        Assertions.assertThat(result.getTotal()).isEqualTo(5);
    }


    @Test
    public void 실패메일조회___실패___날짜정보누락하는경우_메일기록_조회() {
        exceptionRule.expect(Exception.class);
        // given
        Map<String, Object> searchVariable = new HashMap<>();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        undeliveredMailRepositoryImpl.findMails(searchVariable, ORDER_CONDITION, pageable);
    }


    @Test
    public void 실패메일조회___실패___페이징정보누락하는경우_메일기록_조회() {
        exceptionRule.expect(Exception.class);
        // given
        Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("regDate", REG_DATE);

        // when
        undeliveredMailRepositoryImpl.findMails(searchVariable, ORDER_CONDITION, null);
    }


    @Test
    public void 실패메일조회___실패___정렬기준정보누락하는경우_메일기록_조회() {
        // given
        Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("regDate", REG_DATE);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        QueryResults<UndeliveredMailDto> result = undeliveredMailRepositoryImpl.findMails(searchVariable, null, pageable);

        // then
        Assertions.assertThat(result.getResults().get(0).getId()).isEqualTo(1);
        Assertions.assertThat(result.getResults().get(0).getTraceId()).isEqualTo("t1");
        Assertions.assertThat(result.getResults().get(0).getCauseKeyword()).isEqualTo("MailAuthentication");
        Assertions.assertThat(result.getResults().get(1).getId()).isEqualTo(2);
        Assertions.assertThat(result.getResults().get(1).getTraceId()).isEqualTo("t1");
        Assertions.assertThat(result.getResults().get(1).getCauseKeyword()).isEqualTo("MailAuthentication");
        Assertions.assertThat(result.getTotal()).isEqualTo(5);
    }




}
