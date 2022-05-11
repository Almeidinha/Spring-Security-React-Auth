package com.almeidinha.app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

@Component
public class JWTTokenHelper {

    @Value("${jwt.auth.app}")
    private String appName;

    @Value("${jwt.auth.secret_key}")
    private String secretKey;

    @Value("${jwt.auth.expires_in}")
    private int expiresIn;

    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;


    private Claims getAllClaimsFromToken(String token) {
        Claims claims;

        try {
            claims = Jwts.parser()
                    .setSigningKey(this.secretKey)
                    .parseClaimsJws(token)
                    .getBody();

        } catch (Exception exception) {
            claims = null;
        }
        return claims;
    }

    public String getUserNameFromTokens(String token) {
        String userName;

        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            userName = claims.getSubject();
        } catch (Exception exception) {
            userName = null;
        }
        return userName;
    }

    public String generateToken(String userName) throws InvalidKeySpecException, NoSuchAlgorithmException {

        return Jwts.builder()
                .setIssuer(this.appName)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(this.generateExpirationDate())
                .signWith(this.SIGNATURE_ALGORITHM, this.secretKey)
                .compact();
    }

    private Date generateExpirationDate() {
        return new Date(new Date().getTime() + this.expiresIn * 1000L);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String userName = getUserNameFromTokens(token);

        return (
                userName != null
                        && userName.equals(userDetails.getUsername())
                        && !isTokenExpired(token)
        );
    }

    public Boolean isTokenExpired(String token) {
        Date expiredDate = getExpirationDate(token);
        return expiredDate.before(new Date());
    }

    private Date getExpirationDate(String token) {
        Date expireDate;

        try {
            final  Claims claims = this.getAllClaimsFromToken(token);
            expireDate = claims.getExpiration();
        } catch (Exception exception) {
            expireDate = null;
        }

        return expireDate;
    }

    public Date getIssueDateFromToken(String token) {
        Date issueDate;

        try {
            final  Claims claims = this.getAllClaimsFromToken(token);
            issueDate = claims.getIssuedAt();
        } catch (Exception exception) {
            issueDate = null;
        }

        return issueDate;
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return  null;
    }

    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return  request.getHeader("Authorization");
    }

}
