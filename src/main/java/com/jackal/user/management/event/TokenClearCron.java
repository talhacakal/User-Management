package com.jackal.user.management.event;

import com.jackal.user.management.token.JwtService;
import com.jackal.user.management.token.Token;
import com.jackal.user.management.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenClearCron {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Async
    public void clearTokensJob(){
        List<Token> allTokens = this.tokenRepository.findAll();
        var expiredTokens = allTokens.stream().filter(token -> this.jwtService.isTokenExpired(token.getToken())).toList();
        this.tokenRepository.deleteAll(expiredTokens);
    }
}
