package com.jackal.user.management.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class DemoPublicController {

    @GetMapping
    public String greeting(){
        return "GET :: Public controller";
    }
    @PostMapping
    public String postMapping(){
        return "POST :: Public Controller";
    }
    @PutMapping
    public String putMapping(){
        return "PUT :: Public Controller";
    }
    @DeleteMapping
    public String deleteMapping(){
        return "DELETE :: Public Controller";
    }
}