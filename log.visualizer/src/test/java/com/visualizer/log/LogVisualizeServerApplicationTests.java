package com.visualizer.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visualizer.log.handler.CustomErrorCode;
import com.visualizer.log.handler.CustomResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Transactional
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogVisualizeServerApplicationTests {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext wcx;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void beforeEach() {
        mvc = MockMvcBuilders
                .webAppContextSetup(wcx)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    private final String API_NAME_VERSION = "/visualize-api/v2";
    private final String VALID_SEARCH_DATE = "2021-12-14";
    private final String NOT_FOUND_SEARCH_DATE = "2021-12-01";
    private final String FUTURE_SEARCH_DATE = "2025-12-01";


    /**
     * ####################################### ???????????? ??????????????? ?????? #######################################
     */


    // service_x: 4, service_y: 3, service_z: 2
    @Test
    public void ?????????????????????????????????___??????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/error-occur-services")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "-errorOccurCount")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.content", hasSize(3)))
                .andExpect(jsonPath("$.data.content.[0].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.content.[0].errorOccurCount").value(4))
                .andExpect(jsonPath("$.data.content.[1].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.content.[1].errorOccurCount").value(3))
                .andExpect(jsonPath("$.data.content.[2].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.content.[2].errorOccurCount").value(2));
    }

    @Test
    public void ?????????????????????????????????___??????___??????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/error-occur-services")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "errorOccurCount")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.content", hasSize(3)))
                .andExpect(jsonPath("$.data.content.[0].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.content.[0].errorOccurCount").value(2))
                .andExpect(jsonPath("$.data.content.[1].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.content.[1].errorOccurCount").value(3))
                .andExpect(jsonPath("$.data.content.[2].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.content.[2].errorOccurCount").value(4));
    }



    // 2?????? ?????????
    // service_x: 4, service_y: 3, service_z: 2
    @Test
    public void ?????????????????????????????????___??????___??????????????????() throws Exception {
        final int pageNumber = 1;
        final int pageSize = 2;
        mvc.perform(get(API_NAME_VERSION +"/error-occur-services")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "-errorOccurCount")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content.[0].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.content.[0].errorOccurCount").value(2))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(pageNumber))
                .andExpect(jsonPath("$.data.pageable.pageSize").value(pageSize))
                .andExpect(jsonPath("$.data.last").value("true"))
                .andExpect(jsonPath("$.data.totalElements").value(3));
    }


    // ????????? ????????????
    @Test
    public void ?????????????????????????????????___??????___??????_?????????_?????????_?????????????????????????????????() throws Exception {
        // ?????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? -errorOccurCount,tagId ??????
        // ????????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? 0, 10 ??????
        mvc.perform(get(API_NAME_VERSION +"/error-occur-services")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "")
                        .param("page", "")
                        .param("size", "")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.content", hasSize(3)))
                .andExpect(jsonPath("$.data.content.[2].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.content.[2].errorOccurCount").value(2))
                .andExpect(jsonPath("$.data.content.[1].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.content.[1].errorOccurCount").value(3))
                .andExpect(jsonPath("$.data.content.[0].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.content.[0].errorOccurCount").value(4));
    }


    @Test
    public void ?????????????????????????????????___??????___??????_?????????_?????????????????????????????????() throws Exception {
        // ?????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? TODAY
        // ????????? DB ??? loggingType TODAY??? ????????? ??????
        mvc.perform(get(API_NAME_VERSION +"/error-occur-services")
                        .param("loggingDate", "")
                        .param("order", "")
                        .param("page", "")
                        .param("size", "")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()));
    }


    @Test
    public void ?????????????????????????????????___??????___?????????????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/error-occur-services")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "-errorOccurCount")
                        .param("page", "-10")
                        .param("size", "1")
                )
                .andExpect(status().isOk()) // ?????????????????? ?????? ?????? ???????????? ???????????? ???????????? 0?????? ?????????????????? ????????? ?????????????????? ??????
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content.[0].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.content.[0].errorOccurCount").value(4))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0));
    }


    @Test
    public void ?????????????????????????????????___??????___?????????????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/error-occur-services")
                        .param("loggingDate", NOT_FOUND_SEARCH_DATE)
                        .param("order", "-errorOccurCount")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()));
    }


    @Test
    public void ?????????????????????????????????___??????___??????????????????_???????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/error-occur-services")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "-wrong OrderCondition")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomErrorCode.INVALID_ORDER_PARAMETER.getMessage()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    public void ?????????????????????????????????___??????___???????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/error-occur-services")
                        .param("loggingDate", FUTURE_SEARCH_DATE)
                        .param("order", "-errorOccurCount")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomErrorCode.INVALID_SEARCH_DATE_PARAMETER.getMessage()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    public void ?????????????????????????????????___??????___??????????????????_????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/error-occur-services")
                        .param("loggingDate", "2021/12/14")
                        .param("order", "-errorOccurCount")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists()) // ConversionFailedException message
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    /**
     * ####################################### ???????????? ?????? ?????????????????? #######################################
     */
    @Test
    public void ????????????????????????????????????___??????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/avg-process-time")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "tagId,processTime")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.avgProcessTimes.content", hasSize(3)))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[0].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[0].avgProcessTime").value(1.2433333333333332))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[1].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[1].avgProcessTime").value(2.65))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[2].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[2].avgProcessTime").value(0.9));
    }


    @Test
    public void ????????????????????????????????????___??????___??????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/avg-process-time")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "processTime")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.avgProcessTimes.content", hasSize(3)))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[0].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[0].avgProcessTime").value(0.9))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[1].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[1].avgProcessTime").value(1.2433333333333332))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[2].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[2].avgProcessTime").value(2.65));
    }


    @Test
    public void ????????????????????????????????????___??????___??????????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 1;
        mvc.perform(get(API_NAME_VERSION +"/avg-process-time")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "tagId,processTime")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.avgProcessTimes.content", hasSize(1)))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[0].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[0].avgProcessTime").value(1.2433333333333332))
                .andExpect(jsonPath("$.data.avgProcessTimes.pageable.pageNumber").value(pageNumber))
                .andExpect(jsonPath("$.data.avgProcessTimes.pageable.pageSize").value(pageSize))
                .andExpect(jsonPath("$.data.avgProcessTimes.last").value("false"))
                .andExpect(jsonPath("$.data.avgProcessTimes.totalElements").value(3));
    }


    // ????????? ????????????
    @Test
    public void ????????????????????????????????????___??????___??????_?????????_?????????_?????????????????????????????????() throws Exception {
        // ?????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? tagId,processTime ??????
        // ????????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? 0, 10 ??????
        mvc.perform(get(API_NAME_VERSION +"/avg-process-time")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "")
                        .param("page", "")
                        .param("size", "")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.avgProcessTimes.content", hasSize(3)))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[0].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[0].avgProcessTime").value(1.2433333333333332))
                .andExpect(jsonPath("$.data.avgProcessTimes.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.avgProcessTimes.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.data.avgProcessTimes.last").value("true"))
                .andExpect(jsonPath("$.data.avgProcessTimes.totalElements").value(3));
    }


    @Test
    public void ????????????????????????????????????___??????___??????_?????????_?????????????????????????????????() throws Exception {
        // ?????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? TODAY
        // ????????? DB ??? loggingType TODAY??? ????????? ??????
        mvc.perform(get(API_NAME_VERSION +"/avg-process-time")
                        .param("loggingDate", "")
                        .param("order", "")
                        .param("page", "")
                        .param("size", "")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()));
    }


    @Test
    public void ????????????????????????????????????___??????___?????????????????????() throws Exception {
        final int pageNumber = -1;
        final int pageSize = 1;
        mvc.perform(get(API_NAME_VERSION +"/avg-process-time")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "tagId,processTime")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.avgProcessTimes.content", hasSize(1)))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[0].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.avgProcessTimes.content.[0].avgProcessTime").value(1.2433333333333332))
                .andExpect(jsonPath("$.data.avgProcessTimes.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.avgProcessTimes.pageable.pageSize").value(pageSize))
                .andExpect(jsonPath("$.data.avgProcessTimes.last").value("false"))
                .andExpect(jsonPath("$.data.avgProcessTimes.totalElements").value(3));
    }


    @Test
    public void ????????????????????????????????????___??????___?????????????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 10;
        mvc.perform(get(API_NAME_VERSION +"/avg-process-time")
                        .param("loggingDate", NOT_FOUND_SEARCH_DATE)
                        .param("order", "tagId,processTime")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()));
    }


    @Test
    public void ????????????????????????????????????___??????___??????????????????_???????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 10;
        mvc.perform(get(API_NAME_VERSION +"/avg-process-time")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "tagIdprocessTime")
                        .param("page", String.valueOf(pageNumber))
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
    public void ????????????????????????????????????___??????___???????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 10;
        mvc.perform(get(API_NAME_VERSION +"/avg-process-time")
                        .param("loggingDate", FUTURE_SEARCH_DATE)
                        .param("order", "tagId,processTime")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomErrorCode.INVALID_SEARCH_DATE_PARAMETER.getMessage()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    public void ????????????????????????????????????___??????___??????????????????_????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 10;
        mvc.perform(get(API_NAME_VERSION +"/avg-process-time")
                        .param("loggingDate", "20211214")
                        .param("order", "tagId,processTime")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    /**
     * ####################################### ???????????????????????? ?????????????????? #######################################
     */
    @Test
    public void ????????????????????????_??????????????????___??????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/process-time-diff")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "tagId")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content", hasSize(3)))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[0].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[0].maxProcessTimeDiff").value("2.47"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[1].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[1].maxProcessTimeDiff").value("4.7"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[2].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[2].maxProcessTimeDiff").value("0.0"));
    }


    @Test
    public void ????????????????????????_??????????????????___??????___??????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/process-time-diff")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "processTime")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content", hasSize(3)))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[0].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[0].maxProcessTimeDiff").value("0.0"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[1].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[1].maxProcessTimeDiff").value("2.47"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[2].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[2].maxProcessTimeDiff").value("4.7"));
    }

    @Test
    public void ????????????????????????_??????????????????___??????___??????????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 2;
        mvc.perform(get(API_NAME_VERSION +"/process-time-diff")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "processTime")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content", hasSize(2)))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[0].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[0].maxProcessTimeDiff").value("0.0"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.pageable.pageNumber").value(pageNumber))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.pageable.pageSize").value(pageSize))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.last").value(false))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.totalElements").value(3))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.totalPages").value(2));
    }


    // ????????? ????????????
    @Test
    public void ????????????????????????_??????????????????___??????___??????_?????????_?????????_?????????????????????????????????() throws Exception {
        // ?????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? tagId ??????
        // ????????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? 0, 10 ??????
        mvc.perform(get(API_NAME_VERSION +"/process-time-diff")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "") // tagId
                        .param("page", "")
                        .param("size", "")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content", hasSize(3)))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[0].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[0].maxProcessTimeDiff").value("2.47"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.last").value(true))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.totalElements").value(3))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.totalPages").value(1));
    }


    @Test
    public void ????????????????????????_??????????????????___??????___??????_?????????_?????????????????????????????????() throws Exception {
        // ?????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? TODAY
        // ????????? DB ??? loggingType TODAY??? ????????? ??????
        mvc.perform(get(API_NAME_VERSION +"/process-time-diff")
                        .param("loggingDate", "")
                        .param("order", "") // tagId
                        .param("page", "")
                        .param("size", "")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()));
    }

    @Test
    public void ????????????????????????_??????????????????___??????___?????????????????????() throws Exception {
        final int pageNumber = -1;
        final int pageSize = 2;
        mvc.perform(get(API_NAME_VERSION +"/process-time-diff")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("order", "processTime")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content", hasSize(2)))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[0].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[0].maxProcessTimeDiff").value("0.0"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[1].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content.[1].maxProcessTimeDiff").value("2.47"))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.pageable.pageSize").value(pageSize))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.last").value(false))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.totalElements").value(3))
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.totalPages").value(2));
    }


    @Test
    public void ????????????????????????_??????????????????___??????___?????????????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/process-time-diff")
                        .param("loggingDate", NOT_FOUND_SEARCH_DATE)
                        .param("order", "processTime")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()));
    }


    @Test
    public void ????????????????????????_??????????????????___??????___??????????????????_???????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/process-time-diff")
                        .param("loggingDate", NOT_FOUND_SEARCH_DATE)
                        .param("order", "wrong orderCondition")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomErrorCode.INVALID_ORDER_PARAMETER.getMessage()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    public void ????????????????????????_??????????????????___??????___???????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/process-time-diff")
                        .param("loggingDate", FUTURE_SEARCH_DATE)
                        .param("order", "processTime")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomErrorCode.INVALID_SEARCH_DATE_PARAMETER.getMessage()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    public void ????????????????????????_??????????????????___??????___??????????????????_????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION +"/process-time-diff")
                        .param("loggingDate", "20211214")
                        .param("order", "processTime")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    /**
     * ####################################### ???????????? ????????? ??????????????? #######################################
     */
    @Test
    public void ????????????????????????????????????___??????() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/most-call-services")
                        .param("loggingDate", VALID_SEARCH_DATE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.services").exists())
                .andExpect(jsonPath("$.data.services").value("service_x"))
                .andExpect(jsonPath("$.data.callCount").exists())
                .andExpect(jsonPath("$.data.callCount").value(6));
    }

    // ????????? ????????????
    @Test
    public void ????????????????????????????????????___??????___??????_?????????_?????????????????????????????????() throws Exception {
        // ?????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? TODAY
        // ????????? DB ??? loggingType TODAY??? ????????? ??????
        mvc.perform(get(API_NAME_VERSION + "/most-call-services")
                        .param("loggingDate", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()));
    }

    @Test
    public void ????????????????????????????????????___??????___?????????????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/most-call-services")
                        .param("loggingDate", NOT_FOUND_SEARCH_DATE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()));
    }


    @Test
    public void ????????????????????????????????????___??????___???????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/most-call-services")
                        .param("loggingDate", FUTURE_SEARCH_DATE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomErrorCode.INVALID_SEARCH_DATE_PARAMETER.getMessage()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    public void ????????????????????????????????????___??????___??????????????????_????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/most-call-services")
                        .param("loggingDate", "2021214"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    /**
     * ####################################### ?????? ID ?????????????????? #######################################
     */
    @Test
    public void ??????ID??????????????????___??????___????????????????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("tagId", "")
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "loggingTime")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(6)))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[1].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[1].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.logs.content.[2].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[2].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.logs.content.[3].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[3].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.logs.content.[4].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[4].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.logs.content.[5].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[5].tagId").value("service_y"));
    }


    @Test
    public void ??????ID??????????????????___??????___????????????????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("tagId", "")
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "tagId")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(6)))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.logs.content.[1].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[1].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.logs.content.[2].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[2].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[3].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[3].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[4].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[4].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.logs.content.[5].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[5].tagId").value("service_z"));
    }


    @Test
    public void ??????ID??????????????????___??????___????????????????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("tagId", "")
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "logType")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(6)))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[0].logType").value("ERROR"))
                .andExpect(jsonPath("$.data.logs.content.[1].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[1].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.logs.content.[1].logType").value("ERROR"))
                .andExpect(jsonPath("$.data.logs.content.[2].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[2].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.logs.content.[2].logType").value("ERROR"))
                .andExpect(jsonPath("$.data.logs.content.[3].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[3].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[3].logType").value("ERROR"))
                .andExpect(jsonPath("$.data.logs.content.[4].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[4].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.logs.content.[4].logType").value("INFO"))
                .andExpect(jsonPath("$.data.logs.content.[5].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[5].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.logs.content.[5].logType").value("INFO"));
    }


    @Test
    public void ??????ID??????????????????___??????___??????????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 1;
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("tagId", "")
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "loggingTime")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(pageSize)))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.pageable.pageNumber").value(pageNumber))
                .andExpect(jsonPath("$.data.logs.pageable.pageSize").value(pageSize))
                .andExpect(jsonPath("$.data.logs.totalElements").value(6))
                .andExpect(jsonPath("$.data.logs.last").value(false));
    }


    // ????????? ????????????
    @Test
    public void ??????ID??????????????????___??????___??????_?????????_?????????_?????????????????????????????????() throws Exception {
        // ?????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? loggingTime ??????
        // ????????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? 0, 10 ??????
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("tagId", "")
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "")
                        .param("page", "")
                        .param("size", "")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(6)))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.logs.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.data.logs.totalElements").value(6))
                .andExpect(jsonPath("$.data.logs.last").value(true));
    }


    @Test
    public void ??????ID??????????????????___??????___??????_?????????_?????????????????????????????????() throws Exception {
        // ?????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? TODAY
        // ????????? DB ??? loggingType TODAY??? ????????? ??????
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", "")
                        .param("tagId", "")
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "")
                        .param("page", "")
                        .param("size", "")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()));
    }


    @Test
    public void ??????ID??????????????????___??????___?????????????????????() throws Exception {
        final int pageNumber = -2;
        final int pageSize = 1;
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("tagId", "")
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "loggingTime")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(pageSize)))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.logs.pageable.pageSize").value(pageSize))
                .andExpect(jsonPath("$.data.logs.totalElements").value(6))
                .andExpect(jsonPath("$.data.logs.last").value(false));
    }

    @Test
    public void ??????ID??????????????????___??????___??????????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("tagId", "service_x")
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "loggingTime")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(2)))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.logs.content.[1].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[1].tagId").value("service_x"));
    }


    @Test
    public void ??????ID??????????????????___??????___??????????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("tagId", "")
                        .param("logType", "INFO")
                        .param("logData", "")
                        .param("order", "loggingTime")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(2)))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_x"))
                .andExpect(jsonPath("$.data.logs.content.[1].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[1].tagId").value("service_x"));
    }




    @Test
    public void ??????ID??????????????????___??????___??????????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("tagId", "")
                        .param("logType", "")
                        .param("logData", "123")
                        .param("order", "loggingTime")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(2)))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.logs.content.[0].logData").value("+12345"))
                .andExpect(jsonPath("$.data.logs.content.[1].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[1].tagId").value("service_z"))
                .andExpect(jsonPath("$.data.logs.content.[1].logData").value("-12345"));
    }


    @Test
    public void ??????ID??????????????????___??????___?????????????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 1;
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", NOT_FOUND_SEARCH_DATE)
                        .param("tagId", "")
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "loggingTime")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()));
    }


    @Test
    public void ??????ID??????????????????___??????___??????????????????_???????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 1;
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("tagId", "")
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "wrong orderCondtion")
                        .param("page", String.valueOf(pageNumber))
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
    public void ??????ID??????????????????___??????___???????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 1;
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", FUTURE_SEARCH_DATE)
                        .param("tagId", "")
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "loggingTime")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomErrorCode.INVALID_SEARCH_DATE_PARAMETER.getMessage()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    public void ??????ID??????????????????___??????___??????????????????_????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 1;
        mvc.perform(get(API_NAME_VERSION + "/logs/trace-id/{trace-id}", "t3")
                        .param("loggingDate", "20211214")
                        .param("tagId", "")
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "wrong orderCondtion")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    /**
     * ####################################### ????????? ID ?????????????????? #######################################
     */

    @Test
    public void ?????????ID??????????????????___??????___????????????????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/logs/tag-id/{tag-id}", "service_y")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "loggingTime")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(4)))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t2"))
                .andExpect(jsonPath("$.data.logs.content.[0].logType").value("INFO"))
                .andExpect(jsonPath("$.data.logs.content.[1].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[1].traceId").value("t2"))
                .andExpect(jsonPath("$.data.logs.content.[1].logType").value("ERROR"))
                .andExpect(jsonPath("$.data.logs.content.[2].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[2].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[2].logType").value("ERROR"))
                .andExpect(jsonPath("$.data.logs.content.[3].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[3].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[3].logType").value("ERROR"));
    }


    @Test
    public void ?????????ID??????????????????___??????___????????????????????????() throws Exception {
        mvc.perform(get(API_NAME_VERSION + "/logs/tag-id/{tag-id}", "service_y")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "logType")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(4)))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t2"))
                .andExpect(jsonPath("$.data.logs.content.[0].logType").value("ERROR"))
                .andExpect(jsonPath("$.data.logs.content.[1].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[1].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[1].logType").value("ERROR"))
                .andExpect(jsonPath("$.data.logs.content.[2].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[2].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[2].logType").value("ERROR"))
                .andExpect(jsonPath("$.data.logs.content.[3].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[3].traceId").value("t2"))
                .andExpect(jsonPath("$.data.logs.content.[3].logType").value("INFO"));
    }


    @Test
    public void ?????????ID??????????????????___??????___??????????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 1;
        mvc.perform(get(API_NAME_VERSION + "/logs/tag-id/{tag-id}", "service_y")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "logType")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(pageSize)))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t2"))
                .andExpect(jsonPath("$.data.logs.pageable.pageNumber").value(pageNumber))
                .andExpect(jsonPath("$.data.logs.pageable.pageSize").value(pageSize))
                .andExpect(jsonPath("$.data.logs.totalElements").value(4))
                .andExpect(jsonPath("$.data.logs.last").value(false));
    }


    // ????????? ????????????
    @Test
    public void ?????????ID??????????????????___??????___??????_?????????_?????????_?????????????????????????????????() throws Exception {
        // ?????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? loggingTime ??????
        // ????????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? 0, 10 ??????
        mvc.perform(get(API_NAME_VERSION + "/logs/tag-id/{tag-id}", "service_y")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "")
                        .param("page", "")
                        .param("size", "")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(4)))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t2"))
                .andExpect(jsonPath("$.data.logs.content.[0].loggingTime").value("2021-12-14 02:14:23.264"))
                .andExpect(jsonPath("$.data.logs.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.logs.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.data.logs.totalElements").value(4))
                .andExpect(jsonPath("$.data.logs.last").value(true));
    }


    @Test
    public void ?????????ID??????????????????___??????___??????_?????????_?????????????????????????????????() throws Exception {
        // ?????? ???????????? "" ???????????? ??????, ???????????? ?????? ????????? TODAY
        // ????????? DB ??? loggingType TODAY??? ????????? ??????
        mvc.perform(get(API_NAME_VERSION + "/logs/tag-id/{tag-id}", "service_y")
                        .param("loggingDate", "")
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "")
                        .param("page", "")
                        .param("size", "")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()));
    }


    @Test
    public void ?????????ID??????????????????___??????___?????????????????????() throws Exception {
        final int pageNumber = -1;
        final int pageSize = 1;
        mvc.perform(get(API_NAME_VERSION + "/logs/tag-id/{tag-id}", "service_y")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("logType", "")
                        .param("logData", "")
                        .param("order", "logType")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(pageSize)))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t2"))
                .andExpect(jsonPath("$.data.logs.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.logs.pageable.pageSize").value(pageSize))
                .andExpect(jsonPath("$.data.logs.totalElements").value(4))
                .andExpect(jsonPath("$.data.logs.last").value(false));
    }

    @Test
    public void ?????????ID??????????????????___??????___??????????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 10;
        mvc.perform(get(API_NAME_VERSION + "/logs/tag-id/{tag-id}", "service_y")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        .param("logType", "INFO")
                        .param("logData", "")
                        //.param("order", "") ???????????? ?????? ????????? ??? ??????
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(1)))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t2"))
                .andExpect(jsonPath("$.data.logs.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.logs.pageable.pageSize").value(pageSize))
                .andExpect(jsonPath("$.data.logs.totalElements").value(1))
                .andExpect(jsonPath("$.data.logs.last").value(true));
    }

    @Test
    public void ?????????ID??????????????????___??????___??????????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 10;
        mvc.perform(get(API_NAME_VERSION + "/logs/tag-id/{tag-id}", "service_y")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        //.param("logType", "")
                        .param("logData", "START")
                        //.param("order", "") ???????????? ?????? ????????? ??? ??????
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(2)))
                .andExpect(jsonPath("$.data.logs.content.[0].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[0].traceId").value("t2"))
                .andExpect(jsonPath("$.data.logs.content.[0].logType").value("INFO"))
                .andExpect(jsonPath("$.data.logs.content.[1].tagId").value("service_y"))
                .andExpect(jsonPath("$.data.logs.content.[1].traceId").value("t3"))
                .andExpect(jsonPath("$.data.logs.content.[1].logType").value("ERROR"))
                .andExpect(jsonPath("$.data.logs.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.logs.pageable.pageSize").value(pageSize))
                .andExpect(jsonPath("$.data.logs.totalElements").value(2))
                .andExpect(jsonPath("$.data.logs.last").value(true));
    }


    @Test
    public void ?????????ID??????????????????___??????___?????????????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 10;
        mvc.perform(get(API_NAME_VERSION + "/logs/tag-id/{tag-id}", "service_y")
                        .param("loggingDate", NOT_FOUND_SEARCH_DATE)
                        //.param("logType", "")
                        .param("logData", "START")
                        //.param("order", "") ???????????? ?????? ????????? ??? ??????
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()));
    }

    // TODO ASSERT
    @Test
    public void ?????????ID??????????????????___??????__??????????????????_???????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 10;
        mvc.perform(get(API_NAME_VERSION + "/logs/tag-id/{tag-id}", "service_y")
                        .param("loggingDate", VALID_SEARCH_DATE)
                        //.param("logType", "")
                        .param("logData", "START")
                        .param("order", "wrong OrderCondtion")
                        .param("page", String.valueOf(pageNumber))
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
    public void ?????????ID??????????????????___??????__???????????????() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 10;
        mvc.perform(get(API_NAME_VERSION + "/logs/tag-id/{tag-id}", "service_y")
                        .param("loggingDate", FUTURE_SEARCH_DATE)
                        //.param("logType", "")
                        .param("logData", "START")
                        //.param("order", "wrong orderCondtion")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomErrorCode.INVALID_SEARCH_DATE_PARAMETER.getMessage()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }



}