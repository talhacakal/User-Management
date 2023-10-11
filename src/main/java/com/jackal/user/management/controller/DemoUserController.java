package com.jackal.user.management.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@SecurityRequirement(name = "bearerAuth")
public class DemoUserController {

    @GetMapping
    public String getMapping(){
        return "GET :: User Controller";
    }
    @PostMapping
    public String postMapping(){
        return "POST :: User Controller";
    }
    @PutMapping
    public String putMapping(){
        return "PUT :: User Controller";
    }
    @DeleteMapping
    public String deleteMapping(){
        return "DELETE :: User Controller";
    }
}
