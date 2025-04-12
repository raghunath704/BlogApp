package in.raghunath.blogapp.service;

import in.raghunath.blogapp.model.User; // Import your User model
import in.raghunath.blogapp.repo.UserRepo; // Import your User repository
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("userSecurityService") // Use a distinct name, e.g., "userSecurityService"
public class UserSecurityService {

    private static final Logger log = LoggerFactory.getLogger(UserSecurityService.class);
    private final UserRepo userRepository; // Use UserRepo
    // Use constructor injection
    public UserSecurityService(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Checks if the authenticated user is the same as the user being accessed.
     * @param userId The ID of the user being accessed.
     * @param username The username of the currently authenticated user.
     * @return true if the authenticated user is the same, false otherwise.
     * @throws RuntimeException if the user doesn't exist.  (Assuming you have this.  If not, create it or use a general exception)
     */
    public boolean isSelf(String userId, String username) {
        log.debug("Checking if user {} is accessing user {}", username, userId);

        if (username == null || userId == null) {
            log.warn("Username or UserId is null during self check.");
            return false; // Or throw an IllegalArgumentException if you prefer.
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {} for self check", userId);
                    return new RuntimeException("User not found with id: " + userId + " for self check"); //Use UserService Exception
                });

        String accessedUsername = user.getUsername(); // Get username from the found user.
        log.debug("Username from DB: {}", accessedUsername);
        boolean isSelf = accessedUsername != null && accessedUsername.equalsIgnoreCase(username);
        log.debug("Self check result for userId {}: {}", userId, isSelf);
        return isSelf;
    }
}
