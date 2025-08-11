package com.tech.snapbid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tech.snapbid.config.JwtUtil;
import com.tech.snapbid.dto.ApiResponse;
import com.tech.snapbid.dto.LoginResponse;
import com.tech.snapbid.dto.LoginRequest;
import com.tech.snapbid.dto.UserRequestDto;
import com.tech.snapbid.exceptions.InvalidCredentialsException;
import com.tech.snapbid.exceptions.UserAlreadyExistsException;
import com.tech.snapbid.models.User;
import com.tech.snapbid.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public ApiResponse register(UserRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email " + request.getEmail() + " is already in use.");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username " + request.getUsername() + " is already in use.");
        }

        if (request.getRole() == null || request.getRole().name().equalsIgnoreCase("ADMIN")) {
            throw new IllegalArgumentException("Registration as ADMIN is not allowed");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setRole(request.getRole());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return new ApiResponse(true, "User registered successfully");
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            return new LoginResponse(token);
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Authentication failed: " + ex.getMessage());
        }
    }
}


