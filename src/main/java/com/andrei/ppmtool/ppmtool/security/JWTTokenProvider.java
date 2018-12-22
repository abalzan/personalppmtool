package com.andrei.ppmtool.ppmtool.security;

import com.andrei.ppmtool.ppmtool.model.User;
import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class JWTTokenProvider {

    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Date now = new Date(System.currentTimeMillis());
        Date expireDate = new Date(now.getTime() + SecurityConstants.TOKEN_EXPIRATION_TIME);

        String userId = Long.toString(user.getId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", Long.toString(user.getId()));
        claims.put("username", user.getUsername());
        claims.put("fullname", user.getFullname());

        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(expireDate)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SecurityConstants.SECRET).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature " + e);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT Token " + e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token " + e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupoorted JWT Token " + e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claim string is empty " + e);
        }
        return false;
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(SecurityConstants.SECRET).parseClaimsJws(token).getBody();
        String id = (String) claims.get("id");

        return Long.parseLong(id);
    }
}
