package com.tiffy.repository;

import com.tiffy.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void insertUser(@Param("user") User user);
    User findUserById(@Param("id") Long id);
    User findUserByUsername(@Param("username") String username);
    void updateUser(@Param("user") User user);
    void deleteUserById(@Param("id") Long id);
}
