package com.jackal.user.management.Service;

import com.jackal.user.management.Entity.DTO.AuthenticationRequest;
import com.jackal.user.management.Entity.DTO.RegisterRequest;
import com.jackal.user.management.Entity.DTO.UserDTO;
import com.jackal.user.management.Token.JwtService;
import com.jackal.user.management.Token.Token;
import com.jackal.user.management.Token.TokenRepository;
import com.jackal.user.management.User.AppUser;
import com.jackal.user.management.User.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.server.ResponseStatusException;

import static com.jackal.user.management.Token.TokenType.BEARER;
import static com.jackal.user.management.Token.TokenType.REFRESH;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> register(RegisterRequest register) {
        try {
            boolean userExits = this.userRepository.findByEmail(register.getEmail()).isEmpty();
            if (!userExits) return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists: " + register.getEmail());

            AppUser newUser = new AppUser(
                    register.getFirstname(),
                    register.getLastname(),
                    register.getEmail(),
                    passwordEncoder.encode(register.getPassword()),
                    register.getRole()
            );

            var savedUser = this.userRepository.save(newUser);
            return ResponseEntity.ok(new UserDTO(savedUser));

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    public ResponseEntity<?> signin(AuthenticationRequest authRequest){
        try {
            Authentication authenticate = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authenticate);

            AppUser user = this.userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));

            final String jwt = this.jwtService.generateJwtToken(user);
            final String refresh_token = this.jwtService.generateRefreshToken(user);
            this.jwtService.saveUserTokens(user, jwt, refresh_token);

            return userCookies(jwt,refresh_token);

        } catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid username or password.");
        } catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String requestRefreshToken = this.jwtService.extractTokenFromCookie(request.getCookies(), REFRESH);
        String refreshToken = this.tokenRepository
                .findByToken(requestRefreshToken)
                .filter(token -> token.getToken().equals(requestRefreshToken))
                .map(Token::getToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token do not match or missing."));

        var user = this.userRepository.findByEmail(this.jwtService.getUsernameFromJwt(refreshToken))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (this.jwtService.isTokenValid(refreshToken, user)){
            var jwt = this.jwtService.generateJwtToken(user);

            this.jwtService.deleteUserJwtTokens(user.getEmail());
            this.jwtService.saveJwtToken(user, jwt);

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, generateJwtCookie(jwt)).build();

        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
