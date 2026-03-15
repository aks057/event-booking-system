package com.cactro.eventbooking.controller.impl;

import com.cactro.eventbooking.controller.IAuthController;
import com.cactro.eventbooking.dto.request.LoginRequest;
import com.cactro.eventbooking.dto.request.RegisterRequest;
import com.cactro.eventbooking.dto.response.AuthResponse;
import com.cactro.eventbooking.dto.response.Response;
import com.cactro.eventbooking.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthControllerImpl implements IAuthController {

    private final IAuthService authService;

    @Override
    public ResponseEntity<Response<AuthResponse>> register(RegisterRequest request) {
        AuthResponse data = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("User registered successfully", data));
    }

    @Override
    public ResponseEntity<Response<AuthResponse>> login(LoginRequest request) {
        AuthResponse data = authService.login(request);
        return ResponseEntity.ok(Response.success("Login successful", data));
    }
}
