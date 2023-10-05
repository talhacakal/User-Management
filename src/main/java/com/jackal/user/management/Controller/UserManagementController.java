package com.jackal.user.management.Controller;

import com.jackal.user.management.Entity.DTO.ChangePasswordRequest;
import com.jackal.user.management.Service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest passwordRequest, Principal userPrincipal){
        return this.userManagementService.changePassword(passwordRequest, userPrincipal);
    }

}
