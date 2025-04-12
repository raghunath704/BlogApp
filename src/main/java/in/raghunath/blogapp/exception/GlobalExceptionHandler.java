package in.raghunath.blogapp.exception; // Or a suitable package

import in.raghunath.blogapp.DTO.ApiResponse;
import in.raghunath.blogapp.service.BlogService.ResourceNotFoundException; // Make sure this is accessible
import in.raghunath.blogapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice // Intercepts exceptions across controllers
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(false, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserService.UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(UserService.UserNotFoundException ex, WebRequest request) {
        log.warn("User not found: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(false, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(false, "You are not authorized to perform this action.");
        // Or you can use ex.getMessage() if it's suitable for the user
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // Handle other specific exceptions as needed (e.g., ValidationException)

    @ExceptionHandler(Exception.class) // Generic fallback handler
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("An unexpected error occurred: ", ex); // Log the full stack trace
        ApiResponse response = new ApiResponse(false, "An internal server error occurred. Please try again later.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}