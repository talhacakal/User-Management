package com.jackal.user.management.token;

import com.jackal.user.management.user.AppUser;
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

    public String generateJwtToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, jwtExpiration);
    }
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, refreshExpiration);
    }
    public String generateTokenWithExtraClaims(UserDetails userDetails, Map<String, Object> extraClaims){
        return generateToken(extraClaims, userDetails, jwtExpiration);
    }

    public void saveUserTokens(AppUser user, String jwt, String refresh_token){
        this.saveJwtToken(user, jwt);
        this.saveRefrestToken(user, refresh_token);
    }
    public void saveRefrestToken(AppUser user, String refresh_token){
        this.saveToken(user,refresh_token,TokenType.REFRESH);
    }
    public void saveJwtToken(AppUser user, String jwt){
        this.saveToken(user,jwt,TokenType.BEARER);
    }
    public void saveToken(AppUser user, String jwt, TokenType tokenType){
        this.tokenRepository.save(new Token(user, jwt, tokenType, false, false));
    }

    public void deleteAllUserTokens(String email){
        List<Token> tokens = this.tokenRepository.findByUser_Email(email);
        this.tokenRepository.deleteAll(tokens);
    }
    public void deleteUserTokens(String email, TokenType tokenType){
        List<Token> tokens = this.tokenRepository.findByUser_EmailAndTokenType(email, tokenType);
        this.tokenRepository.deleteAll(tokens);
    }

    public String getUsernameFromJwt(String token){
        return extractClaim(token, Claims::getSubject);
    }
    public String getExtraClaimFromToken(String token, String claim) {
        try {
            return extractClaim(token, claims ->  claims.get(claim)).toString();
        } catch (NullPointerException e) {
            return "";
        }
    }
    public String getUsarnemeFromTokenIgnoreExp(String token){
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException e){
            return e.getClaims().getSubject();
        }
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
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
