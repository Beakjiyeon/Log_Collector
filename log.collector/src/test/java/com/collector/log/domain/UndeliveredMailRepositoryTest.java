package com.collector.log.domain;


import com.collector.log.config.TestConfig;
import com.collector.log.handler.CustomErrorCode;
import org.assertj.core.api.Assertions;
import org.hibernate.exception.DataException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


/**
 * 전송에 실패한 메일 INSERT 테스트
 */

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestConfig.class)
@DataJpaTest
@RunWith(SpringRunner.class)
public class UndeliveredMailRepositoryTest {

    @Autowired
    private UndeliveredMailRepository undeliveredMailRepository;

    @Autowired
    private EntityManager entityManagr;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        // INSERT 테스트메소드 여러 개를 한번에 실행하는 경우 AUTO_INCREMENT 증감 유지 이슈 해결
        // 참고: https://bit.ly/3pLmsie, https://bit.ly/31LppHi
        this.entityManagr
                // 로컬 DB 이용시
                .createNativeQuery("ALTER TABLE undelivered_mail AUTO_INCREMENT = 1;")
                // H2 DB 이용시
                // .createNativeQuery(""ALTER TABLE traced_log ALTER COLUMN `id` RESTART WITH 1")
                .executeUpdate();
    }

    /* ########################################## 실패 메일 저장 ########################################## */

    @Test
    public void 실패메일저장___성공() {
        // given
        UndeliveredMail undeliveredMail = UndeliveredMail.builder()
                .causeKeyword("실패원인 키워드")
                .causeContent("실패원인 상세내용")
                .tracedLog(TracedLog.builder()
                        .id(5L)
                        .traceId("T2")
                        .build())
                .build();

        // when
        UndeliveredMail savedMail = undeliveredMailRepository.save(undeliveredMail);

        // then
        Assertions.assertThat(savedMail.getId()).isEqualTo(6);
        Assertions.assertThat(savedMail).isSameAs(undeliveredMail);
        Assertions.assertThat(undeliveredMailRepository.count()).isEqualTo(6); // TEST_DB : LAST_ID + 1
    }


    @Test
    public void 실패메일저장___실패___유효하지않은필드값경우() {
        // then
        exceptionRule.expect(DataIntegrityViolationException.class);

        // given
        UndeliveredMail undeliveredMail = UndeliveredMail.builder()
                .causeKeyword("실패원인 키워드"
                        + "실패원인 키워드"
                        + "실패원인 키워드"
                        + "실패원인 키워드"
                        + "실패원인 키워드"
                        + "실패원인 키워드"
                        + "실패원인 키워드"
                        + "실패원인 키워드"
                        + "실패원인 키워드"
                        + "실패원인 키워드"
                        + "실패원인 키워드"
                        + "실패원인 키워드"
                        + "실패원인 키워드")
                .causeContent("실패원인 상세내용")
                .tracedLog(TracedLog.builder()
                        .id(5L)
                        .traceId("T2")
                        .build())
                .build();

        // when
        undeliveredMailRepository.save(undeliveredMail);
    }
}
