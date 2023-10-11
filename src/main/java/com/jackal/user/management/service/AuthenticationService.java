package com.jackal.user.management.service;

import com.jackal.user.management.dto.AuthenticationRequest;
import com.jackal.user.management.dto.RegisterRequest;
import com.jackal.user.management.event.ClearUserTokensEvent;
import com.jackal.user.management.event.RegistrationCompleteEvent;
import com.jackal.user.management.token.JwtService;
import com.jackal.user.management.token.Token;
import com.jackal.user.management.token.TokenRepository;
import com.jackal.user.management.token.TokenType;
import com.jackal.user.management.user.AppUser;
import com.jackal.user.management.user.AppUserRepository;
import com.jackal.user.management.utils.ErrorDetails;
import com.jackal.user.management.utils.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static com.jackal.user.management.token.TokenType.BEARER;
import static com.jackal.user.management.token.TokenType.REFRESH;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;

    private final AppUserRepository userRepository;
    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> signin(AuthenticationRequest authRequest) throws BadCredentialsException {
        Authentication authenticate = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        AppUser user = this.userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));

        String jwt = this.jwtService.generateJwtToken(user);
        String refresh_token = this.jwtService.generateRefreshToken(user);
        this.jwtService.saveUserTokens(user, jwt, refresh_token);

        this.eventPublisher.publishEvent(new ClearUserTokensEvent(user, jwt, refresh_token));

        return userCookies(jwt,refresh_token);
    }
    public ResponseEntity<?> register(HttpServletRequest request, RegisterRequest register) {
        boolean userExits = this.userRepository.findByEmail(register.getEmail()).isEmpty();
        if (!userExits)
            return new ResponseEntity<ErrorDetails>(new ErrorDetails(("Email already exists: " + register.getEmail()), HttpStatus.CONFLICT.value()),HttpStatus.CONFLICT);

        AppUser newUser = new AppUser(
                register.getFirstname(),
                register.getLastname(),
                register.getEmail(),
                passwordEncoder.encode(register.getPassword()),
                register.getRole()
        );
        var savedUser = this.userRepository.save(newUser);

        var baseURI = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        this.eventPublisher.publishEvent(new RegistrationCompleteEvent(savedUser, baseURI));

        return new ResponseEntity<SuccessResponse>(new SuccessResponse("Registration successful. Please check your email.", HttpStatus.OK.value()),HttpStatus.OK);
    }
    public ResponseEntity<?> verifyEmail(HttpServletRequest request, String token) {
        AppUser user = this.userRepository.findByEmail(this.jwtService.getUsarnemeFromTokenIgnoreExp(token)).orElseThrow();
        if (this.jwtService.isTokenExpired(token) && !this.jwtService.getExtraClaimFromToken(token, "TokenType").equals(TokenType.VERIFICATION.name())){
            var baseURI = request.getRequestURL().toString().replace(request.getRequestURI(), "");
            this.eventPublisher.publishEvent(new RegistrationCompleteEvent(user,baseURI));

            return new ResponseEntity<ErrorDetails>(
                    new ErrorDetails("The email verification link invalid. Email sent again.", HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        }
        user.setEnabled(true);
        this.userRepository.save(user);

        return ResponseEntity.ok(new SuccessResponse("Email verified.", HttpStatus.OK.value()));
    }
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String requestRefreshToken = this.jwtService.extractTokenFromCookie(request.getCookies(), REFRESH);
        String refreshToken = this.tokenRepository
                .findByTokenAndTokenType(requestRefreshToken, REFRESH)
                .map(Token::getToken)
                .orElseThrow(() -> new NoSuchElementException("Refresh token do not match or missing."));

        var user = this.userRepository.findByEmail(this.jwtService.getUsernameFromJwt(refreshToken))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (this.jwtService.isTokenValid(refreshToken, user)){
            var jwt = this.jwtService.generateJwtToken(user);

            this.jwtService.deleteUserTokens(user.getEmail(), BEARER);
            this.jwtService.saveJwtToken(user, jwt);

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, generateJwtCookie(jwt)).build();

        }else return new ResponseEntity<ErrorDetails>(new ErrorDetails("Refresh token is not valid.", HttpStatus.BAD_REQUEST.value()),HttpStatus.BAD_REQUEST);
    }


    private ResponseEntity<?> userCookies(String jwt, String refresh_token){
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, generateJwtCookie(jwt))
                .header(HttpHeaders.SET_COOKIE, generateRefreshCookie(refresh_token))
                .build();
    }
    private String generateJwtCookie(String jwt){
        return ResponseCookie
                .from(BEARER.name(), jwt)
                .path("/")
                .maxAge(this.jwtService.getJwtExpirationAsSecond()).build().toString();
    }
    private String generateRefreshCookie(String refresh_token){
        return ResponseCookie
                .from(REFRESH.name(), refresh_token)
                .path("/")
                .maxAge(this.jwtService.getRefreshExpirationAsSecond()).build().toString();
    }
}
