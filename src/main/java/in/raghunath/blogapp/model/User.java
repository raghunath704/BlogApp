package in.raghunath.blogapp.model;
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
    private String username;
    @Indexed(unique = true)
    private String email;
    private String password;
}
