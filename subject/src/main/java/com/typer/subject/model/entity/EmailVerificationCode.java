package com.typer.subject.model.entity;

import lombok.Data;

/**
 * 对应数据库的实体类
 */
@Data
public class EmailVerificationCode {
    private Integer id;
    private String email;
    private String code;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime expiresAt;
}
