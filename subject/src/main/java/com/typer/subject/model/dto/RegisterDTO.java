package com.typer.subject.model.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String userName;
    private String password;
    private String email;
}
