package in.raghunath.blogapp.service;

import in.raghunath.blogapp.DTO.AuthResponse;
import in.raghunath.blogapp.DTO.LoginRequest;
import in.raghunath.blogapp.DTO.SignupRequest;
import in.raghunath.blogapp.model.RefreshToken; // Import RefreshToken
import in.raghunath.blogapp.model.User;
import in.raghunath.blogapp.repo.UserRepo;
import in.raghunath.blogapp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value; // Import Value
import org.springframework.http.ResponseCookie; // Import ResponseCookie
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.security.core.context.SecurityContextHolder; // Import SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Optional: for signup atomicity

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService; // Inject RefreshTokenService

    @Value("${app.jwt.refresh-cookie-name}") // Inject cookie name
    private String refreshTokenCookieName;

    @Value("${app.jwt.refresh-token-duration-ms}") // Inject RT duration for cookie maxAge
    private Long refreshTokenDurationMs;

    // Update constructor
    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService; // Assign injected service
    }

    @Transactional // Make signup transactional (optional but good practice)
    public AuthResponse registerUser(SignupRequest signupRequest) {
        if (userRepo.findByUsername(signupRequest.getUsername()).isPresent()) {
            // Consider throwing a specific exception like UserAlreadyExistsException
            throw new RuntimeException("Username already exists: " + signupRequest.getUsername());
        }
        if (userRepo.findByEmail(signupRequest.getEmail()).isPresent()) {
            // Consider throwing a specific exception like EmailAlreadyExistsException
            throw new RuntimeException("Email already exists: " + signupRequest.getEmail());
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        userRepo.save(user);

        // Optionally: Generate tokens immediately upon signup? Or require login?
        // For now, let's return success and require login.
        // String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        // RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());
        // ResponseCookie cookie = createRefreshTokenCookie(refreshToken.getToken());

        // Return just the message, user needs to login separately
        return new AuthResponse("User registered successfully. Please login.", null);
    }

    // This method now returns LoginResult containing AT and Cookie for RT
    public LoginResult loginUser(LoginRequest loginRequest) {
        // Authenticate user (this also validates credentials)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Set authentication in context (good practice, though less critical in pure stateless)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get username from authenticated principal
        String username = authentication.getName();

        // Generate Access Token (short-lived)
        String accessToken = jwtUtil.generateAccessToken(username);

        // Create and persist Refresh Token (long-lived, stored in DB)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);

        // Create the HttpOnly cookie for the Refresh Token
        ResponseCookie refreshTokenCookie = ResponseCookie.from(refreshTokenCookieName, refreshToken.getToken())
                .httpOnly(true)
                .secure(true) // IMPORTANT: Set to true in production (requires HTTPS)
                .path("/auth") // IMPORTANT: Scope cookie path (e.g., /auth/refresh, /auth/logout)
                .maxAge(refreshTokenDurationMs / 1000) // maxAge is in seconds
                .sameSite("Strict") // Or "Lax". Strict is generally better for auth tokens.
                .build();

        // Return both the Access Token (for client JS) and the Cookie (for browser)
        return new LoginResult(new AuthResponse("Login successful", accessToken), refreshTokenCookie);
    }

    // New method specifically for handling logout logic
    public void logoutUser(String refreshTokenValue) {
        if (refreshTokenValue != null) {
            refreshTokenService.deleteByToken(refreshTokenValue);
            // Optionally: Blacklist the current Access Token if needed (advanced)
        }
        // Clear context (though less impactful in stateless, doesn't hurt)
        SecurityContextHolder.clearContext();
    }


    // Helper record to return both AuthResponse and Cookie from loginUser
    public record LoginResult(AuthResponse authResponse, ResponseCookie cookie) {}
}