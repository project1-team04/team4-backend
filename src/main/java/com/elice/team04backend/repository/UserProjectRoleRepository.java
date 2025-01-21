package com.elice.team04backend.repository;

import com.elice.team04backend.entity.UserProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProjectRoleRepository extends JpaRepository<UserProjectRole, Long> {
    Optional<UserProjectRole> findByUserIdAndProjectId(Long userId, Long projectId);
    List<UserProjectRole> findAllByProjectId(Long projectId);
    boolean existsByUserIdAndProjectId(Long userId, Long projectId);
}
