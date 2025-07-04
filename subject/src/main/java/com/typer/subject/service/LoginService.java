package com.typer.subject.service;

import com.typer.subject.common.Resp;
import com.typer.subject.model.dto.LoginDTO;

public interface LoginService {
    /**
     * 登录接口
     * @param loginDTO 传递的参数
     * @return 返回成功登录信息，失败的话直接抛异常。
     */
    Resp<String> login(LoginDTO loginDTO);

}
