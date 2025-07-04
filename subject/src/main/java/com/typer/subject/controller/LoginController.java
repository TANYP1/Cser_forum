package com.typer.subject.controller;


import com.typer.subject.common.Resp;
import com.typer.subject.model.dto.LoginDTO;
import com.typer.subject.service.LoginService;
import io.swagger.annotations.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    Resp<String> login(@RequestBody LoginDTO loginDTO){
        try {
            return loginService.login(loginDTO);
        } catch (Exception e) {
            return Resp.failed("登录失败：{}",e.toString());
        }
    }

}
