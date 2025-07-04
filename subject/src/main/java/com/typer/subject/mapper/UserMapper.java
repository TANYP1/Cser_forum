package com.typer.subject.mapper;

import com.typer.subject.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT * from users WHERE email = #{email}")
    User getUserByEmail(String email);
}
