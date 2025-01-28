package com.elice.team04backend.service.impl;

import com.elice.team04backend.common.constant.UserStatus;
import com.elice.team04backend.common.model.UserDetailsImpl;
import com.elice.team04backend.dto.user.GetProfileResponseDto;
import com.elice.team04backend.dto.user.PatchProfileRequestDto;
import com.elice.team04backend.entity.User;
import com.elice.team04backend.repository.UserRepository;
import com.elice.team04backend.service.FirebaseStorageService;
import com.elice.team04backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FirebaseStorageService firebaseStorageService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

        if (user.getStatus().equals(UserStatus.DELETED) || user.getStatus().equals(UserStatus.INACTIVE)) {
            throw new IllegalStateException("비활성화된 회원 혹은 탈퇴한 회원입니다.");
        }

        return new UserDetailsImpl(user);
    }

    @Override
    public void patchProfile(Long userId, PatchProfileRequestDto patchProfileRequestDto, MultipartFile profileImage) {
        // 1. 유저 찾기
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("해당 유저는 존재하지 않습니다"));

        // 2. 프로필 이미지 수정
        String originalProfileImageUrl = user.getProfileImage();
        String updatedProfileImageUrl = user.getProfileImage();

        try {
            if (!profileImage.isEmpty()) {
                firebaseStorageService.deleteImage(originalProfileImageUrl);
                updatedProfileImageUrl = firebaseStorageService.uploadImage(profileImage);
            }
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 업로드에 실패했습니다.");
        }

        // 3. 프로필 수정
        user.patchProfile(patchProfileRequestDto.username(), updatedProfileImageUrl);

        userRepository.save(user);
    }

    @Override
    public GetProfileResponseDto getProfile(Long userId) {
        // 1. 유저 찾기
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("해당 유저는 존재하지 않습니다"));

        return GetProfileResponseDto.from(user);
    }
}
