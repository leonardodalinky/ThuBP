package cn.edu.tsinghua.thubp.web.exception;

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
@Order(value = 10000)
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex, HttpServletRequest request) {
        log.error("occur Exception:" + ex.getMessage());
        return ResponseEntity.status(500).body(ex.getMessage());
    }
}
