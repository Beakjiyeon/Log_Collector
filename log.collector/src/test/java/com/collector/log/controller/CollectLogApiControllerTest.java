package com.collector.log.controller;

import com.collector.log.dto.LogSaveRequestDto;
import com.collector.log.dto.UndeliveredMailDto;
import com.collector.log.handler.CustomErrorCode;
import com.collector.log.handler.ExceptionHandlingAdvice;
import com.collector.log.handler.ParameterException;
import com.collector.log.service.CollectLogService;
import com.collector.log.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureMockMvc
@MockBean(JpaMetamodelMappingContext.class) // jpa auditing
public class CollectLogApiControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc; // ??? API ????????? ??? ??????: ??? ????????????????????? ????????? ???????????? ?????? Spring mvc ?????? ????????? ??? ?????? ?????????

    @MockBean // ??? ????????? ????????? ????????? ????????? ??????. ?????? ?????? ????????? ???????????? @MockBean ?????? ??????
    private CollectLogService collectLogService;

    @MockBean
    private EmailService emailService;

    private final String API_NAME_VERSION = "/collect-api/v2";
    private final String VALID_SEARCH_DATE = "2021-12-15";
    private final String NOT_FOUND_SEARCH_DATE = "2021-12-01";
    private final String FUTURE_SEARCH_DATE = "2025-12-01";

    @Before()
    public void setup() {
        this.mvc = MockMvcBuilders
                .standaloneSetup(new CollectLogApiController(collectLogService, emailService)) // https://bit.ly/31nEeAc
                .setControllerAdvice(new ExceptionHandlingAdvice()) // exception handler
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()) // https://bit.ly/3rEVFHa
                .addFilters(new CharacterEncodingFilter("UTF-8", true)) // MockMvc ?????? ??????
                .alwaysDo(print())
                .build();
    }


    @Test
    public void ??????????????????___??????() throws Exception {
        // data preparation
        List<UndeliveredMailDto> undeliveredMailDtos = new ArrayList<>();
        UndeliveredMailDto dto = UndeliveredMailDto.builder()
                .id(1L)
                .causeKeyword("KEYWORD")
                .causeContent("CONTENT")
                .traceId("TRACEid")
                .tagId("TAGid")
                .logData("LogData")
                .loggingTime(LocalDateTime.now())
                .logId(100L)
                .createAt(LocalDateTime.now())
                .build();
        undeliveredMailDtos.add(dto);
        Page<UndeliveredMailDto> pages = new PageImpl<>(undeliveredMailDtos);

        // mocking
        when(emailService.getFailedMails(any(), any(), any())).thenReturn(pages);

        // when, then
        mvc.perform(get(API_NAME_VERSION + "/fail-mails")
                .param("regDate", String.valueOf(LocalDate.now())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content.[0].id").value(dto.getId()))
                .andExpect(jsonPath("$.data.content.[0].createAt", not(dto.getCreateAt())))
                .andExpect(jsonPath("$.data.content.[0].causeKeyword").value(dto.getCauseKeyword()))
                .andExpect(jsonPath("$.data.content.[0].traceId").value(dto.getTraceId()))
                .andExpect(jsonPath("$.data.content.[0].tagId").value(dto.getTagId()))
                .andExpect(jsonPath("$.data.content.[0].logData").value(dto.getLogData()))//format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                .andExpect(jsonPath("$.data.content.[0].loggingTime").value(dto.getLoggingTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))));
    }


    @Test
    public void ??????????????????___??????___??????????????????????????????????????????() throws Exception {
        // Request Param Converting ???????????? badRequest ??????
        final String regDate = "20211126"; // Failed to convert value of type 'java.lang.String' to required type 'java.time.LocalDate'

        // when, then
        mvc.perform(get(API_NAME_VERSION + "/fail-mails")
                .param("regDate", regDate))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }



    @Test
    public void ??????????????????___??????___????????????????????????????????????() throws Exception {
        // ????????? ?????? ????????? ???????????? ?????? ?????? ??????
        // ???????????? ????????? ????????? ??????????????? ????????? ???????????? ?????? ????????? responseEntity ??????
        when(emailService.getFailedMails(any(),any(), any())).thenThrow(new ParameterException(CustomErrorCode.INVALID_ORDER_PARAMETER));

        final String order = "";
        final String page = "0";
        final String size = "1";

        // when, then
        mvc.perform(get(API_NAME_VERSION + "/fail-mails")
                .param("regDate", String.valueOf(LocalDate.now()))
                .param("order", order)
                .param("page", page)
                .param("size", size))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomErrorCode.INVALID_ORDER_PARAMETER.getMessage()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void ??????????????????___??????___???????????????????????????() throws Exception {
        final String regDate = "2021-11-26";
        final String regHour = "10";

        // when, then
        mvc.perform(post(API_NAME_VERSION + "/fail-mails")
                        .param("regDate", regDate)
                        .param("regHour", regHour))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.status").value(HttpStatus.METHOD_NOT_ALLOWED.value()));
    }


    @Test
    public void ????????????_????????????___??????() throws Exception {
        LogSaveRequestDto requestDto = LogSaveRequestDto.builder()
                        .traceId("TRACE-1")
                        .tagId("SERVICE-A")
                        .logType("INFO")
                        .logData("LOG DATA")
                        .loggingTime(LocalDateTime.now())
                        .build();


        Map<String, Long> map = new HashMap<>();
        map.put("id", 1L);
        when(collectLogService.save(any())).thenReturn(map);

        String content = objectMapper.writeValueAsString(requestDto);

        mvc.perform(post(API_NAME_VERSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    public void ????????????_????????????___??????___????????????????????????????????????() throws Exception {
        HashMap invalidObject = new HashMap();
        invalidObject.put("name", "gilding");

        String content = objectMapper.writeValueAsString(invalidObject);
        mvc.perform(post(API_NAME_VERSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));

    }

    @Test
    public void ????????????_????????????___??????___????????????????????????() {
        // ?????????????????? ????????? ??????
    }


}
