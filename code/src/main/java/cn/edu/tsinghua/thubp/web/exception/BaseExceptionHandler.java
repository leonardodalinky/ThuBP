package cn.edu.tsinghua.thubp.web.exception;

import cn.edu.tsinghua.thubp.user.exception.BaseException;
import cn.edu.tsinghua.thubp.user.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Link
 */
@ControllerAdvice
@Slf4j
@Order(value = 2000)
public class BaseExceptionHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex, request.getRequestURI());
        log.error("occur BaseException:" + errorResponse.toString());
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(errorResponse);
    }
}
