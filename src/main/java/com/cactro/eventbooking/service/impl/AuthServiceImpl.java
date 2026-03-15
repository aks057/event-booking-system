package com.cactro.eventbooking.service.impl;

import com.cactro.eventbooking.config.JwtUtil;
import com.cactro.eventbooking.constant.ErrorCode;
import com.cactro.eventbooking.dto.request.LoginRequest;
import com.cactro.eventbooking.dto.request.RegisterRequest;
import com.cactro.eventbooking.dto.response.AuthResponse;
import com.cactro.eventbooking.entity.Role;
import com.cactro.eventbooking.entity.User;
import com.cactro.eventbooking.exception.BadRequestException;
import com.cactro.eventbooking.repository.UserRepository;
import com.cactro.eventbooking.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse register(RegisterRequest request) {
        log.info("[register] Request to register user, email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .build();

        user = userRepository.save(user);
        log.info("[register] User registered successfully, id: {}", user.getId());

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("[login] Request to login, email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        log.info("[login] User logged in successfully, id: {}", user.getId());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
