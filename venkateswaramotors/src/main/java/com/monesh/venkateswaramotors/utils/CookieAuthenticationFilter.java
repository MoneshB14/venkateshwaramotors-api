package com.monesh.venkateswaramotors.utils;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CookieAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Check if user is already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check for authentication cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("vm_auth_token".equals(cookie.getName()) &&
                        "vm_authenticated_user".equals(cookie.getValue())) {

                    // Get user email from another cookie or session
                    String userEmail = getUserEmailFromCookies(cookies);
                    if (userEmail != null) {
                        try {
                            UserDetails userDetails = this.userService.loadUserByUsername(userEmail);
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());
                            authToken.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        } catch (Exception e) {
                            // Log the error but continue with the filter chain
                            logger.warn("Error authenticating user from cookie: " + e.getMessage());
                        }
                    }
                    break;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getUserEmailFromCookies(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if ("vm_user_email".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}