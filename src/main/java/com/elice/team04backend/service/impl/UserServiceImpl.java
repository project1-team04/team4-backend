package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.model.UserDetailsImpl;
import com.elice.team04backend.entity.User;
import com.elice.team04backend.repository.UserRepository;
import com.elice.team04backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        return userDetails;
    }
}
