package in.raghunath.blogapp.DTO;

import jakarta.validation.constraints.Email; // Import Jakarta validation
import jakarta.validation.constraints.NotBlank; // Import Jakarta validation
import jakarta.validation.constraints.Size;   // Import Jakarta validation
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignupRequest {

    @NotBlank(message = "Username cannot be empty or contain only whitespace") // Use NotBlank for Strings
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    private String email;
}