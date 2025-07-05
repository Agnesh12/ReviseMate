package com.example.revisemate.Security;

import com.example.revisemate.Util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Allow CORS pre-flight and public auth routes
        if ("OPTIONS".equalsIgnoreCase(method)) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/api/auth")) {
            chain.doFilter(request, response);
            return;
        }

        // Validate Bearer token
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Long userId = jwtUtil.validateTokenAndGetUserId(token); // throws if invalid/expired

                // Make userId available in request
                request.setAttribute("userId", userId);

                // ✅ Set Spring Security context (authentication)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,           // principal (can be a full user object later)
                                null,             // no credentials
                                Collections.emptyList() // no roles for now
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                chain.doFilter(request, response);
                return;

            } catch (Exception ex) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired token");
                return;
            }
        }

        // No token = 401 Unauthorized
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token required");
    }
}
