package com.cactro.eventbooking.service;

import com.cactro.eventbooking.dto.request.LoginRequest;
import com.cactro.eventbooking.dto.request.RegisterRequest;
import com.cactro.eventbooking.dto.response.AuthResponse;

public interface IAuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
