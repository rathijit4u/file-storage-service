package org.mourathi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

@ControllerAdvice
public class GlobalException {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<ExceptionDetails> handleNullException(NullPointerException ex){
        logger.error("{}\n{}", ex.getMessage(), ex.getStackTrace());
        return new ResponseEntity<>(new ExceptionDetails(new Date(), ex.getMessage(), ""), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {FileObjectNotFoundException.class})
    public ResponseEntity<ExceptionDetails> fileNotFoundException(Exception ex){
        logger.error("{}\n{}", ex.getMessage(), ex.getStackTrace());
        return new ResponseEntity<>(new ExceptionDetails(new Date(), ex.getMessage(), ""), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ExceptionDetails> handleException(Exception ex){
        logger.error("{}\n{}", ex.getMessage(), ex.getStackTrace());
        return new ResponseEntity<>(new ExceptionDetails(new Date(), ex.getMessage(), ""), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
