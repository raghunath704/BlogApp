package in.raghunath.blogapp.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "blogs")
public class Blog {
    @Id
    private String id;

    private String authorUsername;
    private String topic;

    private String title;

    private String subtitle;
    private String content;
    private Boolean isPublished=false;
    @CreatedDate
    private Date createdAt;
    @CreatedDate
    private Date updatedAt;

    private String imageUrl;

    private String imagePublicId;
}
