package com.typer.subject.utils;

import java.security.SecureRandom;

public class CodeGenerator {

    // 可选的字符集
    private static final String NUMBER = "0123456789";
    private static final String LETTER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBER_LETTER = NUMBER + LETTER;

    private static final SecureRandom random = new SecureRandom();

    /**
     * 生成数字验证码
     * @param length 验证码长度
     * @return 验证码字符串
     */
    public static String generateNumberCode(int length) {
        return generateCode(NUMBER, length);
    }

    /**
     * 生成字母验证码
     * @param length 验证码长度
     * @return 验证码字符串
     */
    public static String generateLetterCode(int length) {
        return generateCode(LETTER, length);
    }

    /**
     * 生成数字+字母验证码
     * @param length 验证码长度
     * @return 验证码字符串
     */
    public static String generateNumberLetterCode(int length) {
        return generateCode(NUMBER_LETTER, length);
    }

    /**
     * 通用验证码生成方法
     */
    private static String generateCode(String charSet, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(charSet.length());
            sb.append(charSet.charAt(idx));
        }
        return sb.toString();
    }

}
