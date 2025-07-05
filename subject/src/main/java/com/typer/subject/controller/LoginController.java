package com.typer.subject.controller;


import com.typer.subject.common.Resp;
import com.typer.subject.model.dto.LoginDTO;
import com.typer.subject.model.dto.RegisterDTO;
import com.typer.subject.model.dto.VerifyCodeDTO;
import com.typer.subject.model.vo.LoginVO;
import com.typer.subject.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.http.HttpResponse;

@RestController
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private LoginService loginService;

    /**
     * 用户登录
     * @param loginDTO 邮箱和密码
     * @param response cookie
     * @return 登录用户名，和管理员信息
     */
    @PostMapping("/login")
    Resp<LoginVO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response){
        try {
            return loginService.login(loginDTO,response);
        } catch (Exception e) {
            logger.error("登录失败：{}", e.getMessage());
            return Resp.failed("登录失败："+e.getMessage(),null);
        }
    }

    /**
     * 获取验证码
     * @param userEmail 用户邮箱
     * @return 验证码信息
     */
    @PostMapping("/getCode")
    Resp<String> getCode(@RequestBody String userEmail){
        try{
            return loginService.getCode(userEmail);
        }catch (Exception e){
            logger.error("获取验证码失败：{}",e.getMessage());
            return Resp.failed("获取验证码失败："+e.getMessage(),null);
        }
    }

    /**
     * 校验邮箱和验证码
     * @param verifyCodeDTO 邮箱和验证码
     * @return 校验结果
     */
    @PostMapping("/verifyCode")
    Resp<Void> verifyCode(@RequestBody VerifyCodeDTO verifyCodeDTO){
        try{
            return loginService.verifyCode(verifyCodeDTO);
        }catch (Exception e){
            logger.error("验证码无效：{}",e.getMessage());
            return Resp.failed("获取验证码失败："+e.getMessage(),null);
        }
    }

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    Resp<Void> register(@RequestBody RegisterDTO registerDTO){
        try{
            return loginService.register(registerDTO);
        }catch (Exception e){
            logger.error("用户注册失败：{}",e.getMessage());
            return Resp.failed("用户注册失败："+e.getMessage(),null);        }
    }



}
