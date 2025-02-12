package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.constant.Provider;
import com.elice.team04backend.common.constant.SocialLoginType;
import com.elice.team04backend.common.constant.UserStatus;
import com.elice.team04backend.common.dto.response.*;
import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.oauth.impl.GoogleOAuth;
import com.elice.team04backend.common.oauth.impl.KakaoOAuth;
import com.elice.team04backend.common.oauth.impl.NaverOAuth;
import com.elice.team04backend.common.utils.JwtTokenProvider;
import com.elice.team04backend.common.utils.RefreshTokenProvider;
import com.elice.team04backend.entity.User;
import com.elice.team04backend.repository.UserRepository;
import com.elice.team04backend.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.elice.team04backend.common.exception.ErrorCode.INVALID_OAUTH_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final KakaoOAuth kakaoOAuth;
    private final NaverOAuth naverOAuth;
    private final GoogleOAuth googleOAuth;
    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    @Override
    public String accessRequest(SocialLoginType socialLoginType) {

        String redirectURL;

        switch (socialLoginType) {
            case KAKAO -> redirectURL = kakaoOAuth.getOauthRedirectURL();
            case NAVER -> redirectURL = naverOAuth.getOauthRedirectURL();
            case GOOGLE -> redirectURL = googleOAuth.getOauthRedirectURL();
            default -> throw new IllegalStateException("Unexpected value: " + socialLoginType);
        }

        return redirectURL;
    }

    @Override
    public AccessTokenResponseDto oAuthLoginOrJoin(SocialLoginType socialLoginType, String authCode, String state, HttpServletResponse response) {
        switch (socialLoginType) {
            case KAKAO: {

                //카카오로 일회성 인가 코드를 보내 액세스 토큰이 담긴 응답객체를 받아옴
                KakaoAccessTokenResponseDto kakaoAccessTokenResponseDto = kakaoOAuth.requestAccessToken(authCode);

                log.info("kakao accessToken: {}", kakaoAccessTokenResponseDto.getAccessToken());

                //액세스 토큰을 다시 카카오로 보내 카카오에 저장된 사용자 정보가 담긴 응답 객체를 받아온다.
                KakaoAccountResponseDto kakaoAccountResponseDto = kakaoOAuth.requestUserInfo(kakaoAccessTokenResponseDto.getAccessToken());

                log.info("kakao account: {}", kakaoAccountResponseDto);

                String userEmail = kakaoAccountResponseDto.getKakaoAccount().getEmail();
                String username = kakaoAccountResponseDto.getKakaoAccount().getProfile().getNickname();
                String profileImageUrl = kakaoAccountResponseDto.getKakaoAccount().getProfile().getProfileImageUrl();
                String providerId = String.valueOf(kakaoAccountResponseDto.getId());
                Provider provider = Provider.KAKAO;

                Optional<User> userOptional = userRepository.findByEmail(userEmail);

                // 이미 존재하는 회원이면 로그인
                if (userOptional.isPresent()) {
                    log.info("kakao 로그인 : {}", userEmail);

                    User user = userOptional.get();

                    return issueTokens(response, user, userEmail);

                } else { // 없는 회원이면 회원가입 진행 후 RefreshToken & AccessToken 발급 후 전송
                    log.info("kakao 회원가입 : {}", userEmail);

                    String randomPassword = UUID.randomUUID().toString();  // 임의의 UUID 문자열 생성
                    User user = User.builder()
                            .email(userEmail)
                            .username(username)
                            .password(randomPassword)
                            .profileImage(profileImageUrl)
                            .provider(provider)
                            .providerId(providerId)
                            .isVerified(true)
                            .status(UserStatus.ACTIVE)
                            .build();
                    
                    userRepository.save(user);

                    return issueTokens(response, user, userEmail);
                }
            }
            case NAVER: {
                NaverAccessTokenResponseDto naverAccessTokenResponseDto = naverOAuth.requestAccessToken(authCode, state);

                log.info("naver accessToken: {}", naverAccessTokenResponseDto.getAccessToken());

                NaverAccountResponseDto naverAccountResponseDto = naverOAuth.requestUserInfo(naverAccessTokenResponseDto.getTokenType(), naverAccessTokenResponseDto.getAccessToken());

                log.info("naver account: {}", naverAccountResponseDto);

                String userEmail = naverAccountResponseDto.getResponse().getEmail();
                String username = naverAccountResponseDto.getResponse().getName();
                String profileImageUrl = naverAccountResponseDto.getResponse().getProfileImage();
                String providerId = naverAccountResponseDto.getResultcode();
                Provider provider = Provider.NAVER;

                Optional<User> userOptional = userRepository.findByEmail(userEmail);

                // 이미 존재하는 회원이면 로그인
                if (userOptional.isPresent()) {
                    log.info("naver 로그인 : {}", userEmail);

                    User user = userOptional.get();

                    return issueTokens(response, user, userEmail);

                } else { // 없는 회원이면 회원가입 진행 후 RefreshToken & AccessToken 발급 후 전송
                    log.info("naver 회원가입 : {}", userEmail);

                    String randomPassword = UUID.randomUUID().toString();  // 임의의 UUID 문자열 생성
                    User user = User.builder()
                            .email(userEmail)
                            .username(username)
                            .password(randomPassword)
                            .profileImage(profileImageUrl)
                            .providerId(providerId)
                            .provider(provider)
                            .isVerified(true)
                            .status(UserStatus.ACTIVE)
                            .build();

                    userRepository.save(user);

                    return issueTokens(response, user, userEmail);
                }

            }
            case GOOGLE: {

                GoogleAccessTokenResponseDto googleAccessTokenResponseDto = googleOAuth.requestAccessToken(authCode);

                log.info("google accessToken: {}", googleAccessTokenResponseDto.getAccessToken());
                log.info("google scope: {}", googleAccessTokenResponseDto.getScope());

                GoogleAccountResponseDto googleAccountResponseDto = googleOAuth.requestUserInfo(googleAccessTokenResponseDto.getTokenType(), googleAccessTokenResponseDto.getAccessToken());

                log.info("google account: {}", googleAccountResponseDto);

                String userEmail = googleAccountResponseDto.getEmail();
                String username = googleAccountResponseDto.getName();
                String profileImageUrl = googleAccountResponseDto.getPicture();
                String providerId = googleAccountResponseDto.getId();
                Provider provider = Provider.GOOGLE;

                Optional<User> userOptional = userRepository.findByEmail(userEmail);

                // 이미 존재하는 회원이면 로그인
                if (userOptional.isPresent()) {
                    log.info("google 로그인 : {}", userEmail);

                    User user = userOptional.get();

                    return issueTokens(response, user, userEmail);

                } else { // 없는 회원이면 회원가입 진행 후 RefreshToken & AccessToken 발급 후 전송
                    log.info("google 회원가입 : {}", userEmail);

                    String randomPassword = UUID.randomUUID().toString();  // 임의의 UUID 문자열 생성
                    User user = User.builder()
                            .email(userEmail)
                            .username(username)
                            .password(randomPassword)
                            .profileImage(profileImageUrl)
                            .providerId(providerId)
                            .provider(provider)
                            .isVerified(true)
                            .status(UserStatus.ACTIVE)
                            .build();

                    userRepository.save(user);

                    return issueTokens(response, user, userEmail);
                }
            }
            default: {
                throw new CustomException(INVALID_OAUTH_TYPE);
            }
        }
    }

    private AccessTokenResponseDto issueTokens(HttpServletResponse response, User user, String userEmail) {
        String refreshToken = refreshTokenProvider.createAndStoreRefreshToken(user.getId());
        String accessToken = jwtTokenProvider.generateAccessToken(userEmail, user.getId(), user.getUsername());

        // 리프레시 토큰을 쿠키로 설정
        refreshTokenProvider.setRefreshTokenCookie(response, refreshToken);

        return new AccessTokenResponseDto(accessToken);
    }
}
