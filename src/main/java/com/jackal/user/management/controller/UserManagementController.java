package com.jackal.user.management.controller;

import com.jackal.user.management.dto.ChangePasswordRequest;
import com.jackal.user.management.dto.PasswordRenewRequest;
import com.jackal.user.management.exception.UserNotFoundException;
import com.jackal.user.management.service.UserManagementService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest passwordRequest, Principal userPrincipal){
        return this.userManagementService.changePassword(passwordRequest, userPrincipal);
    }
    @PostMapping("/forgotpassword")
    public ResponseEntity<?> forgotpassword(HttpServletRequest request, @RequestParam String email) throws UserNotFoundException {
        return this.userManagementService.forgotpassword(request, email);
    }
    @PostMapping("/renewPassword")
    public ResponseEntity<?> renewPassword(@RequestParam String token,@RequestBody PasswordRenewRequest passwordRenew) throws UserNotFoundException {
        return this.userManagementService.renewPassword(token, passwordRenew);
    }

}
