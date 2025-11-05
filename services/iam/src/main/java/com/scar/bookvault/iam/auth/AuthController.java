package com.scar.bookvault.iam.auth;

import com.scar.bookvault.iam.user.User;
import com.scar.bookvault.iam.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/iam/v1/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public record RegisterRequest(@NotBlank String username, @NotBlank String email, @NotBlank String password) {}
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@RequestBody @Valid RegisterRequest req) {
        if (userRepository.existsByUsername(req.username()) || userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("User already exists");
        }
        User u = new User();
        u.setUsername(req.username());
        u.setEmail(req.email());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        userRepository.save(u);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", u.getRole());
        String token = jwtService.generateToken(u.getUsername(), claims);
        return Map.of("accessToken", token, "tokenType", "Bearer");
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody @Valid LoginRequest req) {
        User u = userRepository.findByUsername(req.username()).orElseThrow(() -> new IllegalArgumentException("Bad credentials"));
        if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Bad credentials");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", u.getRole());
        String token = jwtService.generateToken(u.getUsername(), claims);
        return Map.of("accessToken", token, "tokenType", "Bearer");
    }

    @GetMapping("/public-key")
    public Map<String, String> publicKey() {
        return Map.of("publicKeyPem", jwtService.getPublicKeyPem());
    }
}


