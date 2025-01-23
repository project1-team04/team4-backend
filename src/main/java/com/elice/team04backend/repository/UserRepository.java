package com.elice.team04backend.repository;

import com.elice.team04backend.common.constant.UserStatus;
import com.elice.team04backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
    Optional<User> findByRefreshToken(String refreshToken);
    boolean existsByEmail(String email);

}
