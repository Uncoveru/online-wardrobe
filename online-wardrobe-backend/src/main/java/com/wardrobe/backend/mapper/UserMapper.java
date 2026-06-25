package com.wardrobe.backend.mapper;

import com.wardrobe.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper {

    // 新增
    void insert(User user);

    // 更新
    void update(User user);

    // 删除
    void deleteById(Integer id);

    // 按用户名查询
    User findByUserName(String userName);

    // 按手机号查询
    User findByPhone(String phone);

    // 按 ID 查询
    User findById(Integer id);

    // 全部用户
    List<User> findAll();

    // 按用户名/手机号搜索
    List<User> findByParams(@Param("userName") String userName,
                            @Param("phone") String phone);

    // 修改个人资料
    void updateProfile(@Param("id") Integer id, @Param("phone") String phone, @Param("address") String address);

    // 统计指定角色数量（用于判断是否需要初始化管理员）
    int countByRole(@Param("role") Integer role);

    // 更新用户状态（0=待审核, 1=正常, 2=拒绝）
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}
