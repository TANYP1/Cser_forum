package com.typer.subject.interceptor;

import com.typer.subject.mapper.SessionMapper;
import com.typer.subject.model.entity.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private SessionMapper sessionMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        String sessionId = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSIONID".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }
        if (sessionId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        Session session = sessionMapper.getById(sessionId);
        if (session == null || !session.getIsValid() || session.getExpiresAt().before(new Timestamp(System.currentTimeMillis()))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        //刷新有效期
        session.setExpiresAt(new Timestamp(System.currentTimeMillis()+24*60*60*1000));
        sessionMapper.updateSession(session);
        return true;
    }
}
