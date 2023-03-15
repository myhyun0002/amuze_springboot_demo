package com.amuse.www.demo.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@ControllerAdvice
@RestController
public class CommonExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {ValidationException.class})
    public ErrorResponse validationExceptionHandler(ValidationException e) {
        log.error(e.getMessage(), e);
        List<FieldError> errors = e.getErrors();
        return new ErrorResponse(400, e.getMessage(), errors);
    }


    @Getter
    @NoArgsConstructor
    public static class ErrorResponse {
        private int code;
        private String message;
        private List<FieldError> errors;

        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public ErrorResponse(int code, String message, List<FieldError> errors) {
            this.code = code;
            this.message = message;
            this.errors = errors;
        }
    }
}
