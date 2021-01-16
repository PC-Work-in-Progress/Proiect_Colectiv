package com.noteit.noteit.authentication.service;

import com.noteit.noteit.authentication.model.UserPrincipal;
import com.noteit.noteit.users.model.UserEntity;
import com.noteit.noteit.utils.ResourceNotFoundException;
import com.noteit.noteit.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {
        // Let people login with either username or email
//        System.out.println(usernameOrEmail);
        UserEntity user = userRepository.findByUsername(usernameOrEmail);
        System.out.println("USER:" + user.getFull_name());
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(String id) {
        UserEntity user = userRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("User", "id", id)
        );

        return UserPrincipal.create(user);
    }
}