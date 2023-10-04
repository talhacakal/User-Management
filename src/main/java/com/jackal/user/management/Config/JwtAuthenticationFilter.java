package com.jackal.user.management.Config;

import com.jackal.user.management.Token.JwtService;
import com.jackal.user.management.Token.TokenRepository;
import com.jackal.user.management.Token.TokenType;
import com.jackal.user.management.User.AppUserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.spec.PSource;
import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AppUserRepository userRepository;

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/signin",
            "/api/v1/auth/signup",
            "/api/v1/auth/refresh-token"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String jwt;
        final String username;
       try{
           jwt = jwtService.extractTokenFromCookie(request.getCookies(), TokenType.BEARER);
           username = jwtService.getUsernameFromJwt(jwt);

           if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
               var user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found."));
               var isTokenValid = this.tokenRepository.findByToken(jwt)
                       .map(t -> !t.isExpired() && !t.isRevoked())
                       .orElse(false);
               if (jwtService.isTokenValid(jwt, user) && isTokenValid){
                   UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
                   authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                   SecurityContextHolder.getContext().setAuthentication(authToken);
               }
           }
           filterChain.doFilter(request, response);

       } catch (NullPointerException | ExpiredJwtException | IllegalArgumentException | UnsupportedJwtException | MalformedJwtException | SignatureException e){
           response.setStatus(HttpStatus.UNAUTHORIZED.value());
       } catch (Exception e){
           response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
       }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return Arrays.asList(WHITE_LIST_URL).contains(path);
    }
}
