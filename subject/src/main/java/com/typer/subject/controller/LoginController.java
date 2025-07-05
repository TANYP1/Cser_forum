package com.typer.subject.controller;


import com.typer.subject.common.Resp;
import com.typer.subject.model.dto.LoginDTO;
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

    @PostMapping("/login")
    Resp<LoginVO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response){
        try {
            return loginService.login(loginDTO,response);
        } catch (Exception e) {
            logger.error("登录失败：{}", e.getMessage());
            return Resp.failed("登录失败："+e.getMessage(),null);
        }
    }

}
