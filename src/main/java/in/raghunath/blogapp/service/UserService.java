package in.raghunath.blogapp.service;

import in.raghunath.blogapp.DTO.SignupRequest;
import in.raghunath.blogapp.model.User;
import in.raghunath.blogapp.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register new users
    public Boolean registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return false; // Username already exists
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword())); // Encrypt password
        user.setEmail(signupRequest.getEmail());
        userRepository.save(user);

        return true;
    }




    // Retrieve all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Retrieve a user by username
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public Boolean deleteUserByUserId(String id) {
        userRepository.deleteById(id);
        if(userRepository.existsById(id)) return false;
        return true;
    }
}
