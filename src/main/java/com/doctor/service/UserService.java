package com.doctor.service;

import com.doctor.entity.User;

public interface UserService {
    User register(User user);
    User login(String username, String password);
    User getCurrentUser(String username);
}
