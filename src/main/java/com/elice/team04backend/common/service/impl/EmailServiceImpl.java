package com.elice.team04backend.common.service.impl;

import com.elice.team04backend.common.exception.CustomException;
import com.elice.team04backend.common.exception.ErrorCode;
import com.elice.team04backend.common.service.EmailService;
import com.elice.team04backend.entity.EmailTemplate;
import com.elice.team04backend.repository.EmailTemplateRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailTemplateRepository emailTemplateRepository;


    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom("threadly@elice.com");

        javaMailSender.send(message);
    }

    @Override
    public void sendEmailWithTemplate(String templateKey, String to, Map<String, String> variables) {
        EmailTemplate template = emailTemplateRepository.findByTemplateKey(templateKey)
                .orElseThrow(() -> new CustomException(ErrorCode.TEMPLATE_NOT_FOUND));

        String subject = replace(template.getSubject(), variables);
        String content = replace(template.getContent(), variables);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom("threadly@elice.com");
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private String replace(String text, Map<String, String> variables) {
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return text;
    }

    @PostConstruct
    public void init() {
        EmailTemplate inviteTemplate = new EmailTemplate(
                "INVITATION_EMAIL",
                "프로젝트 초대",
                """
                        <p>안녕하세요,</p>
                        <p>귀하를 <strong>{projectName}</strong> 프로젝트에 초대합니다.</p>
                        <p>해당 페이지에 계정이 있으시다면 로그인 후 초대 내용을 확인하실 수 있으며</p>
                        <p>계정이 없으시다면 가입을 하신 후 프로젝트 매니저에게 다시 재요청을 부탁하셔야 합니다.</p>
                        <p>감사합니다.</p>
                        <p>아래 링크를 클릭하여 초대를 수락하세요:</p>
                        <a href="{invitationLink}">Accept Invitation</a>
                        """
        );
        emailTemplateRepository.save(inviteTemplate);

        EmailTemplate acceptTemplate = new EmailTemplate(
                "INVITATION_ACCEPTED",
                "초대 수락",
                """
                        <html>
                            <head>
                                <meta charset="UTF-8">
                                <title>초대 수락</title>
                            </head>
                            <body>
                                <h1>프로젝트에 참여하신걸 환영합니다.</h1>
                                <p>성공적으로 프로젝트에 참여하셨습니다.</p>
                            </body>
                        </html>
                        """
        );
        emailTemplateRepository.save(acceptTemplate);
    }

}
