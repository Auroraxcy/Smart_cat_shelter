package com.doctor.controller;

import com.doctor.dto.ApiResponse;
import com.doctor.entity.User;
import com.doctor.service.UserService;
import com.doctor.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return ApiResponse.success("注册成功", registeredUser);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            User user = userService.login(username, password);
            String token = jwtTokenUtil.generateToken(user.getUsername(), user.getRole().name());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            
            return ApiResponse.success("登录成功", response);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }

    @GetMapping("/current")
    public ApiResponse<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            if (!jwtTokenUtil.validateToken(token)) {
                return ApiResponse.error(401, "Token无效");
            }
            String username = jwtTokenUtil.getUsernameFromToken(token);
            User user = userService.getCurrentUser(username);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(401, "获取用户信息失败");
        }
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout() {
        return ApiResponse.success("登出成功", null);
    }
}
