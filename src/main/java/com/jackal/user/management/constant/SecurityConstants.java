package com.jackal.user.management.constant;

public class SecurityConstants {

    public static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/**",
            "/api/public/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/swagger-ui/index.html"
    };
    public static final String[] ADMIN_LIST_URL = {
            "/api/v1/admin/**"
    };
    public static final String[] USER_LIST_URL = {
            "/api/v1/user/**"
    };

}
