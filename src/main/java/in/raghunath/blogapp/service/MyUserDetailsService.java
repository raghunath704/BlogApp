package in.raghunath.blogapp.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // In a real app, query your database or external service
        // Here, we just return a hard-coded user
        if ("user".equals(username)) {
            return User.builder()
                    .username("user")
                    .password("{noop}password") // {noop} means no password encoder
                    .roles("USER")
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}
