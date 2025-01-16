package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.model.UserDetailsImpl;
import com.elice.team04backend.entity.User;
import com.elice.team04backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // TODO : userdetails Impl 리턴해주기.
        return new UserDetailsImpl(new User());
    }
}
