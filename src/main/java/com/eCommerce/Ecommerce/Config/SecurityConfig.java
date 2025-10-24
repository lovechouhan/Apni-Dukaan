package com.eCommerce.Ecommerce.Config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.eCommerce.Ecommerce.JWTSecurity.JwtAuthenticationEntryPoint;
import com.eCommerce.Ecommerce.JWTSecurity.JwtAuthenticationFilter;
import com.eCommerce.Ecommerce.Services.SecurityCustomUserDetailsService;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint point;

    @Autowired
    private JwtAuthenticationFilter filter;

    @Autowired
    private SecurityCustomUserDetailsService customUserDetailsService;

    @Autowired
    private OAuthenticationSuccessHandler handler;

    @Autowired
    private AuthFailureHandler authFailureHandler;

    // ✅ Authentication Provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ✅ CORS Configuration (Improved)
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList(
                "https://apni-dukaan-production.up.railway.app",
                "http://localhost:8080",
                "http://localhost:3000"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hour

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // ✅ Security Filter Chain
    @Bean
    @Order(2)
    public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 👈 Enable CORS globally
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/verify-otp",
                        "/sellers/sellerRegister", "/sellers/verify-seller-otp",
                        "/sellers/register", "/sellers/verify-otp",
                        "/otp", "/oauth2/**", "/css/**", "/js/**",
                        "/images/**", "/products/**")
                .permitAll()
                .requestMatchers("/user/**", "/user/cart/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/authenticate")
                .defaultSuccessUrl("/user/main", true)
                .failureHandler(authFailureHandler)
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
            )
            .oauth2Login(oauth -> oauth
                .loginPage("/login")
                .successHandler(handler)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }
}
