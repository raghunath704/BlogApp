package in.raghunath.blogapp.repo;

import in.raghunath.blogapp.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    // Optional: Add custom query methods here
    User findByUserName(String userName);

}
