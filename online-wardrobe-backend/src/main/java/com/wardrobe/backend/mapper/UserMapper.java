package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    void insert(User user);
    void update(User user);
    void deleteById(Integer id);
    User findByUserName(String userName);
    User findByPhone(String phone);
    User findById(Integer id);
    List<User> findAll();
    List<User> findByParams(@Param("userName") String userName,
                            @Param("phone") String phone);
    void updateProfile(@Param("id") Integer id, @Param("phone") String phone, @Param("address") String address);
    int countByRole(@Param("role") Integer role);
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}
