package in.raghunath.blogapp.service;

import in.raghunath.blogapp.exception.TokenRefreshException; // Create this custom exception
import in.raghunath.blogapp.model.RefreshToken;
import in.raghunath.blogapp.repo.RefreshTokenRepository;
import in.raghunath.blogapp.repo.UserRepo; // Optional: If needed to verify user exists
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${app.jwt.refreshToken.expiration}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepo userRepo; // To ensure user exists

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(String username) {
        // Ensure the user actually exists before creating a token for them
        userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found for refresh token creation - " + username));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString()); // Generate a secure random opaque token

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token); // Clean up expired tokens
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    // Core method for logout: Delete the token from the database
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    // Optional: Invalidate all tokens for a user
    public void deleteByUsername(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }
}