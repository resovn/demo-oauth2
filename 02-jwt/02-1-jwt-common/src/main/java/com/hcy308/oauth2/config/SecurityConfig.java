package com.hcy308.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Configuration
public class SecurityConfig {

    @SuppressWarnings("WeakerAccess")
    public static final String RESOURCE_ID = "multi-server-with-jwt";

    @Autowired
    private JwtConfig jwtConfig;

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter result = new JwtAccessTokenConverter();
        result.setSigningKey(jwtConfig.getSecret());
        return result;
    }
}
