package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.Services.AuthHelpersServices;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthHelpersServices authHelperService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {


        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userCode = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);     // strip "Bearer " prefix
            try {
                userCode = authHelperService.extractSubject(token);
                log.debug("[JWT FILTER] Extracted userCode={} from token", userCode);
            } catch (JwtException | IllegalArgumentException ex) {
                log.warn("[JWT FILTER] Invalid JWT token: {}", ex.getMessage());
            }
        }

        if (userCode != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (!authHelperService.isTokenExpired(token)) {
                    Claims claims = authHelperService.extractClaims(token);
                    String role = claims.get("role", String.class);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userCode,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("[JWT FILTER] Authentication set for userCode={}, role={}", userCode, role);
                } else {
                    log.warn("[JWT FILTER] Token expired for userCode={}", userCode);
                }
            } catch (JwtException ex) {
                log.warn("[JWT FILTER] Token validation failed: {}", ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}