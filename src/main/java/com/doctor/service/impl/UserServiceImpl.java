package com.doctor.service.impl;

import com.doctor.entity.User;
import com.doctor.enums.UserRole;
import com.doctor.exception.BusinessException;
import com.doctor.repository.UserRepository;
import com.doctor.service.UserService;
import com.doctor.util.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }
        
        // 设置默认角色
        if (user.getRole() == null) {
            user.setRole(UserRole.USER);
        }
        
        // 根据角色验证密码强度
        String passwordValidationResult = PasswordValidator.validatePassword(user.getPassword(), user.getRole());
        if (passwordValidationResult != null) {
            throw new BusinessException(400, passwordValidationResult);
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        return user;
    }

    @Override
    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }
}
