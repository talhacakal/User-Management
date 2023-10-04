package com.jackal.user.management.Token;

import com.jackal.user.management.User.AppUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    private final TokenRepository tokenRepository;

    public String extractTokenFromCookie(Cookie[] cookies, TokenType jwtType){
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(jwtType.name()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    public void deleteAllUserTokens(String email){
        List<Token> tokens = this.tokenRepository.findByUser_Email(email);
        this.tokenRepository.deleteAll(tokens);
    }
    public void deleteUserJwtTokens(String email){
        List<Token> tokens = this.tokenRepository.findByUser_EmailAndTokenType(email, TokenType.BEARER);
        this.tokenRepository.deleteAll(tokens);
    }

    public String generateJwtToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, jwtExpiration);
    }
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    public void saveUserTokens(AppUser user, String jwt, String refresh_token){
        this.saveJwtToken(user, jwt);
        this.saveRefrestToken(user, refresh_token);
    }
    public void saveJwtToken(AppUser user, String jwt){
        this.tokenRepository.save(new Token(user, jwt, TokenType.BEARER, false, false));
    }
    public void saveRefrestToken(AppUser user, String refresh_token){
        this.tokenRepository.save(new Token(user, refresh_token, TokenType.REFRESH, false, false));
    }

    public String getUsernameFromJwt(String token){
        return extractClaim(token, Claims::getSubject);
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public boolean isTokenValid(String token, AppUser user) {
        String email = getUsernameFromJwt(token);
        return (email.equals(user.getEmail())) && !isTokenExpired(token);
    }
    public long getJwtExpirationAsSecond() {
        return jwtExpiration/1000;
    }
    public long getRefreshExpirationAsSecond() {
        return refreshExpiration/1000;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }
    private <T> T demo(String str, Function<TokenRepository, T> claimsResolver){
        return claimsResolver.apply(tokenRepository);
    }
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Claims getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
