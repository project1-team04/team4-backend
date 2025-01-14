package com.elice.team04backend.service.Impl;

import com.elice.team04backend.common.constant.UserStatus;
import com.elice.team04backend.dto.project.ProjectRequestDto;
import com.elice.team04backend.dto.project.ProjectResponseDto;
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


import static org.assertj.core.api.Assertions.*;

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
//        User user = new User("james123@naver.com", "james", "1234", UserStatus.ACTIVE);
//        User signIn = userService.signIn(user);
//        User findUser = userRepository.findById(signIn.getId()).get();
//        assertThat(findUser.getId()).isEqualTo(signIn.getId());
    }

    @Test
    void 프로젝트_생성_테스트() {
        ProjectRequestDto projectRequestDto = new ProjectRequestDto("my project");
        ProjectResponseDto findResponseDto = projectService.postProject(projectRequestDto);
        log.info("id = {}", findResponseDto.getId());
        log.info("name = {}", findResponseDto.getName());
        log.info("issueCnt = {}", findResponseDto.getIssueCount());
        log.info("projectKey = {}", findResponseDto.getProjectKey());
        assertThat(findResponseDto.getName()).isEqualTo(projectRequestDto.getName());
    }

    private String generatedProjectKey(ProjectRequestDto projectRequestDto) {
        StringBuilder sb = new StringBuilder();
        String[] s = projectRequestDto.getName().toUpperCase().split(" ");
        for (String value : s) {
            char input = value.charAt(0);
            if ('0' <= input && input <= '9') {
                continue;
            }
            sb.append(input);
        }
        return sb.toString();
    }
}