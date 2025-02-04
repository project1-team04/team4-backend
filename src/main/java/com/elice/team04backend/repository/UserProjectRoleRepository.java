package com.elice.team04backend.repository;

import com.elice.team04backend.entity.Project;
import com.elice.team04backend.entity.UserProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProjectRoleRepository extends JpaRepository<UserProjectRole, Long> {

    @Query( "SELECT DISTINCT upr FROM UserProjectRole upr " +
            "JOIN FETCH upr.user u " +
            "JOIN FETCH upr.project p " +
            "WHERE u.id = :userId AND p.id = :projectId")
    Optional<UserProjectRole> findByUserIdAndProjectId(
            @Param("userId") Long userId,
            @Param("projectId") Long projectId);

    @Query( "SELECT upr FROM UserProjectRole upr " +
            "JOIN FETCH upr.user " +
            "WHERE upr.project.id = :projectId")
    List<UserProjectRole> findAllByProjectId(@Param("projectId") Long projectId);

    boolean existsByUserIdAndProjectId(Long userId, Long projectId);
    @Query("SELECT DISTINCT upr.project FROM UserProjectRole upr " +
            "WHERE upr.user.id = :userId AND upr.role = 'MANAGER'")
    List<Project> findProjectsByUserIdRoleManager(@Param("userId") Long userId);

    @Query( "SELECT upr FROM UserProjectRole upr " +
            "JOIN FETCH upr.user " +
            "WHERE upr.project.id = :projectId AND upr.role = 'MEMBER'")
    List<UserProjectRole> findAllUserByProjectIdRoleMember(@Param("projectId") Long projectId);
}
