package com.example.revisemate.Security;

import com.example.revisemate.Util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs exactly once per request (Spring Security‑aware) and:
 *   • Skips public auth routes and CORS pre‑flight OPTIONS requests
 *   • Validates “Authorization: Bearer …” tokens
 *   • Injects userId into the request for downstream controllers
 */
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

        /* ──────────────────────────────────────────────
         *  1. Allow CORS pre‑flight and public routes
         * ────────────────────────────────────────────── */
        String path   = request.getRequestURI();
        String method = request.getMethod();

        // CORS pre‑flight (OPTIONS) = always OK
        if ("OPTIONS".equalsIgnoreCase(method)) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }

        // Public auth endpoints = skip JWT check
        if (path.startsWith("/api/auth")) {
            chain.doFilter(request, response);
            return;
        }

        /* ──────────────────────────────────────────────
         *  2. Extract and validate Bearer token
         * ────────────────────────────────────────────── */
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Long userId = jwtUtil.validateTokenAndGetUserId(token); // throws if invalid/expired

                // Make userId available to controllers
                request.setAttribute("userId", userId);

                // If you need Spring Security context (roles, etc.) set it here
                // (omitted for brevity since your app doesn't use roles yet)

                chain.doFilter(request, response);
                return;

            } catch (Exception ex) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired token");
                return;
            }
        }

        // No token → 401
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token required");
    }
}
