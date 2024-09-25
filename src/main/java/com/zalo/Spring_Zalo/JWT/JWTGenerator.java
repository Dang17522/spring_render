package com.zalo.Spring_Zalo.JWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zalo.Spring_Zalo.Entities.User;
import com.zalo.Spring_Zalo.Repo.UserRepo;

import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JWTGenerator {

    private static final Logger log = LoggerFactory.getLogger(JWTGenerator.class);
    

    // tạo chuỗi JWT
    /**
     *
     * @param authentication
     * @return
     */
    public String generateAccessToken(UserDetails  authentication , User user) {
       // String username = authentication.getName();
        Date currentDate = new Date(); // set thời điểm tạo token
        Date expireDate = new Date(currentDate.getTime() + SecurityContaints.JWT_ACCESS_TOKEN_EXPIRATON);// set thời
                                                                                                         // điểm hết hạn
                                                                                                         // token
        String accesstoken = Jwts.builder() // sử dụng jwts để xây dựng chuỗi
                .setSubject(user.getRole().toString())
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .claim("userName", user.getUsername())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("accountStatus", user.isIs_active())
                .claim("roleId", user.getRole().getId())
                .signWith(SignatureAlgorithm.HS512, SecurityContaints.JWT_SECRET)
                .compact();

        // Log khi AccessToken được tạo mới
        log.info("AccessToken generated for user: {}", user.toString());
        return accesstoken;
    }

    /**
     *
     * @param authentication
     * @return
     */
    public String generateRefreshToken(UserDetails  authentication,User user) {
        // String username = authentication.getName();

        // Tạo chuỗi RefreshToken với thời gian hết hạn lâu hơn AccessToken
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityContaints.JWT_REFRESH_TOKEN_EXPIRATION);

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(user.getRole().getRoleName()))
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .claim("userDetails", user)
                .signWith(SignatureAlgorithm.HS512, SecurityContaints.JWT_SECRET)
                .compact();

        return refreshToken;
    }

    /**
     *
     * @param token
     * @return
     */
    public String getUsernameFromJWT(String token) {
        // Giải mã JWT và lấy thông tin chủ thể
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityContaints.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        // Trả về tên người dùng từ thông tin chủ thể
        return claims.getSubject();
    }

    /**
     *
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            // Giải mã và kiểm tra chữ ký của JWT
            Jwts.parser()
                    .setSigningKey(SecurityContaints.JWT_SECRET)
                    .parseClaimsJws(token);
            // Nếu không có lỗi, trả về true để chỉ định rằng JWT là hợp lệ
            // Log khi token được xác thực thành công
            log.info("JWT successfully validated");
            return true;
        } catch (Exception e) {
            // Log khi có lỗi xác thực
            log.info("JWT validation failed: {}", e.getMessage());
            // Nếu có lỗi, ném ngoại lệ AuthenticationCredentialsNotFoundException
            // với thông báo "JWT was incorrect"
            throw new AuthenticationCredentialsNotFoundException("JWT was incorrect");
        }

    }

    /**
     *
     * @param token
     * @return
     */
    public Date getExpirationDateFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityContaints.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }
}
