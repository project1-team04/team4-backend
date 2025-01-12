package com.elice.team04backend.service.Impl;

import com.elice.team04backend.common.constant.UserStatus;
import com.elice.team04backend.dto.Project.ProjectRequestDto;
import com.elice.team04backend.entity.Project;
import com.elice.team04backend.entity.User;
import com.elice.team04backend.repository.ProjectRepository;
import com.elice.team04backend.repository.UserRepository;
import com.elice.team04backend.service.ProjectService;
import com.elice.team04backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@Transactional
@SpringBootTest
@Slf4j
class ProjectServiceImplTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProjectService projectService;

    @Test
    void 유저_생성_테스트() {
        User user = new User("james123@naver.com", "james", "1234", UserStatus.ACTIVE);
        Long userId = userService.signIn(user);
        User findUser = userRepository.findById(userId).get();
        assertThat(findUser.getId()).isEqualTo(userId);
    }

    @Test
    void 프로젝트_생성_테스트() {
        ProjectRequestDto projectRequestDto = new ProjectRequestDto("my project");
        StringBuilder sb = new StringBuilder();
        String[] s = projectRequestDto.getName().toUpperCase().split(" ");
        for (int i = 0; i < s.length; i++) {
            sb.append(s[i].charAt(0));
        }
        log.info("{}", sb);


    }
}