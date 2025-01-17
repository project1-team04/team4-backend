package com.elice.team04backend.repository;

import com.elice.team04backend.dto.project.ProjectResponseDto;
import com.elice.team04backend.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    /**
     * TODO 유저 아이디별로 찾기 + 페이징처리
     */

    @Query("SELECT p FROM Project p " +
            "JOIN p.userProjectRoles upr " +
            "WHERE upr.user.id = :userId")
    Page<Project> findByUserId(@Param("userId") Long userId, Pageable pageable);
    boolean existsByProjectKey(String projectKey);

}
