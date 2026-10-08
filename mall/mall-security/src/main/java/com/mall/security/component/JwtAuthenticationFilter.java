package com.mall.security.component;

import com.mall.common.constant.CommonConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService adminUserDetailsService;
    private final UserDetailsService memberUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(CommonConstant.TOKEN_HEADER);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(CommonConstant.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(CommonConstant.TOKEN_PREFIX.length());
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestUri = request.getRequestURI();
        String type = requestUri.startsWith("/api/admin") ? "admin" : "member";

        if (!jwtTokenUtil.validateToken(token, type)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtTokenUtil.getUsernameFromToken(token);
        UserDetailsService userDetailsService = "admin".equals(type)
                ? adminUserDetailsService
                : memberUserDetailsService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String refreshedToken = jwtTokenUtil.refreshToken(token, type);
        if (refreshedToken != null) {
            response.setHeader(CommonConstant.TOKEN_HEADER, CommonConstant.TOKEN_PREFIX + refreshedToken);
        }

        filterChain.doFilter(request, response);
    }
}
