package in.raghunath.blogapp.repo;

import in.raghunath.blogapp.model.Blog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepo extends MongoRepository<Blog,String> {
}
