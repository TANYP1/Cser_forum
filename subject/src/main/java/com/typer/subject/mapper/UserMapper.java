package com.typer.subject.mapper;

import com.typer.subject.model.entity.CserUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT * from users WHERE email = #{email}")
    CserUser getUserByEmail(String email);
    @Insert("INSERT INTO users (username, password_hash, email, phone_number, is_active, is_admin, is_verified, created_at, updated_at) " +
            "VALUES (#{username}, #{passwordHash}, #{email}, #{phoneNumber}, #{isActive}, #{isAdmin}, #{isVerified}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CserUser cserUser);
}
