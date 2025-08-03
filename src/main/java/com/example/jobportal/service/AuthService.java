package com.example.jobportal.service;

import com.example.jobportal.dto.AuthResponse;
import com.example.jobportal.dto.LoginRequest;
import com.example.jobportal.dto.SignupRequest;
import com.example.jobportal.model.User;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        User user = (User) authentication.getPrincipal();
        
        return new AuthResponse(jwt, user);
    }
    
    public AuthResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }
        
        // Create new user's account
        User user = new User(signUpRequest.getFirstName(),
                           signUpRequest.getLastName(),
                           signUpRequest.getEmail(),
                           encoder.encode(signUpRequest.getPassword()),
                           signUpRequest.getRole());
        
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setBio(signUpRequest.getBio());
        
        userRepository.save(user);
        
        // Generate JWT token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signUpRequest.getEmail(), signUpRequest.getPassword()));
        
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        return new AuthResponse(jwt, user);
    }
    
    public User getCurrentUser() {
        System.out.println("=== GET CURRENT USER DEBUG ===");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication from SecurityContext: " + authentication);
        
        if (authentication == null) {
            System.out.println("ERROR: No authentication found in security context");
            throw new RuntimeException("No authentication found in security context");
        }
        
        System.out.println("Authentication name: " + authentication.getName());
        System.out.println("Authentication authorities: " + authentication.getAuthorities());
        System.out.println("Authentication is authenticated: " + authentication.isAuthenticated());
        
        Object principal = authentication.getPrincipal();
        System.out.println("Principal: " + principal);
        
        if (principal == null) {
            System.out.println("ERROR: No principal found in authentication");
            throw new RuntimeException("No principal found in authentication");
        }
        
        System.out.println("Principal class: " + principal.getClass().getName());
        
        if (!(principal instanceof User)) {
            System.out.println("ERROR: Principal is not a User instance, it's: " + principal.getClass().getName());
            throw new RuntimeException("Principal is not a User instance, it's: " + principal.getClass().getName());
        }
        
        User user = (User) principal;
        System.out.println("User ID: " + user.getId());
        System.out.println("User Email: " + user.getEmail());
        System.out.println("User Role: " + user.getRole());
        System.out.println("=== END GET CURRENT USER DEBUG ===");
        
        return user;
    }
}
