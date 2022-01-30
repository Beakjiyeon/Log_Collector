package com.collector.log.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Data
@Builder
@AllArgsConstructor
public class ApiResult {

    private Integer status;

    private String message;

    private Object data;

    public ResponseEntity<Object> getResponse(HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(this);
    }
}