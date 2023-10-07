package com.jackal.user.management.service;

import com.jackal.user.management.token.JwtService;
import com.jackal.user.management.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtService jwtService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String jwt = this.jwtService.extractTokenFromCookie(request.getCookies(), TokenType.BEARER);
        if (jwt == null) throw new UsernameNotFoundException("User not found.");

        String user = this.jwtService.getUsernameFromJwt(jwt);
        this.jwtService.deleteAllUserTokens(user);

        SecurityContextHolder.clearContext();
    }
}
