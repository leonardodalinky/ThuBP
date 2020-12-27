package cn.edu.tsinghua.thubp.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
@Order(value = 8000)
public class InvalidFormatExceptionHandler {
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(InvalidFormatException ex, HttpServletRequest request) {
        CommonException commonException = new CommonException(CommonErrorCode.USER_NAME_ALREADY_EXIST, ImmutableMap.of("value", ex.getValue()));
        ErrorResponse errorResponse = new ErrorResponse(commonException, request.getRequestURI());
        String s = String.format("occur %s: %s", commonException.getClass().getSimpleName(), errorResponse.toString());
        log.error(s);
        return ResponseEntity.status(commonException.getErrorCode().getStatus()).body(errorResponse);
    }
}
