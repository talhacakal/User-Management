package com.jackal.user.management.service;

import com.jackal.user.management.dto.ChangePasswordRequest;
import com.jackal.user.management.dto.PasswordRenewRequest;
import com.jackal.user.management.event.ForgotPasswordEvent;
import com.jackal.user.management.utils.ErrorDetails;
import com.jackal.user.management.exception.UserNotFoundException;
import com.jackal.user.management.token.JwtService;
import com.jackal.user.management.token.TokenType;
import com.jackal.user.management.user.AppUser;
import com.jackal.user.management.user.AppUserRepository;
import com.jackal.user.management.utils.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AppUserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ResponseEntity<?> changePassword(ChangePasswordRequest passwordRequest, Principal userPrincipal) {
        var user = (AppUser) ((UsernamePasswordAuthenticationToken) userPrincipal).getPrincipal();

        if (!this.passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword()))
            return new ResponseEntity<ErrorDetails>(new ErrorDetails("Wrong password.", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);

        if (!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmationPassword()))
            return new ResponseEntity<ErrorDetails>(new ErrorDetails("Password are not same.", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);

        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        this.userRepository.save(user);

        return ResponseEntity.ok().build();
    }
    public ResponseEntity<?> forgotpassword(HttpServletRequest request, String email) throws UserNotFoundException {
        var user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Invalid email."));

        var baseURI = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        this.eventPublisher.publishEvent(new ForgotPasswordEvent(user, baseURI));

        return new ResponseEntity<SuccessResponse>(new SuccessResponse("Password reset email sent", HttpStatus.OK.value()), HttpStatus.OK);
    }
    public ResponseEntity<?> renewPassword(String token, PasswordRenewRequest passwordRenew) throws UserNotFoundException {
        if (this.jwtService.isTokenExpired(token))
            return new ResponseEntity<>(new ErrorDetails("Token is expired.", HttpStatus.FORBIDDEN.value()), HttpStatus.UNAUTHORIZED);
        if (!this.jwtService.getExtraClaimFromToken(token, "TokenType").equals(TokenType.PASSWORD_REFRESH.name()))
            return new ResponseEntity<>(new ErrorDetails("Invalid token.", HttpStatus.FORBIDDEN.value()), HttpStatus.UNAUTHORIZED);
        if (!passwordRenew.getNewPassword().equals(passwordRenew.getConfirmationPassword()))
            throw new BadCredentialsException("Passwords do not match.");

        var email = this.jwtService.getUsernameFromJwt(token);
        var user = this.userRepository.findByEmail(email)
                .orElseThrow( () -> new UserNotFoundException("User not found"));
        user.setPassword(this.passwordEncoder.encode(passwordRenew.getNewPassword()));
        this.userRepository.save(user);

        return ResponseEntity.ok().build();

    }
}
