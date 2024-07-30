package com.bgspro.componentGenerator.config;

import com.bgspro.componentGenerator.config.jwt.TokenProvider;
import com.bgspro.componentGenerator.error.ErrorMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String token = getAccessToken(authorizationHeader);

        if (token != null) {
            try {
                if (tokenProvider.validToken(token)) {
                    Authentication authentication = tokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException e) {
                handleBusinessException(response, ErrorMessages.TOKEN_EXPIRED, HttpServletResponse.SC_UNAUTHORIZED);
            } catch (UnsupportedJwtException e) {
                handleBusinessException(response, ErrorMessages.UNSUPPORTED_TOKEN, HttpServletResponse.SC_UNAUTHORIZED);
            } catch (MalformedJwtException e) {
                handleBusinessException(response, ErrorMessages.MALFORMED_TOKEN, HttpServletResponse.SC_UNAUTHORIZED);
            } catch (SignatureException e) {
                handleBusinessException(response, ErrorMessages.INVALID_TOKEN_SIGNATURE, HttpServletResponse.SC_UNAUTHORIZED);
            } catch (IllegalArgumentException e) {
                handleBusinessException(response, ErrorMessages.TOKEN_NULL_OR_EMPTY, HttpServletResponse.SC_UNAUTHORIZED);
            } catch (Exception e) {
                handleBusinessException(response, ErrorMessages.INTERNAL_SERVER_ERROR, HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private static void handleBusinessException(HttpServletResponse response, String message, int errorCode) throws IOException {
        response.setStatus(errorCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errors = new HashMap<>();
        errors.put("errorCode", errorCode);
        errors.put("message", message);
        String json = new ObjectMapper().writeValueAsString(errors);
        response.getWriter().write(json);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}