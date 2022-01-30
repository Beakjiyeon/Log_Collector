package com.collector.log.controller;

import com.collector.log.dto.LogSaveRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;

@RestController
public class SampleController {

    private static final String EXCHANGE_NAME = "sample.exchange";

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/sample/queue")
    public String samplePublish() {
        LogSaveRequestDto log = LogSaveRequestDto.builder()
                .traceId("trace1010")
                .tagId("serviceA")
                .logType("ERROR")
                .logData("test")
                .loggingTime(LocalDateTime.now())
                .build();
        try {
            String json = objectMapper.writeValueAsString(log);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, "sample.logcollector.#", json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "message sending3!";
    }
}
