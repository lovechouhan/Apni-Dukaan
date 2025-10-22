package com.eCommerce.Ecommerce.Config;

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

        // using jwt authentication with spring security
        @Autowired
        private JwtAuthenticationEntryPoint point;
        @Autowired
        private JwtAuthenticationFilter filter;

        // Register JwtAuthenticationFilter as a bean

        // default spring security
        @Autowired
        private SecurityCustomUserDetailsService customUserDetailsService;

        @Autowired
        private OAuthenticationSuccessHandler handler;

        @Autowired
        private AuthFailureHandler authFailureHandler;

        // Configuration of Authentication Provider for spring security
        // DB se user ko extract krna hain
        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
                // user detail service ka object paas karna hai
                provider.setUserDetailsService(customUserDetailsService);
                // password encoder ka object set karna hai
                provider.setPasswordEncoder(passwordEncoder());
                return provider;
        }

        // configure your security filter chain here
        // user konse pages and url configure kr payega

       

        @Bean
        public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
                org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
                configuration.addAllowedOrigin("http://localhost:3000"); // Frontend URL
                configuration.addAllowedOrigin("http://localhost:8080"); // Backend URL
                configuration.addAllowedMethod("GET");
                configuration.addAllowedMethod("POST");
                configuration.addAllowedMethod("PUT");
                configuration.addAllowedMethod("DELETE");
                configuration.addAllowedMethod("OPTIONS");
                configuration.addAllowedHeader("Authorization");
                configuration.addAllowedHeader("Content-Type");
                configuration.addAllowedHeader("Accept");
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L); // 1 hour

                org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        @Order(2)
        public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/login", "/register", "/verify-otp", "/sellers/sellerRegister","/sellers/verify-seller-otp",
                                                "/sellers/register","/sellers/verify-otp", "/otp", "/oauth2/**", "/css/**",
                                                "/js/**", "/images/**", "/products/**").permitAll()
                                                .requestMatchers("/user/**").authenticated()  
                                                  .requestMatchers("/user/cart/**").authenticated()  // 👈 cart pages
                                                  
                                                
                                               // .requestMatchers("/sellers/**").hasRole("SELLER")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/authenticate")
                                                .defaultSuccessUrl("/user/main", true)
                                                .failureHandler(authFailureHandler)
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true"))
                                .oauth2Login(oauth -> oauth
                                                .loginPage("/login")
                                                .successHandler(handler))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // 👈 this is the key

                                .csrf(AbstractHttpConfigurer::disable);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(); // NoOpPasswordEncoder is deprecated, use with caution
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
                return builder.getAuthenticationManager();
        }

}
