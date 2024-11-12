package com.tiffy.repository;

import com.tiffy.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    void insertUser(User user);
    User findUserById(Long id);
    User findUserByUsername(String username);
    List<User> findUserByIncludeUserNickname(String nickname);
    User findUserByUserNickname(String nickname);
    void updateUser(User user);
    void deleteUserById(Long id);
    void updateUserPlace(User user);
}
