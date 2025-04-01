package in.raghunath.blogapp.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct; // For key initialization
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
//logging
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.accessToken.expiration}")
    private long accessTokenExpirationMs;

    private Key signingKey;

    @PostConstruct // Initialize the key after dependency injection
    public void init() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secret);
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 JWT secret key: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT secret key configuration.");
        }
    }

    private Key getSigningKey() {
        return this.signingKey;
    }


    // --- Access Token specific methods ---

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs)) // Use access token expiration
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Specify Algorithm explicitly
                .compact();
    }

    // --- General JWT parsing methods ---

    public String getUsernameFromToken(String token) {
        // Consider renaming to getUsernameFromAccessToken if only used for ATs
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            throw e; // Re-throw or handle as needed
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
            throw new IllegalArgumentException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            log.warn("JWT token is malformed: {}", e.getMessage());
            throw new IllegalArgumentException("Malformed JWT token", e);
        } catch (SignatureException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT signature", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty or invalid: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch(ExpiredJwtException e) {
            // If parsing throws ExpiredJwtException, it is expired
            return true;
        } catch (Exception e) {
            log.error("Could not determine token expiration", e);
            // Treat unparsable tokens as expired/invalid
            return true;
        }
    }

    // Validate Access Token
    public boolean validateAccessToken(String token, String username) {
        final String usernameFromToken = getUsernameFromToken(token);
        return (usernameFromToken.equals(username) && !isTokenExpired(token));
    }

}