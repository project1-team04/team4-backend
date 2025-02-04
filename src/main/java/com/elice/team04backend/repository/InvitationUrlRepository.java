package com.elice.team04backend.repository;

import com.elice.team04backend.entity.InvitationUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationUrlRepository extends JpaRepository<InvitationUrl, Long> {
    Optional<InvitationUrl> findByTemplateKey(String templateKey);
}
