package com.tutoring.exception;

import com.tutoring.enumeration.ErrorCode;
import com.tutoring.result.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle all CustomExceptions thrown from controllers or services
     */
    @ExceptionHandler(CustomException.class)
    public RestResult<?> handleCustomException(CustomException ex) {
        log.error("CustomException caught: {}", ex.getMessage(), ex);
        ErrorCode errorCode = ex.getErrorCode();
        // You could choose to use either the default message from errorCode or ex.getMessage()
        // If you used the constructor with a custom message, ex.getMessage() might differ from errorCode.getMessage()
        return RestResult.error(ex.getMessage(), errorCode.getCode());
    }

    /**
     * Handle all unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public RestResult<?> handleException(Exception ex) {
        log.error("Unknown exception: {}", ex.getMessage(), ex);
        // Return a generic internal server error
        return RestResult.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                ErrorCode.INTERNAL_SERVER_ERROR.getCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestResult<?> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        StringBuilder errorMsg = new StringBuilder("Validation failed: ");
        for (FieldError fieldError : fieldErrors) {
            errorMsg
                    .append("[")
                    .append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("] ");
        }

        log.error("Validation error: {}", errorMsg.toString());

        return RestResult.error(errorMsg.toString(), 400);
    }
}
