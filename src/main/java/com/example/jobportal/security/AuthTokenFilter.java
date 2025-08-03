package com.example.jobportal.security;

import com.example.jobportal.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        System.out.println("\n=== AUTH FILTER DEBUG ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Request Method: " + request.getMethod());
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("ContextPath: " + request.getContextPath());
        System.out.println("ServletPath: " + request.getServletPath());
        System.out.println("PathInfo: " + request.getPathInfo());
        
        // Print all headers for debugging
        System.out.println("--- Request Headers ---");
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        System.out.println("--- End Headers ---");
        
        try {
            String jwt = parseJwt(request);
            System.out.println("JWT Token: " + (jwt != null ? "Present" : "NULL"));
            
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                System.out.println("JWT Token is VALID");
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                System.out.println("Username from JWT: " + username);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("UserDetails loaded: " + userDetails.getUsername());
                System.out.println("UserDetails authorities: " + userDetails.getAuthorities());
                
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Authentication set in SecurityContext: " + authentication.getName());
                
                // Print authentication details after setting it
                System.out.println("Authentication in context after setting: " + 
                    (SecurityContextHolder.getContext().getAuthentication() != null ? 
                    SecurityContextHolder.getContext().getAuthentication().getName() : "NULL"));
            } else if (jwt != null) {
                System.out.println("JWT Token is INVALID");
            } else {
                System.out.println("No JWT Token found");
            }
        } catch (Exception e) {
            System.out.println("Error in authentication: " + e.getMessage());
            e.printStackTrace();
            logger.error("Cannot set user authentication: {}", e);
        }
        
        // Print current authentication before continuing filter chain
        System.out.println("Final authentication before proceeding: " + 
            (SecurityContextHolder.getContext().getAuthentication() != null ? 
            SecurityContextHolder.getContext().getAuthentication().getName() +
            " with authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            : "NULL"));
        
        System.out.println("=== END AUTH FILTER DEBUG ===\n");
        filterChain.doFilter(request, response);
    }
    
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        System.out.println("Authorization header: " + (headerAuth != null ? headerAuth.substring(0, Math.min(20, headerAuth.length())) + "..." : "null"));
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        
        return null;
    }
}
