package com.example.demo.controller;

import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class errorController {
    
    @ExceptionHandler(RuntimeException.class)
    public JSONObject runtimeException(RuntimeException exception) {
        System.out.println("runtimeException");
        exception.printStackTrace();
        return utillService.makeJson(false, exception.getMessage());
    }
}
