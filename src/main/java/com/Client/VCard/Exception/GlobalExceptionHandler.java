package com.Client.VCard.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

	 @ExceptionHandler(CustomRunTimeException.class)
	    public ResponseEntity<String> handleCustomException(CustomRunTimeException ex) {
	        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	    }

	 @ExceptionHandler(CustomIllegalArguementException.class)
	 public ResponseEntity<String> handleCustomException(CustomIllegalArguementException ex){
		 return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	 }
    // Add more exception handlers as needed
}
