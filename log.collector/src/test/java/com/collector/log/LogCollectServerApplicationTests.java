package com.collector.log;

import com.collector.log.dto.LogSaveRequestDto;
import com.collector.log.handler.CustomErrorCode;
import com.collector.log.handler.CustomResultMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


@Slf4j
@Transactional // 자동 롤백 처리 여부 선택
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogCollectServerApplicationTests {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext wcx;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManagr;

    @Before
    public void beforeEach() {

        // INSERT 테스트메소드 여러 개를 한번에 실행하는 경우 AUTO_INCREMENT 증감 유지 이슈 해결
        // 참고: https://bit.ly/3pLmsie, https://bit.ly/31LppHi
        this.entityManagr
                // 로컬 DB 이용시
                .createNativeQuery("ALTER TABLE traced_log AUTO_INCREMENT = 1;")
                // H2 DB 이용시
                // .createNativeQuery(""ALTER TABLE traced_log ALTER COLUMN `id` RESTART WITH 1")
                .executeUpdate();
        mvc = MockMvcBuilders
                .webAppContextSetup(wcx)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    private final String API_NAME_VERSION = "/collect-api/v2";
    private final String VALID_SEARCH_DATE = "2021-12-15";
    private final String NOT_FOUND_SEARCH_DATE = "2021-12-01";
    private final String FUTURE_SEARCH_DATE = "2025-12-01";


    @Test
    public void 실패메일조회___성공___디폴트파라미터() throws Exception {
        /**
         * Default Search parameter value
         * regDate : now() BUT TestDB doesn't havae today data
         * page : 0 size : 10
         * order : regDate
         */
        mvc.perform(get(API_NAME_VERSION + "/fail-mails")
                                .param("regDate", VALID_SEARCH_DATE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.content", hasSize(5)))
                .andExpect(jsonPath("$.data.content.[0].id").value(1))
                .andExpect(jsonPath("$.data.content.[0].causeKeyword").value("MailAuthentication"))
                .andExpect(jsonPath("$.data.content.[0].logId").value(1))
                .andExpect(jsonPath("$.data.content.[0].traceId").value("t1"))
                .andExpect(jsonPath("$.data.content.[0].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.content.[0].logData").value("service_x starts"))
                .andExpect(jsonPath("$.data.content.[0].loggingTime").value("2021-12-14 02:14:21.264"))
                .andExpect(jsonPath("$.data.content.[4].id").value(5))
                .andExpect(jsonPath("$.data.content.[4].causeKeyword").value("MailAuthentication"))
                .andExpect(jsonPath("$.data.content.[4].logId").value(12))
                .andExpect(jsonPath("$.data.content.[4].traceId").value("t3"))
                .andExpect(jsonPath("$.data.content.[4].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.content.[4].logData").value("{\"service_y\" : {\"status\":\"END\"}}"))
                .andExpect(jsonPath("$.data.content.[4].loggingTime").value("2021-12-14 02:14:30.264"))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.data.last").value(true));
    }


    @Test
    public void 실패메일조회___성공___날짜기준정렬() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/fail-mails")
                                .param("regDate", VALID_SEARCH_DATE)
                                .param("order", "-createAt")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.content", hasSize(5)))
                .andExpect(jsonPath("$.data.content.[4].id").value(1))
                .andExpect(jsonPath("$.data.content.[4].causeKeyword").value("MailAuthentication"))
                .andExpect(jsonPath("$.data.content.[4].logId").value(1))
                .andExpect(jsonPath("$.data.content.[4].traceId").value("t1"))
                .andExpect(jsonPath("$.data.content.[4].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.content.[4].logData").value("service_x starts"))
                .andExpect(jsonPath("$.data.content.[4].loggingTime").value("2021-12-14 02:14:21.264"))
                .andExpect(jsonPath("$.data.content.[0].id").value(5))
                .andExpect(jsonPath("$.data.content.[0].causeKeyword").value("MailAuthentication"))
                .andExpect(jsonPath("$.data.content.[0].logId").value(12))
                .andExpect(jsonPath("$.data.content.[0].traceId").value("t3"))
                .andExpect(jsonPath("$.data.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.content.[0].logData").value("{\"service_y\" : {\"status\":\"END\"}}"))
                .andExpect(jsonPath("$.data.content.[0].loggingTime").value("2021-12-14 02:14:30.264"))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.data.last").value(true));
    }


    @Test
    public void 실패메일조회___성공___페이지네이션() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 2;
        mvc.perform(get(API_NAME_VERSION + "/fail-mails")
                        .param("regDate", VALID_SEARCH_DATE)
                        .param("order", "-createAt")
                        .param("poge", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.content.[0].id").value(5))
                .andExpect(jsonPath("$.data.content.[0].causeKeyword").value("MailAuthentication"))
                .andExpect(jsonPath("$.data.content.[0].logId").value(12))
                .andExpect(jsonPath("$.data.content.[0].traceId").value("t3"))
                .andExpect(jsonPath("$.data.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.content.[0].logData").value("{\"service_y\" : {\"status\":\"END\"}}"))
                .andExpect(jsonPath("$.data.content.[0].loggingTime").value("2021-12-14 02:14:30.264"))
                .andExpect(jsonPath("$.data.content.[1].id").value(4))
                .andExpect(jsonPath("$.data.content.[1].causeKeyword").value("MailAuthentication"))
                .andExpect(jsonPath("$.data.content.[1].logId").value(9))
                .andExpect(jsonPath("$.data.content.[1].traceId").value("t3"))
                .andExpect(jsonPath("$.data.content.[1].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.content.[1].logData").value("-12345"))
                .andExpect(jsonPath("$.data.content.[1].loggingTime").value("2021-12-14 02:14:26.264"))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(pageNumber))
                .andExpect(jsonPath("$.data.pageable.pageSize").value(pageSize))
                .andExpect(jsonPath("$.data.totalElements").value(5))
                .andExpect(jsonPath("$.data.last").value(false));
    }


    @Test
    public void 실패메일조회___성공___음수페이지넘버() throws Exception {
        final int pageNumber = -1;
        final int pageSize = 2;
        mvc.perform(get(API_NAME_VERSION + "/fail-mails")
                        .param("regDate", VALID_SEARCH_DATE)
                        .param("order", "-createAt")
                        .param("poge", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.content.[0].id").value(5))
                .andExpect(jsonPath("$.data.content.[0].causeKeyword").value("MailAuthentication"))
                .andExpect(jsonPath("$.data.content.[0].logId").value(12))
                .andExpect(jsonPath("$.data.content.[0].traceId").value("t3"))
                .andExpect(jsonPath("$.data.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.content.[0].logData").value("{\"service_y\" : {\"status\":\"END\"}}"))
                .andExpect(jsonPath("$.data.content.[0].loggingTime").value("2021-12-14 02:14:30.264"))
                .andExpect(jsonPath("$.data.content.[1].id").value(4))
                .andExpect(jsonPath("$.data.content.[1].causeKeyword").value("MailAuthentication"))
                .andExpect(jsonPath("$.data.content.[1].logId").value(9))
                .andExpect(jsonPath("$.data.content.[1].traceId").value("t3"))
                .andExpect(jsonPath("$.data.content.[1].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.content.[1].logData").value("-12345"))
                .andExpect(jsonPath("$.data.content.[1].loggingTime").value("2021-12-14 02:14:26.264"))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageable.pageSize").value(pageSize))
                .andExpect(jsonPath("$.data.totalElements").value(5))
                .andExpect(jsonPath("$.data.last").value(false));
    }

    @Test
    public void 실패메일조회___성공___유효하지않은정렬조건() throws Exception {
        final int pageNumber = -1;
        final int pageSize = 2;
        mvc.perform(get(API_NAME_VERSION + "/fail-mails")
                        .param("regDate", VALID_SEARCH_DATE)
                        .param("order", "wrong OrderCondition")
                        .param("poge", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomErrorCode.INVALID_ORDER_PARAMETER.getMessage()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    public void 실패메일조회___실패___미럐날짜값() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/fail-mails")
                        .param("regDate", FUTURE_SEARCH_DATE)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomErrorCode.INVALID_SEARCH_DATE_PARAMETER.getMessage()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    public void 실패메일조회___실패___유효하지않은_날짜형식() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/fail-mails")
                        .param("regDate", "211215")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    public void 실패메일조회___실패___대상없음() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/fail-mails")
                        // DEFAULT SEARCH DATE : TODAY
                        // There is no today data in Test DB
                        //.param("regDate", NOT_FOUND_SEARCH_DATE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()));
    }


    @Test
    public void 로그저장_메일처리___성공() throws Exception {
        final String traceId = "추적 ID";
        final String tagId = "서비스 ID";
        final String logType = "INFO";
        final String logData = "로그 내용";
        final LocalDateTime loggingTime = LocalDateTime.now();
        LogSaveRequestDto requestDto = LogSaveRequestDto.builder()
                .traceId(traceId)
                .tagId(tagId)
                .logType(logType)
                .logData(logData)
                .loggingTime(loggingTime)
                .build();

        String content = objectMapper.writeValueAsString(requestDto);

        mvc.perform(post(API_NAME_VERSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.CREATED.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(15));
    }


    @Test
    public void 로그저장_메일처리___실패___존재하지않는DTO필드() throws Exception {
        HashMap invalidObject = new HashMap();
        invalidObject.put("name", "gilding");
        String content = objectMapper.writeValueAsString(invalidObject);
        mvc.perform(post(API_NAME_VERSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("[name] 예상하지 못한 Json Request 형식 입니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    public void 로그저장_메일처리___실패___유효하지않은DTO필드() throws Exception {
        final String traceId = "추적 ID";
        final String tagId = "서비스 ID";
        final String logType = "TOO LONG LOG TYPE";
        final String logData = "로그 내용";
        final LocalDateTime loggingTime = LocalDateTime.now();
        LogSaveRequestDto requestDto = LogSaveRequestDto.builder()
                .traceId(traceId)
                .tagId(tagId)
                .logType(logType)
                .logData(logData)
                .loggingTime(loggingTime)
                .build();
        String content = objectMapper.writeValueAsString(requestDto);
        mvc.perform(post(API_NAME_VERSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("{logType=문자열 길이는 1 이상 5 이하 여야 합니다.}"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    public void 로그저장_메일처리___실패___유효하지않은DTO필드여러개() throws Exception {
        final String traceId = "추적 ID";
        final String tagId = "TOO LONG TOO LONG TOO LONG TOO LONG TOO LONG TOO LONG 서비스 ID";
        final String logType = "TOO LONG LOG TYPE";
        final String logData = "로그 내용";
        final LocalDateTime loggingTime = LocalDateTime.now();
        LogSaveRequestDto requestDto = LogSaveRequestDto.builder()
                .traceId(traceId)
                .tagId(tagId)
                .logType(logType)
                .logData(logData)
                .loggingTime(loggingTime)
                .build();
        String content = objectMapper.writeValueAsString(requestDto);
        mvc.perform(post(API_NAME_VERSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("{logType=문자열 길이는 1 이상 5 이하 여야 합니다., tagId=문자열 길이는 1 이상 20 이하 여야 합니다.}"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    public void 로그저장_메일처리___실패___널값_로그타입_유효하지않은_로깅시각파라미터() throws Exception {
        JSONObject resultObj = new JSONObject();
        resultObj.put("traceId", "추적 ID");
        resultObj.put("tagId", "서비스 ID");
        resultObj.put("loggingTime", "");
        String content = objectMapper.writeValueAsString(resultObj);
        mvc.perform(post(API_NAME_VERSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("{logType=null 을 허용하지 않고, 공백 문자를 제외한 길이가 0보다 커야 합니다., loggingTime=null 을 허용하지 않고, 공백 문자를 제외한 길이가 0보다 커야 합니다.}"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

}
