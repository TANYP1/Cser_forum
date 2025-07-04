package com.typer.subject.model.entity;

import lombok.Data;

@Data
public class Session {
    private String sessionId;           // 会话唯一标识
    private Integer userId;             // 关联的用户ID
    private java.sql.Timestamp createdAt;   // 会话创建时间
    private java.sql.Timestamp expiresAt;   // 会话过期时间
    private Boolean isValid;            // 会话是否有效

}
