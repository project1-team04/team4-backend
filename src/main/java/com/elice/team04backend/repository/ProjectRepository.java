package com.elice.team04backend.repository;

import com.elice.team04backend.entity.Project;
import com.elice.team04backend.repository.querydsl.ProjectRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {
    @Query("SELECT p FROM Project p " +
            "JOIN UserProjectRole upr ON p.id = upr.project.id " +
            "WHERE upr.user.id = :userId")
    Page<Project> findByUserId(@Param("userId") Long userId, Pageable pageable);
    void deleteById(Long projectId);

    @Query("SELECT p.projectKey FROM Project p WHERE p.projectKey LIKE :prefix%")
    List<String> findAllProjectKeysByPrefix(@Param("prefix") String prefix);
}
