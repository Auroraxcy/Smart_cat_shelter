package com.doctor.filter;

import com.doctor.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);

        if (StringUtils.hasText(jwt)) {
            try {
                if (jwtTokenUtil.validateToken(jwt)) {
                    String username = jwtTokenUtil.getUsernameFromToken(jwt);
                    String role = jwtTokenUtil.getRoleFromToken(jwt);

                    // 构建 UserDetails
                    UserDetails userDetails = User.builder()
                            .username(username)
                            .password("") // 密码不需要
                            .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)))
                            .build();

                    // 设置认证信息到 SecurityContext
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, 
                                    null, 
                                    userDetails.getAuthorities()
                            );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (SignatureException e) {
                logger.error("Invalid JWT signature");
            } catch (MalformedJwtException e) {
                logger.error("Invalid JWT token");
            } catch (ExpiredJwtException e) {
                logger.error("Expired JWT token");
            } catch (UnsupportedJwtException e) {
                logger.error("Unsupported JWT token");
            } catch (IllegalArgumentException e) {
                logger.error("JWT claims string is empty");
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
