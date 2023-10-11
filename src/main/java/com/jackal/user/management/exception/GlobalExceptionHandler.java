package com.jackal.user.management.exception;

import com.jackal.user.management.utils.ErrorDetails;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> badCredentialsExceptionHandler(BadCredentialsException ex){
        var error = new ErrorDetails(ex.getMessage(),HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex){
        var error = new ErrorDetails(
                ex.getFieldErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList(),
                ex.getStatusCode().value()
        );
        return new ResponseEntity<>(error, ex.getStatusCode());
    }
    @ExceptionHandler({NoSuchElementException.class, UsernameNotFoundException.class})
    public ResponseEntity<?> noSuchElementExceptionHandler(Exception ex){
        var error = new ErrorDetails(ex.getMessage(),HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> signatureExceptionHandler(Exception ex) {
        var error = new ErrorDetails(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

}
