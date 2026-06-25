package com.wardrobe.backend.entity;

/**
 * 用户实体
 */
public class User {

    private Integer id;        // 主键
    private String userName;   // 用户名
    private String password;   // 密码（BCrypt 加密存储）
    private String phone;      // 手机号
    private String address;    // 地址
    private Integer role;      // 角色（1=超级管理员, 2=普通用户, 3=操作员）
    private Integer status;    // 状态（0=待审核, 1=正常, 2=审核拒绝）
    private Integer deleted;   // 软删除标记

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
