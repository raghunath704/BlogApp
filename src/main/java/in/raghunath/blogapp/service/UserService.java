package in.raghunath.blogapp.service;

import in.raghunath.blogapp.DTO.ApiResponse;
import in.raghunath.blogapp.DTO.SignupRequest;
import in.raghunath.blogapp.model.User;
import in.raghunath.blogapp.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageUploadService imageUploadService;

    public UserService(UserRepo userRepository, PasswordEncoder passwordEncoder, ImageUploadService imageUploadService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.imageUploadService = imageUploadService;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("User must be authenticated for this operation.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    // Retrieve all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Retrieve a user by username
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public void deleteUserById(String id) {
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException("User not found with id: "+ id);
        }
        User currentUser = getUserByUsername(getCurrentUsername());
        String username=getCurrentUsername();
        String oldPublicId = currentUser.getProfilePhotoPublicId();

        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            try {
                imageUploadService.delete(oldPublicId);
            } catch (IOException e) {
                logger.error("User '{}': Failed to delete old profile photo (public_id: {}) from Cloudinary. Proceeding with upload.", username, oldPublicId, e);

            } catch (Exception e) {
                logger.error("User '{}': Unexpected error deleting old profile photo (public_id: {})", username, oldPublicId, e);
            }
        } else {
            logger.info("User '{}': No existing profile photo found to delete.", username);
        }

        userRepository.deleteById(id);
    }




    public Map<String, String> updateProfilePhoto(MultipartFile file)
            throws UsernameNotFoundException, IOException, IllegalArgumentException {

        String username = getCurrentUsername();
        if (username == null) {
            logger.error("Username could not be retrieved from Authentication object for profile photo upload.");
            throw new UsernameNotFoundException("Please login to upload images");
        }
        User currentUser = getUserByUsername(getCurrentUsername());

        String oldPublicId = currentUser.getProfilePhotoPublicId();

        // 1. Delete old photo if it exists
        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            logger.info("User '{}': Found old profile photo (public_id: {}), attempting deletion.", username, oldPublicId);
            try {
                imageUploadService.delete(oldPublicId);
                logger.info("User '{}': Successfully deleted old profile photo from Cloudinary.", username);
                // Important: Clear the old IDs from the user object immediately
                currentUser.setProfilePhotoPublicId(null);
                currentUser.setProfilePhotoUrl(null);
                // Save this change *before* attempting the new upload for better state management
                userRepository.save(currentUser);
                logger.info("User '{}': Cleared old photo details from database.", username);
            } catch (IOException e) {
                // Log the error, but decide if we should proceed. Often, yes.
                // The old photo might be orphaned, but we want the new one uploaded.
                logger.error("User '{}': Failed to delete old profile photo (public_id: {}) from Cloudinary. Proceeding with upload.", username, oldPublicId, e);
                // Still clear the IDs in the user object in case the DB save above didn't happen or we skipped it
                currentUser.setProfilePhotoPublicId(null);
                currentUser.setProfilePhotoUrl(null);
                userRepository.save(currentUser); // Try saving the nullification again
            } catch (Exception e) {
                logger.error("User '{}': Unexpected error deleting old profile photo (public_id: {})", username, oldPublicId, e);
                // Depending on policy, might re-throw or just log.
            }
        } else {
            logger.info("User '{}': No existing profile photo found to delete.", username);
        }


        // 2. Upload new photo
        logger.info("User '{}': Uploading new profile photo.", username);
        Map uploadResult;
        try {
            uploadResult = imageUploadService.upload(file, "profile_photos"); // Throws IOException or IllegalArgumentException
        } catch (IOException | IllegalArgumentException e) {
            logger.error("User '{}': Failed to upload new profile photo.", username, e);
            // Re-throw the exception to be handled by the controller
            throw e;
        }


        // 3. Update user entity with new details
        String newImageUrl = (String) uploadResult.get("secure_url");
        String newPublicId = (String) uploadResult.get("public_id");

        if (newImageUrl == null || newPublicId == null) {
            // This shouldn't happen if Cloudinary upload succeeded, but good to check.
            logger.error("User '{}': Cloudinary upload succeeded but returned null URL/PublicID. Result: {}", username, uploadResult);
            // Attempt to delete the potentially orphaned uploaded image
            if (newPublicId != null) {
                try { imageUploadService.delete(newPublicId); } catch (IOException ioex) { logger.error("Failed cleanup.", ioex); }
            }
            throw new IOException("Image upload failed to return necessary details from Cloudinary.");
        }


        currentUser.setProfilePhotoUrl(newImageUrl);
        currentUser.setProfilePhotoPublicId(newPublicId);
        userRepository.save(currentUser);
        logger.info("User '{}': Successfully updated profile photo details in database. New URL: {}", username, newImageUrl);

        // 4. Return the results
        Map<String, String> result = new HashMap<>();
        result.put("url", newImageUrl);
        result.put("publicId", newPublicId);
        return result;
    }



    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

}
