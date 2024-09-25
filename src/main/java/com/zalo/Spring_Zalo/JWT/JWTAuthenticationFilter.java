package com.zalo.Spring_Zalo.JWT; 

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zalo.Spring_Zalo.Entities.EnumManager;
import com.zalo.Spring_Zalo.Entities.Roles;
import com.zalo.Spring_Zalo.Repo.RolesRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.zalo.Spring_Zalo.Entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTGenerator tokenGenerator;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private RolesRepo rolesRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = getJWTFromRequest(request);

        if (StringUtils.hasText(token) && tokenGenerator.validateToken(token)) {
            User user = getUserFromJWT(token); 
            // String username = tokenGenerator.getUsernameFromJWT(token);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());

            if (tokenExpired(token)) {
                // Generate new tokens only if the existing one is expired
                String newAccessToken = tokenGenerator.generateAccessToken(userDetails,user);
                String newRefreshToken = tokenGenerator.generateRefreshToken( userDetails,user);

                // Update Authentication with new tokens
                Authentication updatedAuthentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                ((AbstractAuthenticationToken) updatedAuthentication).setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(updatedAuthentication);

                // Send new tokens in the response header
                response.addHeader("Authorization", "Bearer " + newAccessToken);
                response.addHeader("Refresh-Token", newRefreshToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    public User getUserFromJWT(String token) {
    Claims claims = Jwts.parser()
            .setSigningKey(SecurityContaints.JWT_SECRET)
            .parseClaimsJws(token)
            .getBody();

    // Lấy thông tin từ claim "userDetails"
        Roles role = rolesRepo.findById(claims.get("roleId", Integer.class)).get();
       User user = new User();
       user.setId(claims.get("userId", Integer.class));
       user.setUsername(claims.get("userName", String.class));
       user.setEmail(claims.get("email", String.class));
       user.setIs_active(claims.get("accountStatus", Boolean.class));
       user.setRole(role);;
    return user;
}


    private static final long EXPIRATION_THRESHOLD = 1000; // 1 giây

    private boolean tokenExpired(String token) {
        Date expirationDate = tokenGenerator.getExpirationDateFromJWT(token);
        return expirationDate != null && expirationDate.getTime() - System.currentTimeMillis() < EXPIRATION_THRESHOLD;
    }

    private String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
