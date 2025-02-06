package com.elice.team04backend.common.utils;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TempPasswordGeneratorTest {

    @Test
    void generatePassword() {
        String password = TempPasswordGenerator.generateTempPassword();

        // 비밀번호 길이 검증 (8~12자)
        assertTrue(password.length() >= 8 && password.length() <= 12, "비밀번호 길이가 8~12자가 아닙니다.");

        // 특수문자 ! 또는 @ 포함 여부 검증
        assertTrue(password.contains("!") || password.contains("@"), "비밀번호에 ! 또는 @가 포함되어야 합니다.");

        // 영어 대소문자 또는 숫자 포함 여부 검증
        boolean containsUpperCase = Pattern.compile("[A-Z]").matcher(password).find();
        boolean containsLowerCase = Pattern.compile("[a-z]").matcher(password).find();
        boolean containsDigit = Pattern.compile("[0-9]").matcher(password).find();

        assertTrue(containsUpperCase || containsLowerCase || containsDigit, "비밀번호에 영어 대소문자 또는 숫자가 포함되어야 합니다.");
    }

}