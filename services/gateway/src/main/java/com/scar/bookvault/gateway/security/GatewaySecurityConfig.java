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
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            @Value("${security.jwt.publicKeyPem:}") String publicKeyPem) throws Exception {
        String cleanKey = publicKeyPem != null ? publicKeyPem.trim() : "";
        // Check if key is valid (contains actual key content, not just headers)
        boolean hasValidKey = cleanKey.length() > 100 && 
                              (cleanKey.contains("-----BEGIN") || cleanKey.contains("MII"));
        
        http.csrf(csrf -> csrf.disable());

        if (hasValidKey) {
            try {
                ReactiveJwtDecoder decoder = jwtDecoder(cleanKey);
                http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtDecoder(decoder)))
                    .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/api/iam/**").permitAll()
                        .anyExchange().authenticated()
                    );
            } catch (Exception e) {
                // If key parsing fails, fall back to permit all
                http.authorizeExchange(exchange -> exchange.anyExchange().permitAll());
            }
        } else {
            // No valid key provided -> permit all (dev fallback)
            http.authorizeExchange(exchange -> exchange.anyExchange().permitAll());
        }

        return http.build();
    }

    private ReactiveJwtDecoder jwtDecoder(String publicKeyPem) throws Exception {
        RSAPublicKey publicKey = readPublicKeyFromPem(publicKeyPem);
        return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
    }

    private static RSAPublicKey readPublicKeyFromPem(String pem) throws Exception {
        String clean = pem.replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(clean);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }
}


