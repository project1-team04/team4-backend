package com.elice.team04backend.service;

import com.elice.team04backend.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    User signIn(User user);
    User getUser(Long userId);
    List<User> getAllUsers();
    void deleteUser(Long userId);
}
