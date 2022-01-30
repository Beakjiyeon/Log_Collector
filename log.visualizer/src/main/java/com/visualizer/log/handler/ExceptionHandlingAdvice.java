package com.visualizer.log.handler;


import com.visualizer.log.dto.ApiExceptionResult;
import com.visualizer.log.dto.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
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
            TargetException.class,
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
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
        return handleExceptionInternal(ex, errors, headers, status, request);
    }





    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
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
