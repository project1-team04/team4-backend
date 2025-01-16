package com.elice.team04backend.repository;

import com.elice.team04backend.entity.IssueImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueImageRepository extends JpaRepository<IssueImage, Long> {
}
