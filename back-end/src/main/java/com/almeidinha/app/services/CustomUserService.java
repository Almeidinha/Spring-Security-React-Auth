package com.almeidinha.app.services;

import com.almeidinha.app.entities.User;
import com.almeidinha.app.repository.UserDetailRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService implements UserDetailsService {

    private final UserDetailRepository userDetailRepository;

    public CustomUserService(UserDetailRepository userDetailRepository) {
        this.userDetailRepository = userDetailRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userDetailRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User Not found with name: " + username);
        }
        return user;
    }
}
