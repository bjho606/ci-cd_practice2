package com.ssafy.meshroom.backend.global.error;

import com.ssafy.meshroom.backend.global.error.code.CommonErrorCode;
import com.ssafy.meshroom.backend.global.error.code.ErrorCode;
import com.ssafy.meshroom.backend.global.error.dto.ErrorResponse;
import com.ssafy.meshroom.backend.global.error.exception.FullCapacityLimitException;
import com.ssafy.meshroom.backend.global.error.exception.OpenViduException;
import com.ssafy.meshroom.backend.global.error.exception.RestApiException;
import com.ssafy.meshroom.backend.global.error.exception.SessionNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler to handle various exceptions across the application.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FullCapacityLimitException.class)
    public ResponseEntity<Object> handleFullCapacityLimitExceptionException(FullCapacityLimitException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(OpenViduException.class)
    public ResponseEntity<Object> handleOpenViduExceptionException(OpenViduException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(SessionNotExistException.class)
    public ResponseEntity<Object> handleSessionNotExistExceptionException(SessionNotExistException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }
    /**
     * Handles custom exceptions of type RestApiException.
     *
     * @param e RestApiException
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    /**
     * Handles IllegalArgumentException.
     *
     * @param e IllegalArgumentException
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("handleIllegalArgument", e);
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(errorCode, e.getMessage());
    }

    /**
     * Handles validation exceptions.
     *
     * @param e       MethodArgumentNotValidException
     * @param headers HttpHeaders
     * @param status  HttpStatusCode
     * @param request WebRequest
     * @return ResponseEntity with validation error details
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        log.warn("handleIllegalArgument", e);
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(e, errorCode);
    }

    /**
     * Handles generic exceptions.
     *
     * @param ex Exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllException(Exception ex) {
        log.warn("handleAllException", ex);
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(errorCode);
    }

    /**
     * Creates a ResponseEntity with the given error code.
     *
     * @param errorCode ErrorCode
     * @return ResponseEntity with error details
     */
    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(errorCode));
    }

    /**
     * Creates an ErrorResponse from the given error code.
     *
     * @param errorCode ErrorCode
     * @return ErrorResponse
     */
    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }

    /**
     * Creates a ResponseEntity with the given error code and message.
     *
     * @param errorCode ErrorCode
     * @param message   Error message
     * @return ResponseEntity with error details
     */
    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(errorCode, message));
    }

    /**
     * Creates an ErrorResponse from the given error code and message.
     *
     * @param errorCode ErrorCode
     * @param message   Error message
     * @return ErrorResponse
     */
    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(message)
                .build();
    }

    /**
     * Creates a ResponseEntity with the given BindException and error code.
     *
     * @param e         BindException
     * @param errorCode ErrorCode
     * @return ResponseEntity with validation error details
     */
    private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(e, errorCode));
    }

    /**
     * Creates an ErrorResponse from the given BindException and error code.
     *
     * @param e         BindException
     * @param errorCode ErrorCode
     * @return ErrorResponse with validation error details
     */
    private ErrorResponse makeErrorResponse(BindException e, ErrorCode errorCode) {
        List<ErrorResponse.ValidationError> validationErrorList = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ErrorResponse.ValidationError::of)
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .errors(validationErrorList)
                .build();
    }
}
