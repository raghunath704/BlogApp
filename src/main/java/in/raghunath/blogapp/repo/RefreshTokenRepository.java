package in.raghunath.blogapp.repo;

import in.raghunath.blogapp.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    // Method to delete a token by its value (used for logout)
    void deleteByToken(String token);

    // Optional: If you want to invalidate all tokens for a user (e.g., password change)
    void deleteByUsername(String username);
}