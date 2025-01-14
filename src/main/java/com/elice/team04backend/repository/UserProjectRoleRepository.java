package com.elice.team04backend.repository;

import com.elice.team04backend.entity.UserProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProjectRoleRepository extends JpaRepository<UserProjectRole, Long> {
}
