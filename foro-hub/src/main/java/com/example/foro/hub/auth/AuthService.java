package com.example.foro.hub.auth;

import com.example.foro.hub.jwt.AuthenticationService;
import com.example.foro.hub.user.UserModel;
import com.example.foro.hub.user.UserRepository;
import com.example.foro.hub.user.UserRole;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        UserModel user = UserModel.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .enable(true)
                .role(UserRole.USER)
                .build();
        userRepository.save(user);
        return AuthResponse.builder()
                .token(authenticationService.getToken(user))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = authenticationService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }
}
