package com.elice.team04backend.repository;

import com.elice.team04backend.entity.Issue;
import com.elice.team04backend.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    List<Label> findByProjectId(Long projectId);
    Optional<Label> findByProjectIdAndName(Long projectId, String labelName);

}
