package com.cactro.eventbooking.controller;

import com.cactro.eventbooking.dto.request.LoginRequest;
import com.cactro.eventbooking.dto.request.RegisterRequest;
import com.cactro.eventbooking.dto.response.AuthResponse;
import com.cactro.eventbooking.dto.response.Response;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public interface IAuthController {

    @PostMapping("/register")
    ResponseEntity<Response<AuthResponse>> register(@Valid @RequestBody RegisterRequest request);

    @PostMapping("/login")
    ResponseEntity<Response<AuthResponse>> login(@Valid @RequestBody LoginRequest request);
}
