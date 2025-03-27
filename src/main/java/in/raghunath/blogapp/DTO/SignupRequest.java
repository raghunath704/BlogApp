package in.raghunath.blogapp.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignupRequest {
    private String username;
    private String password;
    private String email;


}
