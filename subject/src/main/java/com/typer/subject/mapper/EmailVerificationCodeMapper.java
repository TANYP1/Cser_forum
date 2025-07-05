package com.typer.subject.mapper;

import com.typer.subject.model.entity.EmailVerificationCode;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface EmailVerificationCodeMapper {
    @Insert("INSERT INTO email_verification_code (email, code, created_at, expires_at)\n" +
            "        VALUES (#{email}, #{code}, #{createdAt}, #{expiresAt})")
    int insert(EmailVerificationCode record);

    @Select(" SELECT * FROM email_verification_code\n" +
            "        WHERE email = #{email}\n" +
            "          AND code = #{code}\n" +
            "          AND expires_at > NOW()\n" +
            "        LIMIT 1")
    EmailVerificationCode selectValidCode(String email, String code);

}
