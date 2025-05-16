package com.learning.selfLearningPOC.filter;

import com.learning.selfLearningPOC.util.ProjectConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JWTValidationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(ProjectConstants.JWT_HEADER);
        if(null != jwt) {
            try {
                SecretKey secretKey = Keys.hmacShaKeyFor(ProjectConstants.JWT_SECURITY_SECRET.getBytes(StandardCharsets.UTF_8));
                if(null != secretKey) {
                    Claims claims = Jwts.parser().verifyWith(secretKey)
                            .build().parseSignedClaims(jwt).getPayload();
                    String username = String.valueOf(claims.get("username"));
                    String authorities = String.valueOf(claims.get("authority"));
                    log.info("User: " + username + ", Authority: " + authorities);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
                            AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch(Exception ex) {
                throw new BadCredentialsException("Invalid Token");
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/apiLogin");
    }
}
