package com.typer.subject.service.impl;

import com.typer.subject.common.Resp;
import com.typer.subject.model.dto.LoginDTO;
import com.typer.subject.service.LoginService;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Override
    public Resp<String> login(LoginDTO loginDTO) {
        // 1.取出用户邮箱，查数据库，检查用户是否存在
        return null;
    }
}
