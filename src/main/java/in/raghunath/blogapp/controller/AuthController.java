package in.raghunath.blogapp.controller;


import in.raghunath.blogapp.DTO.LoginRequest;
import in.raghunath.blogapp.DTO.LoginResponse;
import in.raghunath.blogapp.DTO.SignupRequest;
import in.raghunath.blogapp.service.UserService;
import in.raghunath.blogapp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        boolean isRegistered = userService.registerUser(signupRequest);
        if (isRegistered) {
            return ResponseEntity.ok("User registered successfully!");
        } else {
            return ResponseEntity.badRequest().body("Username already exists!");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Extract username from Authentication
            String username = authentication.getName();

            // Generate JWT token
            String token = jwtUtil.generateToken(username);

            return ResponseEntity.ok(new LoginResponse("Login successful", token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new LoginResponse("Invalid username or password", null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully!");
    }
}
