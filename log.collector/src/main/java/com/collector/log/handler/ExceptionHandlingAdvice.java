package com.collector.log.handler;

import com.collector.log.dto.ApiExceptionResult;
import com.collector.log.dto.ApiResult;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.mail.MailException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlingAdvice extends ResponseEntityExceptionHandler {

    /*
     * HTTP 400 Exception
     */
    @ExceptionHandler({
            ParameterException.class, // 커스텀 예외 내용 상 IllegalArgumentException.class 이용해도 무방
            TargetException.class
            /* 커스텀 예외 외 익셉션 클래스는 부모 클래스에서 처리
             * @RequestParam 에서 인자 값이 들어오지 않는 경우 (required=false 하면 예외 발생 안함)
             * MissingServletRequestParameterException.class,
             * 요청 인자 값 형식을 잘못 입력하는 경우 ex. 날짜 형식
             * TypeMismatchException.class
             */
    })
    public ResponseEntity<?> handleBadRequestException(Exception e) {
        log.debug("Bad Request Exception Occurred: {}", e.getMessage(), e);
        HttpStatus status = null;
        if(e instanceof ParameterException) {
            status = HttpStatus.BAD_REQUEST;
        } else if(e instanceof TargetException) {
            status = HttpStatus.NOT_FOUND;
        }
        assert status != null;
        return handleExceptionInternal(e, null, null, status, null);
    }


    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
        return handleExceptionInternal(ex, errors, headers, status, request);
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String fieldName = null;
        Throwable cause = ex.getCause();

        if(cause instanceof JsonParseException) {
            JsonParseException jpe = (JsonParseException) cause;
            fieldName = jpe.getOriginalMessage();
        } else if(cause instanceof MismatchedInputException) {
            MismatchedInputException mie = (MismatchedInputException) cause;
            fieldName = mie.getOriginalMessage();
            if(mie.getPath() != null && mie.getPath().size() > 0) {
                fieldName = mie.getPath().get(0).getFieldName();
            }
        } else if(cause instanceof JsonMappingException) {
            JsonMappingException jme = (JsonMappingException) cause;
            fieldName = jme.getOriginalMessage();
            if (jme.getPath() != null && jme.getPath().size() > 0) {
                fieldName = "" + jme.getPath().get(0).getFieldName();
            }
        }
        String message = "[" + fieldName + "] 예상하지 못한 Json Request 형식 입니다.";
        return handleExceptionInternal(ex, message, headers, status, request);
    }


    @Override
    public ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        String message = ex.getMessage();
        if(body != null) {
            message = body.toString();
        }

        return ApiExceptionResult.builder()
                .status(status.value())
                .message(message)
                .build()
                .getResponse(status);
    }

}
