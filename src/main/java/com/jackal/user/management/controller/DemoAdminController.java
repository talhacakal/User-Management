package com.jackal.user.management.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@SecurityRequirement(name = "bearerAuth")
public class DemoAdminController {

    @GetMapping
    public String greeting(){
        return "GET :: Admin Controller";
    }
    @PostMapping
    public String postMapping(){
        return "POST :: Admin Controller";
    }
    @PutMapping
    public String putMapping(){
        return "PUT :: Admin Controller";
    }
    @DeleteMapping
    public String deleteMapping(){
        return "DELETE :: Admin Controller";
    }

}
