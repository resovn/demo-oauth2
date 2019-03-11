package com.hcy308.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Configuration
public class SecurityConfig {

    @SuppressWarnings("WeakerAccess")
    public static final String RESOURCE_ID = "multi-server-token-store-db";

    @Autowired
    private TokenStore tokenStore;

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore);
        return defaultTokenServices;
    }

}
