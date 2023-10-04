package com.jackal.user.management.Controller;

import com.jackal.user.management.Entity.DTO.AuthenticationRequest;
import com.jackal.user.management.Entity.DTO.RegisterRequest;
import com.jackal.user.management.Service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody RegisterRequest register) throws Exception {
        return this.authService.register(register);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody AuthenticationRequest authenticationRequest){
        return this.authService.signin(authenticationRequest);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return this.authService.refreshToken(request, response);
    }
}
