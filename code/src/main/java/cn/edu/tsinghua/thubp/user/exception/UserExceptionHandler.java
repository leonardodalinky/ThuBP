package cn.edu.tsinghua.thubp.user.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


/**
 * @author Link
 */
@ControllerAdvice
@ResponseBody
@Slf4j
@Order(value = 0)
public class UserExceptionHandler {

    @ExceptionHandler(value = UsernameAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistException(UsernameAlreadyExistException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex, request.getRequestURI());
        log.error("occur UsernameAlreadyExistException:" + errorResponse.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(BaseException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex, request.getRequestURI());
        log.error("occur ResourceNotFoundException:" + errorResponse.toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
