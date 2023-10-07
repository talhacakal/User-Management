package com.jackal.user.management.controller;

import com.jackal.user.management.dto.AuthenticationRequest;
import com.jackal.user.management.dto.RegisterRequest;
import com.jackal.user.management.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;


    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody AuthenticationRequest authenticationRequest) throws BadCredentialsException {
        return this.authService.signin(authenticationRequest);
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(HttpServletRequest request, @RequestBody RegisterRequest register) {
        return this.authService.register(request, register);
    }
    @GetMapping("/signup/verifyEmail")
    public ResponseEntity<?> verifyEmail(HttpServletRequest request,  @RequestParam("token") String token) {
        return this.authService.verifyEmail(request, token);
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return this.authService.refreshToken(request);
    }
}
