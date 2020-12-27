package cn.edu.tsinghua.thubp.web.exception;

import cn.edu.tsinghua.thubp.common.exception.CommonErrorCode;
import cn.edu.tsinghua.thubp.common.exception.CommonException;
import cn.edu.tsinghua.thubp.common.exception.ErrorResponse;
import cn.edu.tsinghua.thubp.security.service.CurrentUserService;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Order(/* value = Integer.MAX_VALUE */)
public class GlobalExceptionHandler {
    @Autowired
    private CurrentUserService currentUserService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        String s = String.format("occur Exception(%s)(userId: %s): %s", ex.getClass(), currentUserService.getUserId(), ex.getMessage());
        log.error(s);
        ErrorResponse errorResponse = new ErrorResponse(CommonErrorCode.UNKNOW_ERROR, request.getRequestURI(), ImmutableMap.of("error", s));
        return ResponseEntity.status(500).body(errorResponse);
    }
}
