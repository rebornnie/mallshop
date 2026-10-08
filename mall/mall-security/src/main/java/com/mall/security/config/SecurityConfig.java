package com.mall.security.config;

import com.mall.security.component.JwtAuthenticationFilter;
import com.mall.security.component.JwtTokenUtil;
import com.mall.security.component.RestAccessDeniedHandler;
import com.mall.security.component.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService adminUserDetailsService;
    private final UserDetailsService memberUserDetailsService;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public SecurityConfig(JwtTokenUtil jwtTokenUtil,
                          @Qualifier("adminUserDetailsService") UserDetailsService adminUserDetailsService,
                          @Qualifier("memberUserDetailsService") UserDetailsService memberUserDetailsService,
                          RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                          RestAccessDeniedHandler restAccessDeniedHandler) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.adminUserDetailsService = adminUserDetailsService;
        this.memberUserDetailsService = memberUserDetailsService;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/admin/login",
                                "/api/ums/login",
                                "/api/ums/register",
                                "/api/oms/pay/callback"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/doc.html",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/pms/product/list",
                                "/api/pms/product/**",
                                "/api/pms/category/list",
                                "/api/pms/brand/list",
                                "/api/pms/search",
                                "/api/oms/coupon/available",
                                "/api/pms/comment/list",
                                "/api/pms/comment/statistics"
                        ).permitAll()
                        .requestMatchers("/api/admin/**").authenticated()
                        .requestMatchers("/api/ums/**").authenticated()
                        .requestMatchers("/api/pms/**").authenticated()
                        .requestMatchers("/api/oms/**").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .cors(cors -> {})
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenUtil, adminUserDetailsService, memberUserDetailsService);
    }
}
