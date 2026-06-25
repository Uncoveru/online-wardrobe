package com.wardrobe.backend.service;

import com.wardrobe.backend.entity.User;
import com.wardrobe.backend.enums.RolePermission;
import com.wardrobe.backend.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.wardrobe.backend.exception.BusinessException;
import java.util.List;

/**
 * 用户服务：注册 / 登录 / 管理员登录 / 资料修改 / 用户管理
 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    // 密码规则：至少6位，同时包含字母和数字
    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d).{6,}$";

    public UserService(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // 用户注册（默认 role=2 普通用户, status=1 正常）
    public User register(User user) {
        checkUserNameDuplicate(user.getUserName());
        checkPhoneDuplicate(user.getPhone());
        validatePassword(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(2);
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        userMapper.insert(user);
        return user;
    }

    // 用户名重复校验
    private void checkUserNameDuplicate(String userName) {
        User existing = userMapper.findByUserName(userName);
        if (existing != null) {
            throw new BusinessException(409, "用户名已存在");
        }
    }

    // 手机号重复校验
    private void checkPhoneDuplicate(String phone) {
        if (phone != null && !phone.isBlank()) {
            User existing = userMapper.findByPhone(phone);
            if (existing != null) {
                throw new BusinessException(409, "手机号已被注册");
            }
        }
    }

    // 密码强度校验
    private void validatePassword(String password) {
        if (password == null || !password.matches(PASSWORD_REGEX)) {
            throw new BusinessException(400, "密码至少6位，且必须同时包含字母和数字");
        }
    }

    // 用户登录（支持用户名或手机号）
    public User login(String account, String password) {
        User user = userMapper.findByUserName(account);
        if (user == null) {
            user = userMapper.findByPhone(account);
        }
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        return user;
    }

    // 管理员/操作员登录（额外校验：必须为管理员角色 + 状态审核通过）
    public User loginAdmin(String account, String password) {
        User user = userMapper.findByUserName(account);
        if (user == null) {
            user = userMapper.findByPhone(account);
        }
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (!RolePermission.fromId(user.getRole()).isAdmin()) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException(403, "账号审核中，请联系超级管理员");
        }
        if (user.getStatus() == 2) {
            throw new BusinessException(403, "账号审核未通过");
        }
        return user;
    }

    // 手机号登录
    public User loginByPhone(String phone, String password) {
        User user = userMapper.findByPhone(phone);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "手机号或密码错误");
        }
        return user;
    }

    // 全部用户列表（脱敏：清除密码和地址）
    public List<User> getAllUsers() {
        List<User> users = userMapper.findAll();
        users.forEach(this::sanitize);
        return users;
    }

    // 按用户名/手机号搜索（脱敏）
    public List<User> searchUsers(String userName, String phone) {
        List<User> users = userMapper.findByParams(userName, phone);
        users.forEach(this::sanitize);
        return users;
    }

    // 脱敏：清除密码和地址
    private void sanitize(User user) {
        user.setPassword(null);
        user.setAddress(null);
    }

    // 管理员新增用户
    public User addUserByAdmin(User user) {
        checkUserNameDuplicate(user.getUserName());
        checkPhoneDuplicate(user.getPhone());
        validatePassword(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(2);
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        userMapper.insert(user);
        user.setPassword(null);
        return user;
    }

    // 操作员注册申请（role=3, status=0 待审核）
    public User registerOperator(User user) {
        checkUserNameDuplicate(user.getUserName());
        checkPhoneDuplicate(user.getPhone());
        validatePassword(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(3);
        user.setStatus(0);
        userMapper.insert(user);
        return user;
    }

    // 审核通过（status=1）
    public void approveUser(Integer id) {
        userMapper.updateStatus(id, 1);
    }

    // 审核拒绝（status=2）
    public void rejectUser(Integer id) {
        userMapper.updateStatus(id, 2);
    }

    // 管理员编辑用户（不允许修改用户名/手机/地址，密码可选填）
    public User updateUserByAdmin(User user) {
        User existing = userMapper.findById(user.getId());
        if (existing == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setUserName(existing.getUserName());
        user.setPhone(existing.getPhone());
        user.setAddress(existing.getAddress());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            validatePassword(user.getPassword());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existing.getPassword());
        }
        if (user.getRole() == null) {
            user.setRole(existing.getRole());
        }
        userMapper.update(user);
        User updated = userMapper.findById(user.getId());
        updated.setPassword(null);
        return updated;
    }

    // 删除用户
    public void deleteUser(Integer id) {
        userMapper.deleteById(id);
    }

    // 按 ID 查用户
    public User getUserById(Integer id) {
        return userMapper.findById(id);
    }

    // 修改密码（需校验旧密码）
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        validatePassword(newPassword);
        User user = userMapper.findById(userId);
        if (user == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(401, "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.update(user);
    }

    // 修改个人资料（手机号、地址）
    public void updateProfile(Integer userId, String phone, String address) {
        userMapper.updateProfile(userId, phone, address);
    }
}
