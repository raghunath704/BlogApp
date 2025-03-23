package in.raghunath.blogapp.service;

import in.raghunath.blogapp.model.User;
import in.raghunath.blogapp.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepository;

    // Create a new user
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Retrieve all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Retrieve a user by username
    public User getUserByUsername(String username) {
        return userRepository.findByUserName(username);
    }
}
