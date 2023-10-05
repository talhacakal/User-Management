package com.jackal.user.management.Service;

import com.jackal.user.management.Entity.DTO.ChangePasswordRequest;
import com.jackal.user.management.User.AppUser;
import com.jackal.user.management.User.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository userRepository;

    public ResponseEntity<?> changePassword(ChangePasswordRequest passwordRequest, Principal userPrincipal) {
        var user = (AppUser) ((UsernamePasswordAuthenticationToken) userPrincipal).getPrincipal();

        if (!this.passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong password");

        if (!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmationPassword()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password are not same");

        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        this.userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}
