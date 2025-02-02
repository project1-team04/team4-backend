package com.elice.team04backend.common.service;

import java.util.Map;

public interface EmailService {
    void sendEmail(String to, String subject, String content);
    void sendEmailWithTemplate(String templateKey, String to, Map<String, String> variables);
}
