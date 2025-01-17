package com.elice.team04backend.common.service;

public interface EmailService {
    void sendEmail(String to, String subject, String content);
}
