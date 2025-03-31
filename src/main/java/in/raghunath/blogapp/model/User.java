package in.raghunath.blogapp.model;
import com.mongodb.lang.NonNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users") // Use the collection name as "users"
public class User {
    @Id
    private String id;


    @Indexed(unique = true) // Enforce unique username
    @NonNull
    private String username;

    @Indexed(unique = true)
    @NonNull
    private String email;
    @NonNull
    private String password;
}
