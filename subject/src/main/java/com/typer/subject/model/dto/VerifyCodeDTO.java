package com.typer.subject.model.dto;

import lombok.Data;

@Data
public class VerifyCodeDTO {
    private String email;
    //验证码
    private String verifyCode;
}
