package com.elice.team04backend.repository;

import com.elice.team04backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    /**
     * TODO 유저 아이디별로 찾기 + 페이징처리
     */
}
