package cn.edu.tsinghua.thubp.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * {@link CommonException} 的处理器.
 */
@ControllerAdvice
@Slf4j
@Order(value = 10000)
public class CommonExceptionHandler {
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(CommonException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex, request.getRequestURI());
        String s = String.format("occur %s: %s", ex.getClass().getSimpleName(), errorResponse.toString());
        log.error(s);
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(errorResponse);
    }
}
