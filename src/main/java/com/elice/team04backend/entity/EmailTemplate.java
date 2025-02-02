package com.elice.team04backend.entity;

import com.elice.team04backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

@Entity
@Getter
@NoArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class EmailTemplate extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String templateKey; //

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public EmailTemplate(String templateKey, String subject, String content) {
        this.templateKey = templateKey;
        this.subject = subject;
        this.content = content;
    }

    public void updateContent(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }
}
