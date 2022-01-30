package com.collector.log.domain;


import com.collector.log.config.TestConfig;
import com.collector.log.dto.LogSaveRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;


/**
 * 전송에 실패한 메일 INSERT 테스트 [failedMailRepository.java]
 *
 * [@DataJpaTest]
 * - Entity 스캔
 * - JPA CRUD 테스트
 * - Repository 관련 빈만 로드
 * - @Transaction 기본 내장
 * - 디폴트 내장 DB
 */

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 로컬 DB
@Import(TestConfig.class) // @DataJpaTest, QueryDsl 함께 사용. 참고: https://bit.ly/30aS5Ji
@DataJpaTest
@RunWith(SpringRunner.class)
public class TracedLogRepositoryTest {

    @Autowired
    private TracedLogRepository tracedLogRepository;
    @Autowired
    private EntityManager entityManagr;


    @Before
    public void setUp() {
        // INSERT 테스트메소드 여러 개를 한번에 실행하는 경우 AUTO_INCREMENT 증감 유지 이슈 해결
        // 참고: https://bit.ly/3pLmsie, https://bit.ly/31LppHi
        this.entityManagr
                // 로컬 DB 이용시
                .createNativeQuery("ALTER TABLE traced_log AUTO_INCREMENT = 1;")
                // H2 DB 이용시
                // .createNativeQuery(""ALTER TABLE traced_log ALTER COLUMN `id` RESTART WITH 1")
                .executeUpdate();
    }


    /* ########################################## 로그 저장 ########################################## */


    @Test
    public void 로그정보_DB저장___성공() {
        // given
        TracedLog tracedLog = LogSaveRequestDto.builder()
                .traceId("TRACE-1")
                .tagId("SERVICE-A")
                .logType("INFO")
                .logData("Repository test 입니다.")
                .loggingTime(LocalDateTime.now())
                .build()
                .toEntity();

        // when
        TracedLog savedTraceLog = tracedLogRepository.save(tracedLog);

        // then
        Assertions.assertThat(tracedLog.getTraceId()).isEqualTo(savedTraceLog.getTraceId());
        Assertions.assertThat(savedTraceLog.getId()).isEqualTo(15L);


    }

    // INSERT 테스트 여러 개 동시 테스트 경우를 가정한 메소드
    @Test
    public void 로그정보_DB저장___성공2() {
        // given
        TracedLog tracedLog = LogSaveRequestDto.builder()
                .traceId("TRACE-1")
                .tagId("SERVICE-B")
                .logType("INFO")
                .logData("Repository test 입니다.")
                .loggingTime(LocalDateTime.now())
                .build()
                .toEntity();

        // when
        TracedLog savedTraceLog = tracedLogRepository.save(tracedLog);
        System.out.println(savedTraceLog.toString());

        // then
        Assertions.assertThat(tracedLog.getTraceId()).isEqualTo(savedTraceLog.getTraceId());
        Assertions.assertThat(savedTraceLog.getId()).isEqualTo(15L);

    }


    @Test
    public void 로그정보_DB저장___실패_유효하지_않은_필드값() {
        // given
        TracedLog tracedLog = LogSaveRequestDto.builder()
                .traceId("t1011")
                .tagId("serviceR")
                .logType("INFO_TO0_LONG" +
                        "INFO_TO0_LONG" +
                        "INFO_TO0_LONG" +
                        "INFO_TO0_LONG" +
                        "INFO_TO0_LONG" +
                        "INFO_TO0_LONG" +
                        "INFO_TO0_LONG"
                )
                .logData("repository test 입니다.")
                .loggingTime(LocalDateTime.now())
                .build()
                .toEntity();

        boolean errorOccur = false;
        // when
        try {
            tracedLogRepository.save(tracedLog);
            // teardown();
        } catch (Exception ex) {
            errorOccur = true;
        }
        // then
        Assertions.assertThat(errorOccur).isEqualTo(true);
    }

}