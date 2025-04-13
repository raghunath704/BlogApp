package in.raghunath.blogapp.controller;

import in.raghunath.blogapp.DTO.ApiResponse;
import in.raghunath.blogapp.DTO.SignupRequest;
import in.raghunath.blogapp.model.User;
import in.raghunath.blogapp.service.AuthService;
import in.raghunath.blogapp.service.ImageUploadService;
import in.raghunath.blogapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private final  UserService userService;
    private final AuthService authService;
    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    // Create a new user (sign up)
    @PostMapping
    public ResponseEntity<Boolean> createUser(@RequestBody SignupRequest signupRequest) {
        authService.registerUser(signupRequest);
        return ResponseEntity.ok(true);
    }

    // Get all users

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get user by username
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }

    // Delete user by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurityService.isSelf(#id, authentication.principal.username)")
    public ResponseEntity<Void> deleteUserById(@PathVariable String id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content
    }

    @PostMapping("/me/profile-photo")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurityService.isSelf(#id, authentication.principal.username)")
    public ResponseEntity<?> uploadProfilePhoto(
            @RequestParam("file") MultipartFile file) {

        try {
            // Delegate the entire operation to the UserService
            Map<String, String> result = userService.updateProfilePhoto( file);
            // Return success response using the map returned by the service
            return ResponseEntity.ok(Map.of(
                    "message", "Profile photo uploaded successfully",
                    "url", result.get("url"), // Get URL from service result
                    "publicId", result.get("publicId") // Get publicId from service result
            ));

        } catch (UsernameNotFoundException e) {
            // Use ResponseStatusException for standard error handling or your ApiResponse
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            // OR: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Handle invalid file type/size errors from ImageUploadService/UserService
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        } catch (IOException e) {
            // Handle Cloudinary communication errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Error communicating with image storage service."));
        } catch (Exception e) {
            // Catch any other unexpected exceptions from the service layer
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "An unexpected server error occurred."));
        }
    }
}

