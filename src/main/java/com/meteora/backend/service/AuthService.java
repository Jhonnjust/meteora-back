package com.meteora.backend.service;

import com.meteora.backend.dto.request.LoginRequest;
import com.meteora.backend.dto.request.RegisterRequest;
import com.meteora.backend.dto.response.AuthResponse;
import com.meteora.backend.exception.BusinessException;
import com.meteora.backend.model.Role;
import com.meteora.backend.model.User;
import com.meteora.backend.repository.UserRepository;
import com.meteora.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse registrar(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new BusinessException("Ja existe uma conta com este e-mail");
        }

        User usuario = User.builder()
                .nome(req.nome())
                .email(req.email())
                .senhaHash(passwordEncoder.encode(req.senha()))
                .role(Role.ROLE_CLIENTE)
                .build();

        usuario = userRepository.save(usuario);
        String token = jwtService.gerarToken(usuario);

        return new AuthResponse(token, "Bearer", usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole().name());
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.senha())
        );

        User usuario = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BusinessException("Usuario nao encontrado"));

        String token = jwtService.gerarToken(usuario);

        return new AuthResponse(token, "Bearer", usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole().name());
    }
}
