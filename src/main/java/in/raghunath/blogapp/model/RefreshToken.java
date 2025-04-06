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
    private String id;

    @Indexed(unique = true)
    @Field("token")
    private String token;

    @Indexed
    @Field("username")
    private String username;

    @Field("expiry_date")
    private Instant expiryDate;

}