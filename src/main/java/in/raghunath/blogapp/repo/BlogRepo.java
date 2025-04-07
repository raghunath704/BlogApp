package in.raghunath.blogapp.repo;

import in.raghunath.blogapp.model.Blog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepo extends MongoRepository<Blog,String> {

    List<Blog> findByIsPublishedTrue();

    // Optional but good: Find published blog by ID
    Optional<Blog> findByIdAndIsPublishedTrue(String id);
}
