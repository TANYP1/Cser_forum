package com.typer.subject.service.impl;

import com.typer.subject.common.Resp;
import com.typer.subject.controller.LoginController;
import com.typer.subject.mapper.SessionMapper;
import com.typer.subject.mapper.UserMapper;
import com.typer.subject.model.dto.LoginDTO;
import com.typer.subject.model.entity.CserSession;
import com.typer.subject.model.entity.CserUser;
import com.typer.subject.model.vo.LoginVO;
import com.typer.subject.service.LoginService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.UUID;


@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SessionMapper sessionMapper;

    private static final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Override
    public Resp<LoginVO> login(LoginDTO loginDTO, HttpServletResponse response) {
        // 1.取出用户邮箱，判断邮箱格式是否合理，不合理抛出异常
        String userEmail=loginDTO.getEmail();
        if(!isValidEmail(userEmail)){
            logger.error("邮箱格式不合理：{}",userEmail);
            throw new IllegalArgumentException("邮箱格式不合理");
        }
        // 1.2查数据库，检查用户是否存在
        CserUser cserUser = userMapper.getUserByEmail(userEmail);
        //1.3如果用户不存在，直接抛出异常
        if(cserUser ==null){
            logger.error("用户不存在，{}",userEmail);
            throw new IllegalArgumentException("用户不存在");
        }
        //2.用户存在，采用对应的编码方式转换密码
        boolean matches = encoder.matches(loginDTO.getPassword(), cserUser.getPasswordHash());
        //3.判断用户密码是否一直
        //3.1不一致：抛出异常
        if(!matches){
            logger.error("用户密码错误：{}",userEmail);
            throw new IllegalArgumentException("用户密码错误");
        }
        //4.用户存在，带着cookie返回，并且返回用户信息
        String sessionId = UUID.randomUUID().toString();
        CserSession cserSession = new CserSession();
        cserSession.setSessionId(sessionId);
        cserSession.setUserId(cserUser.getId());
        cserSession.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        cserSession.setExpiresAt(new Timestamp(System.currentTimeMillis()+24*60*60*1000));
        cserSession.setIsValid(true);
        sessionMapper.insertSession(cserSession);
        //设置cookie
        Cookie cookie = new Cookie("SESSIONID",sessionId);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600*24);
        response.addCookie(cookie);
        //封装loginVo
        LoginVO loginVO = new LoginVO();
        loginVO.setUsername(cserUser.getUsername());
        loginVO.setIsAdmin(cserUser.getIsAdmin());
        logger.info("用户登录成功：{}", cserUser);
        return Resp.ok(loginVO);
    }

    /**
     * 验证邮箱格式
     * @param email 邮箱地址
     * @return 格式是否有效
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // 使用正则表达式验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
