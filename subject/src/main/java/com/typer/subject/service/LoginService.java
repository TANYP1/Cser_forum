package com.typer.subject.service;

import com.typer.subject.common.Resp;
import com.typer.subject.model.dto.LoginDTO;
import com.typer.subject.model.dto.RegisterDTO;
import com.typer.subject.model.dto.VerifyCodeDTO;
import com.typer.subject.model.vo.LoginVO;

import javax.servlet.http.HttpServletResponse;


public interface LoginService {
    /**
     * 登录接口
     * @param loginDTO 传递的参数
     * @return 返回成功登录信息，失败的话直接抛异常。
     */
    Resp<LoginVO> login(LoginDTO loginDTO, HttpServletResponse response);

    /**
     * 获取验证码
     * @param userEmail 用户邮箱
     * @return 验证码信息
     */
    Resp<String> getCode(String userEmail);

    /**
     * 校验邮箱和验证码
     * @param verifyCodeDTO 邮箱和验证码
     * @return 校验结果
     */
    Resp<Void> verifyCode(VerifyCodeDTO verifyCodeDTO);

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    Resp<Void> register(RegisterDTO registerDTO);
}
