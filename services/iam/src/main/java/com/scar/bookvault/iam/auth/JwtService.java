package com.scar.bookvault.iam.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final String issuer;
    private final long ttlSeconds;

    public JwtService(
            @Value("${security.jwt.privateKeyPem:}") String privateKeyPem,
            @Value("${security.jwt.publicKeyPem:}") String publicKeyPem,
            @Value("${security.jwt.issuer:bookvault-iam}") String issuer,
            @Value("${security.jwt.ttlSeconds:3600}") long ttlSeconds
    ) {
        try {
            if (privateKeyPem != null && !privateKeyPem.isEmpty() && publicKeyPem != null && !publicKeyPem.isEmpty()) {
                this.privateKey = readPrivateKeyFromPem(privateKeyPem);
                this.publicKey = readPublicKeyFromPem(publicKeyPem);
            } else {
                KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
                gen.initialize(2048);
                KeyPair kp = gen.generateKeyPair();
                this.privateKey = kp.getPrivate();
                this.publicKey = kp.getPublic();
            }
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to initialize RSA keys", e);
        }
        this.issuer = issuer;
        this.ttlSeconds = ttlSeconds;
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(issuer)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String getPublicKeyPem() {
        String base64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" + base64 + "\n-----END PUBLIC KEY-----\n";
    }

    private static PrivateKey readPrivateKeyFromPem(String pem) {
        try {
            String clean = pem.replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(clean);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid private key", e);
        }
    }

    private static PublicKey readPublicKeyFromPem(String pem) {
        try {
            String clean = pem.replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(clean);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid public key", e);
        }
    }
}


