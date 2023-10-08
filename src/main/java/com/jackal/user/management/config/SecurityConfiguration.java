package com.jackal.user.management.config;

import com.jackal.user.management.constant.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.jackal.user.management.token.TokenType.BEARER;
import static com.jackal.user.management.token.TokenType.REFRESH;
import static com.jackal.user.management.user.Role.ADMIN;
import static com.jackal.user.management.user.Role.USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
                .csrf().disable()
                .authorizeHttpRequests(request->
                        request
                                .requestMatchers(SecurityConstants.WHITE_LIST_URL).permitAll()
                                .requestMatchers(SecurityConstants.ADMIN_LIST_URL).hasAnyRole(ADMIN.name())
                                .requestMatchers(SecurityConstants.USER_LIST_URL).hasAnyRole(USER.name(), ADMIN.name())
                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout
                                .logoutUrl("/api/v1/security/logout")
                                .addLogoutHandler(logoutHandler)
                                .deleteCookies(BEARER.name(), REFRESH.name())
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );
        return http.build();
    }
}
