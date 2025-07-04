package com.typer.subject.model.entity;

import lombok.Data;

@Data
public class User {
    private Integer id;                 // 用户主键ID
    private String username;            // 用户名
    private String passwordHash;        // bcrypt加密后的用户密码
    private String email;               // 用户邮箱
    private String phoneNumber;         // 用户手机号
    private Boolean isActive;           // 用户账号状态
    private Boolean isAdmin;
    private Boolean isVerified;         // 邮箱/手机是否已验证
    private java.sql.Timestamp createdAt;   // 账户创建时间
    private java.sql.Timestamp updatedAt;   // 账户更新时间

}
