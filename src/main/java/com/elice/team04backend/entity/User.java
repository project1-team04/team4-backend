package com.elice.team04backend.entity;

import com.elice.team04backend.common.constant.Provider;
import com.elice.team04backend.common.constant.UserStatus;
import com.elice.team04backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
//@Table(name = "member") // h2 database에 user 예약어가 있어서 잠시 설정
public class User extends BaseEntity {

//    public User(String email, String username, String password, UserStatus status) {
//        this.email = email;
//        this.username = username;
//        this.password = password;
//        this.status = status;
//    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "provider_id")
    private String providerId;

    @OneToMany(mappedBy = "assignee")
    private List<Issue> assigneeIssues = new ArrayList<>();

    @OneToMany(mappedBy = "reporter")
    private List<Issue> reporterIssues = new ArrayList<>();


}

