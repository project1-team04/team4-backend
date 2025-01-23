package com.elice.team04backend.service;

import com.elice.team04backend.dto.user.GetProfileResponseDto;
import com.elice.team04backend.dto.user.PatchProfileRequestDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends UserDetailsService {

    UserDetails loadUserByUsername(String username);

    void patchProfile(Long userId, PatchProfileRequestDto patchProfileRequestDto, MultipartFile profileImage);

    GetProfileResponseDto getProfile(Long userId);
}
