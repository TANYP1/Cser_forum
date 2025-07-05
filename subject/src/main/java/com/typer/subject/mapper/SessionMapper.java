package com.typer.subject.mapper;

import com.typer.subject.model.entity.CserSession;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SessionMapper {
    //创建新的session
    @Insert("INSERT INTO sessions (session_id, user_id, created_at, expires_at, is_valid) " +
            "VALUES (#{sessionId}, #{userId}, #{createdAt}, #{expiresAt}, #{isValid})")
    int insertSession(CserSession cserSession);

    //根据id查找session
    @Select("SELECT * FROM sessions WHERE session_id = #{sessionId}")
    CserSession getById(String sessionId);


    //更新session
    @Update("UPDATE sessions SET expires_at = #{expiresAt} WHERE session_id = #{sessionId}")
    void updateSession(CserSession cserSession);
}
