package com.scar.bookvault.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            @Value("${security.jwt.publicKeyPem:}") String publicKeyPem) throws Exception {
        boolean hasKey = publicKeyPem != null && !publicKeyPem.isBlank();
        
        ServerHttpSecurity.AuthorizeExchangeSpec authorize = http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/api/iam/**").permitAll()
                        .anyExchange().authenticated()
                );

        if (hasKey) {
            ReactiveJwtDecoder decoder = jwtDecoder(publicKeyPem);
            return authorize
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtDecoder(decoder)))
                    .build();
        } else {
            // No key provided -> permit all (dev fallback)
            return authorize
                    .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                    .build();
        }
    }

    private ReactiveJwtDecoder jwtDecoder(String publicKeyPem) throws Exception {
        PublicKey publicKey = readPublicKeyFromPem(publicKeyPem);
        return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
    }

    private static PublicKey readPublicKeyFromPem(String pem) throws Exception {
        String clean = pem.replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(clean);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}


