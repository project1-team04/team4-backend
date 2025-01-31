package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.constant.Provider;
import com.elice.team04backend.common.constant.Role;
import com.elice.team04backend.common.constant.UserStatus;
import com.elice.team04backend.common.dto.request.*;
import com.elice.team04backend.common.model.RedisDAO;
import com.elice.team04backend.common.service.EmailService;
import com.elice.team04backend.common.utils.JwtTokenProvider;
import com.elice.team04backend.common.utils.RefreshTokenProvider;
import com.elice.team04backend.common.utils.TempPasswordGenerator;
import com.elice.team04backend.entity.Project;
import com.elice.team04backend.entity.User;
import com.elice.team04backend.entity.UserProjectRole;
import com.elice.team04backend.repository.ProjectRepository;
import com.elice.team04backend.repository.UserProjectRoleRepository;
import com.elice.team04backend.repository.UserRepository;
import com.elice.team04backend.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.elice.team04backend.common.utils.VerificationCodeGenerator.generateVerificationCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RefreshTokenProvider refreshTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisDAO redisDAO;
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {
        // 1. 이메일 인증 확인
        if(!redisDAO.getValues(signUpRequestDto.getEmail()).equals("VERIFIED")) {
            throw new IllegalStateException("이메일 인증이 안됐습니다.");
        }

        // 2. 이메일 중복 확인
        if (userRepository.existsByEmail(signUpRequestDto.getEmail())){
            throw new IllegalStateException("중복되는 이메일입니다.");
        }

        User signUpUser = User.builder()
                .email(signUpRequestDto.getEmail())
                .username(signUpRequestDto.getUsername())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .isVerified(true)
                .provider(Provider.EMAIL)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(signUpUser);
    }

    @Override
    @Transactional
    public void logout(Long userId, HttpServletRequest request, HttpServletResponse response) {
        // 1. AccessToken 블랙리스트 등록
        invalidateAccessToken(request);

        // 2. RefreshToken 쿠키 만료 시키기
        invalidateRefreshToken(response);

        // 3. RefreshToken User 테이블에서 삭제
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저가 없습니다."));
        user.removeRefreshToken();
    }

    private void invalidateAccessToken(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        redisDAO.setValues(accessToken, "logout", jwtTokenProvider.getAccessTokenExpiration());
    }

    private static void invalidateRefreshToken(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 즉시 만료
        response.addCookie(refreshTokenCookie); // 클라이언트에 삭제 요청
    }

    @Override
    @Transactional
    public String refreshAccessToken(String refreshToken) {

        User user = refreshTokenProvider.validateRefreshToken(refreshToken);

        // Refresh Token 검증
        if (user == null) {
            throw new IllegalStateException("만료된 토큰입니다.");
        }

        return jwtTokenProvider.generateAccessToken(user.getEmail(), user.getId(), user.getUsername());
    }

    @Override
    public void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        // 1. 임시 비밀번호 생성
        String temporaryPassword = TempPasswordGenerator.generateTempPassword();

        // 2. 비밀번호 변경
        User user = userRepository.findByEmailAndStatus(resetPasswordRequestDto.email(), UserStatus.ACTIVE).orElseThrow(() -> new IllegalStateException("해당 유저가 존재하지 않습니다."));
        user.changePassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);

        // 3. 이메일 전송
        emailService.sendEmail(resetPasswordRequestDto.email(), "Threadly 임시 비밀번호입니다.", temporaryPassword);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto) {
        // 1. 유저 찾기
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("해당 유저가 존재하지 않습니다"));

        // 2. 비밀번호 비교
        if(!passwordEncoder.matches(changePasswordRequestDto.oldPassword(), user.getPassword())){
            throw new IllegalStateException("잘못된 비밀번호입니다.");
        }

        // 3. 비밀번호 변경
        user.changePassword(passwordEncoder.encode(changePasswordRequestDto.newPassword()));
        userRepository.save(user);
    }

    @Override
    public void deactivateAccount(Long userId, HttpServletRequest request, HttpServletResponse response) {
        // 1. 유저 찾기
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("해당 유저가 존재하지 않습니다"));

        // 2. 로그아웃 (토큰 비활성화)
        invalidateAccessToken(request);
        invalidateRefreshToken(response);
        user.removeRefreshToken();

        // 3. 유저 계정 비활성화
        user.deactivateAccount();
        userRepository.save(user);
    }

    @Override
    public Boolean validateEmail(ValidateEmailRequestDto validateEmailRequestDto) {
        return userRepository.existsByEmail(validateEmailRequestDto.email());
    }

    @Override
    public void confirmVerificationCode(ConfirmEmailRequestDto confirmEmailRequestDto) {
        String email = confirmEmailRequestDto.email();
        String savedVerificationCode = redisDAO.getValues(email);
        if(!savedVerificationCode.equals(confirmEmailRequestDto.verificationCode())){
            throw new IllegalStateException("잘못된 인증코드입니다.");
        }
        redisDAO.setValues(email, "VERIFIED", 300000L); // 5분간 해당 이메일이 인증됐음을 설정
    }

    @Override
    public void verifyEmail(VerifyEmailRequestDto verifyEmailRequestDto) {
        String verificationCode = generateVerificationCode();
        emailService.sendEmail(verifyEmailRequestDto.email(), "Threadly 인증코드 이메일", verificationCode);
        redisDAO.setValues(verifyEmailRequestDto.email(), verificationCode, 300000L); // 5분간 인증 설정
        log.info("{} 이메일 전송, 인증코드 {}", verifyEmailRequestDto.email(), verificationCode);
    }

    private final ProjectRepository projectRepository;
    private final UserProjectRoleRepository userProjectRoleRepository;

    @PostConstruct
    public void init() {
        User user = User.builder()
                .email("hi563@naver.com")
                .username("정태승")
                .provider(Provider.EMAIL)
                .status(UserStatus.ACTIVE)
                .isVerified(true)
                .password(passwordEncoder.encode("!a12345678"))
                .build();

        User user2 = User.builder()
                .email("hi564@naver.com")
                .username("정태승2")
                .provider(Provider.EMAIL)
                .status(UserStatus.ACTIVE)
                .isVerified(true)
                .password(passwordEncoder.encode("!a12345678"))
                .build();

        User user3 = User.builder()
                .email("rhkdgh930@naver.com")
                .username("명광호")
                .provider(Provider.EMAIL)
                .status(UserStatus.ACTIVE)
                .isVerified(true)
                .password(passwordEncoder.encode("!a12345678"))
                .build();

        User user4 = User.builder()
                .email("nerostarin@naver.com")
                .username("이태정")
                .provider(Provider.EMAIL)
                .status(UserStatus.ACTIVE)
                .isVerified(true)
                .password(passwordEncoder.encode("!a12345678"))
                .build();

        User user5 = User.builder()
                .email("minju_love@naver.com")
                .username("김민주")
                .provider(Provider.EMAIL)
                .status(UserStatus.ACTIVE)
                .isVerified(true)
                .password(passwordEncoder.encode("!a12345678"))
                .build();

        User user6 = User.builder()
                .email("choi_seo@naver.com")
                .username("최서영")
                .provider(Provider.EMAIL)
                .status(UserStatus.ACTIVE)
                .isVerified(true)
                .password(passwordEncoder.encode("!a12345678"))
                .build();

        User user7 = User.builder()
                .email("mkh9900@naver.com")
                .username("명광호")
                .provider(Provider.EMAIL)
                .status(UserStatus.ACTIVE)
                .isVerified(true)
                .password(passwordEncoder.encode("!a12345678"))
                .build();

        Project project = Project.builder()
                .projectKey("h")
                .name("hell")
                .issueCount(0L)
                .build();

        Project project2 = Project.builder()
                .projectKey("m")
                .name("moonlight")
                .issueCount(3L)
                .build();

        Project project3 = Project.builder()
                .projectKey("s")
                .name("sunrise")
                .issueCount(5L)
                .build();

        UserProjectRole userProjectRole = UserProjectRole.builder()
                .user(user)
                .project(project)
                .role(Role.MANAGER)
                .build();

        UserProjectRole userProjectRole2 = UserProjectRole.builder()
                .user(user2)
                .project(project2)
                .role(Role.MANAGER)
                .build();

        UserProjectRole userProjectRole3 = UserProjectRole.builder()
                .user(user3)
                .project(project3)
                .role(Role.MANAGER)
                .build();



        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);

        projectRepository.save(project);
        projectRepository.save(project2);
        projectRepository.save(project3);

        userProjectRoleRepository.save(userProjectRole);
        userProjectRoleRepository.save(userProjectRole2);
        userProjectRoleRepository.save(userProjectRole3);

    }

}
