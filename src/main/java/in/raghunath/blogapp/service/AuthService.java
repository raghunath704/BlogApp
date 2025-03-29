package in.raghunath.blogapp.service;

import in.raghunath.blogapp.DTO.AuthResponse;
import in.raghunath.blogapp.DTO.LoginRequest;
import in.raghunath.blogapp.DTO.AuthResponse;
import in.raghunath.blogapp.DTO.SignupRequest;
import in.raghunath.blogapp.model.User;
import in.raghunath.blogapp.repo.UserRepo;
import in.raghunath.blogapp.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    // Register new users
    public AuthResponse registerUser(SignupRequest signupRequest) {
        if (userRepo.findByUsername(signupRequest.getUsername()).isPresent()) {
            return new AuthResponse("User Already exists", null); // Username already exists
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword())); // Encrypt password
        user.setEmail(signupRequest.getEmail());
        userRepo.save(user);

        String token = jwtUtil.generateToken(user.getUsername());

        return new AuthResponse("UserSuccessfully Registered", token);
    }

    public AuthResponse loginUser(LoginRequest loginRequest) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        User user = userRepo.findByUsername(loginRequest.getUsername()).orElseThrow();
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse("Login successful", token);
    }
}
