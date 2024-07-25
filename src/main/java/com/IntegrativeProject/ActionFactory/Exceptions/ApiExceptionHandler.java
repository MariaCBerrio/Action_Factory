package com.IntegrativeProject.ActionFactory.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value= {ApiRequestException.class})
    public ResponseEntity<Object> handleEmployeeRequestException(ApiRequestException e){
        //1. create payload containing exception details
        HttpStatus badRequest=HttpStatus.BAD_REQUEST;

        ApiException employeeException =new ApiException(
                e.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))

        );
        //2. return response entity
        //que es playload
        return  new ResponseEntity<>(employeeException,badRequest);
    }
}
