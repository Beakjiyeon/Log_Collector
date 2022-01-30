package com.collector.log.controller;


import com.collector.log.dto.ApiResult;
import com.collector.log.dto.LogSaveRequestDto;
import com.collector.log.handler.CustomResultMessage;
import com.collector.log.service.CollectLogService;
import com.collector.log.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


/**
  db version 2
 */
@RequestMapping("/collect-api/v2")
@RequiredArgsConstructor
@RestController("v2.CollectLogApiController")
public class CollectLogApiController {

    private final CollectLogService collectLogService;
    private final EmailService emailService;

    ObjectMapper mapper = new ObjectMapper();


    @RabbitListener(queues = "sample.queue")
    public void saveMqData(String message) {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        System.out.println(">>>" + message);
        try {
            LogSaveRequestDto log = mapper.readValue(message , LogSaveRequestDto.class);
            collectLogService.save(log);
        } catch (IOException e) {
            // todo [M2] Error handling
            e.printStackTrace();
        }
    }


    @PostMapping("")
    public ResponseEntity save(@RequestBody final @Valid LogSaveRequestDto requestDto) {
        return ApiResult.builder()
                .status(HttpStatus.CREATED.value())
                .message(CustomResultMessage.CREATED.getMessage())
                .data(collectLogService.save(requestDto))
                .build()
                .getResponse(HttpStatus.CREATED);
    }


    @GetMapping("/fail-mails")
    public ResponseEntity getFailedMails(
            @RequestParam(value = "regDate", required = false, defaultValue= "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate regDate,
            @RequestParam(value = "order", required = false, defaultValue = "createAt") String order,
            @PageableDefault Pageable pageable
    ) {
        Map<String, Object> searchVariable = new HashMap<>();
        searchVariable.put("regDate", regDate);
        return ApiResult.builder()
                .status(HttpStatus.OK.value())
                .message(CustomResultMessage.OK.getMessage())
                .data(emailService.getFailedMails(searchVariable, order, pageable))
                .build()
                .getResponse(HttpStatus.OK);
    }

}
