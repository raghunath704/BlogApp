package in.raghunath.blogapp.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; // Import specific exceptions
import lombok.extern.slf4j.Slf4j; // Add logging
import org.springframework.beans.factory.annotation.Value;
// Remove UserDetails import, validation logic can live elsewhere or be simplified
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct; // For key initialization
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j // Add SLF4J logging
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret; // Keep this for Access Tokens

    @Value("${jwt.expiration}") // This is now specifically for ACCESS token expiration
    private long accessTokenExpirationMs;

    private Key signingKey; // Initialize once

    @PostConstruct // Initialize the key after dependency injection
    public void init() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secret);
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 JWT secret key: {}", e.getMessage());
            // Handle this critical error appropriately - maybe exit application?
            throw new RuntimeException("Invalid JWT secret key configuration.");
        }
    }

    // No change needed for getSigningKey() if init() is used
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
            return true; // If parsing throws ExpiredJwtException, it is expired
        } catch (Exception e) {
            // Handle other parsing errors if necessary, or let them propagate
            log.error("Could not determine token expiration", e);
            return true; // Treat unparsable tokens as expired/invalid
        }
    }

    // Validate Access Token (No change needed conceptually)
    public boolean validateAccessToken(String token, String username) {
        final String usernameFromToken = getUsernameFromToken(token);
        return (usernameFromToken.equals(username) && !isTokenExpired(token));
    }

    // Removed the UserDetails version as username comparison is simpler here
    // public boolean validateToken(String token, UserDetails user) { ... }
}