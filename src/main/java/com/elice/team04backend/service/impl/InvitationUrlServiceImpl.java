package com.elice.team04backend.service.impl;

import com.elice.team04backend.entity.InvitationUrl;
import com.elice.team04backend.repository.InvitationUrlRepository;
import com.elice.team04backend.service.InvitationUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class InvitationUrlServiceImpl implements InvitationUrlService {
    private final InvitationUrlRepository invitationUrlRepository;

    @Override
    @PostConstruct
    public void init() {
        //프론트 확인을 위해서 url 수정, 백에서 테스트 원할 시 코드 수정하고 진행 해야함
        //back String invitationLink = String.format("http://localhost:8080/api/accept/%s", token);
        //front String invitationLink = String.format("http://localhost:3000/accept/%s", token);
        //server String invitationLink = String.format("http://34.22.102.28:8080/accept/%s", token);
        //http://34.22.102.28:8080

        InvitationUrl invitationUrl = new InvitationUrl(
                "INVITATION_URL",
                "http://34.22.102.28:8080/api/accept/%s"
        );

        InvitationUrl loginUrl = new InvitationUrl(
                "LOGIN_URL",
                "http://34.22.102.28/auth"
        );
        invitationUrlRepository.save(invitationUrl);
        invitationUrlRepository.save(loginUrl);
    }
}
