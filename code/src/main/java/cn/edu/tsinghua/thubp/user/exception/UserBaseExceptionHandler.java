package cn.edu.tsinghua.thubp.user.exception;

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
public class UserBaseExceptionHandler {
    @ExceptionHandler(UserBaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(UserBaseException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex, request.getRequestURI());
        String s = String.format("occur %s: %s", ex.getClass().getSimpleName(), errorResponse.toString());
        log.error(s);
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(errorResponse);
    }
}
