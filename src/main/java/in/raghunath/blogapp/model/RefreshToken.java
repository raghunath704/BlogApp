package in.raghunath.blogapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_tokens") // Maps to the "refresh_tokens" collection
public class RefreshToken {

    @Id
    private String id; // MongoDB uses String for _id

    // It's better to index the token itself for quick lookups
    @Indexed(unique = true)
    @Field("token") // Explicitly map field name
    private String token; // The actual opaque refresh token value

    // Link back to the user
    @Indexed // Index for potential lookups by username
    @Field("username")
    private String username;

    @Field("expiry_date")
    private Instant expiryDate;

}