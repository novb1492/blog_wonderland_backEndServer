package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class restcontroller {
    
    @PostMapping("/auth/test")
    public void test() {
        System.out.println("test");
    }
}
