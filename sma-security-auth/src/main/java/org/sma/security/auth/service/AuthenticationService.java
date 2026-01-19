package org.sma.security.auth.service;

import org.sma.security.auth.dto.JwtTokenDto;
import org.sma.security.auth.dto.LoginRequestDto;
import org.sma.security.auth.model.User;

/**
 * Interface for authentication service
 */
public interface AuthenticationService {

    /**
     * Authenticate user and return JWT token
     */
    JwtTokenDto authenticate(LoginRequestDto loginRequest);

    /**
     * Validate JWT token
     */
    boolean validateToken(String token);

    /**
     * Refresh JWT token
     */
    JwtTokenDto refreshToken(String refreshToken);

    /**
     * Get user from token
     */
    User getUserFromToken(String token);

    /**
     * Register new user
     */
    User registerUser(User user);
}
