package com.eCommerce.Ecommerce.JWTSecurity;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

 import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;




@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json"); // 👈 JSON bol do
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String json = String.format(
            "{\"error\": \"Unauthorized\", \"message\": \"%s\"}",
            authException.getMessage()
        );

        response.getWriter().write(json);
    }
}
