package com.typer.subject.service.impl;

import com.typer.subject.common.Resp;
import com.typer.subject.controller.LoginController;
import com.typer.subject.mapper.EmailVerificationCodeMapper;
import com.typer.subject.mapper.SessionMapper;
import com.typer.subject.mapper.UserMapper;
import com.typer.subject.model.dto.LoginDTO;
import com.typer.subject.model.dto.RegisterDTO;
import com.typer.subject.model.dto.VerifyCodeDTO;
import com.typer.subject.model.entity.CserSession;
import com.typer.subject.model.entity.CserUser;
import com.typer.subject.model.entity.EmailVerificationCode;
import com.typer.subject.model.vo.LoginVO;
import com.typer.subject.service.LoginService;

import com.typer.subject.utils.CodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;


@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SessionMapper sessionMapper;
    @Autowired
    private EmailVerificationCodeMapper codeMapper;
    @Autowired
    private MailService mailService;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private EmailVerificationCodeMapper emailVerificationCodeMapper;

    @Override
    public Resp<LoginVO> login(LoginDTO loginDTO, HttpServletResponse response) {
        // 1.取出用户邮箱，判断邮箱格式是否合理，不合理抛出异常
        String userEmail = loginDTO.getEmail();
        if (!isValidEmail(userEmail)) {
            logger.error("邮箱格式不合理：{}", userEmail);
            throw new IllegalArgumentException("邮箱格式不合理");
        }
        // 1.2查数据库，检查用户是否存在
        CserUser cserUser = userMapper.getUserByEmail(userEmail);
        //1.3如果用户不存在，直接抛出异常
        if (cserUser == null) {
            logger.error("用户不存在，{}", userEmail);
            throw new IllegalArgumentException("用户不存在");
        }
        //2.用户存在，采用对应的编码方式转换密码
        boolean matches = encoder.matches(loginDTO.getPassword(), cserUser.getPasswordHash());
        //3.判断用户密码是否一直
        //3.1不一致：抛出异常
        if (!matches) {
            logger.error("用户密码错误：{}", userEmail);
            throw new IllegalArgumentException("用户密码错误");
        }
        //4.用户存在，带着cookie返回，并且返回用户信息
        String sessionId = UUID.randomUUID().toString();
        CserSession cserSession = new CserSession();
        cserSession.setSessionId(sessionId);
        cserSession.setUserId(cserUser.getId());
        cserSession.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        cserSession.setExpiresAt(new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        cserSession.setIsValid(true);
        sessionMapper.insertSession(cserSession);
        //设置cookie
        Cookie cookie = new Cookie("SESSIONID", sessionId);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600 * 24);
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
     *
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

    @Override
    public Resp<String> getCode(String userEmail) {
        if (!isValidEmail(userEmail)) {
            logger.error("邮箱格式不合理：{}", userEmail);
            throw new IllegalArgumentException("邮箱格式不合理");
        }
        //1.验证邮箱是否注册过
        CserUser user = userMapper.getUserByEmail(userEmail);
        //1.1注册过直接返回信息
        if (user != null) {
            logger.error("用户已经存在：{}",userEmail);
            throw new IllegalArgumentException("用户已经存在");
        }

        //2.未注册过，创建验证码
        String code = CodeGenerator.generateNumberCode(6);
        //3.存储验证码和邮箱
        EmailVerificationCode emailVerificationCode = new EmailVerificationCode();
        emailVerificationCode.setCode(code);
        emailVerificationCode.setEmail(userEmail);
        emailVerificationCode.setCreatedAt(LocalDateTime.now());
        emailVerificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        try {
            emailVerificationCodeMapper.insert(emailVerificationCode);
            //4.发送验证码到邮箱
            mailService.sendSimpleMail(userEmail, "注册验证码", "您的验证码是：" + code+"\n有效期为10min，请在10min内完成注册");
            logger.info("发送验证码成功：{}",code);
            return Resp.ok("success");
        } catch (Exception e) {
            logger.error("验证码发送失败：{}",code);
            throw new IllegalArgumentException("验证码发送失败：" + e.getMessage());
        }
    }

    @Override
    public Resp<Void> verifyCode(VerifyCodeDTO verifyCodeDTO) {
        //1.查询是否存在实例
        String userEmail=verifyCodeDTO.getEmail();
        String code= verifyCodeDTO.getVerifyCode();
        EmailVerificationCode verificationCode = emailVerificationCodeMapper.selectValidCode(userEmail, code);
        if(verificationCode==null){
            //1.不存在返回失败
            logger.error("验证码校验失败：useremail: {},code: {}",userEmail,code);
            throw new IllegalArgumentException("验证码校验失败");
        }
        //2.存在返回成功
        logger.info("验证码校验成功：useremail: {},code: {}",userEmail,code);
        return Resp.ok();
    }

    @Override
    public Resp<Void> register(RegisterDTO registerDTO) {
        //1.验证邮箱是否注册过
        String userEmail = registerDTO.getEmail();
        String password = registerDTO.getPassword();
        String userName = registerDTO.getUserName();
        try {
            CserUser user = userMapper.getUserByEmail(userEmail);
            //2。注册过，返回失败
            if (user != null) {
                logger.error("用户已经注册过了：email: {},name:{}", userEmail, userName);
                throw new IllegalArgumentException("用户已经注册过！");
            }
            //2.1未注册过，校验和刚才的邮箱是否是一个
            EmailVerificationCode verifyEmail = emailVerificationCodeMapper.selectValidByEmail(userEmail);
            if(verifyEmail==null){
                throw new IllegalArgumentException("请使用正确的邮箱注册");
            }
            //3.未注册过，密码加密，写数据库
            String encodePassword = encoder.encode(password);
            CserUser cserUser = new CserUser();
            cserUser.setEmail(userEmail);
            cserUser.setUsername(userName);
            cserUser.setPasswordHash(encodePassword);
            cserUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            cserUser.setIsAdmin(false);
            cserUser.setIsActive(false);
            cserUser.setIsVerified(false);
            cserUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            int insert = userMapper.insert(cserUser);
            if(insert!=1){
                logger.error("用户创建失败：{}",user.getEmail());
                throw new IllegalArgumentException("用户创建失败");
            }
            logger.info("用户注册成功：email：{}，username：{}",userEmail,userName);
            return Resp.ok();
        }catch (Exception e){
            logger.error("用户创建失败：{}",e.getMessage());
            throw new IllegalArgumentException("用户创建失败: "+e.getMessage());
        }
    }

    @Override
    public Resp<Void> fixPassword(LoginDTO loginDTO) {
        String userEmail = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        //1.查询是否有这个用户
        CserUser user = userMapper.getUserByEmail(userEmail);
        //2。未注册过，返回失败
        if (user == null) {
            logger.error("用户没有注册过！：email: {}", userEmail);
            throw new IllegalArgumentException("用户没有注册过，请检查邮箱正确与否！");
        }
        //2.1注册过，校验和刚才的邮箱是否是一个
        EmailVerificationCode verifyEmail = emailVerificationCodeMapper.selectValidByEmail(userEmail);
        if(verifyEmail==null){
            logger.error("修改时间过期，请重新尝试修改,email:{}",userEmail);
            throw new IllegalArgumentException("修改时间过期，请重新尝试修改");
        }
        //3.修改密码
        String encodePassword = encoder.encode(password);
        user.setPasswordHash(encodePassword);
        userMapper.update(user);
        logger.info("用户修改密码成功：email: {}",userEmail);
        return Resp.ok();
    }
    @Override
    public Resp<String> getFixCode(String userEmail) {
        if (!isValidEmail(userEmail)) {
            logger.error("邮箱格式不合理：{}", userEmail);
            throw new IllegalArgumentException("邮箱格式不合理");
        }
        //1.验证邮箱是否注册过
        CserUser user = userMapper.getUserByEmail(userEmail);
        //1.1未注册过直接返回信息
        if (user == null) {
            logger.error("用户不存在：{}",userEmail);
            throw new IllegalArgumentException("用户不存在: "+userEmail);
        }

        //2.注册过，创建验证码
        String code = CodeGenerator.generateNumberCode(6);
        //3.存储验证码和邮箱
        EmailVerificationCode emailVerificationCode = new EmailVerificationCode();
        emailVerificationCode.setCode(code);
        emailVerificationCode.setEmail(userEmail);
        emailVerificationCode.setCreatedAt(LocalDateTime.now());
        emailVerificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        try {
            emailVerificationCodeMapper.insert(emailVerificationCode);
            //4.发送验证码到邮箱
            mailService.sendSimpleMail(userEmail, "注册验证码", "您的验证码是：" + code+"\n有效期为10min，请在10min内完成注册");
            logger.info("发送验证码成功：{}",code);
            return Resp.ok("success");
        } catch (Exception e) {
            logger.error("验证码发送失败：{}",code);
            throw new IllegalArgumentException("验证码发送失败：" + e.getMessage());
        }
    }

}
