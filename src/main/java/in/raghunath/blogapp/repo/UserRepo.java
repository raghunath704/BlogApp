package in.raghunath.blogapp.repo;

import in.raghunath.blogapp.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    // Add this method definition:
    Optional<User> findByEmail(String email); // <--- ADD THIS LINE

}