package com.datmai.moviereservation.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.datmai.moviereservation.common.format.RestResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper; // To convert data into Object data type
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        this.delegate.commence(request, response, authException);
        response.setContentType("application/json;charset=UTF-8");

        RestResponse<Object> res = new RestResponse<>();
        res.setStatus(HttpStatus.UNAUTHORIZED.value());

        String errorMessage = Optional.ofNullable(authException.getCause()) // Check if client send no Token to access
                                                                            // any APIs in the server
                .map(Throwable::getMessage)
                .orElse(authException.getMessage());

        res.setError(errorMessage);
        res.setMessage("Invalid token (expired, incorrect format, or no JWT header)...");
        mapper.writeValue(response.getWriter(), res);
    }
}
