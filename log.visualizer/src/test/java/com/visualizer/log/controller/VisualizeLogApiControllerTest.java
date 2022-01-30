package com.visualizer.log.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.visualizer.log.dto.*;
import com.visualizer.log.handler.CustomResultMessage;
import com.visualizer.log.handler.ExceptionHandlingAdvice;
import com.visualizer.log.service.VisualizeLogService;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
@MockBean(JpaMetamodelMappingContext.class) // jpa auditing
@AutoConfigureMockMvc
public class VisualizeLogApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private VisualizeLogService visualizeLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Before()
    public void setup() {
        this.mvc = MockMvcBuilders
                .standaloneSetup(new VisualizeLogApiController(visualizeLogService))
                .setControllerAdvice(new ExceptionHandlingAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }
    private final String API_NAME_VERSION = "/visualize-api/v2";

    @Test
    public void 에러발생서비스조회___성공() throws Exception {
        // data preparation
        List<ErrorServiceDto> list = new ArrayList<>();
        list.add(new ErrorServiceDto("service_a", 3L));
        list.add(new ErrorServiceDto("service_a", 1L));
        Page<ErrorServiceDto> page = new PageImpl<>(list);

        // mocking
        when(visualizeLogService.getErrorServices(any(), any(), any())).thenReturn(page);

        // when, then
        mvc.perform(get(API_NAME_VERSION + "/error-occur-services")
                        .param("loggingDate", String.valueOf(LocalDate.now()))
                        .param("order", "errorOccurCount")
                        .param("pageable", String.valueOf(PageRequest.of(1,10)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }


    @Test
    public void 서비스별_평균처리시간조회___성공() throws Exception {
        // data preparation
        List<ServiceAvgProcessTimeDto> list = new ArrayList<>();
        list.add(new ServiceAvgProcessTimeDto("service_a",3.0));
        list.add(new ServiceAvgProcessTimeDto("service_b", 1.0));

        Page<ServiceAvgProcessTimeDto> page = new PageImpl<>(list);
        Map<String, Object> map = new HashMap<>();
        map.put("avgProcessTimes", page);

        // mocking
        when(visualizeLogService.getAvgProcessTime(any(), any(), any())).thenReturn(map);

        // when, then
        mvc.perform(get(API_NAME_VERSION  +"/avg-process-time")
                        .param("loggingDate", String.valueOf(LocalDate.now()))
                        .param("order", "tagId,processTime")
                        .param("pageable", String.valueOf(PageRequest.of(1,10)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.avgProcessTimes.content", hasSize(2)));
    }

    @Test
    public void 서비스별_처리시간_최대간극조회___성공() throws Exception {
        // data preparation
        List<ServiceMaxProcessTimeDiffDto> list = new ArrayList<>();
        list.add(new ServiceMaxProcessTimeDiffDto("service_a", 0.0));
        list.add(new ServiceMaxProcessTimeDiffDto("service_b", 1.0));

        Page<ServiceMaxProcessTimeDiffDto> page = new PageImpl<>(list);
        Map<String, Object> map = new HashMap<>();
        map.put("maxProcessTimeDiffs", page);
        // mocking
        when(visualizeLogService.getMaxProcessTimeDiff(any(), any(), any())).thenReturn(map);

        // when, then
        mvc.perform(get(API_NAME_VERSION + "/process-time-diff")
                        .param("loggingDate", String.valueOf(LocalDate.now()))
                        .param("order", "tagId")
                        .param("pageable", String.valueOf(PageRequest.of(1,10)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.maxProcessTimeDiffs.content", hasSize(2)));
    }


    @Test
    public void 가장많이호출된_서비스조회___성공() throws Exception {
        // data preparation
        List<String> list = new ArrayList<>();
        list.add("service_a");
        list.add("service_b");

        MostCallServiceDto mostCallServiceDto = new MostCallServiceDto(list, 5L);

        // mocking
        when(visualizeLogService.getMostCallServices(any())).thenReturn(mostCallServiceDto);

        // when, then
        mvc.perform(get(API_NAME_VERSION + "/most-call-services")
                        .param("loggingDate", String.valueOf(LocalDate.now()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.services").value(list))
                .andExpect(jsonPath("$.data.callCount").value(5L));
    }


    @Test
    public void 추적ID로그내용검색___성공() throws Exception {
        // data preparation
        List<TracedLogDto> list = new ArrayList<>();
        list.add(TracedLogDto.builder()
                .traceId("t1")
                .tagId("s1")
                .logType("INFO")
                .logData("DATA")
                .loggingTime(LocalDateTime.now())
                .build());
        list.add(TracedLogDto.builder()
                .traceId("t1")
                .tagId("s2")
                .logType("INFO")
                .logData("DATA")
                .loggingTime(LocalDateTime.now())
                .build());


        Page<TracedLogDto> page = new PageImpl<>(list);
        Map<String, Object> map = new HashMap<>();
        map.put("logs", page);

        JSONParser jsonParser= new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONArray listJson = (JSONArray) jsonParser.parse(objectMapper.writeValueAsString(list));
        // mocking
        when(visualizeLogService.getLogsByTraceId(any(), any(), any())).thenReturn(map);
        // when, then
        mvc.perform(get(API_NAME_VERSION  + "/logs/trace-id/{trace-id}", "t1")
                        .param("loggingDate", String.valueOf(LocalDate.now()))
                        .param("tagId", "")
                        .param("logType", "INFO")
                        .param("logData", "")
                        .param("order", "loggingTime")
                        .param("pageable", String.valueOf(PageRequest.of(1,10)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(2)))
                //.andExpect(jsonPath("$.data.logs.content").value(objectMapper.writeValueAsString(list)))
                .andExpect(jsonPath("$.data.logs.content").value(Matchers.containsInAnyOrder(listJson.toArray()))); // https://stackoverflow.com/questions/63539533/java-lang-assertionerror-got-a-list-of-values-but-instead-of-the-expected-singl
    }

    @Test
    public void 서비스ID로그내용검색___성공() throws Exception {
        // data preparation
        List<TracedLogDto> list = new ArrayList<>();
        list.add(TracedLogDto.builder()
                .traceId("t1")
                .tagId("s1")
                .logType("INFO")
                .logData("DATA")
                .loggingTime(LocalDateTime.now())
                .build());
        list.add(TracedLogDto.builder()
                .traceId("t1")
                .tagId("s1")
                .logType("ERROR")
                .logData("DATA")
                .loggingTime(LocalDateTime.now())
                .build());


        Page<TracedLogDto> page = new PageImpl<>(list);
        Map<String, Object> map = new HashMap<>();
        map.put("logs", page);

        JSONParser jsonParser= new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONArray listJson = (JSONArray) jsonParser.parse(objectMapper.writeValueAsString(list));
        // mocking
        when(visualizeLogService.getLogsByTagId(any(), any(), any())).thenReturn(map);
        // when, then
        mvc.perform(get(API_NAME_VERSION  +"/logs/tag-id/{tag-id}", "s1")
                        .param("loggingDate", String.valueOf(LocalDate.now()))
                        .param("logType", "INFO")
                        .param("logData", "")
                        .param("order", "loggingTime")
                        .param("pageable", String.valueOf(PageRequest.of(1,10)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CustomResultMessage.OK.getMessage()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").hasJsonPath())
                .andExpect(jsonPath("$.data.logs.content", hasSize(2)))
                //.andExpect(jsonPath("$.data.logs.content").value(objectMapper.writeValueAsString(list)))
                .andExpect(jsonPath("$.data.logs.content").value(Matchers.containsInAnyOrder(listJson.toArray())));
        // https://stackoverflow.com/questions/63539533/java-lang-assertionerror-got-a-list-of-values-but-instead-of-the-expected-singl
    }
}
