package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class errorController {
    
    @ExceptionHandler(RuntimeException.class)
    public JSONObject runtimeException(RuntimeException exception) {
        System.out.println("runtimeException");
        String message=exception.getMessage();
        exception.printStackTrace();
        if(message==null){
            message="알수 없는 오류발생";
        }
        return utillService.makeJson(false, message);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JSONObject processValidationError(MethodArgumentNotValidException exception) {
        System.out.println("processValidationError 유효성 검사 실패");
        BindingResult bindingResult = exception.getBindingResult();
        StringBuilder builder = new StringBuilder();
        List<String>list=new ArrayList<>();
        
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append(fieldError.getDefaultMessage());
            list.add(fieldError.getField());
        }

        return utillService.makeJson(false, builder.toString());
    }
}
