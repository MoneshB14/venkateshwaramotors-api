package com.monesh.venkateswaramotors.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ConditionalJwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // Skip JWT authentication for user management endpoints
        if (requestURI.startsWith("/service-center/user-management/")) {
            // Skip JWT filter entirely and continue with the filter chain
            filterChain.doFilter(request, response);
            return;
        }

        // Apply JWT authentication for other endpoints
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // This filter should always run, but conditionally delegate to JWT filter
        return false;
    }
}