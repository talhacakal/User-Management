package com.jackal.user.management.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByTokenAndTokenType(String token, TokenType tokenType);
    List<Token> findByUser_Email(String email);
    List<Token> findByUser_EmailAndTokenType(String email, TokenType tokenType);

}