package com.taidt9.springbootlibrary.config;

import com.okta.spring.boot.oauth.Okta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private OAuth2ClientProperties oAuth2ClientProperties;

    @Autowired
    public SecurityConfiguration(OAuth2ClientProperties oAuth2ClientProperties) {
        this.oAuth2ClientProperties = oAuth2ClientProperties;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable Cross Site Request Forgery
        http.csrf(AbstractHttpConfigurer::disable);

        // Protect endpoints at /api/<type>/secure
        http.authorizeHttpRequests(configure -> configure.requestMatchers("api/payment/secure/**").authenticated().requestMatchers("api/admin/secure/**").authenticated().requestMatchers("api/books/secure/**").authenticated().requestMatchers("api/reviews/secure/**").authenticated().requestMatchers("api/messages/secure/**").authenticated().anyRequest().permitAll()).oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())));

        // Add CORS filters
        http.cors(Customizer.withDefaults());

        // Add content negotiation strategy
        http.setSharedObject(ContentNegotiationStrategy.class, new HeaderContentNegotiationStrategy());

        // Force a non-empty response body for 401's to make the response friendly
        Okta.configureResourceServer401ResponseBody(http);

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String issuer = oAuth2ClientProperties.getProvider().get("okta").getIssuerUri();
        String jwkSetUri = issuer + "/v1/keys";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}
