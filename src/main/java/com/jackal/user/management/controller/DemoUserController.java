package com.jackal.user.management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class DemoUserController {

    @GetMapping
    public String greeting(){
        return "GET :: User Controller";
    }

}
