package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(String email);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Insert("INSERT INTO user(username, password, email, birthday, avatar, balance) " +
            "VALUES(#{username}, #{password}, #{email}, #{birthday}, #{avatar}, #{balance})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET " +
            "email = #{email}, " +
            "birthday = #{birthday}, " +
            "avatar = #{avatar}, " +
            "password = #{password}, " +
            "balance = #{balance} " +
            "WHERE id = #{id}")
    int update(User user);

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);
} 