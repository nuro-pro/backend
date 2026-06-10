package com.nuro.server.global.exception;

import com.nuro.server.global.response.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<CommonResponse<?>> handleApplicationException(ApplicationException e) {
        log.warn("[ApplicationException] {}", e.getMessage());
        return status(e.getErrorCase().getHttpStatusCode())
                .body(CommonResponse.error(e.getErrorCase()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst()
                .map(ObjectError::getDefaultMessage)
                .orElse(GlobalErrorCase.INVALID_INPUT.getMessage());

        log.warn("[ValidationException] {}", message);
        return status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(GlobalErrorCase.INVALID_INPUT.getErrorCode(), message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("[ConstraintViolationException] {}", e.getMessage());
        return status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(GlobalErrorCase.INVALID_INPUT));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("[HttpMessageNotReadable] {}", e.getMessage());
        return status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(GlobalErrorCase.INVALID_INPUT));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CommonResponse<?>> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("[MissingServletRequestParameter] parameter={}", e.getParameterName());
        return status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(GlobalErrorCase.INVALID_INPUT.getErrorCode(),
                        "필수 요청 파라미터가 누락되었습니다: " + e.getParameterName()));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<CommonResponse<?>> handleMissingPart(MissingServletRequestPartException e) {
        log.warn("[MissingServletRequestPart] partName={}", e.getRequestPartName());
        return status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(GlobalErrorCase.INVALID_INPUT.getErrorCode(),
                        "필수 요청 파트가 누락되었습니다: " + e.getRequestPartName()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponse<?>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("[MethodNotAllowed] {}", e.getMessage());
        return status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(CommonResponse.error(GlobalErrorCase.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleNoResourceFound(NoResourceFoundException e, HttpServletRequest request) {
        log.warn("[NoResourceFound] uri={}, message={}", request.getRequestURI(), e.getMessage());
        return status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(GlobalErrorCase.RESOURCE_NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleUnexpectedException(Exception e, HttpServletRequest request) throws Exception {
        if (request.getRequestURI().startsWith("/actuator")) {
            throw e;
        }
        log.error("[UnexpectedException]", e);
        return status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error(GlobalErrorCase.INTERNAL_SERVER_ERROR));
    }
}