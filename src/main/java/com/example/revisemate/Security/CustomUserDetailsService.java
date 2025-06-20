package com.example.revisemate.Security;

import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedUsername = username.toLowerCase().trim();
        User user = userRepository.findByUsername(normalizedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + normalizedUsername));
        return new CustomUserDetails(user);
    }

}
