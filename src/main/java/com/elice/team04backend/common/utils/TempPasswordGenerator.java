package com.elice.team04backend.common.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TempPasswordGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // 사용할 문자와 숫자
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?"; // 특수문자 목록
    private static final int MIN_LENGTH = 8; // 최소 길이
    private static final int MAX_LENGTH = 12; // 최대 길이
    private static final SecureRandom RANDOM = new SecureRandom(); // 보안 강화된 난수 생성기

    /**
     * 특수문자 1개 이상, 영어(대문자)와 숫자가 포함된 8~12자리 인증 코드 생성
     * @return 인증 코드
     */
    public static String generateTempPassword() {
        int length = RANDOM.nextInt(MAX_LENGTH - MIN_LENGTH + 1) + MIN_LENGTH; // 8~12 사이 랜덤 길이

        List<Character> password = new ArrayList<>();

        // 필수 요소 각각 하나씩 추가
        password.add(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length()))); // 영어 대문자 또는 숫자
        password.add(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length()))); // 추가 문자
        password.add(SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length()))); // 특수문자 추가

        // 나머지 랜덤 문자 채우기
        for (int i = password.size(); i < length; i++) {
            password.add(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        // 리스트를 섞어 랜덤하게 만들기
        Collections.shuffle(password, RANDOM);

        // 문자열로 변환 후 반환
        StringBuilder verificationCode = new StringBuilder();
        for (char c : password) {
            verificationCode.append(c);
        }

        return verificationCode.toString();
    }
}